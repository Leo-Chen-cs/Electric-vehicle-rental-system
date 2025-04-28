package vip.xiaonuo.tasks;

import com.thoughtworks.xstream.converters.time.LocalDateTimeConverter;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vip.xiaonuo.rental.modular.rental.entity.Rental;
import vip.xiaonuo.rental.modular.rental.enums.RentalEnum;
import vip.xiaonuo.rental.modular.rental.service.RentalService;
import vip.xiaonuo.scooter.modular.scooter.entity.Scooter;
import vip.xiaonuo.scooter.modular.scooter.enums.ScooterEnum;
import vip.xiaonuo.scooter.modular.scooter.service.ScooterService;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author: xiaofengs
 * @description :
 * @date: 2025/2/11 20:32
 * @version: 1.0
 */

@Component
public class RentalChange {

    @Resource
    private ScooterService scooterService;

    @Resource
    private RentalService rentalService;

    @Scheduled(fixedRate = 10000)
    public void change() {
        //如果一个租赁到时间并且没有被取消，就更改Scooter的状态，
        //如果一个租赁完成，也更改Scooter的状态
        // 查询所有的租赁记录
        for (Rental rental : rentalService.list()) {
            if (rental.getStatus().equalsIgnoreCase(RentalEnum.WAITING.getValue()) &&
                    rental.getStartTime().toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDateTime().isBefore(LocalDateTime.now())
            ) {
                rental.setStatus(RentalEnum.ACTIVE.getValue());
                rentalService.updateById(rental);
                Scooter scooter = scooterService.getById(rental.getScooterId());
                scooter.setStatus(ScooterEnum.RENTED.getValue());
                scooterService.updateById(scooter);
            } else if (
                    rental.getStatus().equalsIgnoreCase(RentalEnum.ACTIVE.getValue()) &&
                            rental.getEndTime().toInstant().atZone(ZoneId.systemDefault())
                                    .toLocalDateTime().isBefore(LocalDateTime.now())
            ) {
                rental.setStatus(RentalEnum.COMPLETED.getValue());
                rentalService.updateById(rental);
                Scooter scooter = scooterService.getById(rental.getScooterId());
                scooter.setStatus(ScooterEnum.AVAILABLE.getValue());
                scooterService.updateById(scooter);
            }
        }

    }
}

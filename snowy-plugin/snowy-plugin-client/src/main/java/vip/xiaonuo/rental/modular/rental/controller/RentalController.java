/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.rental.modular.rental.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.DevEmailApi;
import vip.xiaonuo.rental.modular.rental.entity.Rental;
import vip.xiaonuo.rental.modular.rental.enums.RentalEnum;
import vip.xiaonuo.rental.modular.rental.param.*;
import vip.xiaonuo.rental.modular.rental.service.RentalService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import vip.xiaonuo.scooter.modular.scooter.service.ScooterService;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * 租赁记录控制器
 *
 * @author 张一风
 * @date 2025/02/10 15:04
 */
@Tag(name = "租赁记录控制器")
@RestController
@Validated
public class RentalController {

    @Resource
    private RentalService rentalService;

    @Resource
    private ScooterService scooterService;

    @Resource
    private ClientUserService clientUserService;

    @Resource
    private DevEmailApi devEmailApi;

    /**
     * 获取租赁记录分页
     *
     * @author 张一风
     * @date 2025/02/10 15:04
     */
    @Operation(summary = "获取租赁记录分页")
    @GetMapping("/rental/rental/page")
    public CommonResult<Page<Rental>> page(RentalPageParam rentalPageParam) {
        return CommonResult.data(rentalService.page(rentalPageParam));
    }


    /**
     * 添加租赁记录
     *
     * @author 张一风
     * @date 2025/02/10 15:04
     */
    @Operation(summary = "添加租赁记录")
    @CommonLog("添加租赁记录")
    @PostMapping("/rental/rental/add")
    public CommonResult<String> add(@RequestBody @Valid RentalAddParam rentalAddParam) {
        rentalService.add(rentalAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑租赁记录
     *
     * @author 张一风
     * @date 2025/02/10 15:04
     */
    @Operation(summary = "编辑租赁记录")
    @CommonLog("编辑租赁记录")
    @PostMapping("/rental/rental/edit")
    public CommonResult<String> edit(@RequestBody @Valid RentalEditParam rentalEditParam) {
        rentalService.edit(rentalEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除租赁记录
     *
     * @author 张一风
     * @date 2025/02/10 15:04
     */
    @Operation(summary = "删除租赁记录")
    @CommonLog("删除租赁记录")
    @PostMapping("/rental/rental/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                       List<RentalIdParam> rentalIdParamList) {
        rentalService.delete(rentalIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取租赁记录详情
     *
     * @author 张一风
     * @date 2025/02/10 15:04
     */
    @Operation(summary = "获取租赁记录详情")
    @GetMapping("/rental/rental/detail")
    public CommonResult<Rental> detail(@Valid RentalIdParam rentalIdParam) {
        return CommonResult.data(rentalService.detail(rentalIdParam));
    }

    @Operation(summary = "租赁滑板车")
    @CommonLog("租赁滑板车")
    @PostMapping("/client/rental/rent")
    public CommonResult<String> rent(@RequestBody @Valid ClientRentParam clientRentParam) {
        // 获取滑板的位置
        String scooterId = clientRentParam.getId();
        BigDecimal latitude = scooterService.getById(scooterId).getLatitude();
        BigDecimal longitude = scooterService.getById(scooterId).getLongitude();
        RentParam rentParam = new RentParam();
        rentParam.setStartLocationLatitude(latitude);
        rentParam.setStartLocationLongitude(longitude);
        BeanUtils.copyProperties(clientRentParam, rentParam);
        rentalService.rent(rentParam);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, -7);
        Date nowMinus7 = cal.getTime();
        // 早于当前时间的不能租赁
        if (rentParam.getStartTime().before(nowMinus7)) {
            return CommonResult.error("Can not rent scooter before current time");
        }
        //发送邮件
        //获取用户的邮件地址
        String userId = StpClientUtil.getLoginIdAsString();
        String email = clientUserService.getById(userId).getEmail();
        // 另一个线程发送邮件
        Thread thread = new Thread(() -> {
            devEmailApi.sendHtmlEmailLocal(
                    email,
                    "Scooter Rent Success",
                    "<h1>You have already booked the scooter , ID: " + rentParam.getId() + "</h1>",
                    null,
                    new ArrayList<>()
            );
        });
        thread.start();
        return CommonResult.ok();
    }

    @Operation(summary = "租赁滑板车")
    @CommonLog("租赁滑板车")
    @PostMapping("/rental/rental/rent")
    public CommonResult<String> adminRent(@RequestBody @Valid AdminRentParam adminRentParam) {
        RentParam rentParam = new RentParam();
        BeanUtils.copyProperties(adminRentParam, rentParam);
        rentalService.rentForUser(adminRentParam);
        //发送邮件
        //获取用户的邮件地址
        String userId = adminRentParam.getUserId();
        String email = clientUserService.getById(userId).getEmail();
        // 另一个线程发送邮件
        Thread thread = new Thread(() -> {
            devEmailApi.sendHtmlEmailLocal(
                    email,
                    "Scooter Rent Success",
                    "<h1>You have already booked the scooter , ID: " + rentParam.getId() + "</h1>",
                    null,
                    new ArrayList<>()
            );
        });
        thread.start();
        return CommonResult.ok();
    }

    @Operation(summary = "获取租赁记录")
    @GetMapping("/client/rental/record")
    public CommonResult<List<Rental>> record() {
        String userId = StpClientUtil.getLoginIdAsString();
        LambdaQueryWrapper<Rental> rentalLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rentalLambdaQueryWrapper.eq(Rental::getUserId, userId);
        List<Rental> rentalList = rentalService.list(rentalLambdaQueryWrapper);
        return CommonResult.data(rentalList);
    }

    @Operation(summary = "取消租赁")
    @GetMapping("/client/rental/cancel")
    public CommonResult<String> cancel(String id) {
        Rental rental = rentalService.getById(id);
        rental.setStatus(RentalEnum.CANCELLED.getValue());
        rentalService.updateById(rental);
        return CommonResult.ok();
    }

    @Operation(summary = "延长预定")
    @PostMapping("/client/rental/extend")
    public CommonResult<String> extend(@RequestBody RentalExtendParam rentalExtendParam) {
        Rental rental = rentalService.getById(rentalExtendParam.getId());
        // 延长时间
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(rental.getEndTime());
        calendar.add(java.util.Calendar.HOUR_OF_DAY, rentalExtendParam.getHours().intValue());
        rental.setEndTime(calendar.getTime());
        rentalService.updateById(rental);
        return CommonResult.ok();
    }


}

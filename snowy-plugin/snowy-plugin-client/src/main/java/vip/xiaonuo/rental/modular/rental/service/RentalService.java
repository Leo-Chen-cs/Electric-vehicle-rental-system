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
package vip.xiaonuo.rental.modular.rental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.rental.modular.rental.entity.Rental;
import vip.xiaonuo.rental.modular.rental.param.*;

import java.util.List;

/**
 * 租赁记录Service接口
 *
 * @author 张一风
 * @date  2025/02/10 15:04
 **/
public interface RentalService extends IService<Rental> {

    /**
     * 获取租赁记录分页
     *
     * @author 张一风
     * @date  2025/02/10 15:04
     */
    Page<Rental> page(RentalPageParam rentalPageParam);

    /**
     * 添加租赁记录
     *
     * @author 张一风
     * @date  2025/02/10 15:04
     */
    void add(RentalAddParam rentalAddParam);

    /**
     * 编辑租赁记录
     *
     * @author 张一风
     * @date  2025/02/10 15:04
     */
    void edit(RentalEditParam rentalEditParam);

    /**
     * 删除租赁记录
     *
     * @author 张一风
     * @date  2025/02/10 15:04
     */
    void delete(List<RentalIdParam> rentalIdParamList);

    /**
     * 获取租赁记录详情
     *
     * @author 张一风
     * @date  2025/02/10 15:04
     */
    Rental detail(RentalIdParam rentalIdParam);

    /**
     * 获取租赁记录详情
     *
     * @author 张一风
     * @date  2025/02/10 15:04
     **/
    Rental queryEntity(String id);

    void rent(RentParam rentParam);

     void rentForUser(AdminRentParam adminRentParam);
}

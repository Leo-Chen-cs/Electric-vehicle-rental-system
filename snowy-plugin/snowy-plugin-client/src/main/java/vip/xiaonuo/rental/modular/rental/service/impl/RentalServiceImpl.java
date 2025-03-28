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
package vip.xiaonuo.rental.modular.rental.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.rental.modular.rental.entity.Rental;
import vip.xiaonuo.rental.modular.rental.enums.RentalEnum;
import vip.xiaonuo.rental.modular.rental.mapper.RentalMapper;
import vip.xiaonuo.rental.modular.rental.param.*;
import vip.xiaonuo.rental.modular.rental.service.RentalService;

import java.util.List;

/**
 * 租赁记录Service接口实现类
 *
 * @author 张一风
 * @date 2025/02/10 15:04
 **/
@Service
public class RentalServiceImpl extends ServiceImpl<RentalMapper, Rental> implements RentalService {

    @Override
    public Page<Rental> page(RentalPageParam rentalPageParam) {
        QueryWrapper<Rental> queryWrapper = new QueryWrapper<Rental>().checkSqlInjection();
        if (ObjectUtil.isNotEmpty(rentalPageParam.getUserId())) {
            queryWrapper.lambda().eq(Rental::getUserId, rentalPageParam.getUserId());
        }
        if (ObjectUtil.isNotEmpty(rentalPageParam.getScooterId())) {
            queryWrapper.lambda().eq(Rental::getScooterId, rentalPageParam.getScooterId());
        }
        if (ObjectUtil.isNotEmpty(rentalPageParam.getStartStartTime()) && ObjectUtil.isNotEmpty(rentalPageParam.getEndStartTime())) {
            queryWrapper.lambda().between(Rental::getStartTime, rentalPageParam.getStartStartTime(), rentalPageParam.getEndStartTime());
        }
        if (ObjectUtil.isNotEmpty(rentalPageParam.getStartEndTime()) && ObjectUtil.isNotEmpty(rentalPageParam.getEndEndTime())) {
            queryWrapper.lambda().between(Rental::getEndTime, rentalPageParam.getStartEndTime(), rentalPageParam.getEndEndTime());
        }
        if (ObjectUtil.isNotEmpty(rentalPageParam.getTotalCost())) {
            queryWrapper.lambda().eq(Rental::getTotalCost, rentalPageParam.getTotalCost());
        }
        if (ObjectUtil.isNotEmpty(rentalPageParam.getStatus())) {
            queryWrapper.lambda().eq(Rental::getStatus, rentalPageParam.getStatus());
        }
        if (ObjectUtil.isAllNotEmpty(rentalPageParam.getSortField(), rentalPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(rentalPageParam.getSortOrder());
            queryWrapper.orderBy(true, rentalPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(rentalPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(Rental::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(RentalAddParam rentalAddParam) {
        Rental rental = BeanUtil.toBean(rentalAddParam, Rental.class);
        this.save(rental);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(RentalEditParam rentalEditParam) {
        Rental rental = this.queryEntity(rentalEditParam.getId());
        BeanUtil.copyProperties(rentalEditParam, rental);
        this.updateById(rental);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<RentalIdParam> rentalIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(rentalIdParamList, RentalIdParam::getId));
    }

    @Override
    public Rental detail(RentalIdParam rentalIdParam) {
        return this.queryEntity(rentalIdParam.getId());
    }

    @Override
    public Rental queryEntity(String id) {
        Rental rental = this.getById(id);
        if (ObjectUtil.isEmpty(rental)) {
            throw new CommonException("租赁记录不存在，id值为：{}", id);
        }
        return rental;
    }

    @Override
    public void rent(RentParam rentParam) {
        Rental rental = new Rental();
        rental.setScooterId(rentParam.getId());
        rental.setStartTime(rentParam.getStartTime());
        // 通过startTime和period计算endTime
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(rentParam.getStartTime());
        calendar.add(java.util.Calendar.HOUR_OF_DAY, rentParam.getPeriod().intValue());
        rental.setEndTime(calendar.getTime());
        rental.setUserId(StpClientUtil.getLoginIdAsString());
        rental.setStartLocationLatitude(rentParam.getStartLocationLatitude());
        rental.setStartLocationLongitude(rentParam.getStartLocationLongitude());
        rental.setTotalCost(rentParam.getTotalCost());
        rental.setStatus(RentalEnum.WAITING.getValue());
        this.save(rental);
    }

    @Override
    public void rentForUser(AdminRentParam adminRentParam) {
        Rental rental = new Rental();
        rental.setScooterId(adminRentParam.getId());
        rental.setStartTime(adminRentParam.getStartTime());
        // 通过startTime和period计算endTime
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(adminRentParam.getStartTime());
        calendar.add(java.util.Calendar.HOUR_OF_DAY, adminRentParam.getPeriod().intValue());
        rental.setEndTime(calendar.getTime());
        rental.setUserId(adminRentParam.getUserId());
        rental.setStartLocationLatitude(adminRentParam.getStartLocationLatitude());
        rental.setStartLocationLongitude(adminRentParam.getStartLocationLongitude());
        rental.setTotalCost(adminRentParam.getTotalCost());
        rental.setStatus(RentalEnum.WAITING.getValue());
        this.save(rental);
    }
}

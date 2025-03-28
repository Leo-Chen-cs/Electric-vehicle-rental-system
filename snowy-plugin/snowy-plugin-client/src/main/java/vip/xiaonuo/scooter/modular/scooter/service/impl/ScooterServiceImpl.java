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
package vip.xiaonuo.scooter.modular.scooter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.scooter.modular.scooter.entity.Scooter;
import vip.xiaonuo.scooter.modular.scooter.enums.ScooterEnum;
import vip.xiaonuo.scooter.modular.scooter.mapper.ScooterMapper;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterAddParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterEditParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterIdParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterPageParam;
import vip.xiaonuo.scooter.modular.scooter.service.ScooterService;

import java.util.List;

/**
 * 滑板管理Service接口实现类
 *
 * @author 张一风
 * @date  2025/02/10 12:59
 **/
@Service
public class ScooterServiceImpl extends ServiceImpl<ScooterMapper, Scooter> implements ScooterService {

    @Override
    public Page<Scooter> page(ScooterPageParam scooterPageParam) {
        QueryWrapper<Scooter> queryWrapper = new QueryWrapper<Scooter>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(scooterPageParam.getScooterName())) {
            queryWrapper.lambda().like(Scooter::getScooterName, scooterPageParam.getScooterName());
        }
        if(ObjectUtil.isNotEmpty(scooterPageParam.getBrand())) {
            queryWrapper.lambda().like(Scooter::getBrand, scooterPageParam.getBrand());
        }
        if(ObjectUtil.isNotEmpty(scooterPageParam.getStatus())) {
            queryWrapper.lambda().eq(Scooter::getStatus, scooterPageParam.getStatus());
        }
        if(ObjectUtil.isNotEmpty(scooterPageParam.getBatteryLevel())) {
            queryWrapper.lambda().eq(Scooter::getBatteryLevel, scooterPageParam.getBatteryLevel());
        }
        if(ObjectUtil.isNotEmpty(scooterPageParam.getStartLastMaintenanceDate()) && ObjectUtil.isNotEmpty(scooterPageParam.getEndLastMaintenanceDate())) {
            queryWrapper.lambda().between(Scooter::getLastMaintenanceDate, scooterPageParam.getStartLastMaintenanceDate(), scooterPageParam.getEndLastMaintenanceDate());
        }
        if(ObjectUtil.isAllNotEmpty(scooterPageParam.getSortField(), scooterPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(scooterPageParam.getSortOrder());
            queryWrapper.orderBy(true, scooterPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(scooterPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(Scooter::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ScooterAddParam scooterAddParam) {
        Scooter scooter = BeanUtil.toBean(scooterAddParam, Scooter.class);
        this.save(scooter);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ScooterEditParam scooterEditParam) {
        Scooter scooter = this.queryEntity(scooterEditParam.getId());
        BeanUtil.copyProperties(scooterEditParam, scooter);
        this.updateById(scooter);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ScooterIdParam> scooterIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(scooterIdParamList, ScooterIdParam::getId));
    }

    @Override
    public Scooter detail(ScooterIdParam scooterIdParam) {
        return this.queryEntity(scooterIdParam.getId());
    }

    @Override
    public Scooter queryEntity(String id) {
        Scooter scooter = this.getById(id);
        if(ObjectUtil.isEmpty(scooter)) {
            throw new CommonException("滑板管理不存在，id值为：{}", id);
        }
        return scooter;
    }


    @Override
    public List<Scooter> getAvailableScooter(Integer num){
        QueryWrapper<Scooter> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Scooter::getStatus, ScooterEnum.AVAILABLE);
        queryWrapper.last("limit " + num);
        queryWrapper.lambda().orderByAsc(Scooter::getId);
        return this.list(queryWrapper);
    }
}

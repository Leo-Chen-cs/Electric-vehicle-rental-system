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
package vip.xiaonuo.pricing.modular.pricing.service.impl;

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
import vip.xiaonuo.pricing.modular.pricing.entity.Pricing;
import vip.xiaonuo.pricing.modular.pricing.mapper.PricingMapper;
import vip.xiaonuo.pricing.modular.pricing.param.PricingAddParam;
import vip.xiaonuo.pricing.modular.pricing.param.PricingEditParam;
import vip.xiaonuo.pricing.modular.pricing.param.PricingIdParam;
import vip.xiaonuo.pricing.modular.pricing.param.PricingPageParam;
import vip.xiaonuo.pricing.modular.pricing.service.PricingService;

import java.util.List;

/**
 * PricingService接口实现类
 *
 * @author 张一风
 * @date  2025/02/10 20:58
 **/
@Service
public class PricingServiceImpl extends ServiceImpl<PricingMapper, Pricing> implements PricingService {

    @Override
    public Page<Pricing> page(PricingPageParam pricingPageParam) {
        QueryWrapper<Pricing> queryWrapper = new QueryWrapper<Pricing>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(pricingPageParam.getStartCreatedAt()) && ObjectUtil.isNotEmpty(pricingPageParam.getEndCreatedAt())) {
            queryWrapper.lambda().between(Pricing::getCreatedAt, pricingPageParam.getStartCreatedAt(), pricingPageParam.getEndCreatedAt());
        }
        if(ObjectUtil.isNotEmpty(pricingPageParam.getStartUpdatedAt()) && ObjectUtil.isNotEmpty(pricingPageParam.getEndUpdatedAt())) {
            queryWrapper.lambda().between(Pricing::getUpdatedAt, pricingPageParam.getStartUpdatedAt(), pricingPageParam.getEndUpdatedAt());
        }
        if(ObjectUtil.isAllNotEmpty(pricingPageParam.getSortField(), pricingPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(pricingPageParam.getSortOrder());
            queryWrapper.orderBy(true, pricingPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(pricingPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(Pricing::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(PricingAddParam pricingAddParam) {
        Pricing pricing = BeanUtil.toBean(pricingAddParam, Pricing.class);
        this.save(pricing);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(PricingEditParam pricingEditParam) {
        Pricing pricing = this.queryEntity(pricingEditParam.getId());
        BeanUtil.copyProperties(pricingEditParam, pricing);
        this.updateById(pricing);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<PricingIdParam> pricingIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(pricingIdParamList, PricingIdParam::getId));
    }

    @Override
    public Pricing detail(PricingIdParam pricingIdParam) {
        return this.queryEntity(pricingIdParam.getId());
    }

    @Override
    public Pricing queryEntity(String id) {
        Pricing pricing = this.getById(id);
        if(ObjectUtil.isEmpty(pricing)) {
            throw new CommonException("Pricing不存在，id值为：{}", id);
        }
        return pricing;
    }
}

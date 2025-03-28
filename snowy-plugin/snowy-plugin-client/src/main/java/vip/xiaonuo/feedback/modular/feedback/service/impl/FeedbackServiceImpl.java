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
package vip.xiaonuo.feedback.modular.feedback.service.impl;

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
import vip.xiaonuo.feedback.modular.feedback.entity.Feedback;
import vip.xiaonuo.feedback.modular.feedback.mapper.FeedbackMapper;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackAddParam;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackEditParam;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackIdParam;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackPageParam;
import vip.xiaonuo.feedback.modular.feedback.service.FeedbackService;

import java.util.List;

/**
 * 反馈管理Service接口实现类
 *
 * @author 张一风
 * @date  2025/02/10 11:07
 **/
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Override
    public Page<Feedback> page(FeedbackPageParam feedbackPageParam) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<Feedback>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(feedbackPageParam.getRating())) {
            queryWrapper.lambda().eq(Feedback::getRating, feedbackPageParam.getRating());
        }
        if(ObjectUtil.isNotEmpty(feedbackPageParam.getComment())) {
            queryWrapper.lambda().like(Feedback::getComment, feedbackPageParam.getComment());
        }
        if(ObjectUtil.isNotEmpty(feedbackPageParam.getStartUpdatedAt()) && ObjectUtil.isNotEmpty(feedbackPageParam.getEndUpdatedAt())) {
            queryWrapper.lambda().between(Feedback::getUpdatedAt, feedbackPageParam.getStartUpdatedAt(), feedbackPageParam.getEndUpdatedAt());
        }
        if(ObjectUtil.isAllNotEmpty(feedbackPageParam.getSortField(), feedbackPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(feedbackPageParam.getSortOrder());
            queryWrapper.orderBy(true, feedbackPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(feedbackPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(Feedback::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(FeedbackAddParam feedbackAddParam) {
        Feedback feedback = BeanUtil.toBean(feedbackAddParam, Feedback.class);
        this.save(feedback);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(FeedbackEditParam feedbackEditParam) {
        Feedback feedback = this.queryEntity(feedbackEditParam.getId());
        BeanUtil.copyProperties(feedbackEditParam, feedback);
        this.updateById(feedback);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FeedbackIdParam> feedbackIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(feedbackIdParamList, FeedbackIdParam::getId));
    }

    @Override
    public Feedback detail(FeedbackIdParam feedbackIdParam) {
        return this.queryEntity(feedbackIdParam.getId());
    }

    @Override
    public Feedback queryEntity(String id) {
        Feedback feedback = this.getById(id);
        if(ObjectUtil.isEmpty(feedback)) {
            throw new CommonException("反馈管理不存在，id值为：{}", id);
        }
        return feedback;
    }

    @Override
    public Page<Feedback> getUserFeedbacks(String userId) {
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<Feedback>().checkSqlInjection();
        queryWrapper.lambda().eq(Feedback::getUserId, userId);
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }
}

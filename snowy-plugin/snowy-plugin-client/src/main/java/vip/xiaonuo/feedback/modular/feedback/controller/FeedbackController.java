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
package vip.xiaonuo.feedback.modular.feedback.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.feedback.modular.feedback.entity.Feedback;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackAddParam;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackEditParam;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackIdParam;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackPageParam;
import vip.xiaonuo.feedback.modular.feedback.service.FeedbackService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 反馈管理控制器
 *
 * @author 张一风
 * @date  2025/02/10 11:07
 */
@Tag(name = "反馈管理控制器")
@RestController
@Validated
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;

    /**
     * 获取反馈管理分页
     *
     * @author 张一风
     * @date  2025/02/10 11:07
     */
    @Operation(summary = "获取反馈管理分页")
    @GetMapping("/feedback/feedback/page")
    public CommonResult<Page<Feedback>> page(FeedbackPageParam feedbackPageParam) {
        return CommonResult.data(feedbackService.page(feedbackPageParam));
    }

        /**
     * 获取反馈管理分页
     *
     * @author 张一风
     * @date  2025/02/10 11:07
     */
    @Operation(summary = "获取反馈管理分页")
    @GetMapping("/client/feedback/page")
    public CommonResult<Page<Feedback>> page2(FeedbackPageParam feedbackPageParam) {
        String loginIdAsString = StpClientUtil.getLoginIdAsString();
        return CommonResult.data(feedbackService.getUserFeedbacks(loginIdAsString));
    }

    /**
     * 添加反馈管理
     *
     * @author 张一风
     * @date  2025/02/10 11:07
     */
    @Operation(summary = "添加反馈管理")
    @CommonLog("添加反馈管理")
    @PostMapping("/feedback/feedback/add")
    public CommonResult<String> add(@RequestBody @Valid FeedbackAddParam feedbackAddParam) {
        feedbackService.add(feedbackAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑反馈管理
     *
     * @author 张一风
     * @date  2025/02/10 11:07
     */
    @Operation(summary = "编辑反馈管理")
    @CommonLog("编辑反馈管理")
    @PostMapping("/feedback/feedback/edit")
    public CommonResult<String> edit(@RequestBody @Valid FeedbackEditParam feedbackEditParam) {
        feedbackService.edit(feedbackEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除反馈管理
     *
     * @author 张一风
     * @date  2025/02/10 11:07
     */
    @Operation(summary = "删除反馈管理")
    @CommonLog("删除反馈管理")
    @PostMapping("/feedback/feedback/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<FeedbackIdParam> feedbackIdParamList) {
        feedbackService.delete(feedbackIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取反馈管理详情
     *
     * @author 张一风
     * @date  2025/02/10 11:07
     */
    @Operation(summary = "获取反馈管理详情")
    @GetMapping("/feedback/feedback/detail")
    public CommonResult<Feedback> detail(@Valid FeedbackIdParam feedbackIdParam) {
        return CommonResult.data(feedbackService.detail(feedbackIdParam));
    }

}

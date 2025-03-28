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
package vip.xiaonuo.pricing.modular.pricing.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.pricing.modular.pricing.entity.Pricing;
import vip.xiaonuo.pricing.modular.pricing.param.PricingAddParam;
import vip.xiaonuo.pricing.modular.pricing.param.PricingEditParam;
import vip.xiaonuo.pricing.modular.pricing.param.PricingIdParam;
import vip.xiaonuo.pricing.modular.pricing.param.PricingPageParam;
import vip.xiaonuo.pricing.modular.pricing.service.PricingService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Pricing控制器
 *
 * @author 张一风
 * @date  2025/02/10 20:58
 */
@Tag(name = "Pricing控制器")
@RestController
@Validated
public class PricingController {

    @Resource
    private PricingService pricingService;

    /**
     * 获取Pricing分页
     *
     * @author 张一风
     * @date  2025/02/10 20:58
     */
    @Operation(summary = "获取Pricing分页")
    @GetMapping("/pricing/pricing/page")
    public CommonResult<Page<Pricing>> page(PricingPageParam pricingPageParam) {
        return CommonResult.data(pricingService.page(pricingPageParam));
    }

    @Operation(summary = "获取Pricing分页")
    @GetMapping("/client/pricing/page")
    public CommonResult<Page<Pricing>> page2(PricingPageParam pricingPageParam) {
        return CommonResult.data(pricingService.page(pricingPageParam));
    }

    /**
     * 添加Pricing
     *
     * @author 张一风
     * @date  2025/02/10 20:58
     */
    @Operation(summary = "添加Pricing")
    @CommonLog("添加Pricing")
    @PostMapping("/pricing/pricing/add")
    public CommonResult<String> add(@RequestBody @Valid PricingAddParam pricingAddParam) {
        pricingService.add(pricingAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑Pricing
     *
     * @author 张一风
     * @date  2025/02/10 20:58
     */
    @Operation(summary = "编辑Pricing")
    @CommonLog("编辑Pricing")
    @PostMapping("/pricing/pricing/edit")
    public CommonResult<String> edit(@RequestBody @Valid PricingEditParam pricingEditParam) {
        pricingService.edit(pricingEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除Pricing
     *
     * @author 张一风
     * @date  2025/02/10 20:58
     */
    @Operation(summary = "删除Pricing")
    @CommonLog("删除Pricing")
    @PostMapping("/pricing/pricing/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<PricingIdParam> pricingIdParamList) {
        pricingService.delete(pricingIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取Pricing详情
     *
     * @author 张一风
     * @date  2025/02/10 20:58
     */
    @Operation(summary = "获取Pricing详情")
    @GetMapping("/pricing/pricing/detail")
    public CommonResult<Pricing> detail(@Valid PricingIdParam pricingIdParam) {
        return CommonResult.data(pricingService.detail(pricingIdParam));
    }
}

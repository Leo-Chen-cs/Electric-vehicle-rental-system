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
package vip.xiaonuo.scooter.modular.scooter.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.rental.modular.rental.entity.Rental;
import vip.xiaonuo.scooter.modular.scooter.entity.Scooter;
import vip.xiaonuo.scooter.modular.scooter.enums.ScooterEnum;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterAddParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterEditParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterIdParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterPageParam;
import vip.xiaonuo.scooter.modular.scooter.service.ScooterService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 滑板管理控制器
 *
 * @author 张一风
 * @date  2025/02/10 12:59
 */
@Tag(name = "滑板管理控制器")
@RestController
@Validated
public class ScooterController {

    @Resource
    private ScooterService scooterService;

    /**
     * 获取滑板管理分页
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    @Operation(summary = "获取滑板管理分页")
    @GetMapping("/scooter/scooter/page")
    public CommonResult<Page<Scooter>> page(ScooterPageParam scooterPageParam) {
        return CommonResult.data(scooterService.page(scooterPageParam));
    }

    /**
     * 添加滑板管理
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    @Operation(summary = "添加滑板管理")
    @CommonLog("添加滑板管理")
    @PostMapping("/scooter/scooter/add")
    public CommonResult<String> add(@RequestBody @Valid ScooterAddParam scooterAddParam) {
        scooterService.add(scooterAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑滑板管理
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    @Operation(summary = "编辑滑板管理")
    @CommonLog("编辑滑板管理")
    @PostMapping("/scooter/scooter/edit")
    public CommonResult<String> edit(@RequestBody @Valid ScooterEditParam scooterEditParam) {
        scooterService.edit(scooterEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除滑板管理
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    @Operation(summary = "删除滑板管理")
    @CommonLog("删除滑板管理")
    @PostMapping("/scooter/scooter/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<ScooterIdParam> scooterIdParamList) {
        scooterService.delete(scooterIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取滑板管理详情
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    @Operation(summary = "获取滑板管理详情")
    @GetMapping("/scooter/scooter/detail")
    public CommonResult<Scooter> detail(@Valid ScooterIdParam scooterIdParam) {
        return CommonResult.data(scooterService.detail(scooterIdParam));
    }

    @Operation(summary = "获取前几个有电的车子")
    @SaIgnore
    @GetMapping("/client/scooter/show")
    public CommonResult<List<Scooter>> getScooter(@RequestParam(name = "num", defaultValue = "5") Integer num) {
        return CommonResult.data(scooterService.getAvailableScooter(num));
    }

    @Operation(summary = "获取前几个有电的车子")
    @SaIgnore
    @GetMapping("/scooter/scooter/show")
    public CommonResult<List<Scooter>> getScooter2(@RequestParam(name = "num", defaultValue = "5") Integer num) {
        return CommonResult.data(scooterService.getAvailableScooter(num));
    }

     @Operation(summary = "获取可用滑板")
    @GetMapping("/scooter/scooter/available")
    public CommonResult<List<Scooter>> page() {
         List<Scooter> availableScooter = scooterService.getAvailableScooter(100);
         return CommonResult.data(availableScooter);
    }
}

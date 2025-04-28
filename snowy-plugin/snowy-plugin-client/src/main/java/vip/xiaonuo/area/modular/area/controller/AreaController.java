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

package vip.xiaonuo.area.modular.area.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.area.modular.area.entity.Area;
import vip.xiaonuo.area.modular.area.enums.AreaEnum;
import vip.xiaonuo.area.modular.area.param.AreaAddParam;
import vip.xiaonuo.area.modular.area.param.AreaEditParam;
import vip.xiaonuo.area.modular.area.param.AreaIdParam;
import vip.xiaonuo.area.modular.area.param.AreaPageParam;
import vip.xiaonuo.area.modular.area.service.AreaService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import vip.xiaonuo.scooter.modular.scooter.entity.Scooter;

import java.util.List;

/**
 * 区域管理控制器
 *
 * @author 胡勋
 * @date  2025/04/19 15:34
 */
@Tag(name = "区域管理控制器")
@RestController
@Validated
public class AreaController {

    @Resource
    private AreaService areaService;

    /**
     * 获取区域管理分页
     *
     * @author 胡勋
     * @date  2025/04/19 15:34
     */
    @Operation(summary = "获取区域管理分页")
    @GetMapping("/area/area/page")
    public CommonResult<Page<Area>> page(AreaPageParam areaPageParam) {
        return CommonResult.data(areaService.page(areaPageParam));
    }

    /**
     * 添加区域管理
     *
     * @author 胡勋
     * @date 2025/04/19 15:59
     */
    @Operation(summary = "添加区域管理")
    @CommonLog("添加区域管理")
    @PostMapping("/area/area/add")
    public CommonResult<String> add(@RequestBody @Valid AreaAddParam areaAddParam) {
        areaService.add(areaAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑区域管理
     *
     * @author 胡勋
     * @date 2025/04/19 16:14
     */
    @Operation(summary = "编辑区域管理")
    @CommonLog("编辑区域管理")
    @PostMapping("/area/area/edit")
    public CommonResult<String> edit(@RequestBody @Valid AreaEditParam areaEditParam) {
        areaService.edit(areaEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除区域管理
     *
     * @author 胡勋
     * @date 2025/04/19 16:15
     */
    @Operation(summary = "删除区域管理")
    @CommonLog("删除区域管理")
    @PostMapping("/area/area/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                           List<AreaIdParam> areaIdParamList) {
        areaService.delete(areaIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取区域管理详情
     *
     * @author 30266
     * @date 2025/04/19 16:17
     */
    @Operation(summary = "获取区域管理详情")
    @GetMapping("/area/area/detail")
    public CommonResult<Area> detail(@Valid AreaIdParam areaIdParam) {
        return CommonResult.data(areaService.detail(areaIdParam));
    }

    /**
     * 获取所有区域列表
     *
     * @author 胡勋
     * @date 2025/04/19 16:17
     */

    @Operation(summary = "获取所有区域列表")
    @SaIgnore
    @GetMapping("/client/area/activeAreas")
    public CommonResult<List<Area>> getActiveAreas() {
        return CommonResult.data(areaService.getActiveAreas());
    }

    /**
     * 获取可运行区域列表
     *
     * @author 胡勋
     * @date 2025/04/19 16:20
     */
    @Operation(summary = "获取可运行区域列表")
    @SaIgnore
    @GetMapping("/client/area/operationalAreas")
    public CommonResult<List<Area>> getRunAreas() {
        return CommonResult.data(areaService.getAreasByType(AreaEnum.OPERATIONAL.getValue()));
    }

    /**
     * 获取禁停区域列表
     *
     * @author 胡勋
     * @date 2025/04/19 16:22
     */
    @Operation(summary = "获取禁停区域列表")
    @SaIgnore
    @GetMapping("/client/area/noParkingAreas")
    public CommonResult<List<Area>> getNoParkingAreas() {
        return CommonResult.data(areaService.getAreasByType(AreaEnum.NO_PARKING.getValue()));
    }

    /**
     * 检查点是否在运营区域内
     *
     * @author 30266
     * @date 2025/04/19 16:24
     */
    @Operation(summary = "Check if a point is within the operational area")
    @SaIgnore
    @GetMapping("/client/area/isInOperationalArea")
    public CommonResult<Boolean> isInOperationalArea(@RequestParam("lat") Double latitude,
                                                     @RequestParam("lng") Double longitude) {
        return CommonResult.data(areaService.isPointInOperationalArea(latitude, longitude));
    }

    /**
     * 检查点是否在禁停区域内
     */
    @Operation(summary = "Check if a point is within any no-parking area")
    @SaIgnore
    @GetMapping("/client/area/isInNoParkingArea")
    public CommonResult<Boolean> isInNoParkingArea(@RequestParam("lat") Double latitude,
                                                   @RequestParam("lng") Double longitude) {
        return CommonResult.data(areaService.isPointInNoParkingArea(latitude, longitude));
    }
}

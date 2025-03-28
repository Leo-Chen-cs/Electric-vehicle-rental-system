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
package vip.xiaonuo.user.modular.user.controller;

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
import vip.xiaonuo.user.modular.user.entity.ClientUser;
import vip.xiaonuo.user.modular.user.param.*;
import vip.xiaonuo.user.modular.user.service.ClientUserService;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 用户管理控制器
 *
 * @author 张一风
 * @date  2025/02/09 23:14
 */
@Tag(name = "用户管理控制器")
@RestController
@Validated
public class UserController {

    @Resource
    private ClientUserService clientUserService;

    /**
     * 获取用户管理分页
     *
     * @author 张一风
     * @date  2025/02/09 23:14
     */
    @Operation(summary = "获取用户管理分页")
    @GetMapping("/user/user/page")
    public CommonResult<Page<ClientUser>> page(ClientUserPageParam clientUserPageParam) {
        return CommonResult.data(clientUserService.page(clientUserPageParam));
    }

    /**
     * 添加用户管理
     *
     * @author 张一风
     * @date  2025/02/09 23:14
     */
    @Operation(summary = "添加用户管理")
    @CommonLog("添加用户管理")
    @PostMapping("/user/user/add")
    public CommonResult<String> add(@RequestBody @Valid ClientUserAddParam clientUserAddParam) {
        clientUserService.add(clientUserAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑用户管理
     *
     * @author 张一风
     * @date  2025/02/09 23:14
     */
    @Operation(summary = "编辑用户管理")
    @CommonLog("编辑用户管理")
    @PostMapping("/user/user/edit")
    public CommonResult<String> edit(@RequestBody @Valid ClientUserEditParam clientUserEditParam) {
        clientUserService.edit(clientUserEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除用户管理
     *
     * @author 张一风
     * @date  2025/02/09 23:14
     */
    @Operation(summary = "删除用户管理")
    @CommonLog("删除用户管理")
    @PostMapping("/user/user/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   List<ClientUserIdParam> clientUserIdParamList) {
        clientUserService.delete(clientUserIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取用户管理详情
     *
     * @author 张一风
     * @date  2025/02/09 23:14
     */
    @Operation(summary = "获取用户管理详情")
    @GetMapping("/user/user/detail")
    public CommonResult<ClientUser> detail(@Valid ClientUserIdParam clientUserIdParam) {
        return CommonResult.data(clientUserService.detail(clientUserIdParam));
    }



}

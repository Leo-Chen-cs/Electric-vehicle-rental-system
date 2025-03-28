package vip.xiaonuo.auth.modular.register.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.modular.login.enums.AuthDeviceTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthExceptionEnum;
import vip.xiaonuo.auth.modular.register.param.RegisterParam;
import vip.xiaonuo.auth.modular.register.service.impl.RegisterServiceImpl;
import vip.xiaonuo.biz.modular.user.param.BizUserAddParam;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.util.CommonCryptogramUtil;

/**
 * @author: xiaofengs
 * @description :
 * @date: 2025/2/9 18:17
 * @version: 1.0
 */
@Tag(name = "注册模块")
@RestController
@Validated
public class RegisterController {

    @Resource
    private RegisterServiceImpl registerService;

    @Operation(summary = "注册")
    @CommonLog("注册用户")
    @PostMapping("/client/user/register")
    public CommonResult<String> register(@RequestBody @Valid RegisterParam registerParam) {
        registerService.doRegister(registerParam);
        return CommonResult.ok();
    }


}

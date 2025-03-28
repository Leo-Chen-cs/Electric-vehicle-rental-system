package vip.xiaonuo.auth.modular.register.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vip.xiaonuo.auth.modular.login.enums.AuthDeviceTypeEnum;
import vip.xiaonuo.auth.modular.login.enums.AuthExceptionEnum;
import vip.xiaonuo.auth.modular.register.enums.RegisterExceptionEnum;
import vip.xiaonuo.auth.modular.register.mapper.RegisterMapper;
import vip.xiaonuo.auth.modular.register.param.RegisterParam;
import vip.xiaonuo.auth.modular.register.service.RegisterService;
import vip.xiaonuo.biz.modular.user.service.BizUserService;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.enums.ClientUserStatusEnum;
import vip.xiaonuo.client.modular.user.param.ClientUserAddParam;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.cache.CommonCacheOperator;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.common.util.CommonCryptogramUtil;
import vip.xiaonuo.dev.api.DevConfigApi;

/**
 * @author: xiaofengs
 * @description :
 * @date: 2025/2/9 18:35
 * @version: 1.0
 */
@Service
public class RegisterServiceImpl extends ServiceImpl<RegisterMapper, ClientUser> implements RegisterService {

    @Resource
    private ClientUserService clientUserService;

    private static final String SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_KEY = "SNOWY_SYS_DEFAULT_CAPTCHA_OPEN";

    private static final String AUTH_VALID_CODE_CACHE_KEY = "auth-validCode:";

    private static final String LOGIN_ERROR_TIMES_KEY_PREFIX = "login-error-times:";

    @Resource
    private CommonCacheOperator commonCacheOperator;

    @Resource
    private DevConfigApi devConfigApi;

    @Autowired
    private RegisterMapper registerMapper;

    public void doRegister(@RequestBody @Valid RegisterParam registerParam) {
        // 获取账号
        String account = registerParam.getAccount();
        // 获取密码
        String password = registerParam.getPassword();
        // 获取设备
        String device = registerParam.getDevice();
        // 默认指定为PC，如在小程序跟移动端的情况下，自行指定即可
        if (ObjectUtil.isEmpty(device)) {
            device = AuthDeviceTypeEnum.PC.getValue();
        } else {
            AuthDeviceTypeEnum.validate(device);
        }
        // 校验验证码
        String defaultCaptchaOpen = devConfigApi.getValueByKey(SNOWY_SYS_DEFAULT_CAPTCHA_OPEN_KEY);
        if (ObjectUtil.isNotEmpty(defaultCaptchaOpen)) {
            if (Convert.toBool(defaultCaptchaOpen)) {
                // 获取验证码
                String validCode = registerParam.getValidCode();
                // 获取验证码请求号
                String validCodeReqNo = registerParam.getValidCodeReqNo();
                // 开启验证码则必须传入验证码
                if (ObjectUtil.isEmpty(validCode)) {
                    throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue());
                }
                // 开启验证码则必须传入验证码请求号
                if (ObjectUtil.isEmpty(validCodeReqNo)) {
                    throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue());
                }
                // 执行校验验证码
                validValidCode(null, validCode, validCodeReqNo);
            }
        }
        // SM2解密并获得前端传来的密码哈希值
        String passwordHash;
        try {
            // 解密，并做哈希值
            passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(password));
        } catch (Exception e) {
            throw new CommonException(AuthExceptionEnum.PWD_DECRYPT_ERROR.getValue());
        }
        // 是否有重复用户名
        if (ObjectUtil.isNotEmpty(clientUserService.getUserByAccount(account))) {
            throw new CommonException(RegisterExceptionEnum.ACCOUNT_EXIST.getValue());
        }
        ClientUser clientUser = new ClientUser();
        clientUser.setName("New User");
        clientUser.setAccount(account);
        clientUser.setPassword(passwordHash);
        clientUser.setUserStatus(ClientUserStatusEnum.ENABLE.getValue());
        clientUser.setEmail(registerParam.getEmail());
        this.save(clientUser);
    }


    private void validValidCode(String phoneOrEmail, String validCode, String validCodeReqNo) {
        // 依据请求号，取出缓存中的验证码进行校验
        Object existValidCode;
        if (ObjectUtil.isEmpty(phoneOrEmail)) {
            existValidCode = commonCacheOperator.get(AUTH_VALID_CODE_CACHE_KEY + validCodeReqNo);
        } else {
            existValidCode = commonCacheOperator.get(AUTH_VALID_CODE_CACHE_KEY + phoneOrEmail + StrUtil.UNDERLINE + validCodeReqNo);
        }
        // 为空则直接验证码错误
        if (ObjectUtil.isEmpty(existValidCode)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_ERROR.getValue());
        }
        // 移除该验证码
        if (ObjectUtil.isEmpty(phoneOrEmail)) {
            commonCacheOperator.remove(AUTH_VALID_CODE_CACHE_KEY + validCodeReqNo);
        } else {
            commonCacheOperator.remove(AUTH_VALID_CODE_CACHE_KEY + phoneOrEmail + StrUtil.UNDERLINE + validCodeReqNo);
        }
        // 不一致则直接验证码错误
        if (!validCode.equalsIgnoreCase(Convert.toStr(existValidCode))) {
            throw new CommonException("验证码错误");
        }
    }
}

package vip.xiaonuo.auth.modular.register.enums;

import lombok.Getter;

@Getter
public enum RegisterExceptionEnum {

    ACCOUNT_EXIST("账号已存在");

    private final String value;

    RegisterExceptionEnum(String value) {
        this.value = value;
    }
}

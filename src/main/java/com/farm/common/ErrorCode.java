package com.farm.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAM_ERROR(40001, "参数错误"),
    SMS_CODE_ERROR(40002, "验证码错误或已过期"),
    LOGIN_ERROR(40003, "账号或密码错误"),
    PHONE_EXISTS(40004, "手机号已注册"),
    UNAUTHORIZED(40100, "未登录或登录已过期"),
    FORBIDDEN(40300, "无权限"),
    NOT_FOUND(40400, "数据不存在"),
    SERVER_ERROR(50000, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

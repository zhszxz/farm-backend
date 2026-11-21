package com.farm.controller;

import com.farm.common.Result;
import com.farm.dto.LoginRequest;
import com.farm.dto.LoginResponse;
import com.farm.dto.RegisterRequest;
import com.farm.dto.SmsCodeRequest;
import com.farm.dto.SmsLoginRequest;
import com.farm.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：注册、登录、短信验证码
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 发送短信验证码（开发模式打印到日志，不真实下发）
     *
     * @param req 手机号
     */
    @PostMapping("/sms-code")
    public Result<Void> smsCode(@Valid @RequestBody SmsCodeRequest req) {
        authService.sendSmsCode(req.getPhone());
        return Result.ok();
    }

    /**
     * 注册（手机号 + 密码）
     *
     * @param req 注册信息
     * @return 登录令牌与用户信息
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.ok(authService.register(req.getPhone(), req.getPassword(), req.getNickname()));
    }

    /**
     * 密码登录（手机号或账号 + 密码）
     *
     * @param req 账号与密码
     * @return 登录令牌与用户信息
     */
    @PostMapping("/login/password")
    public Result<LoginResponse> loginPassword(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.loginByPassword(req.getAccount(), req.getPassword()));
    }

    /**
     * 验证码免密登录（手机号 + 验证码）
     *
     * @param req 手机号与验证码
     * @return 登录令牌与用户信息
     */
    @PostMapping("/login/sms")
    public Result<LoginResponse> loginSms(@Valid @RequestBody SmsLoginRequest req) {
        return Result.ok(authService.loginBySms(req.getPhone(), req.getSmsCode()));
    }
}

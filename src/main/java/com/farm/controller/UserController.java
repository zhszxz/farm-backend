package com.farm.controller;

import com.farm.common.Result;
import com.farm.entity.SysUser;
import com.farm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息接口
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    /**
     * 获取当前登录用户信息（密码已脱敏）
     *
     * @return 当前用户
     */
    @GetMapping("/me")
    public Result<SysUser> me() {
        return Result.ok(authService.currentUser());
    }
}

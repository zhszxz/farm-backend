package com.farm.controller;

import com.farm.common.Result;
import com.farm.mapper.SysUserMapper;
import com.farm.vo.HealthVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查接口
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final SysUserMapper userMapper;

    /**
     * 健康检查：验证服务与数据库连通性
     */
    @GetMapping("/health")
    public Result<HealthVO> health() {
        userMapper.selectCount(null);
        return Result.ok(new HealthVO("UP", "connected"));
    }
}

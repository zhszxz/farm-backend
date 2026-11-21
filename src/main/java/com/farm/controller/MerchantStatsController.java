package com.farm.controller;

import com.farm.common.Result;
import com.farm.service.ClickLogService;
import com.farm.vo.ClickStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家工作台 · 引流统计接口
 */
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantStatsController {

    private final ClickLogService clickLogService;

    /**
     * 我的跳转统计（总数、按产品分布、最近记录）
     */
    @GetMapping("/click-stats")
    public Result<ClickStatsVO> clickStats() {
        return Result.ok(clickLogService.stats());
    }
}

package com.farm.controller;

import com.farm.common.Result;
import com.farm.dto.ClickRequest;
import com.farm.service.ClickLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 跳转记录接口（公开，用户点击"去拼多多购买"时调用）
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClickController {

    private final ClickLogService clickLogService;

    /**
     * 记录一次跳转
     *
     * @param request 跳转信息（店铺/产品/链接/来源）
     */
    @PostMapping("/clicks")
    public Result<Void> record(@RequestBody ClickRequest request) {
        clickLogService.recordClick(request.getShopId(), request.getProductId(), request.getLinkId(), request.getSource());
        return Result.ok();
    }
}

package com.farm.controller;

import com.farm.common.Result;
import com.farm.dto.ShopApplyRequest;
import com.farm.entity.Shop;
import com.farm.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家入驻接口
 */
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    /**
     * 申请成为商家（创建待审核店铺）
     *
     * @param shop 店铺申请信息
     * @return 创建的店铺
     */
    @PostMapping("/apply")
    public Result<Shop> apply(@Valid @RequestBody ShopApplyRequest req) {
        return Result.ok(shopService.apply(req));
    }

    /**
     * 查询我的入驻状态
     *
     * @return 店铺信息（未申请时为 null）
     */
    @GetMapping("/my-apply")
    public Result<Shop> myApply() {
        return Result.ok(shopService.myApply());
    }
}

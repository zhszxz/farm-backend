package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.Shop;
import com.farm.entity.ShopLink;
import com.farm.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家工作台 · 店铺与拼多多链接接口
 */
@RestController
@RequestMapping("/api/merchant")
@RequiredArgsConstructor
public class MerchantShopController {

    private final ShopService shopService;

    @GetMapping("/shop")
    public Result<Shop> getShop() {
        return Result.ok(shopService.getCurrentShop());
    }

    @PutMapping("/shop")
    public Result<Shop> updateShop(@RequestBody Shop shop) {
        return Result.ok(shopService.updateShop(shop));
    }

    @GetMapping("/links")
    public Result<PageResult<ShopLink>> listLinks(@RequestParam(defaultValue = "1") long page,
                                                  @RequestParam(defaultValue = "10") long size) {
        return Result.ok(shopService.listLinks(page, size));
    }

    @PostMapping("/links")
    public Result<ShopLink> addLink(@RequestBody ShopLink link) {
        return Result.ok(shopService.addLink(link));
    }

    @PutMapping("/links/{id}")
    public Result<ShopLink> updateLink(@PathVariable Long id, @RequestBody ShopLink link) {
        return Result.ok(shopService.updateLink(id, link));
    }

    @DeleteMapping("/links/{id}")
    public Result<Void> deleteLink(@PathVariable Long id) {
        shopService.deleteLink(id);
        return Result.ok();
    }
}

package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.Product;
import com.farm.service.ProductService;
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
 * 商家工作台 · 产品管理接口
 */
@RestController
@RequestMapping("/api/merchant/products")
@RequiredArgsConstructor
public class MerchantProductController {

    private final ProductService productService;

    @GetMapping
    public Result<PageResult<Product>> list(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) Integer status) {
        return Result.ok(productService.list(keyword, categoryId, status, page, size));
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        return Result.ok(productService.create(product));
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return Result.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.ok();
    }
}

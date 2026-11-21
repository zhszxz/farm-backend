package com.farm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farm.common.Result;
import com.farm.entity.Product;
import com.farm.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页展示接口（公开）
 */
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final ProductMapper productMapper;

    /**
     * 首页轮播（推荐位上架产品）
     *
     * @return 推荐产品列表
     */
    @GetMapping("/banner")
    public Result<List<Product>> banner() {
        return Result.ok(productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .eq(Product::getIsRecommend, 1)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT 5")));
    }

    /**
     * 应季推荐（上架产品，推荐优先、最新靠前）
     *
     * @return 推荐产品列表
     */
    @GetMapping("/recommend")
    public Result<List<Product>> recommend() {
        return Result.ok(productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getIsRecommend)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT 10")));
    }
}

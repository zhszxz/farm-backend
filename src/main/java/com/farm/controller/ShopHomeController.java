package com.farm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.Product;
import com.farm.entity.Shop;
import com.farm.entity.ShopLink;
import com.farm.mapper.ProductMapper;
import com.farm.mapper.ShopLinkMapper;
import com.farm.mapper.ShopMapper;
import com.farm.vo.ShopHomeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家主页接口（公开）
 */
@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopHomeController {

    private final ShopMapper shopMapper;
    private final ProductMapper productMapper;
    private final ShopLinkMapper shopLinkMapper;

    /**
     * 商家主页（店铺信息 + 拼多多入口）
     */
    @GetMapping("/{id}")
    public Result<ShopHomeVO> detail(@PathVariable Long id) {
        Shop shop = shopMapper.selectById(id);
        if (shop == null || shop.getStatus() == null || shop.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        ShopHomeVO vo = new ShopHomeVO();
        vo.setId(shop.getId());
        vo.setShopName(shop.getShopName());
        vo.setRegion(shop.getRegion());
        vo.setAddress(shop.getAddress());
        vo.setContactName(shop.getContactName());
        vo.setContactPhone(shop.getContactPhone());
        vo.setWechat(shop.getWechat());
        vo.setDescription(shop.getDescription());
        ShopLink link = shopLinkMapper.selectOne(new LambdaQueryWrapper<ShopLink>()
                .eq(ShopLink::getShopId, shop.getId())
                .eq(ShopLink::getStatus, 1)
                .last("LIMIT 1"));
        vo.setLinkUrl(link != null ? link.getLinkUrl() : null);
        return Result.ok(vo);
    }

    /**
     * 商家上架产品列表（分页）
     */
    @GetMapping("/{id}/products")
    public Result<PageResult<Product>> products(@PathVariable Long id,
                                                @RequestParam(defaultValue = "1") long page,
                                                @RequestParam(defaultValue = "10") long size) {
        Page<Product> p = new Page<>(page, size);
        productMapper.selectPage(p, new LambdaQueryWrapper<Product>()
                .eq(Product::getShopId, id)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime));
        return Result.ok(PageResult.of(p));
    }
}

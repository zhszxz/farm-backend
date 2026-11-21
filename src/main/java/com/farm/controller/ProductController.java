package com.farm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.Category;
import com.farm.entity.Product;
import com.farm.entity.Shop;
import com.farm.entity.ShopLink;
import com.farm.mapper.CategoryMapper;
import com.farm.mapper.ProductMapper;
import com.farm.mapper.ShopLinkMapper;
import com.farm.mapper.ShopMapper;
import com.farm.vo.ProductDetailVO;
import com.farm.vo.ShopBriefVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品展示接口（公开）
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductMapper productMapper;
    private final ShopMapper shopMapper;
    private final ShopLinkMapper shopLinkMapper;
    private final CategoryMapper categoryMapper;

    /**
     * 产品列表（仅上架；支持小类/大类/关键字/季节筛选，分页）
     */
    @GetMapping
    public Result<PageResult<Product>> list(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) Long bigCategoryId,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String season) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasSeason = season != null && !season.isBlank();
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .like(hasKeyword, Product::getName, keyword)
                .eq(hasSeason, Product::getSeason, season)
                .orderByDesc(Product::getCreateTime);
        if (bigCategoryId != null) {
            wrapper.inSql(Product::getCategoryId,
                    "SELECT id FROM category WHERE parent_id = " + bigCategoryId + " AND deleted = 0");
        }
        Page<Product> p = new Page<>(page, size);
        productMapper.selectPage(p, wrapper);
        return Result.ok(PageResult.of(p));
    }

    /**
     * 产品详情（含品种/大类名、商家信息、拼多多链接）
     */
    @GetMapping("/{id}")
    public Result<ProductDetailVO> detail(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setCategoryId(product.getCategoryId());
        vo.setUnit(product.getUnit());
        vo.setImages(product.getImages());
        vo.setDescription(product.getDescription());
        vo.setOrigin(product.getOrigin());
        vo.setSeason(product.getSeason());
        if (product.getCategoryId() != null) {
            Category category = categoryMapper.selectById(product.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
                if (category.getParentId() != null && category.getParentId() != 0) {
                    Category parent = categoryMapper.selectById(category.getParentId());
                    if (parent != null) {
                        vo.setBigCategoryName(parent.getName());
                    }
                }
            }
        }
        Shop shop = shopMapper.selectById(product.getShopId());
        if (shop != null) {
            ShopBriefVO brief = new ShopBriefVO();
            brief.setId(shop.getId());
            brief.setShopName(shop.getShopName());
            brief.setWechat(shop.getWechat());
            brief.setContactPhone(shop.getContactPhone());
            ShopLink link = shopLinkMapper.selectOne(new LambdaQueryWrapper<ShopLink>()
                    .eq(ShopLink::getShopId, shop.getId())
                    .eq(ShopLink::getStatus, 1)
                    .last("LIMIT 1"));
            brief.setLinkUrl(link != null ? link.getLinkUrl() : null);
            vo.setShop(brief);
        }
        return Result.ok(vo);
    }
}

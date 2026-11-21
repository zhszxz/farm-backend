package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.entity.Product;
import com.farm.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 产品服务（商家工作台：产品增删改查、上下架）
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ShopService shopService;

    /**
     * 当前店铺的产品列表（支持关键字/品种/状态筛选）
     *
     * @param keyword    产品名称关键字
     * @param categoryId 品种（小类）ID
     * @param status     状态
     * @return 产品列表
     */
    public PageResult<Product> list(String keyword, Long categoryId, Integer status, long pageNum, long pageSize) {
        Long shopId = shopService.getCurrentShop().getId();
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        Page<Product> page = new Page<>(pageNum, pageSize);
        productMapper.selectPage(page, new LambdaQueryWrapper<Product>()
                .eq(Product::getShopId, shopId)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(status != null, Product::getStatus, status)
                .like(hasKeyword, Product::getName, keyword)
                .orderByDesc(Product::getCreateTime));
        return PageResult.of(page);
    }

    /**
     * 新增产品
     *
     * @param product 产品信息
     * @return 创建的产品
     */
    public Product create(Product product) {
        Long shopId = shopService.getCurrentShop().getId();
        product.setId(null);
        product.setShopId(shopId);
        if (product.getStatus() == null) {
            product.setStatus(0);
        }
        if (product.getIsRecommend() == null) {
            product.setIsRecommend(0);
        }
        productMapper.insert(product);
        return product;
    }

    /**
     * 编辑产品（校验归属）
     *
     * @param id  产品ID
     * @param req 产品信息
     * @return 更新后的产品
     */
    public Product update(Long id, Product req) {
        Product product = requireOwn(id);
        product.setCategoryId(req.getCategoryId());
        product.setName(req.getName());
        product.setUnit(req.getUnit());
        product.setImages(req.getImages());
        product.setDescription(req.getDescription());
        product.setOrigin(req.getOrigin());
        product.setSeason(req.getSeason());
        productMapper.updateById(product);
        return product;
    }

    /**
     * 上下架产品（校验归属）
     *
     * @param id     产品ID
     * @param status 目标状态：1-上架 2-下架
     */
    public void updateStatus(Long id, Integer status) {
        Product product = requireOwn(id);
        product.setStatus(status);
        productMapper.updateById(product);
    }

    /**
     * 删除产品（校验归属，逻辑删除）
     *
     * @param id 产品ID
     */
    public void delete(Long id) {
        requireOwn(id);
        productMapper.deleteById(id);
    }

    /**
     * 校验产品属于当前店铺
     *
     * @param id 产品ID
     * @return 产品
     */
    private Product requireOwn(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        Long shopId = shopService.getCurrentShop().getId();
        if (!shopId.equals(product.getShopId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return product;
    }
}

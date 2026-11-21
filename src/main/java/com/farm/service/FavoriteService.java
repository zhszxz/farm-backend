package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.entity.Favorite;
import com.farm.entity.Product;
import com.farm.entity.Shop;
import com.farm.mapper.FavoriteMapper;
import com.farm.mapper.ProductMapper;
import com.farm.mapper.ShopMapper;
import com.farm.security.UserContext;
import com.farm.vo.FavoriteItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 收藏关注服务
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final ShopMapper shopMapper;
    private final ObjectMapper objectMapper;

    /**
     * 收藏（幂等：已收藏则不重复插入）
     */
    public Favorite add(Integer targetType, Long targetId) {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (targetType == null || targetId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "参数错误");
        }
        LambdaQueryWrapper<Favorite> w = new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId);
        Favorite exists = favoriteMapper.selectOne(w);
        if (exists != null) {
            return exists;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType(targetType);
        favorite.setTargetId(targetId);
        favoriteMapper.insert(favorite);
        return favorite;
    }

    /**
     * 取消收藏（物理删除）
     */
    public void remove(Integer targetType, Long targetId) {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, targetType)
                .eq(Favorite::getTargetId, targetId));
    }

    /**
     * 我的收藏列表（分页，聚合产品/店铺名称、封面、状态）
     */
    public PageResult<FavoriteItemVO> myFavorites(long pageNum, long pageSize) {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        favoriteMapper.selectPage(page, new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime));
        List<FavoriteItemVO> items = buildItems(page.getRecords());
        return PageResult.from(items, page.getTotal(), page.getCurrent(), page.getSize());
    }

    private List<FavoriteItemVO> buildItems(List<Favorite> favorites) {
        List<Long> productIds = new ArrayList<>();
        Set<Long> shopIds = new HashSet<>();
        for (Favorite f : favorites) {
            if (f.getTargetType() != null && f.getTargetType() == 1) {
                productIds.add(f.getTargetId());
            } else if (f.getTargetType() != null && f.getTargetType() == 2) {
                shopIds.add(f.getTargetId());
            }
        }
        Map<Long, Product> productMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            productMap = productMapper.selectBatchIds(productIds).stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
        }
        Set<Long> shopIdsToLoad = new HashSet<>(shopIds);
        for (Product p : productMap.values()) {
            if (p.getShopId() != null) {
                shopIdsToLoad.add(p.getShopId());
            }
        }
        Map<Long, Shop> shopMap = new HashMap<>();
        if (!shopIdsToLoad.isEmpty()) {
            shopMap = shopMapper.selectBatchIds(shopIdsToLoad).stream()
                    .collect(Collectors.toMap(Shop::getId, s -> s));
        }

        List<FavoriteItemVO> items = new ArrayList<>();
        for (Favorite f : favorites) {
            FavoriteItemVO vo = new FavoriteItemVO();
            vo.setId(f.getId());
            vo.setTargetType(f.getTargetType());
            vo.setTargetId(f.getTargetId());
            vo.setCreateTime(f.getCreateTime());
            if (f.getTargetType() != null && f.getTargetType() == 1) {
                Product p = productMap.get(f.getTargetId());
                if (p == null) {
                    vo.setName("商品已失效");
                    vo.setTargetStatus(-1);
                } else {
                    vo.setName(p.getName());
                    vo.setCoverImage(firstImage(p.getImages()));
                    vo.setTargetStatus(p.getStatus());
                    Shop s = shopMap.get(p.getShopId());
                    vo.setShopName(s != null ? s.getShopName() : null);
                }
            } else {
                Shop s = shopMap.get(f.getTargetId());
                if (s == null) {
                    vo.setName("店铺已失效");
                    vo.setTargetStatus(-1);
                } else {
                    vo.setName(s.getShopName());
                    vo.setShopName(s.getShopName());
                    vo.setTargetStatus(s.getStatus());
                }
            }
            items.add(vo);
        }
        return items;
    }

    /**
     * 解析 images（JSON 字符串）取首图
     */
    private String firstImage(String images) {
        if (images == null || images.isBlank()) {
            return null;
        }
        try {
            List<String> arr = objectMapper.readValue(images, new TypeReference<List<String>>() {});
            return arr.isEmpty() ? null : arr.get(0);
        } catch (Exception e) {
            return null;
        }
    }
}

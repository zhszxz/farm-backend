package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.Favorite;
import com.farm.service.FavoriteService;
import com.farm.vo.FavoriteItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收藏关注接口（需登录）
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/favorites")
    public Result<Favorite> add(@RequestBody Favorite favorite) {
        return Result.ok(favoriteService.add(favorite.getTargetType(), favorite.getTargetId()));
    }

    @DeleteMapping("/favorites/{targetType}/{targetId}")
    public Result<Void> remove(@PathVariable Integer targetType, @PathVariable Long targetId) {
        favoriteService.remove(targetType, targetId);
        return Result.ok();
    }

    @GetMapping("/user/favorites")
    public Result<PageResult<FavoriteItemVO>> myFavorites(@RequestParam(defaultValue = "1") long page,
                                                          @RequestParam(defaultValue = "10") long size) {
        return Result.ok(favoriteService.myFavorites(page, size));
    }
}

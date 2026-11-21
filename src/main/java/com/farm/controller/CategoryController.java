package com.farm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farm.common.Result;
import com.farm.entity.Category;
import com.farm.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 产品分类接口（公开）
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    /**
     * 获取启用分类（按排序升序）
     *
     * @param parentId 父级ID：不传=全部；0=仅大类；其它=该大类下的小类
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<Category>> categories(@RequestParam(required = false) Long parentId) {
        return Result.ok(categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .eq(parentId != null, Category::getParentId, parentId)
                        .orderByAsc(Category::getSort)));
    }
}

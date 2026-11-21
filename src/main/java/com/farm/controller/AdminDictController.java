package com.farm.controller;

import com.farm.common.Result;
import com.farm.entity.Category;
import com.farm.entity.Channel;
import com.farm.entity.CostType;
import com.farm.entity.SysDict;
import com.farm.service.AdminService;
import com.farm.vo.DictTypeVO;
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

import java.util.List;

/**
 * 后台管理 · 字典与分类管理接口（仅管理员）
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDictController {

    private final AdminService adminService;

    // ============ 分类（大类/小类） ============

    @GetMapping("/categories")
    public Result<List<Category>> listCategories() {
        return Result.ok(adminService.listCategories());
    }

    @PostMapping("/categories")
    public Result<Category> createCategory(@RequestBody Category category) {
        return Result.ok(adminService.createCategory(category));
    }

    @PutMapping("/categories/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return Result.ok(adminService.updateCategory(id, category));
    }

    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return Result.ok();
    }

    // ============ 销售渠道 ============

    @GetMapping("/channels")
    public Result<List<Channel>> listChannels() {
        return Result.ok(adminService.listChannels());
    }

    @PostMapping("/channels")
    public Result<Channel> createChannel(@RequestBody Channel channel) {
        return Result.ok(adminService.createChannel(channel));
    }

    @PutMapping("/channels/{id}")
    public Result<Channel> updateChannel(@PathVariable Long id, @RequestBody Channel channel) {
        return Result.ok(adminService.updateChannel(id, channel));
    }

    @DeleteMapping("/channels/{id}")
    public Result<Void> deleteChannel(@PathVariable Long id) {
        adminService.deleteChannel(id);
        return Result.ok();
    }

    // ============ 成本类型 ============

    @GetMapping("/cost-types")
    public Result<List<CostType>> listCostTypes() {
        return Result.ok(adminService.listCostTypes());
    }

    @PostMapping("/cost-types")
    public Result<CostType> createCostType(@RequestBody CostType costType) {
        return Result.ok(adminService.createCostType(costType));
    }

    @PutMapping("/cost-types/{id}")
    public Result<CostType> updateCostType(@PathVariable Long id, @RequestBody CostType costType) {
        return Result.ok(adminService.updateCostType(id, costType));
    }

    @DeleteMapping("/cost-types/{id}")
    public Result<Void> deleteCostType(@PathVariable Long id) {
        adminService.deleteCostType(id);
        return Result.ok();
    }

    // ============ 通用字典（单位/季节等） ============

    @GetMapping("/dicts")
    public Result<List<SysDict>> listDicts(@RequestParam(required = false) String dictType) {
        return Result.ok(adminService.listDicts(dictType));
    }

    /**
     * 字典类型列表（供前端下拉）
     */
    @GetMapping("/dict-types")
    public Result<List<DictTypeVO>> listDictTypes() {
        return Result.ok(adminService.listDictTypes());
    }

    @PostMapping("/dicts")
    public Result<SysDict> createDict(@RequestBody SysDict dict) {
        return Result.ok(adminService.createDict(dict));
    }

    @PutMapping("/dicts/{id}")
    public Result<SysDict> updateDict(@PathVariable Long id, @RequestBody SysDict dict) {
        return Result.ok(adminService.updateDict(id, dict));
    }

    @DeleteMapping("/dicts/{id}")
    public Result<Void> deleteDict(@PathVariable Long id) {
        adminService.deleteDict(id);
        return Result.ok();
    }
}

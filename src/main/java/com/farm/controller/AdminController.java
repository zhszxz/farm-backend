package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.dto.ShopAuditRequest;
import com.farm.entity.Product;
import com.farm.entity.Shop;
import com.farm.entity.SysUser;
import com.farm.service.AdminService;
import com.farm.vo.AdminClickStatsVO;
import com.farm.vo.AdminOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台管理接口（仅管理员）
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/overview")
    public Result<AdminOverviewVO> overview() {
        return Result.ok(adminService.overview());
    }

    @GetMapping("/shops")
    public Result<PageResult<Shop>> listShops(@RequestParam(defaultValue = "1") long page,
                                              @RequestParam(defaultValue = "10") long size,
                                              @RequestParam(required = false) Integer status) {
        return Result.ok(adminService.listShops(status, page, size));
    }

    @PutMapping("/shops/{id}/audit")
    public Result<Void> auditShop(@PathVariable Long id, @RequestBody ShopAuditRequest body) {
        adminService.auditShop(id, body.getPass(), body.getReason());
        return Result.ok();
    }

    @GetMapping("/users")
    public Result<PageResult<SysUser>> listUsers(@RequestParam(defaultValue = "1") long page,
                                                 @RequestParam(defaultValue = "10") long size,
                                                 @RequestParam(required = false) Integer role,
                                                 @RequestParam(required = false) String phone) {
        return Result.ok(adminService.listUsers(role, phone, page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/products")
    public Result<PageResult<Product>> listProducts(@RequestParam(defaultValue = "1") long page,
                                                    @RequestParam(defaultValue = "10") long size,
                                                    @RequestParam(required = false) Integer status) {
        return Result.ok(adminService.listProducts(status, page, size));
    }

    @PutMapping("/products/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateProductStatus(id, status);
        return Result.ok();
    }

    /**
     * 设置/取消产品推荐（推荐位由管理员设置，对应 C6）
     *
     * @param id        产品ID
     * @param recommend 1-推荐 0-取消
     */
    @PutMapping("/products/{id}/recommend")
    public Result<Void> updateProductRecommend(@PathVariable Long id, @RequestParam Integer recommend) {
        adminService.updateProductRecommend(id, recommend);
        return Result.ok();
    }

    @GetMapping("/click-stats")
    public Result<AdminClickStatsVO> clickStats() {
        return Result.ok(adminService.clickStats());
    }
}

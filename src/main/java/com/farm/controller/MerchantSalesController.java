package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.SalesRecord;
import com.farm.service.SalesService;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 商家工作台 · 销售记录接口
 */
@RestController
@RequestMapping("/api/merchant/sales")
@RequiredArgsConstructor
public class MerchantSalesController {

    private final SalesService salesService;

    @GetMapping
    public Result<PageResult<SalesRecord>> list(@RequestParam(defaultValue = "1") long page,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) Integer year,
                                                @RequestParam(required = false) Long categoryId,
                                                @RequestParam(required = false) Long channelId) {
        return Result.ok(salesService.list(year, categoryId, channelId, page, size));
    }

    @PostMapping
    public Result<SalesRecord> create(@RequestBody SalesRecord record) {
        return Result.ok(salesService.create(record));
    }

    @PutMapping("/{id}")
    public Result<SalesRecord> update(@PathVariable Long id, @RequestBody SalesRecord record) {
        return Result.ok(salesService.update(id, record));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        salesService.delete(id);
        return Result.ok();
    }

    @PostMapping("/import")
    public Result<Integer> importExcel(@RequestParam("file") MultipartFile file) {
        return Result.ok(salesService.importExcel(file));
    }
}

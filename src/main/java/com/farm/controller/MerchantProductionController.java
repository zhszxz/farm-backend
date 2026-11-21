package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.ProductionRecord;
import com.farm.service.ProductionService;
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

/**
 * 商家工作台 · 产量记录接口
 */
@RestController
@RequestMapping("/api/merchant/productions")
@RequiredArgsConstructor
public class MerchantProductionController {

    private final ProductionService productionService;

    @GetMapping
    public Result<PageResult<ProductionRecord>> list(@RequestParam(defaultValue = "1") long page,
                                                     @RequestParam(defaultValue = "10") long size,
                                                     @RequestParam(required = false) Integer year,
                                                     @RequestParam(required = false) Long categoryId) {
        return Result.ok(productionService.list(year, categoryId, page, size));
    }

    @PostMapping
    public Result<ProductionRecord> create(@RequestBody ProductionRecord record) {
        return Result.ok(productionService.create(record));
    }

    @PutMapping("/{id}")
    public Result<ProductionRecord> update(@PathVariable Long id, @RequestBody ProductionRecord record) {
        return Result.ok(productionService.update(id, record));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productionService.delete(id);
        return Result.ok();
    }
}

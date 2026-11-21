package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.entity.CostRecord;
import com.farm.service.CostService;
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
 * 商家工作台 · 成本记录接口
 */
@RestController
@RequestMapping("/api/merchant/costs")
@RequiredArgsConstructor
public class MerchantCostController {

    private final CostService costService;

    @GetMapping
    public Result<PageResult<CostRecord>> list(@RequestParam(defaultValue = "1") long page,
                                               @RequestParam(defaultValue = "10") long size,
                                               @RequestParam(required = false) Integer year,
                                               @RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) Long costTypeId) {
        return Result.ok(costService.list(year, categoryId, costTypeId, page, size));
    }

    @PostMapping
    public Result<CostRecord> create(@RequestBody CostRecord record) {
        return Result.ok(costService.create(record));
    }

    @PutMapping("/{id}")
    public Result<CostRecord> update(@PathVariable Long id, @RequestBody CostRecord record) {
        return Result.ok(costService.update(id, record));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        costService.delete(id);
        return Result.ok();
    }
}

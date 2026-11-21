package com.farm.controller;

import com.farm.common.Result;
import com.farm.entity.Channel;
import com.farm.entity.CostType;
import com.farm.entity.SysDict;
import com.farm.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 只读字典接口（登录用户可用，供商家录入/商品表单下拉）
 */
@RestController
@RequestMapping("/api/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping("/channels")
    public Result<List<Channel>> channels() {
        return Result.ok(dictService.listChannels());
    }

    @GetMapping("/cost-types")
    public Result<List<CostType>> costTypes() {
        return Result.ok(dictService.listCostTypes());
    }

    @GetMapping
    public Result<List<SysDict>> dicts(@RequestParam(required = false) String dictType) {
        return Result.ok(dictService.listDicts(dictType));
    }
}

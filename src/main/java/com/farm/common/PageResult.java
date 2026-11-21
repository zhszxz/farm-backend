package com.farm.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页响应结构（统一替换原先的全量数组）
 */
@Data
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> records;
    /** 总记录数 */
    private long total;
    /** 当前页码（1 起） */
    private long page;
    /** 每页条数 */
    private long size;

    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPage(page.getCurrent());
        result.setSize(page.getSize());
        return result;
    }

    public static <T> PageResult<T> from(List<T> records, long total, long page, long size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        return result;
    }
}

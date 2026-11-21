package com.farm.controller;

import com.alibaba.excel.EasyExcel;
import com.farm.common.Result;
import com.farm.dto.NameAmountDTO;
import com.farm.dto.ProfitDTO;
import com.farm.dto.TrendDTO;
import com.farm.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 数据报表接口（商家看自己，管理员可指定店铺或全平台）
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 利润核算（按年份）
     *
     * @param shopId    店铺ID（仅管理员生效，缺省=全平台）
     * @param startYear 起始年份（可选）
     * @param endYear   结束年份（可选）
     * @return 各年销售额/成本/利润/利润率
     */
    @GetMapping("/profit")
    public Result<List<ProfitDTO>> profit(@RequestParam(required = false) Long shopId,
                                          @RequestParam(required = false) Integer startYear,
                                          @RequestParam(required = false) Integer endYear) {
        return Result.ok(reportService.profit(shopId, startYear, endYear));
    }

    /**
     * 单品跨年趋势（产量/销量/销售额）
     *
     * @param shopId     店铺ID（仅管理员生效）
     * @param categoryId 品种（小类）ID（必填）
     * @param startYear  起始年份（可选）
     * @param endYear    结束年份（可选）
     * @return 各年产量/销量/销售额
     */
    @GetMapping("/trend")
    public Result<List<TrendDTO>> trend(@RequestParam(required = false) Long shopId,
                                        @RequestParam Long categoryId,
                                        @RequestParam(required = false) Integer startYear,
                                        @RequestParam(required = false) Integer endYear) {
        return Result.ok(reportService.trend(shopId, categoryId, startYear, endYear));
    }

    /**
     * 某年各产品销售额对比（柱状图数据）
     *
     * @param shopId 店铺ID（仅管理员生效）
     * @param year   年份（必填）
     * @return 品种名与销售额
     */
    @GetMapping("/product-compare")
    public Result<List<NameAmountDTO>> productCompare(@RequestParam(required = false) Long shopId,
                                                      @RequestParam Integer year) {
        return Result.ok(reportService.productCompare(shopId, year));
    }

    /**
     * 某年各渠道销售额占比（饼图数据）
     *
     * @param shopId 店铺ID（仅管理员生效）
     * @param year   年份（必填）
     * @return 渠道名与销售额
     */
    @GetMapping("/channel-share")
    public Result<List<NameAmountDTO>> channelShare(@RequestParam(required = false) Long shopId,
                                                    @RequestParam Integer year) {
        return Result.ok(reportService.channelShare(shopId, year));
    }

    /**
     * 某年各大类销售额占比（饼图数据）
     *
     * @param shopId 店铺ID（仅管理员生效）
     * @param year   年份（必填）
     * @return 大类名与销售额
     */
    @GetMapping("/category-share")
    public Result<List<NameAmountDTO>> categoryShare(@RequestParam(required = false) Long shopId,
                                                     @RequestParam Integer year) {
        return Result.ok(reportService.categoryShare(shopId, year));
    }

    /**
     * 销售额 Top N 排行（默认 10）
     *
     * @param shopId 店铺ID（仅管理员生效）
     * @param topN   返回条数（默认 10）
     * @return 品种名与销售额
     */
    @GetMapping("/top")
    public Result<List<NameAmountDTO>> top(@RequestParam(required = false) Long shopId,
                                           @RequestParam(required = false) Integer topN) {
        return Result.ok(reportService.top(shopId, topN));
    }

    /**
     * 导出利润核算报表为 Excel
     *
     * @param shopId    店铺ID（仅管理员生效）
     * @param startYear 起始年份（可选）
     * @param endYear   结束年份（可选）
     * @param response  HTTP 响应（输出 xlsx）
     */
    @GetMapping("/export")
    public void export(@RequestParam(required = false) Long shopId,
                       @RequestParam(required = false) Integer startYear,
                       @RequestParam(required = false) Integer endYear,
                       HttpServletResponse response) throws IOException {
        List<ProfitDTO> list = reportService.profit(shopId, startYear, endYear);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=profit-report.xlsx");
        EasyExcel.write(response.getOutputStream(), ProfitDTO.class).sheet("利润核算").doWrite(list);
    }
}

package com.farm.mapper;

import com.farm.dto.NameAmountDTO;
import com.farm.dto.ProfitDTO;
import com.farm.dto.TrendDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 报表聚合查询 Mapper（对应数据库设计第 6 节 SQL）
 */
public interface ReportMapper {

    /**
     * 利润核算（按年份）：利润 = 销售额 - 成本
     */
    @Select("<script>" +
            "SELECT t.year AS year, " +
            "t.sales_amount AS sales_amount, " +
            "COALESCE(c.cost_amount, 0) AS cost_amount, " +
            "t.sales_amount - COALESCE(c.cost_amount, 0) AS profit, " +
            "CASE WHEN t.sales_amount = 0 THEN NULL " +
            "ELSE ROUND((t.sales_amount - COALESCE(c.cost_amount, 0)) / t.sales_amount * 100, 2) END AS profit_rate " +
            "FROM (" +
            "SELECT year, SUM(amount) AS sales_amount FROM sales_record " +
            "WHERE deleted = 0 " +
            "<if test='shopId != null'> AND shop_id = #{shopId}</if> " +
            "<if test='startYear != null'> AND year &gt;= #{startYear}</if> " +
            "<if test='endYear != null'> AND year &lt;= #{endYear}</if> " +
            "GROUP BY year" +
            ") t " +
            "LEFT JOIN (" +
            "SELECT year, SUM(amount) AS cost_amount FROM cost_record " +
            "WHERE deleted = 0 " +
            "<if test='shopId != null'> AND shop_id = #{shopId}</if> " +
            "<if test='startYear != null'> AND year &gt;= #{startYear}</if> " +
            "<if test='endYear != null'> AND year &lt;= #{endYear}</if> " +
            "GROUP BY year" +
            ") c ON c.year = t.year " +
            "ORDER BY t.year" +
            "</script>")
    List<ProfitDTO> profit(@Param("shopId") Long shopId,
                           @Param("startYear") Integer startYear,
                           @Param("endYear") Integer endYear);

    /**
     * 单品跨年趋势（产量 / 销量 / 销售额）
     */
    @Select("<script>" +
            "SELECT y.year AS year, " +
            "COALESCE(p.production, 0) AS production, " +
            "COALESCE(s.sales_qty, 0) AS sales_qty, " +
            "COALESCE(s.sales_amount, 0) AS sales_amount " +
            "FROM (" +
            "SELECT DISTINCT year FROM (" +
            "SELECT year FROM sales_record WHERE deleted = 0 AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) " +
            "<if test='shopId != null'> AND shop_id = #{shopId}</if> " +
            "<if test='startYear != null'> AND year &gt;= #{startYear}</if> " +
            "<if test='endYear != null'> AND year &lt;= #{endYear}</if> " +
            "UNION " +
            "SELECT year FROM production_record WHERE deleted = 0 AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) " +
            "<if test='shopId != null'> AND shop_id = #{shopId}</if> " +
            "<if test='startYear != null'> AND year &gt;= #{startYear}</if> " +
            "<if test='endYear != null'> AND year &lt;= #{endYear}</if> " +
            ") yy" +
            ") y " +
            "LEFT JOIN (" +
            "SELECT year, SUM(quantity) AS production FROM production_record " +
            "WHERE deleted = 0 AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) " +
            "<if test='shopId != null'> AND shop_id = #{shopId}</if> " +
            "<if test='startYear != null'> AND year &gt;= #{startYear}</if> " +
            "<if test='endYear != null'> AND year &lt;= #{endYear}</if> " +
            "GROUP BY year" +
            ") p ON p.year = y.year " +
            "LEFT JOIN (" +
            "SELECT year, SUM(quantity) AS sales_qty, SUM(amount) AS sales_amount FROM sales_record " +
            "WHERE deleted = 0 AND (category_id = #{categoryId} OR category_id IN (SELECT id FROM category WHERE parent_id = #{categoryId})) " +
            "<if test='shopId != null'> AND shop_id = #{shopId}</if> " +
            "<if test='startYear != null'> AND year &gt;= #{startYear}</if> " +
            "<if test='endYear != null'> AND year &lt;= #{endYear}</if> " +
            "GROUP BY year" +
            ") s ON s.year = y.year " +
            "ORDER BY y.year" +
            "</script>")
    List<TrendDTO> trend(@Param("shopId") Long shopId,
                         @Param("categoryId") Long categoryId,
                         @Param("startYear") Integer startYear,
                         @Param("endYear") Integer endYear);

    /**
     * 某年各产品（品种）销售额对比
     */
    @Select("<script>" +
            "SELECT c.id AS id, c.name AS name, SUM(s.amount) AS amount " +
            "FROM sales_record s JOIN category c ON c.id = s.category_id " +
            "WHERE s.deleted = 0 AND s.year = #{year} " +
            "<if test='shopId != null'> AND s.shop_id = #{shopId}</if> " +
            "GROUP BY c.id, c.name ORDER BY amount DESC" +
            "</script>")
    List<NameAmountDTO> productCompare(@Param("shopId") Long shopId, @Param("year") Integer year);

    /**
     * 某年各渠道销售额占比
     */
    @Select("<script>" +
            "SELECT ch.id AS id, ch.name AS name, SUM(s.amount) AS amount " +
            "FROM sales_record s JOIN channel ch ON ch.id = s.channel_id " +
            "WHERE s.deleted = 0 AND s.year = #{year} " +
            "<if test='shopId != null'> AND s.shop_id = #{shopId}</if> " +
            "GROUP BY ch.id, ch.name ORDER BY amount DESC" +
            "</script>")
    List<NameAmountDTO> channelShare(@Param("shopId") Long shopId, @Param("year") Integer year);

    /**
     * 某年各大类销售额占比（大类由小类父级推导）
     */
    @Select("<script>" +
            "SELECT COALESCE(p.id, c.id) AS id, COALESCE(p.name, c.name) AS name, SUM(s.amount) AS amount " +
            "FROM sales_record s " +
            "JOIN category c ON c.id = s.category_id " +
            "LEFT JOIN category p ON p.id = c.parent_id AND p.id &lt;&gt; 0 " +
            "WHERE s.deleted = 0 AND s.year = #{year} " +
            "<if test='shopId != null'> AND s.shop_id = #{shopId}</if> " +
            "GROUP BY COALESCE(p.id, c.id), COALESCE(p.name, c.name) ORDER BY amount DESC" +
            "</script>")
    List<NameAmountDTO> categoryShare(@Param("shopId") Long shopId, @Param("year") Integer year);

    /**
     * 销售额 Top N 排行（按品种）
     */
    @Select("<script>" +
            "SELECT c.id AS id, c.name AS name, SUM(s.amount) AS amount " +
            "FROM sales_record s JOIN category c ON c.id = s.category_id " +
            "WHERE s.deleted = 0 " +
            "<if test='shopId != null'> AND s.shop_id = #{shopId}</if> " +
            "GROUP BY c.id, c.name ORDER BY amount DESC LIMIT #{topN}" +
            "</script>")
    List<NameAmountDTO> top(@Param("shopId") Long shopId, @Param("topN") Integer topN);
}

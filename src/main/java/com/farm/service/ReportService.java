package com.farm.service;

import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.dto.NameAmountDTO;
import com.farm.dto.ProfitDTO;
import com.farm.dto.TrendDTO;
import com.farm.mapper.ReportMapper;
import com.farm.security.LoginUser;
import com.farm.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据报表服务：商家看自己，管理员可看指定店铺或全平台
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;
    private final ShopService shopService;

    /**
     * 解析报表数据范围：管理员可指定 shopId（null=全平台），商家强制看自己
     *
     * @param shopIdParam 前端传入的 shopId（仅管理员生效）
     * @return 实际查询的 shopId（null=全平台）
     */
    private Long resolveShopId(Long shopIdParam) {
        LoginUser user = UserContext.get();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Integer role = user.getRole();
        if (role != null && role == 3) {
            return shopIdParam;
        }
        if (role != null && role == 2) {
            return shopService.getCurrentShop().getId();
        }
        throw new BusinessException(ErrorCode.FORBIDDEN);
    }

    /**
     * 利润核算（按年份）
     */
    public List<ProfitDTO> profit(Long shopId, Integer startYear, Integer endYear) {
        return reportMapper.profit(resolveShopId(shopId), startYear, endYear);
    }

    /**
     * 单品跨年趋势（产量/销量/销售额）
     */
    public List<TrendDTO> trend(Long shopId, Long categoryId, Integer startYear, Integer endYear) {
        if (categoryId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "品种不能为空");
        }
        return reportMapper.trend(resolveShopId(shopId), categoryId, startYear, endYear);
    }

    /**
     * 某年各产品销售额对比
     */
    public List<NameAmountDTO> productCompare(Long shopId, Integer year) {
        if (year == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "年份不能为空");
        }
        return reportMapper.productCompare(resolveShopId(shopId), year);
    }

    /**
     * 某年各渠道销售额占比
     */
    public List<NameAmountDTO> channelShare(Long shopId, Integer year) {
        if (year == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "年份不能为空");
        }
        return reportMapper.channelShare(resolveShopId(shopId), year);
    }

    /**
     * 某年各大类销售额占比
     */
    public List<NameAmountDTO> categoryShare(Long shopId, Integer year) {
        if (year == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "年份不能为空");
        }
        return reportMapper.categoryShare(resolveShopId(shopId), year);
    }

    /**
     * 销售额 Top N 排行（默认 10）
     */
    public List<NameAmountDTO> top(Long shopId, Integer topN) {
        int n = (topN == null || topN <= 0) ? 10 : topN;
        return reportMapper.top(resolveShopId(shopId), n);
    }
}

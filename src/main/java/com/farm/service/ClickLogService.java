package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farm.entity.ClickLog;
import com.farm.mapper.ClickLogMapper;
import com.farm.vo.ClickStatItemVO;
import com.farm.vo.ClickStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跳转统计服务（商家看自己，平台记录公开跳转）
 */
@Service
@RequiredArgsConstructor
public class ClickLogService {

    private final ClickLogMapper clickLogMapper;
    private final ShopService shopService;

    /**
     * 当前店铺跳转统计（总数、按产品分布、最近记录）
     *
     * @return 统计结果
     */
    public ClickStatsVO stats() {
        Long shopId = shopService.getCurrentShop().getId();
        List<ClickLog> list = clickLogMapper.selectList(new LambdaQueryWrapper<ClickLog>()
                .eq(ClickLog::getShopId, shopId)
                .orderByDesc(ClickLog::getClickTime));

        Map<Long, Long> grouped = list.stream()
                .filter(c -> c.getProductId() != null)
                .collect(Collectors.groupingBy(ClickLog::getProductId, Collectors.counting()));
        List<ClickStatItemVO> byProduct = grouped.entrySet().stream()
                .map(e -> new ClickStatItemVO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        ClickStatsVO vo = new ClickStatsVO();
        vo.setTotal(list.size());
        vo.setByProduct(byProduct);
        vo.setRecent(list.stream().limit(10).collect(Collectors.toList()));
        return vo;
    }

    /**
     * 记录一次跳转（公开接口调用，用户点击"去拼多多购买"时）
     */
    public void recordClick(Long shopId, Long productId, Long linkId, String source) {
        ClickLog log = new ClickLog();
        log.setShopId(shopId);
        log.setProductId(productId);
        log.setLinkId(linkId);
        log.setSource(source);
        clickLogMapper.insert(log);
    }
}

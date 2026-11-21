package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.common.PageResult;
import com.farm.entity.Message;
import com.farm.entity.Shop;
import com.farm.entity.SysUser;
import com.farm.mapper.MessageMapper;
import com.farm.mapper.ShopMapper;
import com.farm.mapper.SysUserMapper;
import com.farm.security.UserContext;
import com.farm.vo.MyMessageVO;
import com.farm.vo.ReceivedMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 留言咨询服务
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;
    private final ShopMapper shopMapper;
    private final SysUserMapper userMapper;
    private final ShopService shopService;

    /**
     * 用户给商家留言
     */
    public Message create(Long shopId, String content) {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (shopId == null || content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "参数错误");
        }
        Message message = new Message();
        message.setShopId(shopId);
        message.setUserId(userId);
        message.setContent(content);
        message.setStatus(0);
        messageMapper.insert(message);
        return message;
    }

    /**
     * 我发出的留言（分页，聚合店铺名）
     */
    public PageResult<MyMessageVO> sent(long pageNum, long pageSize) {
        Long userId = UserContext.userId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Page<Message> page = new Page<>(pageNum, pageSize);
        messageMapper.selectPage(page, new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .orderByDesc(Message::getCreateTime));
        List<Message> records = page.getRecords();
        Set<Long> shopIds = records.stream().map(Message::getShopId).collect(Collectors.toSet());
        Map<Long, Shop> shopMap = new HashMap<>();
        if (!shopIds.isEmpty()) {
            shopMap = shopMapper.selectBatchIds(shopIds).stream()
                    .collect(Collectors.toMap(Shop::getId, s -> s));
        }
        final Map<Long, Shop> finalShopMap = shopMap;
        List<MyMessageVO> vos = records.stream().map(m -> {
            MyMessageVO vo = new MyMessageVO();
            vo.setId(m.getId());
            vo.setShopId(m.getShopId());
            Shop s = finalShopMap.get(m.getShopId());
            vo.setShopName(s != null ? s.getShopName() : "店铺已失效");
            vo.setContent(m.getContent());
            vo.setReply(m.getReply());
            vo.setStatus(m.getStatus());
            vo.setCreateTime(m.getCreateTime());
            vo.setReplyTime(m.getReplyTime());
            return vo;
        }).collect(Collectors.toList());
        return PageResult.from(vos, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 我收到的留言（商家，分页，聚合留言人昵称）
     */
    public PageResult<ReceivedMessageVO> received(long pageNum, long pageSize) {
        Long shopId = shopService.getCurrentShop().getId();
        Page<Message> page = new Page<>(pageNum, pageSize);
        messageMapper.selectPage(page, new LambdaQueryWrapper<Message>()
                .eq(Message::getShopId, shopId)
                .orderByDesc(Message::getCreateTime));
        List<Message> records = page.getRecords();
        Set<Long> userIds = records.stream().map(Message::getUserId).collect(Collectors.toSet());
        Map<Long, SysUser> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
        }
        final Map<Long, SysUser> finalUserMap = userMap;
        List<ReceivedMessageVO> vos = records.stream().map(m -> {
            ReceivedMessageVO vo = new ReceivedMessageVO();
            vo.setId(m.getId());
            vo.setUserId(m.getUserId());
            SysUser u = finalUserMap.get(m.getUserId());
            vo.setUserNickname(u != null ? u.getNickname() : "用户已注销");
            vo.setContent(m.getContent());
            vo.setReply(m.getReply());
            vo.setStatus(m.getStatus());
            vo.setCreateTime(m.getCreateTime());
            vo.setReplyTime(m.getReplyTime());
            return vo;
        }).collect(Collectors.toList());
        return PageResult.from(vos, page.getTotal(), page.getCurrent(), page.getSize());
    }

    /**
     * 商家回复留言（校验归属）
     */
    public Message reply(Long id, String reply) {
        Long shopId = shopService.getCurrentShop().getId();
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        if (!shopId.equals(message.getShopId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        message.setReply(reply);
        message.setStatus(1);
        message.setReplyTime(LocalDateTime.now());
        messageMapper.updateById(message);
        return message;
    }
}

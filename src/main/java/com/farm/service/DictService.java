package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farm.entity.Channel;
import com.farm.entity.CostType;
import com.farm.entity.SysDict;
import com.farm.mapper.ChannelMapper;
import com.farm.mapper.CostTypeMapper;
import com.farm.mapper.SysDictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 只读字典服务（供登录用户前端下拉使用，不含管理写权限）
 */
@Service
@RequiredArgsConstructor
public class DictService {

    private final ChannelMapper channelMapper;
    private final CostTypeMapper costTypeMapper;
    private final SysDictMapper dictMapper;

    public List<Channel> listChannels() {
        return channelMapper.selectList(new LambdaQueryWrapper<Channel>().orderByAsc(Channel::getSort));
    }

    public List<CostType> listCostTypes() {
        return costTypeMapper.selectList(new LambdaQueryWrapper<CostType>().orderByAsc(CostType::getSort));
    }

    public List<SysDict> listDicts(String dictType) {
        return dictMapper.selectList(new LambdaQueryWrapper<SysDict>()
                .eq(dictType != null && !dictType.isBlank(), SysDict::getDictType, dictType)
                .orderByAsc(SysDict::getSort));
    }
}

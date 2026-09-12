package com.yihecode.camera.ai.service.map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.map.MapRule;
import com.yihecode.camera.ai.mapper.map.MapRuleMapper;
import org.springframework.stereotype.Service;

/**
* Camera or Box Rule rule Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class MapRuleServiceImpl extends ServiceImpl<MapRuleMapper, MapRule> implements MapRuleService {

    /**
* Query most after One Data
*
* @return
*/
    @Override
    public MapRule getLatest(Integer type) {
        LambdaQueryWrapper<MapRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapRule::getType, type);
        queryWrapper.orderByDesc(MapRule::getId);
        queryWrapper.last("limit 1");
        return this.getOne(queryWrapper, false);
    }
}
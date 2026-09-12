package com.yihecode.camera.ai.service.map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.map.MapRule;

/**
* Camera or Box Rule rule Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface MapRuleService extends IService<MapRule> {

    /**
* Query most after One Data
* @return
*/
    MapRule getLatest(Integer type);
}
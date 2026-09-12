package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.AlarmLevel;

import java.util.Map;

/**
* Alert Level Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface AlarmLevelService extends IService<AlarmLevel> {

    /**
* Back map result structure
* @return
*/
    Map<Long, AlarmLevel> getDataMap();

}
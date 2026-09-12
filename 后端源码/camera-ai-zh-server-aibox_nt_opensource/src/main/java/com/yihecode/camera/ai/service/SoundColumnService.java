package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.SoundColumn;

import java.util.List;
import java.util.Map;

/**
* IP Speaker Pole Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface SoundColumnService extends IService<SoundColumn> {

    SoundColumn getBySn(String sn);

    /**
* Get Speaker Pole Data map result structure
* @return
*/
    Map<Long, SoundColumn> getDataMap();
}
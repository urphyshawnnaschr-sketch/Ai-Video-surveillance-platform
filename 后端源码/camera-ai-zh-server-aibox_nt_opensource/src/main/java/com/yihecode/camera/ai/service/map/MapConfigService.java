package com.yihecode.camera.ai.service.map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.map.MapConfig;

import java.util.List;

/**
* Ground image or image Layer Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface MapConfigService extends IService<MapConfig> {

    /**
* Query Data
* @return
*/
    List<MapConfig> listData();

    /**
* Delete Data
* @param id
*/
    void deleteData(Long id);
}
package com.yihecode.camera.ai.service.map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.map.MapObject;

import java.util.List;

/**
* Ground image or image Layer and Camera or Box Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface MapObjectService extends IService<MapObject> {

    /**
* By Ground image or image Layer Delete
* @param mapId
*/
    void deleteByMapID(Long mapId);

    /**
* Query Data
* @param mapId
* @param objectId
* @return
*/
    MapObject getData(Long mapId, Long objectId);

    /**
* By Ground image / image Layer ID Query
* @param mapId
* @param type
* @return
*/
    List<MapObject> listData(Long mapId, Integer type);

    /**
* By Type Query object_id List
* @param type
* @return
*/
    List<Long> listObjectIDByType(Integer type);
}
package com.yihecode.camera.ai.service.map;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.map.MapObject;
import com.yihecode.camera.ai.mapper.map.MapObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* Ground image or image Layer and Camera or Box Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Service
public class MapObjectServiceImpl extends ServiceImpl<MapObjectMapper, MapObject> implements MapObjectService {

    /**
* By Ground image or image Layer Delete
*
* @param mapId
*/
    @Override
    public void deleteByMapID(Long mapId) {
        LambdaQueryWrapper<MapObject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapObject::getMapId, mapId);
        this.remove(queryWrapper);
    }

    /**
* Query Data
*
* @param mapId
* @param objectId
* @return
*/
    @Override
    public MapObject getData(Long mapId, Long objectId) {
        LambdaQueryWrapper<MapObject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapObject::getMapId, mapId);
        queryWrapper.eq(MapObject::getObjectId, objectId);
        return this.getOne(queryWrapper, false);
    }

    /**
* By Ground image / image Layer ID Query
*
* @param mapId
* @return
*/
    @Override
    public List<MapObject> listData(Long mapId, Integer type) {
        LambdaQueryWrapper<MapObject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapObject::getMapId, mapId);
        if(type != null) {
            queryWrapper.eq(MapObject::getType, type);
        }
        List<MapObject> mapObjectList = this.list(queryWrapper);
        return mapObjectList == null ? new ArrayList<>() : mapObjectList;
    }

    /**
* By Type Query object_id List
*
* @param type
* @return
*/
    @Override
    public List<Long> listObjectIDByType(Integer type) {
        LambdaQueryWrapper<MapObject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MapObject::getObjectId);
        queryWrapper.eq(MapObject::getType, type);
        List<MapObject> mapObjectList = this.list(queryWrapper);
        if(mapObjectList == null || mapObjectList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> objectIdList = new ArrayList<>();
        for(MapObject mapObject : mapObjectList) {
            if(mapObject == null || mapObject.getObjectId() == null) {
                continue;
            }
            objectIdList.add(mapObject.getObjectId());
        }
        return objectIdList;
    }
}
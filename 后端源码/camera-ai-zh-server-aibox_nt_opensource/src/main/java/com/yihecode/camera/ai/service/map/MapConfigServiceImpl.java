package com.yihecode.camera.ai.service.map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.AlarmLevel;
import com.yihecode.camera.ai.entity.map.MapConfig;
import com.yihecode.camera.ai.mapper.AlarmLevelMapper;
import com.yihecode.camera.ai.mapper.map.MapConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* Ground image or image Layer Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class MapConfigServiceImpl extends ServiceImpl<MapConfigMapper, MapConfig> implements MapConfigService {

    @Autowired
    private MapObjectService mapObjectService;

    /**
* Query Data
*
* @return
*/
    @Override
    public List<MapConfig> listData() {
        LambdaQueryWrapper<MapConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(MapConfig::getSort);
        List<MapConfig> mapConfigList = this.list(queryWrapper);
        return mapConfigList == null ? new ArrayList<>() : mapConfigList;
    }

    /**
* Delete Data
*
* @param id
*/
    @Override
    public void deleteData(Long id) {
        //Delete Data
this.removeById(id);

// Delete Relate Data
mapObjectService.deleteByMapID(id);
}
}
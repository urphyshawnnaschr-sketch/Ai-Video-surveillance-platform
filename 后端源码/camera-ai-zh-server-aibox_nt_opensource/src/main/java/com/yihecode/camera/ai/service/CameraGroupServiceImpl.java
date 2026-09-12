package com.yihecode.camera.ai.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.mapper.CameraGroupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Camera Group
*
* @author zhou
* @since 2025.6.16
*/
@Slf4j
@Service
public class CameraGroupServiceImpl extends ServiceImpl<CameraGroupMapper, CameraGroup> implements CameraGroupService {

    @Autowired
    private CameraGroupItemService cameraGroupItemService;

    /**
* Query All
*
* @return
*/
    @Override
    public List<CameraGroup> listAll() {
        LambdaQueryWrapper<CameraGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(CameraGroup::getParentId);
        queryWrapper.orderByAsc(CameraGroup::getSort);
        return this.list(queryWrapper);
    }

    /**
* Batch Delete
*
* @param removeIds
*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeBatch(List<Long> removeIds) {
        this.removeByIds(removeIds);

        for(Long groupId : removeIds) {
            cameraGroupItemService.deleteByGroup(groupId);
        }
    }

    /**
* Get Current Node and child Node
*
* @param id
* @return
*/
    @Override
    public List<CameraGroup> getCurrentAndChild(Long id) {
        List<CameraGroup> cameraGroupList = this.list();

        List<CameraGroup> results = new ArrayList<>();
        for(CameraGroup cameraGroup : cameraGroupList) {
            if(cameraGroup.getId().equals(id)) {
                results.add(cameraGroup);
                break;
            }
        }

        List<Long> parentIds = new ArrayList<>();
        parentIds.add(id);
        while (true) {
            List<Long> subIds = new ArrayList<>();

            for(CameraGroup cameraGroup : cameraGroupList) {
                if(cameraGroup.getParentId() != null && parentIds.contains(cameraGroup.getParentId())) {
                    results.add(cameraGroup);
                    subIds.add(cameraGroup.getId());
                }
            }

            if(subIds.isEmpty()) {
                break;
            }

            parentIds.clear();
            parentIds.addAll(subIds);
        }
        return results;
    }

    /**
* Query most big Group Hierarchy
*
* @return
*/
    @Override
    public int getMaxLevel() {
        LambdaQueryWrapper<CameraGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CameraGroup::getLevel);
        queryWrapper.last("limit 0, 1");

        CameraGroup cameraGroup = this.getOne(queryWrapper, false);
        if(cameraGroup == null) {
            return 1;
        }
        return cameraGroup.getLevel();
     }

    /**
* By Hierarchy Query
*
* @param level
* @return
*/
    @Override
    public List<CameraGroup> listByLevel(Integer level) {
        LambdaQueryWrapper<CameraGroup> queryWrapper = new LambdaQueryWrapper<>();
        if(level != null && level > 0) {
            queryWrapper.eq(CameraGroup::getLevel, level);
        }
        queryWrapper.orderByAsc(CameraGroup::getSort);
        return this.list(queryWrapper);
    }

    /**
* Get Current Node and up Level Name List
*
* @param id
* @param cameraGroupList
* @return
*/
    @Override
    public String getCurrentAndParentNames(Long id, List<CameraGroup> cameraGroupList) {
        if(cameraGroupList.isEmpty()) {
            return "-";
        }

        Map<Long, CameraGroup> cameraGroupMap = cameraGroupList.stream().collect(Collectors.toMap(CameraGroup::getId, Function.identity()));

        //to up check find parent Level Node
List<String> names = new ArrayList<>();
Long currentId = id;
do {
CameraGroup cameraGroup = cameraGroupMap.get(currentId);
if(cameraGroup == null) {
currentId = 0L;
} else {
names.add(cameraGroup.getName());
if(cameraGroup.getParentId() == null || cameraGroup.getParentId() == 0L) {
currentId = 0L;
} else {
currentId = cameraGroup.getParentId();
}
}

} while (currentId > 0);

// No data
if(names.isEmpty()) {
return"-";
}

Collections.reverse(names);
return String.join("", names);
}
}

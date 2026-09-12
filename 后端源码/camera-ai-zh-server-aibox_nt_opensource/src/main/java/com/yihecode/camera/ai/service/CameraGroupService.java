package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.CameraGroup;

import java.util.List;

/**
* Camera Group
*
* @author zhou
* @since 2025.6.16
*/
public interface CameraGroupService extends IService<CameraGroup> {

    /**
* Query All
* @return
*/
    List<CameraGroup> listAll();

    /**
* Batch Delete
* @param removeIds
*/
    void removeBatch(List<Long> removeIds);

    /**
* Get Current Node and child Node
* @param id
* @return
*/
    List<CameraGroup> getCurrentAndChild(Long id);

    /**
* Query most big Group Hierarchy
* @return
*/
    int getMaxLevel();

    /**
* By Hierarchy Query
* @param level
* @return
*/
    List<CameraGroup> listByLevel(Integer level);

    /**
* Get Current Node and up Level Name List
* @param id
* @param cameraGroupList
* @return
*/
    String getCurrentAndParentNames(Long id, List<CameraGroup> cameraGroupList);

}

package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.entity.CameraGroupItem;
import com.yihecode.camera.ai.web.vo.CameraGroupItemListVo;
import com.yihecode.camera.ai.web.vo.CameraGroupItemModifyVo;

import java.util.List;

/**
* Camera Group Detail
*
* @author zhou
* @since 2025.6.16
*/
public interface CameraGroupItemService extends IService<CameraGroupItem> {

    /**
* By Group ID Query
* @param groupId
* @return
*/
    List<CameraGroupItem> listByGroup(Long groupId);

    /**
* By Group ID Delete
* @param groupId
*/
    void deleteByGroup(Long groupId);

    /**
* Page Query
* @param page
* @param limit
* @param cameraGroupIds
* @return
*/
    IPage<CameraGroupItem> listPage(Integer page, Integer limit, List<Long> cameraGroupIds, List<Long> cameraIds);

    /**
* Add / Modify Data
* @param modifyVo
*/
    void saveData(CameraGroupItemModifyVo modifyVo);

    /**
* By Group ID Count Count
* @param groupId
* @return
*/
    int countByGroup(Long groupId);

    /**
* By Camera ID Query Group IDs
* @param cameraIds
* @return
*/
    List<Long> listIdByCamera(List<Long> cameraIds);

    /**
* By Camera Query The belong Group
* @param cameraId
* @return
*/
    CameraGroupItem getByCamera(Long cameraId);
}

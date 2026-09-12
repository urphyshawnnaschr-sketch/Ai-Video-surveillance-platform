package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceSyncBox;

import java.util.List;

/**
* Face and Box Sync Object table
* @author zhou
* @since 2025-07-110
*/
public interface FaceSyncBoxService extends IService<FaceSyncBox> {

    /**
* By Box ID Query Sync Record Count
* @param boxId
* @return
*/
    int countByBox(Long boxId);

    /**
* Query Pending Sync Record
* @param boxId
* @return
*/
    List<FaceSyncBox> listSyncByBox(Long boxId);
}

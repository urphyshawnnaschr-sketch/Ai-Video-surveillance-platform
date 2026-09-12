package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.UserGroupRelationships;

import java.util.List;

/**
* User group close System Service
*/
public interface ApUserGroupRelationshipsService extends IService<UserGroupRelationships> {

    /**
* By User id Query The In Annotation group
* @param userId User id
* @return
*/
    List<UserGroupRelationships> findByUser(Long userId);
}

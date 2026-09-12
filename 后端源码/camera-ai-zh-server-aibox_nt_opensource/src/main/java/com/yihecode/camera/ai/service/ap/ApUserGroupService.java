package com.yihecode.camera.ai.service.ap;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.dto.ApGroupQueryDTO;
import com.yihecode.camera.ai.dto.ApUserGroupDTO;
import com.yihecode.camera.ai.entity.ap.UserGroup;

import java.util.List;

/**
* Annotation group Service
*/
public interface ApUserGroupService extends IService<UserGroup> {

    /**
* Query List
* @param groupQueryDTO
* @return
*/
    List<ApUserGroupDTO> findGroupList(ApGroupQueryDTO groupQueryDTO);

    /**
* Page Query
* @param groupQueryDTO
* @return
*/
    IPage<ApUserGroupDTO> findGroupPage(ApGroupQueryDTO groupQueryDTO);

    /**
* Save Annotation group
* @param groupDTO
* @return
*/
    Long saveGroup(ApUserGroupDTO groupDTO);

    /**
* Update Annotation group
* @param groupDTO
*/
    void updateGroupById(ApUserGroupDTO groupDTO);

    /**
* Delete Annotation group
* @param groupId
*/
    void deleteGroupById(Long groupId);


    /**
* Add User
* @param groupId
* @param userIds
* @return
*/
    Integer addGroupUser(Long groupId, JSONArray userIds);

    /**
* produce produce Annotation group User
* @param groupId
* @param userIds
* @return
*/
    Integer deleteGroupUser(Long groupId, JSONArray userIds);
}

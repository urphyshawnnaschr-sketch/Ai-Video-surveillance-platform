package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.UserRole;

import java.util.List;

/**
* User Role Service
*/
public interface ApUserRoleService extends IService<UserRole> {

    /**
* Query User Role Name List
* @param accountId
* @return
*/
    String findRoleNames(Long accountId);

    /**
* By Account id Query Relate Role ids
* @param accountId
* @return
*/
    List<Long> findRoleIds(Long accountId);

    /**
* By Role id Query Relate User ids
* @param roleId
* @return
*/
    List<Long> findUserIds(Long roleId);

    /**
* Whether System Management member
* @param accountId
* @return
*/
    boolean checkRole(Long accountId, String roleCode);

    /**
* By Role id Query User id
* @param roleIds
* @return
*/
    List<Long> listUserIds(List<Long> roleIds);
}

package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.RolePermission;
import com.yihecode.camera.ai.entity.ap.UserRole;

import java.util.List;

/**
* Role and Menu Service
* @author zhoumingxing
*/
public interface ApRolePermissionService extends IService<RolePermission> {

    /**
* By role_id Query Menu List
* @param roleId
* @return
*/
    List<Long> findMenuByRole(Long roleId);

    /**
* Delete Role and Menu Relate
* @param roleId
* @param menusId
*/
    void deleteMenus(Long roleId, Long menusId);

    /**
* Delete Role
* @param roleId
*/
    void deleteRole(Long roleId);
}

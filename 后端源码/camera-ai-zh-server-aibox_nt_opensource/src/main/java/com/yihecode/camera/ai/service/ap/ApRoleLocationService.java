package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.RoleLocation;

import java.util.List;

/**
* Role and Region Service
* @author Abyss
*/
public interface ApRoleLocationService extends IService<RoleLocation> {

    /**
* By role_id Query Region List
* @param roleId
* @return
*/
    List<Long> findLocationByRole(Long roleId);

    /**
* Delete Role and Region Relate
* @param roleId
* @param locationId
*/
    void deleteLocation(Long roleId, Long locationId);

    /**
* Delete Role
* @param roleId
*/
    void deleteRole(Long roleId);
}

package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.Role;

import java.util.List;

/**
* Role Service
*/
public interface ApRoleService extends IService<Role> {

    /**
* By English Name Query
* @param nameEn
* @return
*/
    Role getByEn(String nameEn);

    /**
* Save Role
* @param role
* @throws Exception
*/
    void saveRole(Role role) throws Exception;

    /**
* Query Data List
* @return
*/
    List<Role> listData();

    /**
* Delete Role
* @param roleId
*/
    void deleteRole(Long roleId);

    /**
* By Role Code Query
* @param roleCode
* @return
*/
    Role getByRoleCode(String roleCode);
}

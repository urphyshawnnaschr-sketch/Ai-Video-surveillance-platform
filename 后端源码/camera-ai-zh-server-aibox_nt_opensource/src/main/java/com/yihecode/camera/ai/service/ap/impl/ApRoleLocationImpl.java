package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.RoleLocation;
import com.yihecode.camera.ai.entity.ap.RolePermission;
import com.yihecode.camera.ai.mapper.ap.ApRoleLocationMapper;
import com.yihecode.camera.ai.mapper.ap.ApRolePermissionMapper;
import com.yihecode.camera.ai.service.ap.ApRoleLocationService;
import com.yihecode.camera.ai.service.ap.ApRolePermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* Role and Region real current class
* @author Abyss
*/
@Service
public class ApRoleLocationImpl extends ServiceImpl<ApRoleLocationMapper, RoleLocation> implements ApRoleLocationService {

    /**
* By role_id Query Menu List
*
* @param roleId
* @return
*/
    @Override
    public List<Long> findLocationByRole(Long roleId) {
        LambdaQueryWrapper<RoleLocation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleLocation::getRoleId, roleId);
        //Query Menu Ids
List<RoleLocation> roleLocations = this.list(queryWrapper);
if(roleLocations == null) {
return new ArrayList<>();
}
// turn Complete list
List<Long> locationIds = new ArrayList<>();
for(RoleLocation rolePermission: roleLocations) {
locationIds.add(rolePermission.getLocationId());
}
return locationIds;
}

/**
* Delete Role and Menu Relate
*
* @param roleId
* @param locationId
*/
@Override
public void deleteLocation(Long roleId, Long locationId) {
LambdaQueryWrapper<RoleLocation> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(RoleLocation::getRoleId, roleId);
queryWrapper.eq(RoleLocation::getLocationId, locationId);
this.remove(queryWrapper);
}

/**
* Delete Role
*
* @param roleId
*/
@Override
public void deleteRole(Long roleId) {
LambdaQueryWrapper<RoleLocation> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(RoleLocation::getRoleId, roleId);
this.remove(queryWrapper);
}
}
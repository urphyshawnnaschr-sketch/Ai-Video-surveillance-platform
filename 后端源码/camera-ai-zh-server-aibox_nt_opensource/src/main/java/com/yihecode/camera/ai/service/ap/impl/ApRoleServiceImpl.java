package com.yihecode.camera.ai.service.ap.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.entity.ap.RoleLocation;
import com.yihecode.camera.ai.entity.ap.RolePermission;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApRoleMapper;
import com.yihecode.camera.ai.service.ap.ApRoleLocationService;
import com.yihecode.camera.ai.service.ap.ApRolePermissionService;
import com.yihecode.camera.ai.service.ap.ApRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Role real current class
*/
@Service
public class ApRoleServiceImpl extends ServiceImpl<ApRoleMapper, Role> implements ApRoleService {

    //
@Autowired
private ApRolePermissionService apRolePermissionService;

@Autowired
private ApRoleLocationService apRoleLocationService;

/**
* By English Name Query
*
* @param nameEn
* @return
*/
@Override
public Role getByEn(String nameEn) {
LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Role::getNameEn, nameEn);
return this.getOne(queryWrapper);
}

/**
* Save Role
*
* @param role
* @throws Exception
*/
@Override
public void saveRole(Role role) throws Exception {
// By Code Query Whether already exists
Role roleOld = this.getByEn(role.getNameEn());

// Add & Edit Data
if(role.getId() == null) {
// Code via Store, not Allow re reply Add
if(roleOld!= null) {
throw new BizException("Error, Role Code Exist");
}
// Add
role.setCreatedAt(new Date());
role.setUpdatedAt(new Date());
this.save(role);
// Add Role
List<Long> menusIds = role.getMenusIds();
if(menusIds!= null &&!menusIds.isEmpty()) {
for(Long menusId: menusIds) {
RolePermission rolePermission = new RolePermission();
rolePermission.setRoleId(role.getId());
rolePermission.setMenuId(menusId);
apRolePermissionService.save(rolePermission);
}
}
// Bind Box
List<Long> locationIds = role.getLocationIds();
if(locationIds!= null &&!locationIds.isEmpty()) {
for(Long locationId: locationIds) {
RoleLocation roleLocation = new RoleLocation();
roleLocation.setRoleId(role.getId());
roleLocation.setLocationId(locationId);
apRoleLocationService.save(roleLocation);
}
}
} else {
// Code does not exist, and and Current Role not Consistent
if(roleOld!= null &&!roleOld.getId().equals(role.getId())) {
throw new BizException("Error, Role Code Exist");
}
// Edit
role.setUpdatedAt(new Date());
this.saveOrUpdate(role);
// Update Role
List<Long> menusIdsByRole = apRolePermissionService.findMenuByRole(role.getId());
//
List<Long> menusIds = role.getMenusIds();
if(menusIds == null) {
menusIds = new ArrayList<>();
}
// Add increase Add Menu IDs
for(Long menusId: menusIds) {
if(!menusIdsByRole.contains(menusId)) {
RolePermission rolePermission = new RolePermission();
rolePermission.setRoleId(role.getId());
rolePermission.setMenuId(menusId);
apRolePermissionService.save(rolePermission);
}
}
// Delete Delete Menu IDs
for(Long menusId: menusIdsByRole) {
if(!menusIds.contains(menusId)) {
apRolePermissionService.deleteMenus(role.getId(), menusId);
}
}
// Region Edit
List<Long> locationIdsByRole = apRoleLocationService.findLocationByRole(role.getId());
List<Long> locationIds = role.getLocationIds();
if(locationIds == null) {
locationIds = new ArrayList<>();
}
// Add increase Add Menu IDs
for(Long locationId: locationIds) {
if(!locationIdsByRole.contains(locationId)) {
RoleLocation roleLocation = new RoleLocation();
roleLocation.setRoleId(role.getId());
roleLocation.setLocationId(locationId);
apRoleLocationService.save(roleLocation);
}
}
// Delete Delete Menu IDs
for(Long locationId: locationIdsByRole) {
if(!locationIds.contains(locationId)) {
apRoleLocationService.deleteLocation(role.getId(), locationId);
}
}

}
}

/**
* Query Data List
*
* @return
*/
@Override
public List<Role> listData() {
LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.isNull(Role::getDeletedAt);
queryWrapper.orderByAsc(Role::getCreatedAt);
List<Role> roles = this.list(queryWrapper);
return roles == null? new ArrayList<>(): roles;
}

/**
* Delete Role
*
* @param roleId
*/
@Override
public void deleteRole(Long roleId) {
// Delete
this.removeById(roleId);
//
apRolePermissionService.deleteRole(roleId);
//
apRoleLocationService.deleteRole(roleId);
}

/**
* By Role Code Query
*
* @param roleCode
* @return
*/
@Override
public Role getByRoleCode(String roleCode) {
LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Role::getNameEn, roleCode);
return this.getOne(queryWrapper, false);
}
}
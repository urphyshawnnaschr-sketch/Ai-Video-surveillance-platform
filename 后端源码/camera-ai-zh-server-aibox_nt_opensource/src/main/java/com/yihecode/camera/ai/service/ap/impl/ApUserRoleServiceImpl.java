package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.entity.ap.UserRole;
import com.yihecode.camera.ai.mapper.ap.ApUserRoleMapper;
import com.yihecode.camera.ai.service.ap.ApRoleService;
import com.yihecode.camera.ai.service.ap.ApUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleStatus;
import java.util.ArrayList;
import java.util.List;

/**
* User Role real current class
*/
@Service
public class ApUserRoleServiceImpl extends ServiceImpl<ApUserRoleMapper, UserRole> implements ApUserRoleService {

    @Autowired
    private ApRoleService apRoleService;

    /**
* Query User Role Name List
*
* @param accountId
* @return
*/
    @Override
    public String findRoleNames(Long accountId) {
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, accountId);
        //
List<UserRole> userRoleList = this.list(queryWrapper);
if(userRoleList == null) {
userRoleList = new ArrayList<>();
}
//
List<String> roleNames = new ArrayList<>();
for(UserRole userRole: userRoleList) {
Role role = apRoleService.getById(userRole.getRoleId());
if(role!= null) {
roleNames.add(role.getNameCh());
}
}
return String.join(",", roleNames);
}

/**
* By Account id Query Relate Role ids
*
* @param accountId@return
*/
@Override
public List<Long> findRoleIds(Long accountId) {
LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(UserRole::getUserId, accountId);
//
List<UserRole> userRoleList = this.list(queryWrapper);
if(userRoleList == null) {
userRoleList = new ArrayList<>();
}
//
List<Long> roleIds = new ArrayList<>();
for(UserRole userRole: userRoleList) {
roleIds.add(userRole.getRoleId());
}
return roleIds;
}

/**
* By Role id Query Relate User ids
*
* @param roleId
* @return
*/
@Override
public List<Long> findUserIds(Long roleId) {
LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(UserRole::getRoleId, roleId);
//
List<UserRole> userRoleList = this.list(queryWrapper);
if(userRoleList == null) {
userRoleList = new ArrayList<>();
}
//
List<Long> userIds = new ArrayList<>();
for(UserRole userRole: userRoleList) {
userIds.add(userRole.getUserId());
}
return userIds;
}

/**
* By Account id and Role Code Query Whether the Role
*
* @param accountId
* @return
*/
@Override
public boolean checkRole(Long accountId, String roleCode) {
Role role = apRoleService.getByRoleCode(roleCode);
if(role == null) {
return false;
}
//
LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(UserRole::getUserId, accountId);
List<UserRole> userRoles = this.list(queryWrapper);
if(userRoles == null || userRoles.isEmpty()) {
return false;
}
//
for(UserRole userRole: userRoles) {
if(userRole.getRoleId().equals(role.getId())) {
return true;
}
}
return false;
}

/**
* By Role id Query User id
*
* @param roleIds
* @return
*/
@Override
public List<Long> listUserIds(List<Long> roleIds) {
if(roleIds == null || roleIds.isEmpty()) {
return new ArrayList<>();
}
//
LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(UserRole::getRoleId, roleIds);
List<UserRole> userRoles = this.list(queryWrapper);
if(userRoles == null || userRoles.isEmpty()) {
return new ArrayList<>();
}
//
List<Long> userIds = new ArrayList<>();
for(UserRole userRole: userRoles) {
if(!userIds.contains(userRole.getUserId())) {
userIds.add(userRole.getUserId());
}
}
return userIds;
}

}
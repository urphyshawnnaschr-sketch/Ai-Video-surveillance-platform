package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.entity.ap.RolePermission;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApRolePermissionMapper;
import com.yihecode.camera.ai.service.ap.ApRolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Role and Menu real current class
* @author zhoumingxing
*/
@Service
public class ApRolePermissionImpl extends ServiceImpl<ApRolePermissionMapper, RolePermission> implements ApRolePermissionService {

    /**
* By role_id Query Menu List
*
* @param roleId
* @return
*/
    @Override
    public List<Long> findMenuByRole(Long roleId) {
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, roleId);
        //Query Menu Ids
List<RolePermission> rolePermissions = this.list(queryWrapper);
if(rolePermissions == null) {
return new ArrayList<>();
}
// turn Complete list
List<Long> menusIds = new ArrayList<>();
for(RolePermission rolePermission: rolePermissions) {
menusIds.add(rolePermission.getMenuId());
}
return menusIds;
}

/**
* Delete Role and Menu Relate
*
* @param roleId
* @param menusId
*/
@Override
public void deleteMenus(Long roleId, Long menusId) {
LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(RolePermission::getRoleId, roleId);
queryWrapper.eq(RolePermission::getMenuId, menusId);
this.remove(queryWrapper);
}

/**
* Delete Role
*
* @param roleId
*/
@Override
public void deleteRole(Long roleId) {
LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(RolePermission::getRoleId, roleId);
this.remove(queryWrapper);
}
}
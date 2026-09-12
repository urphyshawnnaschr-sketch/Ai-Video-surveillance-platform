package com.yihecode.camera.ai.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.Menus;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.ApMenusService;
import com.yihecode.camera.ai.service.ap.ApRolePermissionService;
import com.yihecode.camera.ai.service.ap.ApRoleService;
import com.yihecode.camera.ai.service.ap.ApUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* Custom Permission Load API real current class
*/
@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private ApRoleService roleService;

    @Resource
    private ApUserRoleService userRoleService;

    @Resource
    private ApRolePermissionService rolePermissionService;

    @Resource
    private ApMenusService menusService;

    @Resource
    private AccountService accountService;

    /**
* Back One Account The Own has Permission code Set combine
*/
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if(loginId == null) {
            return new ArrayList<>();
        }

        Long accountId = Convert.toLong(loginId, 0L);

        Account account = accountService.getById(accountId);
        if(account == null || account.getState() == null || account.getState() != 0) {
            return new ArrayList<>();
        }

        List<Menus> menusList = menusService.listData();
        if(menusList.isEmpty()) {
            return new ArrayList<>();
        }

        //super Level Management member, Back All Permission Code
if(account.getIsSuper()!= null && account.getIsSuper() == 1) {
List<String> perms = new ArrayList<>();
for(Menus menus: menusList) {
perms.add(menus.getAuth());
}
perms.add("sys_super:perm");
return perms;
}

// Query User Permission Code
List<Long> userRoleIds = userRoleService.findRoleIds(accountId);
if(userRoleIds == null || userRoleIds.isEmpty()) {
return new ArrayList<>();
}

//
List<Long> finalMenusIds = new ArrayList<>();
for(Long roleId: userRoleIds) {
List<Long> menusIds = rolePermissionService.findMenuByRole(roleId);
if(menusIds!= null &&!menusIds.isEmpty()) {
finalMenusIds.addAll(menusIds);
}
}

List<String> perms = new ArrayList<>();
for(Menus menus: menusList) {
if(finalMenusIds.contains(menus.getId())) {
perms.add(menus.getAuth());
}
}
return perms;
}

/**
* Back One Account The Own has Role ID Set combine (Permission and Role can part open Validate)
*/
@Override
public List<String> getRoleList(Object loginId, String loginType) {
if(loginId == null) {
return new ArrayList<>();
}

Long accountId = Convert.toLong(loginId, 0L);

Account account = accountService.getById(accountId);
if(account == null || account.getState() == null || account.getState()!= 0) {
return new ArrayList<>();
}

List<Role> roles = roleService.listData();
if(roles.isEmpty()) {
return new ArrayList<>();
}

// super Level Management member, Back All Role Code
if(account.getIsSuper()!= null && account.getIsSuper() == 1) {
List<String> roleCodes = new ArrayList<>();
for(Role role: roles) {
roleCodes.add(role.getNameEn());
}
roleCodes.add("sys_super_role");
return roleCodes;
}

// Query User Role Code
List<Long> userRoleIds = userRoleService.findRoleIds(accountId);
if(userRoleIds == null || userRoleIds.isEmpty()) {
return new ArrayList<>();
}

List<String> userRoleCodes = new ArrayList<>();
for(Role role: roles) {
if(userRoleIds.contains(role.getId())) {
userRoleCodes.add(role.getNameEn());
}
}
return userRoleCodes;
}

}

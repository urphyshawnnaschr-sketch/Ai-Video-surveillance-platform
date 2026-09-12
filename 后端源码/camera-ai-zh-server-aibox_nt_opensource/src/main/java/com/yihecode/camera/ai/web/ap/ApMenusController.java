package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.Menus;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.mapper.ap.ApRolePermissionMapper;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.ApMenusService;
import com.yihecode.camera.ai.service.ap.ApRolePermissionService;
import com.yihecode.camera.ai.service.ap.ApUserRoleService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
* Menu Management
* @author zhoumingxing
*/
@Slf4j
@Api(tags = "System Management _ Menu Management")
@Controller
@RequestMapping({"/ap/menus"})
public class ApMenusController {

    @Autowired
    private ApMenusService apMenusService;

    @Autowired
    private ApRolePermissionService apRolePermissionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApUserRoleService apUserRoleService;

    /**
* Add & Edit Menu
* @param menus
* @return
* @throws Exception
*/
    @ApiOperation(value = "Add & Edit Menu")
    @SaCheckPermission(value = {"systemManagement-menu"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult save(@RequestBody Menus menus) throws Exception {
        //Validate Menu Name
if(StrUtil.isBlank(menus.getName())) {
return JsonResultUtils.fail("Please enter Menu Name");
}
// Validate Unique One Code
if(StrUtil.isBlank(menus.getAuth())) {
return JsonResultUtils.fail("Please enter Menu Unique One Code");
}
//
if(menus.getParent() == null) {
return JsonResultUtils.fail("Please select in up Level Menu");
}

// Add & Edit Data
apMenusService.saveMenus(menus);
return JsonResultUtils.success();
}

/**
* Delete Menu
* @param id
* @return
* @throws Exception
*/
@ApiOperation(value ="Delete Menu")
@ApiImplicitParam(name ="id", value ="Menu ID")
@SaCheckPermission(value = {"systemManagement-menu"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) throws Exception {
// Delete
apMenusService.removeById(id);
return JsonResultUtils.success();
}

/**
* Query Menu Detail
* @param id
* @return
* @throws Exception
*/
@ApiOperation(value ="Query Menu Detail")
@ApiImplicitParam(name ="id", value ="Menu ID")
@SaCheckPermission(value = {"systemManagement-menu"}, mode = SaMode.OR)
@PostMapping({"/detail"})
@ResponseBody
public JsonResult<Menus> detail(Long id) throws Exception {
//
Menus menus = apMenusService.getById(id);
return JsonResultUtils.success(menus);
}

/**
* Query Menu Tree result structure
* @return
*/
@ApiOperation(value ="Query Menu Tree result structure")
@ApiImplicitParam(name ="roleId", value ="Role ID")
@SaCheckPermission(value = {"systemManagement-role","systemManagement-menu"}, mode = SaMode.OR)
@PostMapping({"/tree"})
@ResponseBody
public JsonResult<List<Menus>> listTree(Long roleId) {
// Query Menu List
List<Menus> menusList = apMenusService.listData();

// Query Menu and Role Relate
List<Long> checkedMenusIds = apRolePermissionService.findMenuByRole(roleId);
for(Menus menus: menusList) {
if(checkedMenusIds.contains(menus.getId())) {
menus.setChecked(true);
}
}

// turn Complete Tree result structure
List<Menus> trees = new ArrayList<>();
for (Menus treeResult: menusList) {
if (treeResult.getParent() == null || treeResult.getParent() == 0l) {
treeResult.setParent(0l);
trees.add(findChildren(treeResult, menusList));
}
}
return JsonResultUtils.success(trees);
}

/**
* Query child Node
*/
private Menus findChildren(Menus tree, List<Menus> treeList) {
for (Menus node: treeList) {
if (tree.getId().equals(node.getParent())) {
if (tree.getChildren() == null) {
tree.setChildren(new ArrayList<>());
}
tree.getChildren().add(findChildren(node, treeList));
}
}
return tree;
}

@ApiOperation("By Login id Query Menu")
@PostMapping("/user/tree")
@ResponseBody
public JsonResult userTree(@RequestHeader("Lang") String language) {
Long accountId = StpUtil.getLoginIdAsLong();
// Whether System Management member
boolean isAdmin = false;
Account account = accountService.getById(accountId);
if(account!= null && account.getIsSuper()!= null && account.getIsSuper() == 1) {
isAdmin = true;
}

// User Relate Role
List<Long> userRoles = apUserRoleService.findRoleIds(accountId);
// Role Relate Menu ids
List<Long> menusIds = new ArrayList();
for(Long roleId: userRoles) {
List<Long> roleMenusIds = apRolePermissionService.findMenuByRole(roleId);
for(Long roleMenusId: roleMenusIds) {
if(menusIds.contains(roleMenusId)) {
continue;
}
menusIds.add(roleMenusId);
}
}

// Query All Menu
List<Menus> userMenuss = new ArrayList<>();
List<Menus> menuss = apMenusService.listData();
for(Menus menus: menuss) {
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
menus.setName(menus.getEnglishName());
}
//
if (menus.getIsHidden() == null || menus.getIsHidden() == 0) {
menus.getMeta().put("hideMenu", false);
} else {
menus.getMeta().put("hideMenu", true);
}

// has Permission Menu
if (menusIds.contains(menus.getId()) || isAdmin) {
userMenuss.add(menus);
}
}

// turn Tree result structure
List<Menus> tree = new ArrayList<>();
for(Menus menus: userMenuss) {
if(menus.getParent() == 0) {
tree.add(findChildren(menus, userMenuss));
}
}
return JsonResultUtils.success(tree);
}

}

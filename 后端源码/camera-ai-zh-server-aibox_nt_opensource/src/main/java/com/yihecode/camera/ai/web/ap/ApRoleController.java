package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.service.ap.ApRoleService;
import com.yihecode.camera.ai.service.ap.ApUserRoleService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "System Management _ Role Management")
@Controller
@RequestMapping({"/ap/role"})
public class ApRoleController {

    @Autowired
    private ApRoleService apRoleService;

    @Autowired
    private ApUserRoleService apUserRoleService;

    @Autowired
    private ApDepartService apDepartService;

    @Autowired
    private AccountService accountService;

    /**
* Query Data List
* @return
*/
    @ApiOperation(value = "Query Role List")
    @SaCheckPermission(value = {"systemManagement-organizational", "systemManagement-role"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public JsonResult<List<Role>> listData() {
        List<Role> roles = apRoleService.listData();
        if(roles == null) {
            roles = new ArrayList<>();
        }
        //
Long userId = StpUtil.getLoginIdAsLong();
Account account = accountService.getById(userId);
if(account.getIsSuper()!= null && account.getIsSuper() == 1) {// Management member Query All
for(Role role: roles) {
List<Long> userIds = apUserRoleService.findUserIds(role.getId());
role.setAccountNum(userIds.size());
}
return JsonResultUtils.success(roles);
} else {
if(account.getDepartId() == null) {// not has belong belong Department, Direct connect Back
return JsonResultUtils.success(roles);
}

// Query All Account
List<Account> accounts = accountService.list();
if(accounts == null) {
accounts = new ArrayList<>();
}
// check find same Level or child Level Department Node
List<Long> departIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
// By Department id Filter Account
List<Long> accountIds = new ArrayList<>();
for(Account account1: accounts) {
if(account1.getDepartId()!= null && departIds.contains(account1.getDepartId())) {
accountIds.add(account1.getId());
}
}
//
for(Role role: roles) {
List<Long> userIds = apUserRoleService.findUserIds(role.getId());
int count = 0;
for(Long userId1: userIds) {
if(accountIds.contains(userId1)) {
count++;
}
}
role.setAccountNum(count);
}
return JsonResultUtils.success(roles);
}

// return JsonResultUtils.success(apRoleService.listData());
}

/**
* Add Role
* @param role
* @return
* @throws Exception
*/
@ApiOperation(value ="Add Role")
@SaCheckPermission(value = {"role-add","role-edit"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult save(@RequestBody Role role) throws Exception {
// Validate Role Code
if(StrUtil.isBlank(role.getNameEn())) {
throw new BizException("Please enter Role Code");
}
// Validate Role Name
if(StrUtil.isBlank(role.getNameCh())) {
throw new BizException("Please enter Role Name");
}
// Add & Edit Data
apRoleService.saveRole(role);
return JsonResultUtils.success();
}

/**
* Delete Role
* @param id
* @return
* @throws Exception
*/
@ApiOperation(value ="Delete Role")
@ApiImplicitParam(name ="id", value ="Role ID")
@SaCheckPermission(value = {"role-delete"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) throws Exception {
apRoleService.deleteRole(id);
return JsonResultUtils.success();
}

/**
* Role Detail
* @param id
* @return
* @throws Exception
*/
@ApiOperation(value ="Role Detail")
@ApiImplicitParam(name ="id", value ="Role ID")
@SaCheckPermission(value = {"systemManagement-role"}, mode = SaMode.OR)
@PostMapping({"/detail"})
@ResponseBody
public JsonResult detail(Long id) throws Exception {
Role role = apRoleService.getById(id);
if(role == null) {
return JsonResultUtils.fail("find not to Role");
}
return JsonResultUtils.success(role);
}
}

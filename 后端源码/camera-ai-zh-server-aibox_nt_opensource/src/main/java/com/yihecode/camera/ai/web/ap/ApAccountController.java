package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.dto.*;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.entity.ap.Menus;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.entity.ap.UserTeam;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.ap.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.ap.vo.AccountBatchDeleteRequestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Api(tags = "System Management _ User Management")
@SaCheckLogin
@Controller
@RequestMapping({"/ap/account"})
public class ApAccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApUserTeamService apUserTeamService;

    @Autowired
    private ApUserRoleService apUserRoleService;

    @Autowired
    private ApRoleService apRoleService;

    @Autowired
    private ApDepartService apDepartService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ApRolePermissionService apRolePermissionService;

    @Autowired
    private ApMenusService apMenusService;

    @ApiOperation(value = "Account List Pagination")
    @SaCheckPermission(value = {"systemManagement-organizational","edgePlatform-groupView", "faceControl-faceHistory", "edgePlatform-videoPreview"}, mode = SaMode.OR)
    @PostMapping({"/listPage"})
    @ResponseBody
    public PageResult listPage(@RequestBody  ApAccountQueryDTO accountQueryDTO) {
//if(accountQueryDTO.getTeamId() == null) {// must Refer Fixed Team ID
// return PageResultUtils.success(0L, new ArrayList<>());
//}

// Query Department
List<Depart> departs = apDepartService.listData();

//
IPage<ApAccountDTO> apAccountDTOIPage = accountService.findAccountPage(accountQueryDTO);
List<ApAccountDTO> apAccountDTOS = apAccountDTOIPage.getRecords();
// Password Clear Divide
if(apAccountDTOS!= null) {
for(ApAccountDTO apAccountDTO: apAccountDTOS) {
apAccountDTO.setPassword("******");

// Department Hierarchy show open
List<String> departNames = new ArrayList<>();
Long departId = apAccountDTO.getDepartId();
if(departId == null) {
departNames.add("-");
} else {
while(true) {
boolean found = false;
for (Depart depart: departs) {
if (depart.getId().equals(departId)) {
departNames.add(depart.getName());
departId = depart.getParentId();
found = true;
}
}
//
if(!found) {
break;
}
}
}
if(departNames.isEmpty()) {
departNames.add("-");
}
Collections.reverse(departNames);
apAccountDTO.setDepartNames(String.join("-", departNames));

//
if(apAccountDTO.getId() == 1) {
ApRoleDTO apRoleDTO = new ApRoleDTO();
apRoleDTO.setId(0L);
apRoleDTO.setNameCh("Management member");
apRoleDTO.setNameEn("administrator");
apAccountDTO.setRoles(Collections.singletonList(apRoleDTO));
}
}
}
return PageResultUtils.success(apAccountDTOIPage.getTotal(), apAccountDTOIPage.getRecords());
}

@ApiOperation(value ="Save Account")
@SaCheckPermission(value = {"user-add"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult<Long> save(@RequestBody ApAccountModifyDTO apAccountModifyDTO) {
return JsonResultUtils.success(accountService.saveAccount(apAccountModifyDTO));
}

@ApiOperation(value ="Modify Account")
@SaCheckPermission(value = {"user-edit"}, mode = SaMode.OR)
@PostMapping({"/update"})
@ResponseBody
public JsonResult<ApProjectDTO> update(@RequestBody ApAccountModifyDTO apAccountModifyDTO) {
accountService.updateAccountById(apAccountModifyDTO);
return JsonResultUtils.success(null);
}

@ApiOperation(value ="Delete Account")
@PostMapping({"/delete"})
@ApiImplicitParam(name ="accountId", value ="Account id")
@ResponseBody
public JsonResult delete(@RequestParam("accountId") Long accountId) {
//
Account account = accountService.getById(accountId);
if(account == null) {
return JsonResultUtils.fail("Delete User Failed, Account does not exist or Deleted");
}
// Whether super Level Management member
if(account!= null && account.getIsSuper()!= null && account.getIsSuper() == 1) {
return JsonResultUtils.fail("Delete User Failed, System Management member not Allow Delete");
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
if ((menus.getParent() == null || menus.getParent() == 0) && menusIds.contains(menus.getId())) {// only Need Directory Type Menu
userMenuss.add(menus);
}
}

// Whether Run Delete
String delAccountCheckMenus = configService.getByValTag("delAccountCheckMenus");
boolean canDel = true;
for(Menus menus: userMenuss) {
if(delAccountCheckMenus.contains(menus.getAuth())) {// Contain Disable Menu, not Allow Delete
canDel = false;
break;
}
}

//
if(!canDel) {
return JsonResultUtils.fail("Delete User Failed, Please Ensure User no Management, Edit Permission");
}
accountService.deleteAccountById(accountId);
return JsonResultUtils.success();
}

@ApiOperation(value ="Delete Account")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/batchDelete"})
@ResponseBody
public JsonResult batchDelete(@RequestBody AccountBatchDeleteRequestVo requestVo) {
if(requestVo.getAccountIds() == null || requestVo.getAccountIds().isEmpty()) {
return JsonResultUtils.fail("not has Pending Delete Data");
}
//
List<Long> accountIds = requestVo.getAccountIds();
for(Long accountId: accountIds) {
accountService.deleteAccountById(accountId);
}
return JsonResultUtils.success();
}

@ApiOperation(value ="Query Detail")
//@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="accountId", value ="Account id")
//})
@SaCheckPermission(value = {"systemManagement-organizational"}, mode = SaMode.OR)
@PostMapping({"/detail"})
@ResponseBody
public JsonResult<ApAccountDTO> detail(@RequestParam("accountId") Long accountId) {
ApAccountDTO accountDTO = accountService.findAccountById(accountId);
return JsonResultUtils.success(accountDTO);
}

@ApiOperation(value ="Reset Password")
@ApiImplicitParam(name ="accountId", value ="Account id")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/resetPassword"})
@ResponseBody
public JsonResult resetPassword(@RequestParam("accountId") Long accountId) {
accountService.updateResetPassword(accountId);
return JsonResultUtils.success();
}

@PostMapping("updatePassword")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="accountId", value ="Account id"),
@ApiImplicitParam(name ="password", value ="Account Password")
})
@SaCheckPermission(value = {"user-edit"}, mode = SaMode.OR)
@ResponseBody
public JsonResult updatePassword(@RequestParam("accountId") Long accountId, @RequestParam("password") String password) {
accountService.updatePassword(accountId, password);
return JsonResultUtils.success();
}

/**
* By Creator Query The Create Team All Complete member
* @return
*/
@ApiOperation(value ="By Creator Query The Create Team All Complete member")
@SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
@PostMapping({"/listAllTeamUser"})
@ResponseBody
public JsonResult listAllTeamUser() {
List<ApAccountDTO> apAccountDTOList = accountService.findAllTeamUser(StpUtil.getLoginIdAsLong());
return JsonResultUtils.success(apAccountDTOList);
}

/**
* By Team ID Query All Complete member
* @return
*/
@ApiOperation(value ="By Team ID Query All Complete member")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="teamId", value ="Team ID"),
@ApiImplicitParam(name ="name", value ="Name")
})
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@PostMapping({"/listTeamuser"})
@ResponseBody
public JsonResult listTeamUser(Long teamId, String name) {
List<ApAccountDTO> apAccountDTOList = accountService.findTeamUser(teamId, name);
return JsonResultUtils.success(apAccountDTOList);
}

/**
* By Annotation group Id Query User List
* @param groupId
* @return
*/
@ApiOperation(value ="By Annotation group Id Query All Complete member")
@ApiImplicitParam(name ="groupId", value ="Annotation group ID")
@SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
@PostMapping({"/listGroupUser"})
@ResponseBody
public JsonResult listGroupUser(Long groupId) {
List<ApAccountDTO> apAccountDTOList = accountService.findGroupUser(groupId);
return JsonResultUtils.success(apAccountDTOList);
}

/**
* Export Account List
* @param teamId
* @param response
* @throws IOException
*/
@ApiOperation("Export Account List")
@ApiImplicitParam(name ="teamId", value ="Team ID")
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@GetMapping("downloadUsers")
public void downloadUsers(Long teamId, HttpServletResponse response) throws IOException {
// Here Note has same Learn reverse should make Use swagger will import Cause each kind Problem, Please Direct connect Use Browser or Use postman
try {
// Query Team
UserTeam userTeam = apUserTeamService.getById(teamId);
if(userTeam == null) {
throw new BizException("Team does not exist");
}
// Query Team Complete member
List<ApAccountDTO> apAccountDTOList = accountService.findTeamUser(teamId);
//
List<ApAccountExcelDTO> datas = new ArrayList<>();
for(ApAccountDTO apAccountDTO: apAccountDTOList) {
// Role Name List
String roleNames = apUserRoleService.findRoleNames(apAccountDTO.getId());
//
ApAccountExcelDTO dto = new ApAccountExcelDTO();
dto.setAccount(apAccountDTO.getAccount());
dto.setPassword("");
dto.setName(apAccountDTO.getName());
dto.setTeamName(userTeam.getName());
dto.setRoleNames(roleNames);
dto.setCreatedAt(DateUtil.format(apAccountDTO.getCreatedAt(),"yyyy/MM/dd HH:mm"));
datas.add(dto);
}

//
response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
response.setCharacterEncoding("utf-8");
// Here URLEncoder.encode can with Prevent in Text Messy code when and easyexcel not has close System
String fileName = URLEncoder.encode(userTeam.getName() +"_ Account List","UTF-8").replaceAll("\\+","%20");
response.setHeader("Content-disposition","attachment;filename*=utf-8''"+ fileName +".xlsx");
// Here Need Set not Close Stream
EasyExcel.write(response.getOutputStream(), ApAccountExcelDTO.class).autoCloseStream(Boolean.FALSE).sheet("Account List").doWrite(datas);
} catch (Exception e) {
// Reset response
response.reset();
response.setContentType("application/json");
response.setCharacterEncoding("utf-8");
Map<String, Object> map = MapUtils.newHashMap();
map.put("status","failure");
map.put("message","Download File Failed"+ e.getMessage());
map.put("code", 500);
map.put("msg","Download File Failed"+ e.getMessage());
response.getWriter().println(JSON.toJSONString(map));
}
}

//
@ApiOperation(value ="By Role Count Person member Count")
@SaCheckPermission(value = {"flowDsetection","systemManagement-organizational"}, mode = SaMode.OR)
@PostMapping({"/statics"})
@ResponseBody
public JsonResult<List<Role>> statics() {
Map<Long, Integer> roleCntMap = accountService.listGroupByRole();
List<Role> roleList = apRoleService.listData();
if(roleList == null) {
roleList = new ArrayList<>();
}
//
for(Role role: roleList) {
Integer num = roleCntMap.get(role.getId());
role.setAccountNum(num == null? 0: num);
}
return JsonResultUtils.success(roleList);
}

//
@ApiOperation(value ="Whether System Management member")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/super"})
@ResponseBody
public JsonResult<Boolean> isSuper() {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if (account == null || account.getIsSuper() == null ||!account.getIsSuper().equals(1)) {
return JsonResultUtils.success(false);
}
return JsonResultUtils.success(true);
}
}

package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.dto.ApAccountModifyDTO;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ApTeamDTO;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.entity.ap.UserGroup;
import com.yihecode.camera.ai.entity.ap.UserGroupRelationships;
import com.yihecode.camera.ai.entity.ap.UserTeam;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.ap.vo.UserTeamBatchRequestVo;
import com.yihecode.camera.ai.web.ap.vo.UserTeamDeleteUserRequestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
//import net.sourceforge.pinyin4j.PinyinHelper;
//import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
//import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiIgnore
@Api(tags ="Team Management")
@SaCheckLogin
@Controller
@RequestMapping({"/ap/userTeam"})
public class ApUserTeamController {

@Autowired
private ApUserTeamService apUserTeamService;

@Autowired
private ApUserTeamRelationshipsService apUserTeamRelationshipsService;

@Autowired
private AccountService accountService;

@Autowired
private ApRoleService apRoleService;

@Autowired
private ApUserGroupRelationshipsService apUserGroupRelationshipsService;

@Autowired
private ApUserGroupService apUserGroupService;

@ApiOperation(value ="Query Group Queue table")
@SaCheckPermission(value = {"apmgr-summary"}, mode = SaMode.OR)
@PostMapping({"/listData"})
@ResponseBody
public JsonResult<List<UserTeam>> listData() {
List<UserTeam> userTeamList = apUserTeamService.listData();
return JsonResultUtils.success(userTeamList);
}

@ApiOperation(value ="Query Group Queue table - Pagination")
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@PostMapping({"/listPage"})
@ResponseBody
public PageResult listPage(@RequestBody ApTeamDTO teamDTO) {
// TODO: 2023/8/1 Platform Management member can with Query All
System.out.println(teamDTO);
IPage<UserTeam> teamDTOIPage = apUserTeamService.findTeamPage(teamDTO);
List<UserTeam> records = teamDTOIPage.getRecords();
if(records == null) {
records = new ArrayList<>();
}
//
for(UserTeam userTeam: records) {
// Creator member
Account createAccount = accountService.getById(userTeam.getUserId());
userTeam.setCreatedName(createAccount == null?"": createAccount.getName());

// Team Person member Count
int userNum = apUserTeamRelationshipsService.countUserNum(userTeam.getId());
userTeam.setUserNum(userNum);
}

System.out.println(records);

return PageResultUtils.success(teamDTOIPage.getTotal(), records);
}

@ApiOperation(value ="Save Team")
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult<Long> save(@RequestBody ApTeamDTO teamDTO) {
// Creator User ID
Long userId = StpUtil.getLoginIdAsLong();
teamDTO.setUserId(userId);
return JsonResultUtils.success(apUserTeamService.saveTeam(teamDTO));
}

@ApiOperation(value ="Modify Team")
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@PostMapping({"/update"})
@ResponseBody
public JsonResult<ApProjectDTO> update(@RequestBody ApTeamDTO teamDTO) {
apUserTeamService.updateTeamById(teamDTO);
return JsonResultUtils.success(null);
}

@ApiOperation(value ="Delete Team")
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@DeleteMapping({"/delete"})
//@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="teamId", value ="Team id")
//})
@ResponseBody
public JsonResult delete(@RequestParam("teamId") Long teamId) {
apUserTeamService.deleteTeamById(teamId);
return JsonResultUtils.success();
}

/**
* Batch Create User
* @param userTeamBatchRequestVo
* @return
*/
@ApiOperation(value ="Batch Create User")
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@PostMapping({"/saveBatchUsers"})
@ResponseBody
public JsonResult<Long> saveBatchUsers(@RequestBody UserTeamBatchRequestVo userTeamBatchRequestVo) {
//
if(userTeamBatchRequestVo.getTeamId() == null) {
return JsonResultUtils.fail("Please select in Team");
}
//
if(userTeamBatchRequestVo.getRoleIds() == null || userTeamBatchRequestVo.getRoleIds().isEmpty()) {
return JsonResultUtils.fail("Please select in Role");
}
//
if(userTeamBatchRequestVo.getUserNum() == null || userTeamBatchRequestVo.getUserNum() <= 0) {
return JsonResultUtils.fail("Please enter Count");
}
//
if(StrUtil.isBlank(userTeamBatchRequestVo.getPrefix())) {
return JsonResultUtils.fail("Please enter Account front Concat");
}
//
UserTeam userTeam = apUserTeamService.getById(userTeamBatchRequestVo.getTeamId());
if(userTeam == null) {
return JsonResultUtils.fail("Team does not exist");
}
//String shortTeamName = shortTeamName(userTeam.getName());
//
List<String> roleNameEns = new ArrayList<>();
for(Long roleId: userTeamBatchRequestVo.getRoleIds()) {
Role role = apRoleService.getById(roleId);
if(role == null) {
return JsonResultUtils.fail("Role does not exist ["+ roleId +"]");
}
roleNameEns.add(role.getNameEn());
}
String roleNameEnStr = String.join("-", roleNameEns);
apUserTeamService.saveBatchUsers(userTeamBatchRequestVo.getTeamId(), userTeamBatchRequestVo.getRoleIds(), userTeamBatchRequestVo.getUserNum(), userTeamBatchRequestVo.getPrefix(), roleNameEnStr);
return JsonResultUtils.success();
}

/**
* from Team Delete User
* @param userTeamDeleteUserRequestVo
* @return
*/
@ApiOperation(value ="from Team Delete User")
@SaCheckPermission(value = {"apmgr-team"}, mode = SaMode.OR)
@PostMapping({"/deleteUser"})
@ResponseBody
public JsonResult<Long> deleteUser(@RequestBody UserTeamDeleteUserRequestVo userTeamDeleteUserRequestVo) {
//
if(userTeamDeleteUserRequestVo.getTeamId() == null) {
return JsonResultUtils.fail("Please select in Team");
}
//
if(userTeamDeleteUserRequestVo.getUserId() == null) {
return JsonResultUtils.fail("Please select in User");
}
// Determine User In some Annotation group, like Result Exist, rule not Allow Direct connect Delete
List<UserGroupRelationships> userGroupRelationshipss = apUserGroupRelationshipsService.findByUser(userTeamDeleteUserRequestVo.getUserId());
if(!userGroupRelationshipss.isEmpty()) {
List<String> groupNames = new ArrayList<>();
for(UserGroupRelationships userGroupRelationships: userGroupRelationshipss) {
UserGroup userGroup = apUserGroupService.getById(userGroupRelationships.getGroupId());
if(userGroup!= null) {
groupNames.add(userGroup.getName());
}
}
return JsonResultUtils.fail("User Relate Annotation group ["+ String.join(",", groupNames) +"], Please Solve Divide Relate again Delete");
}

apUserTeamRelationshipsService.deleteUser(userTeamDeleteUserRequestVo.getTeamId(), userTeamDeleteUserRequestVo.getUserId());
return JsonResultUtils.success();
}

/**
* Team Name front Two Char Base Zoom write
* @param name
* @return
*/
// private String shortTeamName(String name) {
// if(StrUtil.isBlank(name)) {
// return"";
//}
// //
// HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
// format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//
// //
// try {
// //
// List<String> firstChars = new ArrayList<>();
// char[] chars = name.toCharArray();
// for (char ch: chars) {
// String[] str = PinyinHelper.toHanyuPinyinStringArray(ch, format);
// for (String s: str) {
// if (s.length() > 0) {
// firstChars.add(String.valueOf(s.charAt(0)).toUpperCase());
//}
//}
//}
// //
// if(firstChars.isEmpty()) {
// return"";
//}
// //
// if(firstChars.size() == 1) {
// return String.join("", firstChars) +"-";
//}
// return String.join("", firstChars.subList(0, 2)) +"-";
//} catch (Exception e) {
// //
// return"";
//}
//}
}

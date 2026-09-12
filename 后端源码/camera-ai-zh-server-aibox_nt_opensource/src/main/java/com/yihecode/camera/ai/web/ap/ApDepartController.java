package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.ap.vo.DepartMoveRequestVo;
import com.yihecode.camera.ai.web.ap.vo.DepartSaveRequestVo;
import com.yihecode.camera.ai.web.vo.IdVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* Organization Organization Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "System Management _ Organization Organization Management")
@Controller
@RequestMapping({"/ap/depart"})
public class ApDepartController {

    //
@Autowired
private ApDepartService apDepartService;
@Autowired
private AccountService accountService;

@ApiOperation("Query Detail")
@SaCheckPermission(value = {"systemManagement-organizational"}, mode = SaMode.OR)
@PostMapping("/detail")
@ResponseBody
public JsonResult detail(@RequestBody IdVo idVo) {
if(idVo.getId() == null) {
return JsonResultUtils.fail("Param Is Empty");
}
Depart depart = apDepartService.getById(idVo.getId());
if(depart == null) {
return JsonResultUtils.fail("find not to Department Data");
}
return JsonResultUtils.success(depart);
}

//
@ApiOperation(value ="Query Data Tree List")
@SaCheckPermission(value = {"flowDsetection","systemManagement-organizational","alarmManagement","edgePlatform-casketManagement","edgePlatform-videoPreview"}, mode = SaMode.OR)
@PostMapping({"/listTree"})
@ResponseBody
public JsonResult<List<Depart>> listTree() {
List<Depart> Departs = apDepartService.listData();
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return JsonResultUtils.success(new ArrayList<>());
}
//
if(account.getIsSuper()!= null && account.getIsSuper().equals(1)) {
List<Depart> tree = new ArrayList<>();
for(Depart Depart: Departs) {
if(Depart.getParentId() == 0) {
tree.add(findChildren(Depart, Departs));
}
}
return JsonResultUtils.success(tree);
} else {
List<Depart> tree = new ArrayList<>();
for(Depart Depart: Departs) {
if (Depart.getId().equals(account.getDepartId())) {
tree.add(findChildren(Depart, Departs));
}
}
return JsonResultUtils.success(tree);
}
}

/**
* deliver belong Method
* @param tree parent Node Object
* @param treeList All List
* @return
*/
public Depart findChildren(Depart tree, List<Depart> treeList) {
for (Depart node: treeList) {
if (tree.getId().equals(node.getParentId())) {
if (tree.getChildren() == null) {
tree.setChildren(new ArrayList<>());
}
// deliver belong Call self Body
tree.getChildren().add(findChildren(node, treeList));
}
}
return tree;
}

//
@ApiOperation(value ="Save Data")
@SaCheckPermission(value = {"organizational-edit","organizational-add"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult<Void> save(@RequestBody DepartSaveRequestVo departSaveRequestVo) {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null || account.getIsSuper() == null ||!account.getIsSuper().equals(1)) {
return JsonResultUtils.fail("Non System Management member, You not has Operation Permission");
}
//
if(StrUtil.isBlank(departSaveRequestVo.getName())) {
return JsonResultUtils.fail("Please enter Department Name");
}
if(departSaveRequestVo.getParentId() == null) {
return JsonResultUtils.fail("Please select up Level Department");
}
// Non Root Node, Determine Node Whether Exist
if(departSaveRequestVo.getParentId()!= 0) {
Depart Depart = apDepartService.getById(departSaveRequestVo.getParentId());
if(Depart == null) {
return JsonResultUtils.fail("up Level Department does not exist");
}
}
// Add
if(departSaveRequestVo.getId() == null) {
Depart Depart = new Depart();
Depart.setName(departSaveRequestVo.getName());
Depart.setParentId(departSaveRequestVo.getParentId());
Depart.setCreatedBy(StpUtil.getLoginIdAsLong());
Depart.setCreatedAt(new Date());
Depart.setUpdatedBy(StpUtil.getLoginIdAsLong());
Depart.setUpdatedAt(new Date());
apDepartService.save(Depart);
return JsonResultUtils.success();
}

// Update
Depart Depart = apDepartService.getById(departSaveRequestVo.getId());
if(Depart == null) {
return JsonResultUtils.fail("Department does not exist");
}
Depart.setName(departSaveRequestVo.getName());
Depart.setParentId(departSaveRequestVo.getParentId());
Depart.setUpdatedBy(StpUtil.getLoginIdAsLong());
Depart.setUpdatedAt(new Date());
apDepartService.updateById(Depart);
return JsonResultUtils.success();
}

//
@ApiOperation(value ="Delete Data")
@SaCheckPermission(value = {"organizational-delete"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(@RequestBody IdVo idVo) {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null || account.getIsSuper() == null ||!account.getIsSuper().equals(1)) {
return JsonResultUtils.fail("Non System Management member, You not has Operation Permission");
}
//
if(idVo.getId() == null) {
return JsonResultUtils.fail("Param Is Empty");
}
//
List<Depart> Departs = apDepartService.listData();
List<Depart> tree = new ArrayList<>();
for(Depart Depart: Departs) {
if(Depart.getId().equals(idVo.getId())) {
tree.add(findChildren(Depart, Departs));
}
}
//
if(tree.isEmpty()) {
return JsonResultUtils.fail("not has find to Department Info");
}

// Tree turn for list
List<Depart> result = new ArrayList<>();
this.treeToList(result, tree.get(0));

// Delete
List<Long> ids = result.stream().map(Depart::getId).collect(Collectors.toList());
apDepartService.deleteData(ids);

return JsonResultUtils.success();
}

/**
* deliver belong transmit over Incoming tree turn for list
*
* @param result Result Set
* @param root Current Object
*/
public void treeToList(List<Depart> result, Depart root) {
result.add(root);
if (CollectionUtils.isEmpty(root.getChildren())) {
return;
}
//
for (Depart child: root.getChildren()) {
this.treeToList(result, child);
}
}

//
@ApiOperation(value ="Node Move")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/move"})
@ResponseBody
public JsonResult move(@RequestBody DepartMoveRequestVo requestVo) {
if(requestVo.getSourceId() == null) {
return JsonResultUtils.fail("Source Node Is Empty");
}
if(requestVo.getTargetId() == null) {
return JsonResultUtils.fail("Target Node Is Empty");
}
apDepartService.updateParentId(requestVo.getSourceId(), requestVo.getTargetId());
return JsonResultUtils.success();
}

//
@ApiOperation(value ="Department List")
@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@GetMapping({"/list"})
@ResponseBody
public JsonResult<List<Depart>> list() {
return JsonResultUtils.success(apDepartService.listData());
}
}
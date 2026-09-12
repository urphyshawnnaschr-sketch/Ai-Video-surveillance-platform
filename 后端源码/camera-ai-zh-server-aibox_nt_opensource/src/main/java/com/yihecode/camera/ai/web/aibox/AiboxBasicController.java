package com.yihecode.camera.ai.web.aibox;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.aibox.vo.AiboxBasicModifyVo;
import com.yihecode.camera.ai.web.aibox.vo.AiboxDeleteVo;
import com.yihecode.camera.ai.web.aibox.vo.AiboxTreeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
* Edge Box Resource Management
*/
@Api(tags = "Box Management _ Resource Management")
@RestController
@RequestMapping("/aibox/basic")
public class AiboxBasicController {

    @Resource
    private LocationService locationService;

    @Resource
    private AccountService accountService;

    @Resource
    private ApDepartService apDepartService;

    @Resource
    private CameraService cameraService;

    //
@ApiOperation(value ="Basic Info")
@SaCheckPermission(value = {"caske-add","caske-edit"}, mode = SaMode.OR)
@PostMapping(value ="/save")
public JsonResult<Void> save(@RequestBody AiboxBasicModifyVo modifyVo) {
if(StrUtil.isBlank(modifyVo.getName())) {
return JsonResultUtils.fail("Please enter Box Name");
}
if(StrUtil.isBlank(modifyVo.getSn())) {
return JsonResultUtils.fail("Please enter Box No");
}
if(modifyVo.getDepartId() == null) {
return JsonResultUtils.fail("Please select belong belong Department");
}
//
Location locationBySn = locationService.getByBoxNo(modifyVo.getSn());
Location locationByIp = locationService.getByIp(modifyVo.getIpAddr());
//
if(modifyVo.getId() == null) {
if(locationBySn!= null) {
return JsonResultUtils.fail("Box Code Exist, not can re reply Add");
}
// if(locationByIp!= null) {
// return JsonResultUtils.fail("Box IP Exist, not can re reply Add");
//}
Location location = new Location();
location.setType("2");
location.setLocationType("2");
location.setIsDef(0);
location.setSort(0);
location.setParentId(0L);
location.setName(modifyVo.getName());
location.setIpAddr(modifyVo.getIpAddr());
location.setBoxNo(modifyVo.getSn());
location.setDepartId(modifyVo.getDepartId());
locationService.save(location);
} else {
//
Location location = locationService.getById(modifyVo.getId());
if(location == null) {
return JsonResultUtils.fail("Box Info does not exist");
}
//
if(locationBySn!= null &&!locationBySn.getId().equals(modifyVo.getId())) {
return JsonResultUtils.fail("Box Code Exist, not can Modify");
}
// if(locationByIp!= null &&!locationByIp.getId().equals(modifyVo.getId())) {
// return JsonResultUtils.fail("Box IP Exist, not can Modify");
//}
//
location.setLocationType("2");
location.setType("2");
location.setName(modifyVo.getName());
location.setIpAddr(modifyVo.getIpAddr());
location.setBoxNo(modifyVo.getSn());
location.setDepartId(modifyVo.getDepartId());
locationService.saveOrUpdate(location);
}
return JsonResultUtils.success();
}

//
@ApiOperation(value ="By belong belong Organization Query Organization List")
@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
@PostMapping(value ="/tree")
public JsonResult<List<AiboxTreeVo>> tree() {
Long accountId = StpUtil.getLoginIdAsLong();
Account account = accountService.getById(accountId);
if(account == null || account.getState() == null || account.getState()!= 0) {
return JsonResultUtils.success(new ArrayList<>());
}
// Whether super Level Management member
boolean isSuper = false;
if(account.getIsSuper()!= null && account.getIsSuper() == 1) {
isSuper = true;
}
// Query belong belong Organization Department
List<Depart> departList = apDepartService.listData();
if(departList == null) {
departList = new ArrayList<>();
}
// Query All Box
List<Location> locations = locationService.listInferBox();
// will Box Mount to belong belong Organization down Surface
List<Long> hasParentIds = new ArrayList<>(); // Box Node Exist belong belong Department Box ids
List<AiboxTreeVo> treeVos = new ArrayList<>();
for(Depart depart: departList) {
for(Location location: locations) {
if(depart.getId().equals(location.getDepartId())) {
AiboxTreeVo treeVo = new AiboxTreeVo();
treeVo.setId(location.getId());
treeVo.setName(location.getName());
treeVo.setOnline(false);
treeVo.setDepart(false);
treeVo.setParentId(depart.getId());
//
if(location.getBoxHeartTime()!= null && (System.currentTimeMillis() - location.getBoxHeartTime()) < 8 * 60 * 1000) {
treeVo.setOnline(true);
}
//treeVo.setOnline(true);
treeVos.add(treeVo);
//
hasParentIds.add(location.getId());
}
}
AiboxTreeVo treeVo = new AiboxTreeVo();
treeVo.setId(depart.getId());
treeVo.setName(depart.getName());
treeVo.setOnline(true);
treeVo.setDepart(true);
treeVo.setParentId(depart.getParentId());
treeVos.add(treeVo);
}
// for at super Level Management member, will no Organization Node in Line Default Process
if(isSuper) {
// not has up Level Node Box
boolean hasNonDepNode = false; // Whether has no belong belong Node
for (Location location: locations) {
if (!hasParentIds.contains(location.getId()) &&!location.getParentId().equals(0L)) {
AiboxTreeVo treeVo = new AiboxTreeVo();
treeVo.setId(location.getId());
treeVo.setName(location.getName());
treeVo.setOnline(true);
treeVo.setDepart(true);
treeVo.setParentId(9999L);
//
if (location.getBoxHeartTime()!= null && (System.currentTimeMillis() - location.getBoxHeartTime()) < 8 * 60 * 1000) {
treeVo.setOnline(true);
}
//treeVo.setOnline(true);
treeVos.add(treeVo);
hasNonDepNode = true;
}
}
// no Organization Box Add Default Node
if (hasNonDepNode) {
AiboxTreeVo treeVo = new AiboxTreeVo();
treeVo.setId(9999L);
treeVo.setName("no belong belong Organization");
treeVo.setOnline(true);
treeVo.setDepart(true);
treeVo.setParentId(0L);
treeVos.add(treeVo);
}
}
// In Build Complete treeVos List after, Add Sort Logic
treeVos.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

// turn out tree result structure Before in Line Sort
Long rootParentId = isSuper? 0L: (account.getDepartId() == null? 55555L: account.getDepartId());
List<AiboxTreeVo> tree = new ArrayList<>();

// Sort
treeVos.sort(Comparator.comparing(AiboxTreeVo::getName));
if(rootParentId == 0) {
for (AiboxTreeVo treeVo: treeVos) {
if (treeVo.getParentId().equals(rootParentId)) {
tree.add(findChildren(treeVo, treeVos));
}
}
return JsonResultUtils.success(tree);
} else {
for (AiboxTreeVo treeVo: treeVos) {
if (treeVo.getId().equals(rootParentId)) {
tree.add(findChildren(treeVo, treeVos));
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
public AiboxTreeVo findChildren(AiboxTreeVo tree, List<AiboxTreeVo> treeList) {
for (AiboxTreeVo node: treeList) {
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
@ApiOperation(value ="Delete Box")
@SaCheckPermission(value = {"caske-delete"}, mode = SaMode.OR)
@PostMapping(value ="/delete")
public JsonResult<Void> delete(@RequestBody AiboxDeleteVo deleteVo) {
if(deleteVo.getId() == null) {
return JsonResultUtils.fail("Please select need Delete Data");
}
Location location = locationService.getById(deleteVo.getId());
if(location == null) {
return JsonResultUtils.fail("Data Not Exist or Deleted");
}

//
List<Camera> cameras = cameraService.listByBoxId(deleteVo.getId());
if(cameras!= null &&!cameras.isEmpty()) {
return JsonResultUtils.fail("not can Delete, the Device Relate Camera Point Bit");
}

//
List<Location> subLocations = locationService.listSubLocations(deleteVo.getId());
if(subLocations!= null &&!subLocations.isEmpty()) {
return JsonResultUtils.fail("not can Delete, the Device Contain child Node");
}

//
locationService.removeById(deleteVo.getId());

return JsonResultUtils.success();
}
}

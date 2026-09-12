package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.service.CameraGroupService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.dto.CameraGroupTreeDTO;
import com.yihecode.camera.ai.web.vo.IdVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
* Camera Group
*
* @author zhou
* @since 2025.6.16
*/
@Api(tags = "Camera _ Group Management")
@RestController
@RequestMapping("camera/group")
public class CameraGroupController {

    @Autowired
    private CameraGroupService cameraGroupService;

    /**
* Tree result structure
* @return
*/
    @ApiOperation(value = "Data List _Tree List")
    @SaCheckPermission(value = {"edgePlatform-groupView", "faceControl-faceHistory", "edgePlatform-videoPreview"}, mode = SaMode.OR)
    @PostMapping("tree")
    public JsonResult<List<CameraGroupTreeDTO>> tree() {
        List<CameraGroup> cameraGroupList = cameraGroupService.listAll();
        Map<Long, CameraGroupTreeDTO> nodeMap = cameraGroupList.stream().collect(Collectors.toMap(CameraGroup::getId, CameraGroupTreeDTO::new));

        List<CameraGroupTreeDTO> rootNodes = new ArrayList<>();

        //No One Step: find out All Root Node
for (CameraGroupTreeDTO node: nodeMap.values()) {
if (node.getParentId() == null || node.getParentId() == 0) {
node.setLevel(1);
rootNodes.add(node);
}
}

// No Two Step: make Use Queue in Line Wide Degree Optimize First Iterate, Ensure parent Node First by Process
Queue<CameraGroupTreeDTO> queue = new LinkedList<>(rootNodes);

while (!queue.isEmpty()) {
CameraGroupTreeDTO currentNode = queue.poll();
Long currentId = currentNode.getId();

// check find Current Node All child Node
for (CameraGroupTreeDTO childNode: nodeMap.values()) {
if (currentId.equals(childNode.getParentId())) {
currentNode.addChild(childNode);
childNode.setLevel(currentNode.getLevel() + 1);
queue.offer(childNode); // will child Node Add in Queue Continue Process its child Node
}
}
}

return JsonResultUtils.success(rootNodes);
}



@ApiOperation(value ="Delete Add / Edit")
@SaCheckPermission("edgePlatform-groupView")
@PostMapping("save")
public JsonResult<Long> save(@RequestBody CameraGroup cameraGroup) {
if(StrUtil.isBlank(cameraGroup.getName())) {
return JsonResultUtils.fail("Group Name Name Required");
}
if(cameraGroup.getParentId() == null) {
return JsonResultUtils.fail("up Level Group must select");
}
if(cameraGroup.getSort() == null) {
cameraGroup.setSort(1);
}

// Hierarchy
if(cameraGroup.getParentId() == null || cameraGroup.getParentId() == 0) {
cameraGroup.setLevel(1);
} else {
CameraGroup parentGroup = cameraGroupService.getById(cameraGroup.getParentId());
if(parentGroup == null) {
return JsonResultUtils.fail("up Level Group does not exist");
}
cameraGroup.setLevel(parentGroup.getLevel() + 1);
}

// Add / Edit
cameraGroupService.saveOrUpdate(cameraGroup);
return JsonResultUtils.success(cameraGroup.getId());
}

@ApiOperation(value ="Group Delete")
@SaCheckPermission("edgePlatform-groupView")
@PostMapping("delete")
public JsonResult<Long> delete(@RequestBody IdVo idVo) {
List<CameraGroup> cameraGroupList = cameraGroupService.list();

// Pending Delete ID
List<Long> removeIds = new ArrayList<>();
removeIds.add(idVo.getId());

// parent Level ID
List<Long> parentIds = new ArrayList<>();
parentIds.add(idVo.getId());

while(true) {
List<Long> subIds = new ArrayList<>();
for (CameraGroup cameraGroup: cameraGroupList) {
if (cameraGroup.getParentId()!= null && parentIds.contains(cameraGroup.getParentId())) {
subIds.add(cameraGroup.getId());
removeIds.add(cameraGroup.getId());
}
}
if(subIds.isEmpty()) {
break;
}
parentIds.clear();
parentIds.addAll(subIds);
}

cameraGroupService.removeBatch(removeIds);
return JsonResultUtils.success(idVo.getId());
}

@ApiOperation(value ="Group Detail")
@SaCheckPermission("edgePlatform-groupView")
@PostMapping("info")
public JsonResult<CameraGroup> info(@RequestBody IdVo idVo) {
CameraGroup cameraGroup = cameraGroupService.getById(idVo.getId());
return JsonResultUtils.success(cameraGroup);
}
}

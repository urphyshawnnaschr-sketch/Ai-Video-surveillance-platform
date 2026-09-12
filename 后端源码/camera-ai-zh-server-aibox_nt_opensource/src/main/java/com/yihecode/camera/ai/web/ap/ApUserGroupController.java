package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.dto.ApGroupQueryDTO;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ApProjectQueryDTO;
import com.yihecode.camera.ai.dto.ApUserGroupDTO;
import com.yihecode.camera.ai.service.ap.ApUserGroupService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@Api(tags = "Annotation group Management")
@SaCheckLogin
@Controller
@RequestMapping({"/ap/userGroup"})
public class ApUserGroupController {

    @Autowired
    private ApUserGroupService apUserGroupService;

    @ApiOperation(value = "Query Annotation group List")
    @SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
    @GetMapping({"/list"})
    @ResponseBody
    public JsonResult<List<ApUserGroupDTO>> list(ApGroupQueryDTO groupQueryDTO) {
        return JsonResultUtils.success(apUserGroupService.findGroupList(groupQueryDTO));
    }

    @ApiOperation(value = "Annotation group List Pagination")
    @SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
    @GetMapping({"/listPage"})
    @ResponseBody
    public PageResult listPage(ApGroupQueryDTO groupQueryDTO) {
        IPage<ApUserGroupDTO> apGroupDTOIPage = apUserGroupService.findGroupPage(groupQueryDTO);
        return PageResultUtils.success(apGroupDTOIPage.getTotal(), apGroupDTOIPage.getRecords());
    }

    @ApiOperation(value = "Save Annotation group")
    @SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult<Long> save(@RequestBody ApUserGroupDTO groupDTO) {
        return JsonResultUtils.success(apUserGroupService.saveGroup(groupDTO));
    }

    @ApiOperation(value = "Modify Annotation group")
    @SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
    @PostMapping({"/update"})
    @ResponseBody
    public JsonResult<ApProjectDTO> update(@RequestBody ApUserGroupDTO groupDTO) {
        apUserGroupService.updateGroupById(groupDTO);
        return JsonResultUtils.success(null);
    }

    @ApiOperation(value = "Delete Annotation group")
    @SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
    @DeleteMapping({"/delete"})
    //@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="projectId", value ="Annotation group id")
//})
@ResponseBody
public JsonResult delete(@RequestParam("groupId") Long groupId) {
System.out.println(groupId);
apUserGroupService.deleteGroupById(groupId);
return JsonResultUtils.success();
}

@ApiOperation(value ="Add Complete member")
@SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
@PostMapping({"/addUser/{groupId}"})
@ResponseBody
public JsonResult<Integer> addUser(@PathVariable Long groupId, @RequestBody JSONArray userIds) {
return JsonResultUtils.success(apUserGroupService.addGroupUser(groupId, userIds));
}

@ApiOperation(value ="Delete Complete member")
@SaCheckPermission(value = {"apmgr-group"}, mode = SaMode.OR)
@PostMapping({"/deleteUser/{groupId}"})
@ResponseBody
public JsonResult<Integer> deleteUser(@PathVariable Long groupId,@RequestBody JSONArray userIds) {
return JsonResultUtils.success(apUserGroupService.deleteGroupUser(groupId, userIds));
}

}

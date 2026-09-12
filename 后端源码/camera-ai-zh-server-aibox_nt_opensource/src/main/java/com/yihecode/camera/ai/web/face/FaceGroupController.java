package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.face.FaceGroup;
import com.yihecode.camera.ai.service.face.FaceGroupService;
import com.yihecode.camera.ai.service.face.FaceReportService;
import com.yihecode.camera.ai.service.face.FaceUserService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* Face Group - Face Recognition child Module
*/
@Api(tags = "Face Recognition _ Group Management")
@SaCheckLogin
@RestController
@RequestMapping("/face/group")
public class FaceGroupController {

    @Resource
    private FaceGroupService faceGroupService;

    @Resource
    private FaceUserService faceUserService;

    @Resource
    private FaceReportService faceReportService;

    @ApiOperation("Group Detail")
    @ApiImplicitParam(name = "id", value = "Group ID")
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @GetMapping({"/detail"})
    @ResponseBody
    public JsonResult<FaceGroup> detail(Long id) {
        return JsonResultUtils.success(faceGroupService.getById(id));
    }

    @ApiOperation("Group Save")
    @SaCheckPermission(value = {"face-group"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult<Long> save(@RequestBody FaceGroup faceGroup) {
        if(StrUtil.isBlank(faceGroup.getName())) {
            return JsonResultUtils.fail("Please enter Group Name Name");
        }
        faceGroupService.saveOrUpdate(faceGroup);
        return JsonResultUtils.success(faceGroup.getId());
    }

    @ApiOperation("Group Delete")
    @ApiImplicitParam(name = "id", value = "Group ID")
    @SaCheckPermission(value = {"face-group"}, mode = SaMode.OR)
    @PostMapping({"/delete"})
    @ResponseBody
    public JsonResult<Long> delete(Long id) {
        faceGroupService.removeById(id);
        faceUserService.deleteByGroup(id);
        return JsonResultUtils.success(id);
    }

    @ApiOperation("Group Query")
    @SaCheckPermission(value = {"face-group", "face-view", "faceControl-faceRecognition", "faceControl-faceHistory", "faceControl-faceManagent"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public JsonResult<List<FaceGroup>> listData() {
        List<FaceGroup> faceGroupList = faceGroupService.list();
        if(faceGroupList == null) {
            faceGroupList = new ArrayList<>();
        }

        //
List<FaceGroup> resultList = new ArrayList<>();
FaceGroup allGroup = new FaceGroup();
allGroup.setName("total Plan");
allGroup.setUserCount(faceReportService.countByGroupId(null));
resultList.add(allGroup);
for(FaceGroup faceGroup: faceGroupList) {
faceGroup.setUserCount(faceReportService.countByGroupId(faceGroup.getId()));
resultList.add(faceGroup);
}
return JsonResultUtils.success(resultList);
}
}

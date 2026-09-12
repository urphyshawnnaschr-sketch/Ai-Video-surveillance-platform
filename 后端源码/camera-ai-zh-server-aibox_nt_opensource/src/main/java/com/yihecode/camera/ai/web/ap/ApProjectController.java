package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ApProjectQueryDTO;
import com.yihecode.camera.ai.dto.ApProjectStatisticsQueryDTO;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.ap.ApProjectService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.vo.ap.*;
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
@Api(tags = "Project Management")
@SaCheckLogin
@Controller
@RequestMapping({"/ap/project"})
public class ApProjectController {

    @Autowired
    private ApProjectService apProjectService;

    @ApiOperation(value = "Save Project")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult<Long> save(@RequestBody ApProjectDTO projectDTO) throws BizException {
        return JsonResultUtils.success(
                apProjectService.savepProject(projectDTO));
    }

    @ApiOperation(value = "Modify Project")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @PostMapping({"/update"})
    @ResponseBody
    public JsonResult<ApProjectDTO> update(@RequestBody ApProjectDTO projectDTO) throws BizException {
        apProjectService.updatepProjectById(projectDTO);
        return JsonResultUtils.success(null);
    }

    @ApiOperation(value = "By id Project Detail")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @GetMapping({"/getDetail"})
    @ResponseBody
    public JsonResult<ApProjectDTO> getDeatilById(@RequestParam(value = "id", defaultValue = "0") Long id) {

        return JsonResultUtils.success(apProjectService.getpProjectById(id));
    }

    @ApiOperation(value = "Project Deliver")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @PostMapping({"/submit"})
    //@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="projectId", value ="Project id")
//})
@ResponseBody
public JsonResult submit(@RequestParam("projectId") Long projectId) throws BizException {
apProjectService.submitProject(projectId);
return JsonResultUtils.success(null);
}

@ApiOperation(value ="Query Project List")
@SaCheckPermission(value = {"apmgr-summary"}, mode = SaMode.OR)
@GetMapping({"/list"})
@ResponseBody
public JsonResult<List<ApProjectDTO>> list(ApProjectQueryDTO projectQueryDTO) {

return JsonResultUtils.success(apProjectService.findpProjectList(projectQueryDTO));
}

@ApiOperation(value ="Project List Pagination")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/listPage"})
@ResponseBody
public PageResult<List<ApProjectDTO>> listPage(ApProjectQueryDTO projectQueryDTO) {
IPage<ApProjectDTO> apProjectDTOIPage = apProjectService.findpProjectPage(projectQueryDTO);
return PageResultUtils.success(apProjectDTOIPage.getTotal(), apProjectDTOIPage.getRecords());
}

@ApiOperation(value ="Delete Project")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@DeleteMapping({"/delete"})
@ResponseBody
public JsonResult delete(@RequestParam("projectId") Long projectId) {
apProjectService.deletepProjectById(projectId);

// todo increase Quantity Training Project not Annotation just Delete Image need Resume Complete can Create

return JsonResultUtils.success();
}

@ApiOperation(value ="Get down One id, Upload File Before Get One down Primary Key id, transmit File Hour Need Belt up this id, has Primary Key Situation down, not Need Request the API")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/getNextId"})
@ResponseBody
public JsonResult getNextId() {
return JsonResultUtils.success(apProjectService.getNextId());
}

@ApiOperation(value ="Project Count: Annotation")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/compute/annotation"})
//@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="projectId", value ="Project id"),
//@ApiImplicitParam(name ="type", value ="1 whole Body,2 Person")
//})
@ResponseBody
public JsonResult<ComputeAnnotationVO> computeAnnotation(@RequestParam(value ="projectId", required = true, defaultValue ="0") Long projectId, @RequestParam(value ="type", required = true) Integer type) {

return JsonResultUtils.success(apProjectService.computeAnnotation(projectId, type));
}

@ApiOperation(value ="Project Count: Annotation Label than Example")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/compute/lable/annotation"})
//@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="projectId", value ="Project id")
//})
@ResponseBody
public JsonResult<List<ComputeAnnotationLableVO>> computeAnnotationLableList(@RequestParam(value ="projectId", required = true, defaultValue ="0") Long projectId) {

return JsonResultUtils.success(apProjectService.computeAnnotationLableList(projectId));
}
@ApiOperation(value ="Project Count: Quality Check Count")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/compute/annotation/review"})
//@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="projectId", value ="Project id")
//})
@ResponseBody
public JsonResult<ComputeAnnotationReviewVO> annotationReview(@RequestParam(value ="projectId", required = true, defaultValue ="0") Long projectId) {

return JsonResultUtils.success(apProjectService.annotationReview(projectId));
}

@ApiOperation(value ="Project Count: Project Dimension Degree Polyline image")
@SaCheckPermission(value = {"apmgr-summary"}, mode = SaMode.OR)
@GetMapping({"/compute/project/efficiency"})
@ResponseBody
public JsonResult<ProjectEfficiencyVO> projectEfficiency(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {

return JsonResultUtils.success(apProjectService.projectEfficiency(apProjectStatisticsQueryDTO));
}

@ApiOperation(value ="Project Count: Project Latitude Pagination Data")
@SaCheckPermission(value = {"apmgr-summary"}, mode = SaMode.OR)
@GetMapping({"/compute/project/statistics"})
@ResponseBody
public PageResult<List<ProjectStatisticsVO>> userStatistics(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {

IPage<ProjectStatisticsVO> projectEfficiencyVOIPage = apProjectService.projectStatistics(apProjectStatisticsQueryDTO);
return PageResultUtils.success(projectEfficiencyVOIPage.getTotal(), projectEfficiencyVOIPage.getRecords());
}

@ApiOperation(value ="Project Count: Team Dimension Degree Polyline image")
@SaCheckPermission(value = {"apmgr-summary"}, mode = SaMode.OR)
@GetMapping({"/compute/team/efficiency"})
@ResponseBody
public JsonResult<ProjectEfficiencyVO> teamEfficiency(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {

return JsonResultUtils.success(apProjectService.teamEfficiency(apProjectStatisticsQueryDTO));
}
@ApiOperation(value ="Project Count: Team Latitude Pagination Data")
@SaCheckPermission(value = {"apmgr-summary"}, mode = SaMode.OR)
@GetMapping({"/compute/team/statistics"})
@ResponseBody
public PageResult<List<ProjectStatisticsVO>> projectStatistics(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {
IPage<ProjectStatisticsVO> projectEfficiencyVOIPage = apProjectService.teamStatistics(apProjectStatisticsQueryDTO);
return PageResultUtils.success(projectEfficiencyVOIPage.getTotal(), projectEfficiencyVOIPage.getRecords());
}


}

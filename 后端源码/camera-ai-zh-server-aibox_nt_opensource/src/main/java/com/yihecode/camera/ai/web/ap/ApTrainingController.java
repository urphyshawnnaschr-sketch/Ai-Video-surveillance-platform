package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.ap.ApTrainingService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.web.ap.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@ApiIgnore
@Api(tags = "Annotation Platform - increase Quantity Training")
@SaCheckLogin
@Controller
@RequestMapping({ "/ap/training" })
public class ApTrainingController {

    @Autowired
    private ApTrainingService apTrainingService;

    @ApiOperation(value = "List API")
    @SaCheckPermission(value = {"apmgr-train"}, mode = SaMode.OR)
    @GetMapping({ "/list" })
    @ResponseBody
    public PageResult<List<ReportAggVo>> list(TrainingListRequestVo request) {
        if  (request.getPage() == null || request.getPage() <= 0) {
            request.setPage(1);
        }
        if  (request.getLimit() == null || request.getLimit() <= 0) {
            request.setLimit(20);
        }

        //todo Menu Permission

int total = apTrainingService.count(request);
if (total == 0) {
return PageResultUtils.success(0L, new ArrayList<>());
}
List<ReportAggVo> reportList = apTrainingService.list(request);

return PageResultUtils.success((long) total, reportList);
}

@ApiOperation(value ="Preview Image")
@SaCheckPermission(value = {"apmgr-train"}, mode = SaMode.OR)
@PostMapping({"/preview"})
@ResponseBody
public PageResult<List<Report>> preview(@RequestBody TrainingPreviewRequestVo request) throws BizException {
String errMsg = checkParam(request.getTrainingBaseVo());
if (StringUtils.isNotEmpty(errMsg)) {
return PageResultUtils.fail(errMsg);
}

if (request.getPage() == null || request.getPage() <= 0) {
request.setPage(1);
}
if (request.getLimit() == null || request.getLimit() <= 0) {
request.setLimit(20);
}

// todo Menu Permission

Long[] range = apTrainingService.range(request.getTrainingBaseVo());
int total = apTrainingService.count(request.getTrainingBaseVo(), range);
if (total == 0) {
return PageResultUtils.success(0L, new ArrayList<>());
}
List<Report> reportList = apTrainingService.list(request.getTrainingBaseVo(), range, request.getPage(), request.getLimit());

return PageResultUtils.success((long) total, reportList);
}

@ApiOperation(value ="Create Annotation Project")
@SaCheckPermission(value = {"apmgr-train"}, mode = SaMode.OR)
@PostMapping({"/create"})
@ResponseBody
public JsonResult<Long> createProject(@RequestBody TrainingCreateProjectRequestVo request) throws Exception {
String errMsg = checkParam(request.getTrainingBaseVo());
if (StringUtils.isNotEmpty(errMsg)) {
return JsonResultUtils.fail(errMsg);
}
// todo Project Param Validation
// todo Permission Validation
// todo Add Lock

Long[] range = apTrainingService.range(request.getTrainingBaseVo());
List<Report> reportList = apTrainingService.list(request.getTrainingBaseVo(), range, null, null);
Long projectId = apTrainingService.createProject(request, reportList);

// Delete not Extract in Image
apTrainingService.delete(request.getTrainingBaseVo(), range);

return JsonResultUtils.success(projectId);
}

@ApiOperation(value ="Delete Data")
@SaCheckPermission(value = {"apmgr-train"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult<Integer> delete(@RequestBody TrainingDeleteRequestVo request) {
String errMsg = checkParam(request.getTrainingBaseVo());
if (StringUtils.isNotEmpty(errMsg)) {
return JsonResultUtils.fail(errMsg);
}

// todo Permission Validation
// todo Add Lock

Integer num = apTrainingService.delete(request);

return JsonResultUtils.success(num);
}

private String checkParam(TrainingBaseVo trainingBaseVo) {
if (trainingBaseVo == null) {
return"Param Is Empty";
}
if (trainingBaseVo.getAlgorithmId() == null || trainingBaseVo.getAlgorithmId() <= 0) {
return"Please select Algorithm";
}
if (trainingBaseVo.getCameraId() == null || trainingBaseVo.getCameraId() <= 0) {
return"Please select Camera";
}
if (trainingBaseVo.getInterval() == null || trainingBaseVo.getInterval() <= 0) {
trainingBaseVo.setInterval(1);
}

if (trainingBaseVo.getBegin() == null || trainingBaseVo.getBegin() <= 0) {
trainingBaseVo.setBegin(1);
}
if (trainingBaseVo.getEnd() == null || trainingBaseVo.getEnd() <= 0) {
trainingBaseVo.setEnd(10);
}

if (trainingBaseVo.getBegin() > trainingBaseVo.getEnd()) {
return"Please select combine Fit Range";
}

return"";
}
}

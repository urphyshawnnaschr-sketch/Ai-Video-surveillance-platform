package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraAlgorithm;
import com.yihecode.camera.ai.entity.ReportPeriod;
import com.yihecode.camera.ai.enums.CameraAction;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.CameraAlgorithmService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.ReportPeriodService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import com.yihecode.camera.ai.vo.ReportPeriodVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Alert Hour Segment Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Alert Hour Segment Management")
@SaCheckLogin
@Slf4j
@Controller
@RequestMapping({"/report/period"})
public class ReportPeriodController {

    //
@Autowired
private CameraService cameraService;

//
@Autowired
private AlgorithmService algorithmService;

//
@Autowired
private ReportPeriodService reportPeriodService;

//
@Autowired
private CameraAlgorithmService cameraAlgorithmService;

/**
*
* @return
*/
@ApiOperation("Query Algorithm List")
@ApiImplicitParam(name ="cameraId", value ="Camera id")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/algorithms"})
@ResponseBody
public JsonResult<List<Algorithm>> listAlgorithms(Long cameraId) {
if(cameraId == null) {
return JsonResultUtils.success(new ArrayList<>());
}

//
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(cameraId);

//
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}

//
List<Algorithm> subList = new ArrayList<>();
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
for(Algorithm algorithm: algorithmList) {
if(algorithm.getId().equals(cameraAlgorithm.getAlgorithmId())) {
subList.add(algorithm);
break;
}
}
}
return JsonResultUtils.success(subList);
}

/**
*
* @return
*/
@ApiOperation("Query Data List")
@ApiImplicitParam(name ="cameraId", value ="Camera id")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/listData"})
@ResponseBody
public PageResult<List<ReportPeriodVo>> listData(Long cameraId) {
if(cameraId == null) {
return PageResultUtils.success(null, new ArrayList());
}

Camera camera = cameraService.getById(cameraId);
if(camera == null) {
return PageResultUtils.success(null, new ArrayList());
}

//
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}

//
Map<Long, String> algorithmNames = new HashMap<>();
for(Algorithm algorithm: algorithmList) {
algorithmNames.put(algorithm.getId(), algorithm.getName());
}

//
List<ReportPeriodVo> dataList = new ArrayList<>();

//
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByCamera(camera.getId());
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
//
String algorithmName = algorithmNames.get(cameraAlgorithm.getAlgorithmId());
if(algorithmName == null) {
continue;
}

//
List<ReportPeriod> reportPeriodList = reportPeriodService.listData(camera.getId(), cameraAlgorithm.getAlgorithmId());
for(ReportPeriod reportPeriod: reportPeriodList) {
//
ReportPeriodVo vo = new ReportPeriodVo();
vo.setId(reportPeriod.getId());
vo.setCameraId(reportPeriod.getCameraId());
vo.setCameraName(camera.getName());
vo.setAlgorithmId(reportPeriod.getAlgorithmId());
vo.setAlgorithmName(algorithmName);
vo.setPeriod(reportPeriod.getStartText() +"-"+ reportPeriod.getEndText());
dataList.add(vo);
}
}
return PageResultUtils.success(null, dataList);
}

/**
*
* @param reportPeriod
* @return
*/
@ApiOperation("Save Hour Segment Data")
@ApiImplicitParam(name ="reportPeriod", value ="Hour Segment Entity")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/save"})
@ResponseBody
public JsonResult<Void> save(ReportPeriod reportPeriod) throws Exception {
if(reportPeriod.getCameraId() == null) {
return JsonResultUtils.fail("Please select Camera");
}

if(reportPeriod.getAlgorithmId() == null) {
return JsonResultUtils.fail("Please select Algorithm");
}

if(StrUtil.isBlank(reportPeriod.getStartText())) {
return JsonResultUtils.fail("Please select Start Hour Point");
}

if(StrUtil.isBlank(reportPeriod.getEndText())) {
return JsonResultUtils.fail("Please select End Hour Point");
}

//
Integer startTime = Integer.valueOf(reportPeriod.getStartText().replaceAll(":",""));
Integer endTime = Integer.valueOf(reportPeriod.getEndText().replaceAll(":",""));
if(endTime <= startTime) {
return JsonResultUtils.fail("Start Hour Point must be less than End Hour Point");
}
//
reportPeriod.setStartTime(startTime);
reportPeriod.setEndTime(endTime);
reportPeriodService.saveData(reportPeriod);
return JsonResultUtils.success();
}

/**
*
* @param id
* @return
*/
@ApiOperation("Delete Hour Segment Entity")
@SaCheckPermission("XXXXXXX")
@ApiImplicitParam(name ="id", value ="Data id")
@RequestMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) {
//
ReportPeriod reportPeriod = reportPeriodService.getById(id);

//
reportPeriodService.removeById(id);

//
// if(reportPeriod!= null && reportPeriod.getCameraId()!= null) {
// cameraService.updateActionByCamera(reportPeriod.getCameraId(), CameraAction.ACTION_UPD.getType());
//}
return JsonResultUtils.success();
}


/**
*
* @return
*/
@ApiOperation("Query Data List")
@ApiImplicitParam(name ="cameraId", value ="Camera id")
@SaCheckPermission("XXXXXXX")
@PostMapping({"/list"})
@ResponseBody
public JsonResult<List<ReportPeriod>> list(Long cameraId, Long algorithmId) {
List<ReportPeriod> reportPeriodList = reportPeriodService.listData(cameraId, algorithmId);
if(reportPeriodList == null) {
reportPeriodList = new ArrayList<>();
}
return JsonResultUtils.success(reportPeriodList);
}
}

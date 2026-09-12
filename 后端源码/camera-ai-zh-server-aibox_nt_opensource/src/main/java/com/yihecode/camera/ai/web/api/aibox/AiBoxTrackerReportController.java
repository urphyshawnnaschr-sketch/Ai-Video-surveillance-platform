package com.yihecode.camera.ai.web.api.aibox;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.tracker.TrackerReport;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.tracker.TrackerReportService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.TrackerReportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.*;

/**
* Alert Push Data, Integrate Algorithm Push Alert Data
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiIgnore
@SaIgnore
@Api(tags = "Edge Box Alarm Report Management")
@Slf4j
@RestController
@RequestMapping({ "/api/aibox/tracker/report" })
public class AiBoxTrackerReportController {

    @Resource
    private CameraService cameraService;

    @Resource
    private AlgorithmService algorithmService;

    @Resource
    private ConfigService configService;

    @Resource
    private LocationService locationService;

    @Resource
    private TrackerReportService trackerReportService;

    @Resource
    private ApiTrackerReportThirdPushService apiTrackerReportThirdPushService;

    @ApiOperation("Edge Box Alarm Report")
    @PostMapping({ "", "/" })
    public JsonResult<?> report(@RequestBody TrackerReportVo trackerReportVo) {
        try {
            //
if (trackerReportVo.getCameraId() == null) {
log.error("Edge Box Report Person Stream Quantity Data Exception: cameraId Value Error {}", trackerReportVo);
return JsonResultUtils.fail("Camera Param Is Empty");
}
// Query Camera
Camera camera = cameraService.getById(trackerReportVo.getCameraId());
if (camera == null) {
log.error("Edge Box Report Person Stream Quantity Data Exception: Camera does not exist {}", trackerReportVo.getCameraId());
return JsonResultUtils.fail("find not to Camera");
}
// Query Box Info
Location location = locationService.getById(camera.getLocationId());
// Alert Time
Date reportTime = new Date();
/* remove 2024-10-11 First Note Sell
if (StrUtil.isNotBlank(trackerReportVo.getTimestamp())) {
try {
reportTime = DateUtil.parse(trackerReportVo.getTimestamp(),"yyyy-MM-dd HH:mm:ss");
} catch (Exception e) {
}
}

*/
// Save Person Stream Quantity Data
TrackerReport trackerReport = new TrackerReport();
BeanUtils.copyProperties(trackerReportVo, trackerReport);
trackerReport.setCreatedAt(reportTime);
trackerReport.setCreatedMills(reportTime.getTime());
trackerReport.setBoxId(camera.getLocationId());
trackerReport.setDepartId((location == null || location.getDepartId() == null)? 0L: location.getDepartId());
trackerReportService.save(trackerReport);

// Push Third Party
sendToThrid(camera, location, trackerReport);

return JsonResultUtils.success();
} catch (Exception e) {
log.error("Edge Box Report Person Stream Quantity Data Exception: {}", e.getMessage());
return JsonResultUtils.fail("Person Stream Quantity Alarm Report Exception @"+ e.getMessage());
}
}

/**
* Send to Third Party
*/
private void sendToThrid(Camera camera, Location location, TrackerReport trackerReport) {
String reportPushUrl = configService.getByValTag("reportPushUrl");
if (StrUtil.isNotBlank(reportPushUrl)) {
// Determine Algorithm Whether Check select
Algorithm algorithm = algorithmService.getByNameEn("peopleTrack");
//if(algorithm == null || algorithm.getPushEnable() == null || algorithm.getPushEnable()!= 1) {
if(algorithm == null) {
// log.error("Person Stream Push Error, Person Stream Quantity Algorithm not Config, or not Enable Push");
return;
}

//
try {
JSONObject reportMap = new JSONObject();
reportMap.put("pushType","track");
reportMap.put("cameraId", String.valueOf(camera.getId()));
reportMap.put("cameraName", camera.getName());
reportMap.put("rtspUrl", camera.getRtspUrl());
reportMap.put("algorithmId", String.valueOf(algorithm.getId()));
reportMap.put("algorithmName", algorithm.getName());
reportMap.put("algorithmNameEn", algorithm.getNameEn());
reportMap.put("alarmAt", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
reportMap.put("boxSn", (location == null || location.getBoxNo() == null)?"": location.getBoxNo());
reportMap.put("boxId", location == null?"0": String.valueOf(location.getId()));
reportMap.put("boxIpAddr", (location == null || location.getIpAddr() == null)?"": location.getIpAddr());
reportMap.put("boxName", (location == null || location.getName() == null)?"": location.getName());
reportMap.put("alarmCount", 1);
reportMap.put("enterCount", trackerReport.getEnterCount());
reportMap.put("leaveCount", trackerReport.getLeaveCount());

apiTrackerReportThirdPushService.push(reportPushUrl, reportMap);
} catch (Exception e) {
log.error("Person Stream Quantity Third Party Push Exception: {}", e.getMessage());
}
}
}

/**
* Send to Third Party
*/
private void sendToThridHuawei(Camera camera, Location location, TrackerReport trackerReport) {
String reportPushUrl = configService.getByValTag("reportPushUrl");
if (StrUtil.isNotBlank(reportPushUrl)) {
Algorithm algorithm = algorithmService.getByNameEn("peopleTrack");
if(algorithm == null) {
// log.error("Person Stream Push Error, Person Stream Quantity Algorithm not Config, or not Enable Push");
return;
}

//
try {
JSONObject reportMap = new JSONObject();
reportMap.put("pushType","track");
reportMap.put("transId", String.valueOf(trackerReport.getId()));
reportMap.put("enteringNumber", String.valueOf(trackerReport.getEnterCount()));
reportMap.put("leavingNumber", String.valueOf(trackerReport.getLeaveCount()));
reportMap.put("stsDate", DateUtil.format(new Date(),"yyyy/MM/dd HH:mm:ss"));
log.info("Person Stream Quantity Third Party Push Exception, Fixed make Change huawei, Request Data {}", reportMap);
apiTrackerReportThirdPushService.push(reportPushUrl, reportMap);
} catch (Exception e) {
log.error("Person Stream Quantity Third Party Push Exception, Fixed make Change huawei: {}", e.getMessage());
}
}
}

// public static void main(String[] args) {
// JSONObject reportMap = new JSONObject(new LinkedHashMap());
// reportMap.put("pushType","track");
// reportMap.put("cameraId", String.valueOf(1845678841293451266L));
// reportMap.put("cameraName","big Doorway");
// reportMap.put("rtspUrl","rtsp://admin:123456@192.168.0.100/Streaming/Channels/101");
// reportMap.put("algorithmId", String.valueOf(1696809711436365855L));
// reportMap.put("algorithmName","Person Stream Quantity Detection");
// reportMap.put("algorithmNameEn","peopleTrack");
// reportMap.put("alarmAt", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
// reportMap.put("boxSn","d6010341334135364310857356921b25");
// reportMap.put("boxId","1");
// reportMap.put("boxIpAddr","192.168.1.127");
// reportMap.put("boxName","chaoxing");
// reportMap.put("alarmCount", 1);
// reportMap.put("enterCount", 29);
// reportMap.put("leaveCount", 12);
// System.out.println(JSONObject.toJSONString(reportMap));
//}
}


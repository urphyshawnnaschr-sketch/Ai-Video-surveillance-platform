//package com.yihecode.camera.ai.web.api;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.http.HttpUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.yihecode.camera.ai.entity.Camera;
//import com.yihecode.camera.ai.entity.Report;
//import com.yihecode.camera.ai.enums.ReportType;
//import com.yihecode.camera.ai.service.*;
//import com.yihecode.camera.ai.utils.JsonResult;
//import com.yihecode.camera.ai.utils.JsonResultUtils;
//
//import java.net.URLEncoder;
//import java.util.Date;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import springfox.documentation.annotations.ApiIgnore;
//
///**
// * Video Stream Alert Push, Integrate Algorithm will Break open Camera in Line Alert
// * Algorithm via not again will Break open Camera Make for Alert Push to in Station
// *
// * @author zhoumingxing
// * @mail 465769438@qq.com
// */
//@ApiIgnore
////@Deprecated
//@Controller
//@RequestMapping({"/api/stream"})
//public class StreamApiController {
// private static final Logger log = LoggerFactory.getLogger(StreamApiController.class);
//
// @Autowired
// private ReportService reportService;
//
// @Autowired
// private CameraService cameraService;
//
// @Autowired
// private ConfigService configService;
//
//
// /**
// *
// * @param cameraId
// * @param state
// * @return
// */
// @RequestMapping({"","/"})
// @ResponseBody
// public JsonResult stream(@RequestParam(value ="camera_id", required = false) Long cameraId,
// @RequestParam(value ="state", required = false) Integer state) {
// log.info("stream alarm: camera_id: {}, state: {}", cameraId, state);
// try {
// if (cameraId == null) {
// return JsonResultUtils.fail("the camera_id parameter is null");
//}
//
// if (state == null) {
// return JsonResultUtils.fail("the state@(must 0 or 1) parameter is null");
//}
//
// //
// Camera camera = cameraService.getById(cameraId);
// if(camera == null) {
// return JsonResultUtils.fail("the camera is not found");
//}
//
// //
// if (state >= 1) {
// Report report = new Report();
// report.setCameraId(cameraId);
// report.setAlgorithmId(0L);
// report.setFileName("");
// report.setParams("");
// report.setType(ReportType.STREAM.getType());
// report.setCreatedAt(new Date());
// report.setCreatedMills(System.currentTimeMillis());
// report.setDisplay(0);
// this.reportService.save(report);
//
// // Update RTSP_URL
// /*
// if(camera.getWareHouseId()!= null && camera.getWareHouseId() > 0) {
// WareHouse wareHouse = wareHouseService.getById(camera.getWareHouseId());
// if(wareHouse!= null && StrUtil.isNotBlank(wareHouse.getIndexCode())) {
// HikVisionUtils utils = new HikVisionUtils();
// String rtspUrl = utils.requestRtsp(wareHouse.getIndexCode());
// if(StrUtil.isNotBlank(rtspUrl)) {
// // cameraService.updateRtspUrl(camera.getId(), rtspUrl);
//
//
// // Temp Hour Method Case
// cameraService.delete(camera.getId());
//
// Camera newCamera = new Camera();
// newCamera.setName(camera.getName());
// newCamera.setRtspUrl(rtspUrl);
// newCamera.setAction(CameraAction.ACTION_NULL.getType());
// newCamera.setRunning(camera.getRunning());
// newCamera.setState(1);
// newCamera.setCreatedAt(new Date());
// newCamera.setUpdatedAt(new Date());
// newCamera.setFrequency(camera.getFrequency());
// newCamera.setIntervalTime(camera.getIntervalTime());
// newCamera.setFileName(camera.getFileName());
// newCamera.setFileWidth(camera.getFileWidth());
// newCamera.setFileHeight(camera.getFileHeight());
// newCamera.setCanvasWidth(camera.getCanvasWidth());
// newCamera.setCanvasHeight(camera.getCanvasHeight());
// newCamera.setParams(camera.getParams());
// newCamera.setApiParams(camera.getApiParams());
// newCamera.setScaleRatio(camera.getScaleRatio());
// newCamera.setWareHouseId(camera.getWareHouseId());
// cameraService.save(newCamera);
//
// //
// cameraAlgorithmService.updateCameraId(camera.getId(), newCamera.getId());
//
// //
// reportPeriodService.updateCameraId(camera.getId(), newCamera.getId());
//
// //
// cameraService.updateAndState(newCamera.getId(), CameraAction.ACTION_UPD.getType(), 0);
//
//}
//}
//}
//
// */
//}
//
// //
// // this.cameraService.updateRunning(cameraId, (state == 0)? CameraRunningState.RUNNING.getType(): CameraRunningState.CLOSED.getType());
//
// //
// return JsonResultUtils.success();
//} catch (Exception e) {
// log.error("Call Stream Status Report API Exception", e);
// return JsonResultUtils.fail();
//}
//}
//
//
// /**
// * Get Play Put Address
// * @param cameraId
// * @return
// */
// @PostMapping("/getPlayUrl")
// @ResponseBody
// public JsonResult<String> getPlayUrl(Long cameraId) {
// String streamType = configService.getByValTag("streamType"); // Push Stream Type
// String playUrl = configService.getByValTag("playUrl"); // Play Put Address
//
// //
// Camera camera = cameraService.getById(cameraId);
// if(camera == null) {
// return JsonResultUtils.fail("Camera does not exist or Deleted");
//}
// //
// if(StrUtil.isBlank(camera.getRtspUrl())) {
// return JsonResultUtils.fail("Camera not has Config Stream Address!!!");
//}
//
// // Query Media Server Whether Exist Stream Media
// if(queryExistPlayUrl(cameraId)) {
// String _playUrl = playUrl +"/Media/"+ cameraId +".flv";
// return JsonResultUtils.success(_playUrl);
//}
// // not has Push Stream, Add Push Stream to Media Server
// if(StrUtil.isBlank(streamType) ||"rtsp".equals(streamType)) {// rtsp Push Stream Play Put
// publishPlayUrl(cameraId, camera.getRtspUrl());
// String _playUrl = playUrl +"/Media/"+ cameraId +".flv";
// return JsonResultUtils.success(_playUrl);
//} else {// Algorithm Push Stream Play Put
// cameraService.updateVideoPlayToMediaServer(cameraId, 1);
// String _playUrl = playUrl +"/Media/"+ cameraId +".flv";
// return JsonResultUtils.success(_playUrl);
//}
//}
//
// /**
// * from Stream Media Query Play Put Address
// */
// private boolean queryExistPlayUrl(Long cameraId) {
// // Query Media Server Whether Exist Stream Media
// try {
// String mediaUrl = configService.getByValTag("mediaUrl"); // Media Management Address
// String reqUrl = mediaUrl +"/index/api/getMediaList?secret=035c73f7-bb6b-4889-a715-d9eb2d1925cc&app=Media&stream="+ cameraId;
// String result = HttpUtil.get(reqUrl, 5000);
// JSONObject resultJson = JSON.parseObject(result);
// if (resultJson.containsKey("code") && resultJson.getInteger("code") == 0) {
// return true;
//}
// return false;
//} catch (Exception e) {
// e.printStackTrace();
//}
// return false;
//}
//
// /**
// * Release Play Put Address
// */
// private void publishPlayUrl(Long cameraId, String rtspUrl) {
// try {
// String mediaUrl = configService.getByValTag("mediaUrl"); // Media Management Address
// String reqRtspUrl = mediaUrl +"/index/api/addStreamProxy?secret=035c73f7-bb6b-4889-a715-d9eb2d1925cc&vhost=_defaultVhost_&app=Media&stream="+ cameraId +"&enable_hls=0&convertOutWidth=800&convertOutHeight=480&url="+ URLEncoder.encode(rtspUrl,"utf-8");
// String result = HttpUtil.get(reqRtspUrl, 5000);
// JSONObject resultJson = JSON.parseObject(result);
// //if (resultJson.containsKey("code") && resultJson.getInteger("code") == 0) {
// //}
//} catch (Exception e) {
// System.out.println("Release Play Put Address Exception:"+ e.getMessage());
//}
//}
//
//
//}
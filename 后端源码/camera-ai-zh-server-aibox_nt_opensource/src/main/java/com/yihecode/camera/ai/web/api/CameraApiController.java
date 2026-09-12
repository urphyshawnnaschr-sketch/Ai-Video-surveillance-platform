//package com.yihecode.camera.ai.web.api;
//
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.yihecode.camera.ai.entity.*;
//import com.yihecode.camera.ai.enums.CameraAction;
//import com.yihecode.camera.ai.enums.CameraRunningState;
//import com.yihecode.camera.ai.enums.CommState;
//import com.yihecode.camera.ai.service.*;
//import com.yihecode.camera.ai.utils.JsonResult;
//import com.yihecode.camera.ai.utils.JsonResultUtils;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.yihecode.camera.ai.utils.PageResult;
//import com.yihecode.camera.ai.utils.PageResultUtils;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
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
// * Camera List API, Integrate Algorithm Pull Get most new Camera Config Data
// *
// * @author zhoumingxing
// * @mail 465769438@qq.com
// */
//@ApiIgnore
//@Slf4j
//@Controller
//@RequestMapping({"/api/camera"})
//public class CameraApiController {
//
// //
// @Autowired
// private CameraService cameraService;
//
// //
// @Autowired
// private AlgorithmService algorithmService;
//
// //
// @Autowired
// private CameraAlgorithmService cameraAlgorithmService;
//
// //
// @Autowired
// private VideoPlayService videoPlayService;
//
// //
// @Autowired
// private ConfigService configService;
//
// //
// @Autowired
// private LocationService locationService;
//
// /**
// *
// * @return
// */
// @RequestMapping({"/list"})
// @ResponseBody
// public JsonResult listData() {
// try {
// //
// List<Camera> cameraList = this.cameraService.list();
// if (cameraList == null) {
// cameraList = new ArrayList<>();
//}
//
// //
// List<Algorithm> algorithmList = this.algorithmService.list();
// if (algorithmList == null) {
// algorithmList = new ArrayList<>();
//}
// // Person Stream Quantity Tracking Algorithm ID, like Result Exist, rule not Notification model Framework Push Stream, by Person Stream Quantity Algorithm Push Stream
// Long personTrackId = 0L;
// for(Algorithm algorithm: algorithmList) {
// if(algorithm.getNameEn().equals("person_tracker")) {
// personTrackId = algorithm.getId();
// break;
//}
//}
// //
// Map<Long, String> algorithmNames = new HashMap<>();
// for (Algorithm algorithm: algorithmList) {
// algorithmNames.put(algorithm.getId(), algorithm.getName());
//}
//
// // English Name Queue
// Map<Long, String> algorithmEnNames = new HashMap<>();
// for (Algorithm algorithm: algorithmList) {
// algorithmEnNames.put(algorithm.getId(), algorithm.getNameEn());
//}
//
//// // Video Play Put outer net and inner net Port Mapper Rule rule
//// String videoPortRule = configService.getByValTag("video_port_rule");
//// Integer videoPortDiss = 0;
//// if(StrUtil.isNotBlank(videoPortRule)) {
//// videoPortDiss = Integer.valueOf(videoPortRule);
////}
//// // Video Play Put inner net Push Stream ip
//// String videoInnerIp = configService.getByValTag("video_inner_ip");
//// // Video Play Put List Port Mapper
//// Map<Long, Integer> videoPortMap = new HashMap<>();
//// List<VideoPlay> videoPlays = videoPlayService.list();
//// if(videoPlays!= null) {
//// for(VideoPlay videoPlay: videoPlays) {
//// videoPortMap.put(videoPlay.getCameraId(), videoPlay.getVideoPort());
////}
////}
//
// String streamType = configService.getByValTag("streamType"); // Push Stream Mode
// String pushPort = configService.getByValTag("pushPort"); // Push Stream Port
// String pushIp = configService.getByValTag("pushIp"); // Push Stream IP Address
// if(StrUtil.isBlank(pushIp)) {
// pushIp ="127.0.0.1";
//}
//
// //
// List<Map<String, Object>> dataList = new ArrayList<>();
//
// //
// for (Camera camera: cameraList) {
// // Box up Camera not need Put to model Remove Execute
// Location location = locationService.getById(camera.getLocationId());
// if(location!= null &&"2".equals(location.getType())) {
// continue;
//}
//
// //
// Map<String, Object> cameraMap = new HashMap<>();
// cameraMap.put("camera_id", camera.getId());
// cameraMap.put("camera_name", camera.getName());
// cameraMap.put("rtsp_url", camera.getRtspUrl());
// cameraMap.put("action", camera.getAction());
// cameraMap.put("state", camera.getState());
// cameraMap.put("interval_time", camera.getIntervalTime());
// cameraMap.put("frequency", camera.getFrequency());
// cameraMap.put("params", camera.getApiParams());
// cameraMap.put("video_play", camera.getVideoPlay() == null? 0: camera.getVideoPlay());
// cameraMap.put("alarm_interval", camera.getAlarmInterval() == null? -1.0: camera.getAlarmInterval()); // Negative Value table show Push All Alert
//
// // Camera for Valid Status
// // when running Status for Stop Status Hour, will will action Set for 2, But is Camera state still is 0, The with Make down Special Special Process, just not Need Algorithm Make Modify
// if(camera.getState() == 0) {
// // Capture Image Stop
// if(camera.getRunning() == null || camera.getRunning() == CameraRunningState.CLOSED.getType()) {
// cameraMap.put("action", CameraAction.ACTION_DEL.getType());
// cameraMap.put("state", CommState.DISABLED.getType());
//}
//}
//
// // Image Address Type, will Camera Status Set for Invalid, Action for Delete
//// if(camera.getRtspType()!= null && camera.getRtspType() == 2) {
//// cameraMap.put("action", CameraAction.ACTION_DEL.getType());
//// cameraMap.put("state", CommState.DISABLED.getType());
////}
//
// // Mark Param Process modify by zhou: Algorithm not Need the Param
// /*
// if(StrUtil.isBlank(camera.getApiParams())) {
// cameraMap.put("params","");
//} else {
// // Prevent Exception
// try {
// JSONArray apiParams = JSON.parseArray(camera.getApiParams());
// int len = apiParams.size();
// if(len == 0) {
// cameraMap.put("params","");
//} else {
// JSONArray sub = new JSONArray();
// for(int i = 0; i < len; i++) {
// if(i == 0) {
// cameraMap.put("params", apiParams.getJSONArray(0).toString());
//} else {
// sub.add(apiParams.getJSONArray(i));
//}
//}
// if(sub.size() == 0) {
// cameraMap.put("other_params","");
//} else {
// cameraMap.put("other_params", JSON.toJSONString(sub));
//}
//}
//} catch (Exception e) {
// //
//}
//}
//
// */
// cameraMap.put("params","");
//
// //
// boolean hasPersonTrack = false;
// List<CameraAlgorithm> cameraAlgorithmList = this.cameraAlgorithmService.listByCamera(camera.getId());
// List<Map<String, Object>> algorithms = new ArrayList<>();
// if (cameraAlgorithmList!= null) {
// for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
// if(algorithmNames.containsKey(cameraAlgorithm.getAlgorithmId())) {
// Map<String, Object> algorithMap = new HashMap<>();
// algorithMap.put("algorithm_id", cameraAlgorithm.getAlgorithmId());
// algorithMap.put("algorithm_name", algorithmEnNames.get(cameraAlgorithm.getAlgorithmId()));
// algorithMap.put("algorithm_name_en", algorithmEnNames.get(cameraAlgorithm.getAlgorithmId()));
// algorithMap.put("algorithm_confidence", cameraAlgorithm.getConfidence());
// algorithMap.put("algorithm_rois", cameraAlgorithm.getImagePoints());
// algorithms.add(algorithMap);
// // Determine Whether Person Stream Quantity Algorithm
// if(cameraAlgorithm.getAlgorithmId().equals(personTrackId)) {
// hasPersonTrack = true;
//}
//}
//}
//}
// cameraMap.put("algorithms", algorithms);
// // Push Stream Address
// if(StrUtil.isNotBlank(streamType) &&"algo".equals(streamType)) {
//// // Video Play Put Port
//// int videoPlayPort = videoPortMap.get(camera.getId());
//// // Push Stream Port
//// int videoPushPort = videoPlayPort + videoPortDiss;
// // Camera Bind Person Stream Quantity Algorithm, by Person Stream Quantity Algorithm Push Stream
// if(hasPersonTrack) {
// cameraMap.put("video_play", 0);
// cameraMap.put("rtmp_url","");
// cameraMap.put("rtsp_push_url","");
// cameraMap.put("remark","Person Stream Quantity Algorithm Push Stream");
//} else {// by model Framework Push Stream
// cameraMap.put("video_play", 1);
// cameraMap.put("rtmp_url","rtmp://"+ pushIp +":"+ pushPort +"/Media/"+ camera.getId());
// cameraMap.put("rtsp_push_url","rtsp://"+ pushIp +":"+ pushPort +"/Media/"+ camera.getId());
// cameraMap.put("remark","model Framework Push Stream");
//}
//
//} else {
// // Video Whether Play Put
// cameraMap.put("video_play", 0);
// //
// cameraMap.put("rtmp_url","");
// cameraMap.put("rtsp_push_url","");
//}
// dataList.add(cameraMap);
//}
//
// this.cameraService.updateAction();
//
// return JsonResultUtils.success(dataList);
//} catch (Exception e) {
// log.error("Call Camera List API Exception", e);
// return JsonResultUtils.fail();
//}
//}
//
// @ApiOperation("Query Work Dynamic Camera")
// //@ApiImplicitParams(value = {
// //@ApiImplicitParam(name ="page", value ="Page Number"),
// //@ApiImplicitParam(name ="limit", value ="Page Size")
//})
// @PostMapping("listPageActives")
// @ResponseBody
// public PageResult<List<Map<String, Object>>> listPageActives(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="10") Integer limit) {
// IPage<Camera> cameraIPage = cameraService.listPageActives(page, limit);
// List<Camera> records = cameraIPage.getRecords();
// if(records == null) {
// records = new ArrayList<>();
//}
// //
// List<Map<String, Object>> dataList = new ArrayList<>();
// for(Camera record: records) {
// Map<String, Object> dataMap = new HashMap<>();
// dataMap.put("id", record.getId());
// dataList.add(dataMap);
//}
// return PageResultUtils.success(cameraIPage.getTotal(), dataList);
//}
//
// public static void main(String[] args) {
// System.out.println(System.currentTimeMillis());
//}
//}
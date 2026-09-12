package com.yihecode.camera.ai.web.wvp;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.config.WvpConfig;
import com.yihecode.camera.ai.entity.wvp.GBDevice;
import com.yihecode.camera.ai.service.wvp.GBDeviceChannelService;
import com.yihecode.camera.ai.service.wvp.GBDeviceService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//Code Rule Scope https://blog.csdn.net/weixin_70208651/article/details/137799708

@Slf4j
@Api(tags ="GB Standard Device Info Management")
@Controller
@RequestMapping({"/wvp/device"})
public class GBDeviceController {

@Autowired
private GBDeviceService deviceService;

@Autowired
private GBDeviceChannelService deviceChannelService;

@Autowired
private WvpConfig wvpConfig;

@SaCheckPermission("gb-device-list")
@ApiOperation("Query GB Standard Device Info List")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size")
})
@PostMapping("listPage")
@ResponseBody
public PageResult<List<GBDevice>> listPage(@RequestParam(defaultValue ="1") Integer page,
@RequestParam(defaultValue ="10") Integer limit) {
IPage<GBDevice> deviceIPage = deviceService.listPage(page, limit);
List<GBDevice> records = deviceIPage.getRecords();
if(records == null) {
records = new ArrayList<>();
}

for(GBDevice device: records) {
int count = deviceChannelService.getCountByDeviceId(device.getDeviceId());
device.setChannelNums(count);
}
return PageResultUtils.success(deviceIPage.getTotal(), deviceIPage.getRecords());
}

@SaCheckPermission("gb-device-list")
@ApiOperation("Query GB Standard Device Detail")
@ApiImplicitParam(name ="id", value ="Data ID")
@GetMapping("info")
@ResponseBody
public JsonResult<GBDevice> info(Long id) {
GBDevice gbDevice = deviceService.getById(id);
return JsonResultUtils.success(gbDevice);
}

@SaCheckPermission("gb-device-list")
@ApiOperation("Edit GB Standard Device Info")
@PostMapping("save")
@ResponseBody
public JsonResult<?> save(@RequestBody GBDevice gbDevice) {
deviceService.saveOrUpdate(gbDevice);
return JsonResultUtils.success();
}

@SaCheckPermission("gb-device-list")
@ApiOperation("Delete GB Standard Device Info")
@ApiImplicitParam(name ="id", value ="Data ID")
@GetMapping("delete")
@ResponseBody
public JsonResult<?> delete(Long id) {
deviceService.removeById(id);
return JsonResultUtils.success();
}

@SaCheckPermission("gb-device-list")
@ApiOperation("Sync Device Channel")
@PostMapping("sync")
@ResponseBody
public JsonResult<?> sync(Long id) {
if(StrUtil.isBlank(wvpConfig.getWvpIp()) || wvpConfig.getWvpPort() == null) {
return JsonResultUtils.fail("GB Standard Service not Config, Param View gb28181-conf Config Info");
}

GBDevice gbDevice = deviceService.getById(id);
if(gbDevice == null) {
return JsonResultUtils.fail("Sync Failed, find not to GB Standard Device Info");
}

try {
String reqUrl = String.format("http://%s:%s/api/device/query/devices/%s/sync", wvpConfig.getWvpIp(), wvpConfig.getWvpPort(), gbDevice.getDeviceId());
String response = HttpUtil.get(reqUrl, 5000);
log.info("Call GB Standard Service Sync API Return Data, response:{}", response);
return JsonResultUtils.success();
} catch (Exception e) {
return JsonResultUtils.fail("Sync Failed, Connection GB Standard Platform Failed");
}
}

@SaCheckPermission("gb-device-list")
@ApiOperation("Get Sync Device Channel Result")
@PostMapping("syncStatus")
@ResponseBody
public JsonResult<?> syncStatus(Long id) {
GBDevice gbDevice = deviceService.getById(id);
if(gbDevice == null) {
return JsonResultUtils.fail("Sync Failed, find not to GB Standard Device Info");
}

try {
String reqUrl = String.format("http://%s:%s/api/device/query/%s/sync_status", wvpConfig.getWvpIp(), wvpConfig.getWvpPort(), gbDevice.getDeviceId());
String response = HttpUtil.get(reqUrl, 5000);
log.info("Call GB Standard Service Sync Status API Return Data, response:{}", response);
JSONObject root = JSON.parseObject(response);

// if(root.containsKey("code")) {
// if(root.getIntValue("code") == 0) {
// return JsonResultUtils.successMsg("Sync Complete Complete");
//} else {
// return JsonResultUtils.successMsg("Sync Failed,"+ root.getString("msg"));
//}
//} else {
// return JsonResultUtils.fail("Sync Failed");
//}
return JsonResultUtils.success(root);
} catch (Exception e) {
return JsonResultUtils.fail("Sync Failed");
}
}
}

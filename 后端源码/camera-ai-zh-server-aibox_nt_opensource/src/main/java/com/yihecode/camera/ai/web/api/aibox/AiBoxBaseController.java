package com.yihecode.camera.ai.web.api.aibox;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yihecode.camera.ai.entity.AiboxStatus;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.CameraPullVo;
import com.yihecode.camera.ai.web.api.aibox.vo.RegisterVo;
import com.yihecode.camera.ai.web.api.aibox.vo.StatusVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.*;

/**
* Edge Box Info Report
*/
@SaIgnore
@ApiIgnore
@Api(tags = "Edge Box Info Report Management")
@Slf4j
@RestController
@RequestMapping("/api/aibox/base")
public class AiBoxBaseController {

    @Resource
    private ConfigService configService;

    @Resource
    private LocationService locationService;

    @Resource
    private AiboxStatusService aiboxStatusService;

    @Resource
    private AlgorithmService algorithmService;

    /**
* Box Register Info Report
*
* @return
*/
    @ApiOperation("Edge Box Register")
    @PostMapping("/register")
    public JsonResult register(@RequestBody RegisterVo registerVo) {
        //Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if (StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(registerVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Query Box Info
Location location = locationService.getBoxSnForRemote(registerVo.getSn());
if (location == null) {
// Get Root Default Node
Long parentId = 0L;
Location def = locationService.getDefRoot(2);
if(def!= null) {
parentId = def.getId();
}
//
location = new Location();
location.setName(registerVo.getSn());
location.setSort(1);
location.setParentId(parentId);
location.setParentNames("");
location.setParentIds("");
location.setLatitude(null);
location.setLongitude(null);
location.setIpAddr(registerVo.getIpAddr());
location.setType("2");
location.setOnline("1");
location.setLocationType("2");
location.setIsDef(0);
location.setBoxNo(registerVo.getSn());
location.setBoxHeartTime(System.currentTimeMillis());
location.setMakers(registerVo.getMakers());
location.setDeviceMode(registerVo.getDeviceMode());
location.setCpuVersion(registerVo.getCpuVersion());
location.setOsVersion(registerVo.getOsVersion());
location.setKernelVersion(registerVo.getKernelVersion());
location.setDiskTotal(registerVo.getDiskTotal());
location.setMemoryTotal(registerVo.getMemoryTotal());
location.setLyndriverVersion(registerVo.getLyndriverVersion());
location.setLynsdkVersion(registerVo.getLynsdkVersion());
} else {
location.setType("2");
location.setIpAddr(registerVo.getIpAddr());
location.setBoxHeartTime(System.currentTimeMillis());
location.setLyndriverVersion(registerVo.getLyndriverVersion());
location.setLynsdkVersion(registerVo.getLynsdkVersion());
}
locationService.saveOrUpdate(location);

String baseUrl = configService.getByValTag("FACE_HTTP_BASE_URL");
Map<String, Object> resultMap = new HashMap<>();
resultMap.put("face_server_url", baseUrl);
return JsonResultUtils.success(resultMap);
}

/**
* Box Info Report
*
* @return
*/
@ApiOperation("Edge Box Status Report")
@PostMapping("/status")
public JsonResult<Void> status(@RequestBody StatusVo statusVo) {
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if (StrUtil.isBlank(aiboxKey)) {
aiboxKey ="-1";
}
// key not Consistent
if(!SecureUtil.md5(aiboxKey).equals(statusVo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}
// Query Box Info
Location location = locationService.getBoxSnForRemote(statusVo.getSn());
if (location == null) {
return JsonResultUtils.fail("sn find not to Box Info");
}
// Update Box most after Heartbeat Time
Location updateLocation = new Location();
updateLocation.setId(location.getId());
updateLocation.setBoxHeartTime(System.currentTimeMillis());
updateLocation.setType("2");
locationService.updateById(updateLocation);
// Add Box Resource Status
AiboxStatus aiboxStatus = new AiboxStatus();
aiboxStatus.setAiboxId(location.getId());
aiboxStatus.setAiboxSn(location.getBoxNo());
aiboxStatus.setDisKUsed(statusVo.getDiskUsed());
aiboxStatus.setMemoryUsed(statusVo.getMemoryUsed());
aiboxStatus.setCpuUsed(statusVo.getCpuUsed());
aiboxStatus.setApuUsed(statusVo.getApuUsed());
aiboxStatus.setVicUsed(statusVo.getVicUsed());
aiboxStatus.setIpeUsed(statusVo.getIpeUsed());
aiboxStatus.setTemperatureUsed(statusVo.getTemperatureUsed());
aiboxStatus.setCreatedAt(new Date());
aiboxStatus.setTimeTag(DateUtil.format(new Date(),"HH:mm:ss"));
aiboxStatusService.save(aiboxStatus);
//
return JsonResultUtils.success();
}

/**
* Box Event List
*
* @return
*/
@ApiOperation("Edge Box Event List")
@PostMapping("/event")
public JsonResult<?> event(@RequestBody CameraPullVo cameraPullVo) {
return JsonResultUtils.success(new ArrayList<>());
}

@ApiOperation("Edge Box Get Algorithm List")
@PostMapping("/algorithms")
public JsonResult<List<Map<String, Object>>> algorithms() {
List<Algorithm> algorithms = algorithmService.list();
if(algorithms == null || algorithms.isEmpty()) {
return JsonResultUtils.success(new ArrayList<>());
}

List<Map<String, Object>> dataList = new ArrayList<>();
for(Algorithm algorithm: algorithms) {
Map<String, Object> map = new HashMap<>();
map.put("algorithm_id", algorithm.getId());
map.put("share_mode", algorithm.getShareMode());
dataList.add(map);
}
return JsonResultUtils.success(dataList);
}
}

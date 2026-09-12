package com.yihecode.camera.ai.web.aibox;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.unit.DataSizeUtil;
import com.yihecode.camera.ai.entity.AiboxStatus;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.service.AiboxStatusService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
* Edge Box Resource Status Management
*/
@Api(tags = "Box Management _ Resource Status Management")
@RestController
@RequestMapping("/aibox/status")
public class AiboxStatusController {

    @Resource
    private LocationService locationService;

    @Resource
    private AiboxStatusService aiboxStatusService;

    @ApiOperation(value = "Basic Info")
    @ApiImplicitParam(name = "id", value = "Primary Key")
    @SaCheckPermission(value = {"caske-view", "edgePlatform-casketManagement"}, mode = SaMode.OR)
    @PostMapping(value = {"", "/"})
    public JsonResult<Map<String, Object>> base(Long id) {
        //Get most after One Report Data
AiboxStatus aiboxStatusLast = aiboxStatusService.getLast(id);
// Query Box Info
Location location = locationService.getById(id);
if(location!= null) {
// Disk total Quantity
Long diskTotal = location.getDiskTotal();
diskTotal = diskTotal == null? 0: diskTotal;
location.setDiskTotalStr(DataSizeUtil.format(diskTotal));
// inner Store total Quantity
Long memoryTotal = location.getMemoryTotal();
memoryTotal = memoryTotal == null? 0: memoryTotal;
location.setMemoryTotalStr(DataSizeUtil.format(memoryTotal));
// Disk remaining remainder Quantity
location.setDiskFreeTotalStr(location.getDiskTotalStr());
//
if(aiboxStatusLast!= null) {
// Calculate remaining remainder total Quantity
Double disKUsed = aiboxStatusLast.getDisKUsed();
if(disKUsed!= null) {
Long diskFree = Double.valueOf(diskTotal * (1 - disKUsed)).longValue();
location.setDiskFreeTotalStr(DataSizeUtil.format(diskFree));
}
}
}

// Query Recent 10 Record
List<AiboxStatus> aiboxStatuses = aiboxStatusService.listLast(id);
if(aiboxStatuses == null) {
aiboxStatuses = new ArrayList<>();
}
// cpu make Use Rate
List<String> labels = new ArrayList<>();
List<Double> diskDatas = new ArrayList<>();
List<Double> memoryDatas = new ArrayList<>();
List<Double> cpuDatas = new ArrayList<>();
List<Double> apuDatas = new ArrayList<>();
List<Double> vicDatas = new ArrayList<>();
List<Double> ipeDatas = new ArrayList<>();
List<Double> temperatureDatas = new ArrayList<>();
for(AiboxStatus aiboxStatus: aiboxStatuses) {
labels.add(aiboxStatus.getTimeTag());
diskDatas.add(aiboxStatus.getDisKUsed() == null? 0: aiboxStatus.getDisKUsed());
memoryDatas.add(aiboxStatus.getMemoryUsed() == null? 0: aiboxStatus.getMemoryUsed());
cpuDatas.add(aiboxStatus.getCpuUsed() == null? 0: aiboxStatus.getCpuUsed());
apuDatas.add(aiboxStatus.getApuUsed() == null? 0: aiboxStatus.getApuUsed());
vicDatas.add(aiboxStatus.getVicUsed() == null? 0: aiboxStatus.getVicUsed());
ipeDatas.add(aiboxStatus.getIpeUsed() == null? 0: aiboxStatus.getIpeUsed());
temperatureDatas.add(aiboxStatus.getTemperatureUsed() == null? 0: aiboxStatus.getTemperatureUsed());
}
//
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("base", location);
dataMap.put("labels", labels);
dataMap.put("diskDatas", diskDatas);
dataMap.put("memoryDatas", memoryDatas);
dataMap.put("cpuDatas", cpuDatas);
dataMap.put("apuDatas", apuDatas);
dataMap.put("vicDatas", vicDatas);
dataMap.put("ipeDatas", ipeDatas);
dataMap.put("temperatureDatas", temperatureDatas);
//
if(aiboxStatusLast == null) {
dataMap.put("cpuUsed", 0);
dataMap.put("memoryUsed", 0);
} else {
dataMap.put("cpuUsed", aiboxStatusLast.getCpuUsed() == null? 0: aiboxStatusLast.getCpuUsed() * 100);
dataMap.put("memoryUsed", aiboxStatusLast.getMemoryUsed() == null? 0: aiboxStatusLast.getMemoryUsed() * 100);
}
return JsonResultUtils.success(dataMap);
}
}

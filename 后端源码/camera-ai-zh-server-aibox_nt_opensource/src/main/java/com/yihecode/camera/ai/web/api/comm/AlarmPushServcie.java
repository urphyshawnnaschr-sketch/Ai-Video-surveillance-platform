package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.SocialResultBusinessType;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.ExternalAccessUtils;
import com.yihecode.camera.ai.web.vo.ReportStatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Message Send
*/
@Slf4j
@Component
public class AlarmPushServcie {

    @Autowired
    private SocialConfigService socialConfigService;

    @Autowired
    private SocialHookService socialHookService;

    @Autowired
    private AlarmFeishuPushV2Service alarmFeishuPushService;

    @Autowired
    private AlarmDingDingPushV2Service alarmDingDingPushService;

    @Autowired
    private AlarmWechatPushV2Service alarmWechatPushService;

    @Autowired
    private SocialResultService socialResultService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ReportService reportService;

    /**
* Async Send
* @param camera
* @param algorithm
* @param report
*/
    public void send(Camera camera, Algorithm algorithm, Report report) {
        List<SocialConfig> socialConfigList = socialConfigService.listData(camera.getId(), algorithm.getId());
        if(socialConfigList == null || socialConfigList.isEmpty()) {
            //Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(0L);
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl("");
socialResult.setState(0);
socialResult.setErrorDetail("Camera not has Config Send Group");
socialResult.setSendText("");
socialResult.setCameraName(camera.getName());
socialResult.setAlgorithmName(algorithm.getName());
socialResult.setReportId(report.getId());
socialResultService.save(socialResult);

// Push Result
reportService.updatePushResult(report, 2,"Camera not has Config Send Group");
return;
}

List<SocialHook> socialHookList = socialHookService.listAll();
if(socialHookList.isEmpty()) {
// Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(0L);
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl("");
socialResult.setState(0);
socialResult.setErrorDetail("not has Send Group Config, Please Check Handle public Push");
socialResult.setSendText("");
socialResult.setCameraName(camera.getName());
socialResult.setAlgorithmName(algorithm.getName());
socialResult.setReportId(report.getId());
socialResultService.save(socialResult);

// Push Result
reportService.updatePushResult(report, 2,"not has Send Group Config");
return;
}
Map<Long, SocialHook> socialHookMap = socialHookList.stream().collect(Collectors.toMap(SocialHook::getId, Function.identity()));

// Title

// Content
String title ="Camera Name:"+ camera.getName();
String content = String.format("Monitor Content:%s,\n Alarm Time:%s.", algorithm.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"));

// web outer net Access Address not Config
String webUrl = configService.getByValTag("webUrl");
// if(StrUtil.isBlank(webUrl) || webUrl.contains("127.0.0.1")) {
// log.error("Alarm Push,web outer net Access Address not Config or Contain 127.0.0.1, no Method Push");
// return;
//}

long timestamp = System.currentTimeMillis();
String key = ExternalAccessUtils.encrypt(report.getId(), timestamp);

// page Surface Access Address
String pageUrl = webUrl +"/report/ext/detail?id="+ report.getId() +"&key="+ key +"&t="+ timestamp;

// Image Access Address
String imgUrl = webUrl +"/report/ext/stream?id="+ report.getId() +"&key="+ key +"&t="+ timestamp;

//
for(SocialConfig socialConfig: socialConfigList) {
SocialHook socialHook = socialHookMap.get(socialConfig.getSocialId());
if(socialHook == null) {
continue;
}

if(socialHook.getType() == null || socialHook.getState() == null || socialHook.getState()!= 1) {
// Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(socialHook.getId());
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl("");
socialResult.setState(0);
socialResult.setErrorDetail("Send Group Type Error or Closed");
socialResult.setSendText("");
socialResult.setCameraName(camera.getName());
socialResult.setAlgorithmName(algorithm.getName());
socialResult.setReportId(report.getId());
socialResultService.save(socialResult);

// Push Result
reportService.updatePushResult(report, 2,"Send Group Type Error or Closed");
continue;
}

if(socialHook.getType() == 0) {// Feishu
alarmFeishuPushService.send(socialHook, report.getFileName(), report.getParams(), title, content, pageUrl, imgUrl, camera.getName(), algorithm.getName(), report.getId(), report.getCreatedAt(), SocialResultBusinessType.REPORT.getType());
}

if(socialHook.getType() == 1) {// WeWork
alarmWechatPushService.send(socialHook, title, content, pageUrl, imgUrl, camera.getName(), algorithm.getName(), report.getId());
}

if(socialHook.getType() == 2) {// DingTalk
alarmDingDingPushService.send(socialHook, title, content, pageUrl, imgUrl, camera.getName(), algorithm.getName(), report.getId());
}
}
}

/**
* Sync Send
* @param camera
* @param algorithm
* @param report
* @return
*/
public AlarmPushResult sendSync(Camera camera, Algorithm algorithm, Report report) {
List<SocialConfig> socialConfigList = socialConfigService.listData(camera.getId(), algorithm.getId());
if(socialConfigList == null || socialConfigList.isEmpty()) {
// Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(0L);
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl("");
socialResult.setState(0);
socialResult.setErrorDetail("Camera not has Config Send Group");
socialResult.setSendText("");
socialResult.setCameraName(camera.getName());
socialResult.setAlgorithmName(algorithm.getName());
socialResult.setReportId(report.getId());
socialResultService.save(socialResult);

// Push Result
reportService.updatePushResult(report, 2,"Camera not has Config Send Group");

return AlarmPushResult.builder().success(false).error("Camera not has Config Send Group").build();
}

List<SocialHook> socialHookList = socialHookService.listAll();
if(socialHookList.isEmpty()) {
// Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(0L);
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl("");
socialResult.setState(0);
socialResult.setErrorDetail("not has Send Group Config, Please Check Handle public Push");
socialResult.setSendText("");
socialResult.setCameraName(camera.getName());
socialResult.setAlgorithmName(algorithm.getName());
socialResult.setReportId(report.getId());
socialResultService.save(socialResult);

// Push Result
reportService.updatePushResult(report, 2,"not has Send Group Config");

return AlarmPushResult.builder().success(false).error("not has Send Group Config, Please Check Handle public Push").build();
}
Map<Long, SocialHook> socialHookMap = socialHookList.stream().collect(Collectors.toMap(SocialHook::getId, Function.identity()));

// Title

// Content
String title ="Camera Name:"+ camera.getName();
String content = String.format("Monitor Content:%s,\n Alarm Time:%s.", algorithm.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"));

// web outer net Access Address not Config
String webUrl = configService.getByValTag("webUrl");
// if(StrUtil.isBlank(webUrl) || webUrl.contains("127.0.0.1")) {
// log.error("Alarm Push,web outer net Access Address not Config or Contain 127.0.0.1, no Method Push");
// return;
//}

// page Surface Access Address
String pageUrl = webUrl +"/report/detail?id="+ report.getId();

// Image Access Address
String imgUrl = webUrl +"/report/stream?id="+ report.getId();

//
List<String> errorList = new ArrayList<>();
int success = 0;
for(SocialConfig socialConfig: socialConfigList) {
SocialHook socialHook = socialHookMap.get(socialConfig.getSocialId());
if(socialHook == null) {
errorList.add("Send Group not find to or Deleted");
continue;
}

if(socialHook.getType() == null || socialHook.getState() == null || socialHook.getState()!= 1) {
errorList.add(socialHook.getName() +", Send Group Type Error or Closed");
continue;
}

if(socialHook.getType() == 0) {// Feishu
AlarmPushResult alarmPushResult = alarmFeishuPushService.sendSync(socialHook, report.getFileName(), report.getParams(), title, content, pageUrl, imgUrl, camera.getName(), algorithm.getName(), report.getId());
log.info("Sync Send Feishu Alarm Result:"+ alarmPushResult.isSuccess() +","+ alarmPushResult.getErrorDetail());
if(alarmPushResult.isSuccess()) {
log.info("Manual Sync Send Feishu Alarm Success:");
errorList.add(socialHook.getName() +", Send Success");
success += 1;
} else {
log.info("Manual Sync Send Feishu Alarm Failed: {}"+ alarmPushResult.getError());
errorList.add(socialHook.getName() +","+ alarmPushResult.getError());
}
}

// if(socialHook.getType() == 1) {// WeWork
// alarmWechatPushService.send(socialHook, title, content, pageUrl, imgUrl, camera.getName(), algorithm.getName(), report.getId());
//}
//
// if(socialHook.getType() == 2) {// DingTalk
// alarmDingDingPushService.send(socialHook, title, content, pageUrl, imgUrl, camera.getName(), algorithm.getName(), report.getId());
//}
}

if(success > 0) {
if(success == errorList.size()) {
// Push Result
reportService.updatePushResult(report, 1,"Push Success");
log.info("Manual Sync Send Feishu Alarm All Success Update Status:");
return AlarmPushResult.builder().success(true).error("All Send Success").build();
} else {
// Push Result
log.info("Manual Sync Send Feishu Alarm part part Success Update Status:");
reportService.updatePushResult(report, 1, String.join(",", errorList));

return AlarmPushResult.builder().success(true).error(String.join(",", errorList)).build();
}
} else {
log.info("Manual Sync Send Feishu Alarm Failed Update Status:");
// Push Result
reportService.updatePushResult(report, 2, String.join(",", errorList));

return AlarmPushResult.builder().success(false).error(String.join(",", errorList)).build();
}
}
}

package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.notify.dingding.DingdingRobotSendUtils;
import com.yihecode.camera.ai.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
* Alarm DingTalk Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmDingDingPushService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public void send(Camera camera, Algorithm algorithm, Report report, Account account) {
        //Config not Enable
if(!projectConfig.isPushToDingDing()) {
return;
}

// DingTalk not Enabled
String dingdingEnable = configService.getByValTag("dingdingEnable"+ account.getId());
if(!"true".equalsIgnoreCase(dingdingEnable)) {
return;
}

// DingTalk Bot API Address
String webhook = configService.getByValTag("dingdingUrl"+ account.getId());
if(StrUtil.isBlank(webhook)) {
log.error("DingTalk Push, DingTalk Bot API Address not Config, no Method Push");
return;
}

// web outer net Access Address not Config
String webUrl = configService.getByValTag("webUrl");
if(StrUtil.isBlank(webUrl) || webUrl.contains("127.0.0.1")) {
log.error("DingTalk Push,web outer net Access Address not Config or Contain 127.0.0.1, no Method Push");
return;
}

CompletableFuture.runAsync(() -> {
String sign = configService.getByValTag("dingdingSign"+ account.getId());
String clickUrl = webUrl +"/report/detail?id="+ report.getId();
String picUrl = webUrl +"/report/stream?id="+ report.getId();
String title ="Camera Name:"+ camera.getName();
String text = String.format("Monitor Content:%s,\n Alarm Time:%s.", algorithm.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"));
DingdingRobotSendUtils.send(webhook, sign, title, text, picUrl, clickUrl);
}, executor);
}

}

package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.notify.wework.WeWorkRobotSendUtils;
import com.yihecode.camera.ai.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
* Alarm WeWork Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmWechatPushService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public void send(Camera camera, Algorithm algorithm, Report report, Account account) {
        if(!projectConfig.isPushToWeChat()) {
            return ;
        }

        //WeWork not Enabled
String weworkEnable = configService.getByValTag("weworkEnable"+ account.getId());
if(!"true".equalsIgnoreCase(weworkEnable)) {
return;
}

// WeWork Bot API Address
String webhook = configService.getByValTag("weworkUrl"+ account.getId());
if(StrUtil.isBlank(webhook)) {
log.error("WeWork Push, WeWork Bot API Address not Config, no Method Push");
return;
}

// web outer net Access Address not Config
String webUrl = configService.getByValTag("webUrl");
if(StrUtil.isBlank(webUrl) || webUrl.contains("127.0.0.1")) {
log.error("WeWork Push,web outer net Access Address not Config or Contain 127.0.0.1, no Method Push");
return;
}

CompletableFuture.runAsync(() -> {
String clickUrl = webUrl +"/report/detail?id="+ report.getId();
String picUrl = webUrl +"/report/stream?id="+ report.getId();
String title ="Camera Name:"+ camera.getName();
String description = String.format("Monitor Content:%s,\n Alarm Time:%s.", algorithm.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"));
WeWorkRobotSendUtils.sendTextAndImage(webhook, title, description, clickUrl, picUrl);
}, executor);
}

}

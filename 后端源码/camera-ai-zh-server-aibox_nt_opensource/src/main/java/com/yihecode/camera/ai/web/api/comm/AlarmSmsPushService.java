package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.notify.sms.SendSmsUtil;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.SmsPhoneService;
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
public class AlarmSmsPushService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private SmsPhoneService smsPhoneService;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public void send(Camera camera, Algorithm algorithm, Account account) {
        if(!projectConfig.isPushToSms()) {
            return ;
        }

        String smsEnable = configService.getByValTag("smsEnable" + account.getId());
        if (!"true".equals(smsEnable)) {
            return ;
        }

        String mobiles = smsPhoneService.listByAccountIdStr(account.getId());
        if (StrUtil.isBlank(mobiles)) {
            log.error("SMS Push, no Method SMS Push, not has can Use Phone");
            return ;
        }

        String smsAppKey = configService.getByValTag("smsAppKey" + account.getId());
        String smsTplId = configService.getByValTag("smsTplId" + account.getId());
        if (StrUtil.isBlank(smsAppKey) || StrUtil.isBlank(smsTplId)) {
            log.error("SMS Push, no Method SMS Push, Param Config Error,smsAppKey: {}, smsTplId: {}", smsAppKey, smsTplId);
            return ;
        }

        CompletableFuture.runAsync(() -> {
            SendSmsUtil.send(mobiles, camera.getName(), algorithm.getName(), smsAppKey, smsTplId);
        }, executor);
    }

}

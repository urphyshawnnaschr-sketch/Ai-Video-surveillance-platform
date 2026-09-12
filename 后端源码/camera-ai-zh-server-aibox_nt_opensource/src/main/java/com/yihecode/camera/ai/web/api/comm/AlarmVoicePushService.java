package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.notify.voice.SendVoiceUtil;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.VoicePhoneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
* Alarm Voice Push, Corresponding Aliyun Voice Service
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmVoicePushService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private VoicePhoneService voicePhoneService;

    @Autowired
    private ProjectConfig projectConfig;

    public void send(Camera camera, Algorithm algorithm, Report report, Account account, Long alarmLevelId) {
        if(!projectConfig.isPushToVoice()) {
            return ;
        }

        String voiceEnable = configService.getByValTag("voiceEnable" + account.getId());
        if (!"true".equals(voiceEnable)) {
            return ;
        }

        //Voice Param Config Error
String voiceAppId = configService.getByValTag("voiceAppId"+ account.getId());
String voiceAppSecret = configService.getByValTag("voiceAppSecret"+ account.getId());
String voiceTemplateId = configService.getByValTag("voiceTemplateId"+ account.getId());
if(StrUtil.isBlank(voiceAppId) || StrUtil.isBlank(voiceAppSecret) || StrUtil.isBlank(voiceTemplateId)) {
log.error("Voice Push, no Method Voice Push, Param Config Error, voiceAppId: {}, voiceAppSecret: {}, voiceTemplateId: {}", voiceAppId, voiceAppSecret, voiceTemplateId);
return;
}

// Query Phone and Filter select out Match Alert Level
List<VoicePhone> voicePhones = voicePhoneService.listPhones(account.getId());

// According to Alarm Level Filter select Send Phone
List<String> mobiles = new ArrayList<>();
for (VoicePhone voicePhone: voicePhones) {
if (voicePhone.getLevelId().equals(alarmLevelId)) {
mobiles.add(voicePhone.getPhone());
}
}

//
if (mobiles.isEmpty()) {
log.error("Voice Push, no Method Voice Push, not Use can Use Phone");
return;
}

// Send Voice
String content = String.format("%s, at %s send produce %s Alarm", camera.getName(), DateUtil.format(report.getCreatedAt(),"yyyy Year MM Month dd Day HH Hour mm part ss s"), algorithm.getName());
SendVoiceUtil.sendAsync(mobiles, voiceTemplateId, content, voiceAppId, voiceAppSecret);
}

}

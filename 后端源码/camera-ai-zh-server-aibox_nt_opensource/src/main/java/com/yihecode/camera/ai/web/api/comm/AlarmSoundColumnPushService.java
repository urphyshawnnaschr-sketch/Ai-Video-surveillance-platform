package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.utils.SoundColumnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
* Alarm Speaker Pole Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmSoundColumnPushService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private ProjectConfig projectConfig;

    @Value("${dataModelsDir}")
    public String MODEL_DIR;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public void send(Camera camera, Algorithm algorithm, Map<Long, SoundColumn> soundColumnMap) {
        if(!projectConfig.isSoundColumnEnable()) {
            return ;
        }

        if (StrUtil.isBlank(algorithm.getSoundFile()) || camera.getSoundColumnId() == null || soundColumnMap.get(camera.getSoundColumnId()) == null) {
            return;
        }

        //Audio File Whether Exist
String path = MODEL_DIR +"/soundFile/"+ algorithm.getSoundFile();
if (!FileUtil.exist(path) ||!FileUtil.isFile(path)) {
log.error("Speaker Pole Push, no Method Push Speaker Pole,mp3 File Error, file: {}", path);
return;
}

CompletableFuture.runAsync(() -> {
// Generate mp3 Address
String ipAddr = configService.getByValTag("ipAddr");
String mp3file ="http://"+ ipAddr +"/algorithm/sound/stream?id="+ algorithm.getId();

//
SoundColumn soundColumn = soundColumnMap.get(camera.getSoundColumnId());
if ("yuelang".equals(soundColumn.getType())) {
SoundColumnUtils.sendYueLangPlay(soundColumn.getServer(), soundColumn.getSn(), mp3file,
soundColumn.getUserName(), soundColumn.getPassword(), soundColumn.getVol());
} else {
SoundColumnUtils.sendPlay(soundColumn.getServer(), soundColumn.getSn(), mp3file);
}
}, executor);
}

}

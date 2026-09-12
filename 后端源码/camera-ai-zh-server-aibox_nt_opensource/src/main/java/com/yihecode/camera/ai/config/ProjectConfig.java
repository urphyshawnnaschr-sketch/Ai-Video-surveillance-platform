package com.yihecode.camera.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
* work Process Config
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
@Configuration
@Data
public class ProjectConfig {

    @Value("${proj-confs.version:1.0.0}")
    private String version;

    @Value("${proj-confs.sound-column-enable:false}")
    private boolean soundColumnEnable;

    @Value("${proj-confs.face-recognize-enable:false}")
    private boolean faceRecognizeEnable;

    @Value("${proj-confs.people-track-enable:false}")
    private boolean peopleTrackEnable;

    @Value("${proj-confs.push-to-wechat:false}")
    private boolean pushToWeChat;

    @Value("${proj-confs.push-to-dingding:false}")
    private boolean pushToDingDing;

    @Value("${proj-confs.push-to-sms:false}")
    private boolean pushToSms;

    @Value("${proj-confs.push-to-voice:false}")
    private boolean pushToVoice;

    @Value("${proj-confs.ap-enable:false}")
    private boolean apEnable;

    @Value("${proj-confs.app-enable:false}")
    private boolean appEnable;

    @Value("${proj-confs.record-enable:false}")
    private boolean recordEnable;

    @Value("${proj-confs.platform:chaoxing}")
    private List<String> platform;

    @Value("${proj-confs.max-infer-num:6}")
    private Integer maxInferNum;

    @Value("${proj-confs.oss-net:true}")
    private boolean ossNet;

    @Value("${proj-confs.cross-net:false}")
    private boolean crossNet;

    @Value("${proj-confs.cloud_stream_port:554}")
    private Integer cloudStreamPort;

    @Value("${proj-confs.alarm-auto-push:true}")
    private boolean alarmAutoPush;

    @Value("${proj-confs.alarm-auto-push-success-code:0}")
    private int alarmAutoPushSuccessCode;

    @Value("${proj-confs.alarm-auto-audit:true}")
    private boolean alarmAutoAudit;

    @Value("${proj-confs.alarm-drop-key:Xmw65}")
    private String alarmDropKey;

    @Value("${proj-confs.wvp-gb-enable:false}")
    private boolean wvpGbEnable;

    @Value("${proj-confs.login-error-count:5}")
    private Integer loginErrorCount;

    @Value("${proj-confs.login-error-time:300}")
    private Integer loginErrorTime;

    @Value("${proj-confs.login-error-lock-time:15}")
    private Integer loginErrorLockTime;

    @Value("${proj-confs.third-push-video-url:}")
    private String thirdPushVideoUrl;

    @Value("${proj-confs.third-push-video-type:url}")
    private String thirdPushVideoType;
}

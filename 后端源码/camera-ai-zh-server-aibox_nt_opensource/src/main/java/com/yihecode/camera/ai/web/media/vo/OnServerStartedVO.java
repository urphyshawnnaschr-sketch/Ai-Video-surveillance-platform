package com.yihecode.camera.ai.web.media.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
* zlm hook on_server_started Event Param
*
* @author zhoumingxing 465769438@qq.com
* @since 2025/2/12
*/
@Data
public class OnServerStartedVO {

    @JsonProperty("api.apiDebug")
    private String apiDebug;

    @JsonProperty("api.secret")
    private String apiSecret;

    @JsonProperty("ffmpeg.bin")
    private String ffmpegBin;

    @JsonProperty("ffmpeg.cmd")
    private String ffmpegCmd;

    @JsonProperty("ffmpeg.log")
    private String ffmpegLog;

    @JsonProperty("general.mediaServerId")
    private String generalMediaServerId;

    @JsonProperty("general.addMuteAudio")
    private String generalAddMuteAudio;

    @JsonProperty("general.enableVhost")
    private String generalEnableVhost;

    @JsonProperty("general.flowThreshold")
    private String generalFlowThreshold;

    @JsonProperty("general.maxStreamWaitMS")
    private String generalMaxStreamWaitMS;

    @JsonProperty("general.publishToHls")
    private String generalPublishToHls;

    @JsonProperty("general.publishToMP4")
    private String generalPublishToMP4;

    @JsonProperty("general.publishToRtxp")
    private String generalPublishToRtxp;

    @JsonProperty("general.resetWhenRePlay")
    private String generalResetWhenRePlay;

    @JsonProperty("general.streamNoneReaderDelayMS")
    private String generalStreamNoneReaderDelayMS;

    @JsonProperty("general.ultraLowDelay")
    private String generalUltraLowDelay;

    @JsonProperty("hls.fileBufSize")
    private String hlsFileBufSize;

    @JsonProperty("hls.filePath")
    private String hlsFilePath;

    @JsonProperty("hls.segDur")
    private String hlsSegDur;

    @JsonProperty("hls.segNum")
    private String hlsSegNum;

    @JsonProperty("hls.segRetain")
    private String hlsSegRetain;

    @JsonProperty("hook.admin_params")
    private String hookAdminParams;

    @JsonProperty("hook.enable")
    private String hookEnable;

    @JsonProperty("hook.on_flow_report")
    private String hookOnFlowReport;

    @JsonProperty("hook.on_http_access")
    private String hookOnHttpAccess;

    @JsonProperty("hook.on_play")
    private String hookOnPlay;

    @JsonProperty("hook.on_publish")
    private String hookOnPublish;

    @JsonProperty("hook.on_record_mp4")
    private String hookOnRecordMp4;

    @JsonProperty("hook.on_rtsp_auth")
    private String hookOnRtspAuth;

    @JsonProperty("hook.on_rtsp_realm")
    private String hookOnRtspRealm;

    @JsonProperty("hook.on_server_started")
    private String hookOnServerStarted;

    @JsonProperty("hook.on_shell_login")
    private String hookOnShellLogin;

    @JsonProperty("hook.on_stream_changed")
    private String hookOnStreamChanged;

    @JsonProperty("hook.on_stream_none_reader")
    private String hookOnStreamNoneReader;

    @JsonProperty("hook.on_stream_not_found")
    private String hookOnStreamNotFound;

    @JsonProperty("hook.timeoutSec")
    private String hookTimeoutSec;

    @JsonProperty("http.charSet")
    private String httpCharSet;

    @JsonProperty("http.keepAliveSecond")
    private String httpKeepAliveSecond;

    @JsonProperty("http.maxReqCount")
    private String httpMaxReqCount;

    @JsonProperty("http.maxReqSize")
    private String httpMaxReqSize;

    @JsonProperty("http.notFound")
    private String httpNotFound;

    @JsonProperty("http.port")
    private String httpPort;

    @JsonProperty("http.rootPath")
    private String httpRootPath;

    @JsonProperty("http.sendBufSize")
    private String httpSendBufSize;

    @JsonProperty("http.sslport")
    private String httpSslPort;

    @JsonProperty("multicast.addrMax")
    private String multicastAddrMax;

    @JsonProperty("multicast.addrMin")
    private String multicastAddrMin;

    @JsonProperty("multicast.udpTTL")
    private String multicastUdpTTL;

    @JsonProperty("record.appName")
    private String recordAppName;

    @JsonProperty("record.fastStart")
    private String recordFastStart;

    @JsonProperty("record.fileBufSize")
    private String recordFileBufSize;

    @JsonProperty("record.filePath")
    private String recordFilePath;

    @JsonProperty("record.fileRepeat")
    private String recordFileRepeat;

    @JsonProperty("record.fileSecond")
    private String recordFileSecond;

    @JsonProperty("record.sampleMS")
    private String recordSampleMS;

    @JsonProperty("rtmp.handshakeSecond")
    private String rtmpHandshakeSecond;

    @JsonProperty("rtmp.keepAliveSecond")
    private String rtmpKeepAliveSecond;

    @JsonProperty("rtmp.modifyStamp")
    private String rtmpModifyStamp;

    @JsonProperty("rtmp.port")
    private String rtmpPort;

    @JsonProperty("rtp.audioMtuSize")
    private String rtpAudioMtuSize;

    @JsonProperty("rtp.clearCount")
    private String rtpClearCount;

    @JsonProperty("rtp.cycleMS")
    private String rtpCycleMS;

    @JsonProperty("rtp.maxRtpCount")
    private String rtpMaxRtpCount;

    @JsonProperty("rtp.videoMtuSize")
    private String rtpVideoMtuSize;

    @JsonProperty("rtsp.authBasic")
    private String rtspAuthBasic;

    @JsonProperty("rtsp.directProxy")
    private String rtspDirectProxy;

    @JsonProperty("rtsp.handshakeSecond")
    private String rtspHandshakeSecond;

    @JsonProperty("rtsp.keepAliveSecond")
    private String rtspKeepAliveSecond;

    @JsonProperty("rtsp.modifyStamp")
    private String rtspModifyStamp;

    @JsonProperty("rtsp.port")
    private String rtspPort;

    @JsonProperty("rtsp.sslport")
    private String rtspSslPort;

    @JsonProperty("shell.maxReqSize")
    private String shellMaxReqSize;

    @JsonProperty("shell.port")
    private String shellPort;

}

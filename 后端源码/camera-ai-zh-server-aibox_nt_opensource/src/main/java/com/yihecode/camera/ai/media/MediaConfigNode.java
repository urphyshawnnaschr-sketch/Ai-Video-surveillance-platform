package com.yihecode.camera.ai.media;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class MediaConfigNode {

    @JSONField(name = "secret")
    private String secret;

    @JSONField(name = "api.apiDebug")
    private Integer apiDebug = 0;

//@JSONField(name ="ffmpeg.bin")
// private String ffmpegBin ="/usr/bin/ffmpeg";

@JSONField(name ="ffmpeg.cmd")
private String ffmpegCmd ="%s -i %s -an -c:v libx264 -f rtsp %s";

// @JSONField(name ="general.mediaServerId")
// private String generalMediaServerId;

@JSONField(name ="general.addMuteAudio")
private Integer generalAddMuteAudio = 0;

@JSONField(name ="general.enableVhost")
private Integer generalEnableVhost = 0;

@JSONField(name ="hook.enable")
private Integer hookEnable = 1;

@JSONField(name ="hook.on_play")
private String hookOnPlay;

@JSONField(name ="hook.on_publish")
private String hookOnPublish;

@JSONField(name ="hook.on_record_mp4")
private String hookOnRecordMp4;

@JSONField(name ="hook.on_server_started")
private String hookOnServerStarted;

@JSONField(name ="hook.on_stream_changed")
private String hookOnStreamChanged;

@JSONField(name ="hook.on_stream_none_reader")
private String hookOnStreamNoneReader;

@JSONField(name ="hook.on_stream_not_found")
private String hookOnStreamNotFound;

@JSONField(name ="hook.on_server_keepalive")
private String hookOnServerKeepalive;

@JSONField(name ="hook.alive_interval")
private Float hookAliveInterval;

@JSONField(name ="protocol.auto_close")
private Integer protocolAutoClose = 0;

@JSONField(name ="protocol.enable_audio")
private Integer protocolEnableAudio = 0;

@JSONField(name ="protocol.enable_fmp4")
private Integer protocolEnableFmp4 = 0;

@JSONField(name ="protocol.enable_hls")
private Integer protocolEnableHls = 1;

@JSONField(name ="protocol.enable_hls_fmp4")
private Integer protocolEnableHlsFmp4 = 0;

@JSONField(name ="protocol.enable_fmp4")
private Integer protocolEnableMp4 = 0;

@JSONField(name ="protocol.enable_rtmp")
private Integer protocolEnableRtmp = 0;

@JSONField(name ="protocol.enable_rtsp")
private Integer protocolEnableRtsp = 1;

@JSONField(name ="protocol.enable_ts")
private Integer protocolEnableTs = 0;
}
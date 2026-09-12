package com.yihecode.camera.ai.entity.wvp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.eclipse.jgit.internal.storage.file.PackReverseIndex;

@ApiModel(value = "GB Standard Media Node Info")
@Data
@TableName("wvp_media_server")
public class GBMediaServer {

    @TableId
    private String id;

    @TableField("ip")
    private String ip;

    @TableField("hook_ip")
    private String hookIp;

    @TableField("sdp_ip")
    private String sdpIp;

    @TableField("stream_ip")
    private String streamIp;

    @TableField("http_port")
    private Integer httpPort;

    @TableField("http_ssl_port")
    private Integer httpSslPort;

    @TableField("rtmp_port")
    private Integer rtmpPort;

    @TableField("rtmp_ssl_port")
    private Integer rtmpSslPort;

    @TableField("rtp_proxy_port")
    private Integer rtpProxyPort;

    @TableField("rtsp_port")
    private Integer rtspPort;

    @TableField("rtsp_ssl_port")
    private Integer rtspSslPort;

    @TableField("flv_port")
    private Integer flvPort;

    @TableField("flv_ssl_port")
    private Integer flvSslPort;

    @TableField("ws_flv_port")
    private Integer wsFlvPort;

    @TableField("ws_flv_ssl_port")
    private Integer wsFlvSslPort;

    @TableField("auto_config")
    private Integer autoConfig;

    @TableField("secret")
    private String secret;

    @TableField("type")
    private String type;

    @TableField("rtp_enable")
    private Integer rtpEnable;

    @TableField("rtp_port_range")
    private String rtpPortRange;

    @TableField("send_rtp_port_range")
    private String sendRtpPortRange;

    @TableField("record_assist_port")
    private Integer recordAssistPort;

    @TableField("default_server")
    private Integer defaultServer;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;

    @TableField("hook_alive_interval")
    private Integer hookAliveInterval;

    @TableField("record_path")
    private String recordPath;

    @TableField("record_day")
    private Integer recordDay;

    @TableField("transcode_suffix")
    private String transcodeSuffix;
}

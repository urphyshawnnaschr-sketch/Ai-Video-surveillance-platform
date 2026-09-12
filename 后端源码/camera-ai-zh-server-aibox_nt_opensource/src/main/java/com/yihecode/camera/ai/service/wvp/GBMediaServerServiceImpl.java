package com.yihecode.camera.ai.service.wvp;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.MediaServer;
import com.yihecode.camera.ai.entity.wvp.GBMediaServer;
import com.yihecode.camera.ai.mapper.wvp.GBMediaServerMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* GB Standard Media Node Info Management
*/
@Service
public class GBMediaServerServiceImpl extends ServiceImpl<GBMediaServerMapper, GBMediaServer> implements GBMediaServerService {

    /**
* Add Node
*
* @param mediaServer
*/
    @Override
    public void saveData(MediaServer mediaServer) {
        GBMediaServer gbMediaServer = this.getById(mediaServer.getId());
        if(gbMediaServer == null) {
            gbMediaServer = new GBMediaServer();
            gbMediaServer.setId(String.valueOf(mediaServer.getId()));
            gbMediaServer.setIp(mediaServer.getIp());
            gbMediaServer.setHookIp(mediaServer.getIp());
            gbMediaServer.setSdpIp(mediaServer.getIp());
            gbMediaServer.setStreamIp(mediaServer.getIp());
            gbMediaServer.setHttpPort(mediaServer.getHttpPort());
            gbMediaServer.setHttpSslPort(443);
            gbMediaServer.setRtmpPort(1935);
            gbMediaServer.setRtmpSslPort(0);
            gbMediaServer.setRtpProxyPort(10000);
            gbMediaServer.setRtspPort(mediaServer.getRtspPort());
            gbMediaServer.setRtspSslPort(0);
            gbMediaServer.setFlvPort(mediaServer.getHttpPort());
            gbMediaServer.setFlvSslPort(0);
            gbMediaServer.setWsFlvPort(mediaServer.getHttpPort());
            gbMediaServer.setWsFlvSslPort(0);
            gbMediaServer.setAutoConfig(0);
            gbMediaServer.setSecret(mediaServer.getSecret());
            gbMediaServer.setType("zlm");
            gbMediaServer.setRtpEnable(1);
            gbMediaServer.setRtpPortRange(mediaServer.getRtpPortRange());
            gbMediaServer.setSendRtpPortRange(mediaServer.getSendRtpPortRange());
            gbMediaServer.setRecordAssistPort(0);
            gbMediaServer.setDefaultServer(0);
            gbMediaServer.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            gbMediaServer.setUpdateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            gbMediaServer.setHookAliveInterval(10);
            gbMediaServer.setRecordPath("");
            gbMediaServer.setRecordDay(7);
            gbMediaServer.setTranscodeSuffix("");
        } else {
            gbMediaServer = new GBMediaServer();
            gbMediaServer.setIp(mediaServer.getIp());
            gbMediaServer.setHookIp(mediaServer.getIp());
            gbMediaServer.setSdpIp(mediaServer.getIp());
            gbMediaServer.setStreamIp(mediaServer.getIp());
            gbMediaServer.setHttpPort(mediaServer.getHttpPort());
            gbMediaServer.setHttpSslPort(443);
            gbMediaServer.setRtmpPort(1935);
            gbMediaServer.setRtmpSslPort(0);
            gbMediaServer.setRtpProxyPort(10000);
            gbMediaServer.setRtspPort(mediaServer.getRtspPort());
            gbMediaServer.setRtspSslPort(0);
            gbMediaServer.setFlvPort(mediaServer.getHttpPort());
            gbMediaServer.setFlvSslPort(0);
            gbMediaServer.setWsFlvPort(mediaServer.getHttpPort());
            gbMediaServer.setWsFlvSslPort(0);
            gbMediaServer.setAutoConfig(0);
            gbMediaServer.setSecret(mediaServer.getSecret());
            gbMediaServer.setType("zlm");
            gbMediaServer.setRtpEnable(1);
            gbMediaServer.setRtpPortRange(mediaServer.getRtpPortRange());
            gbMediaServer.setSendRtpPortRange(mediaServer.getSendRtpPortRange());
            gbMediaServer.setRecordAssistPort(0);
            gbMediaServer.setDefaultServer(0);
            gbMediaServer.setUpdateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            gbMediaServer.setHookAliveInterval(10);
            gbMediaServer.setRecordPath("");
            gbMediaServer.setRecordDay(7);
            gbMediaServer.setTranscodeSuffix("");
        }
        this.saveOrUpdate(gbMediaServer);
    }
}

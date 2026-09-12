package com.yihecode.camera.ai.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.config.MediaNodeConfig;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.config.WvpConfig;
import com.yihecode.camera.ai.entity.MediaServer;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.MediaServerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
* Stream Media Node Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Service
public class MediaServerServiceImpl extends ServiceImpl<MediaServerMapper, MediaServer> implements MediaServerService {

    @Autowired
    private MediaNodeConfig mediaNodeConfig;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    private WvpConfig wvpConfig;

    /**
* By IP and Port Query
*
* @param ip
* @param httpPort
* @return
*/
    @Override
    public MediaServer getByIpAndPort(String ip, Integer httpPort) {
        LambdaQueryWrapper<MediaServer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaServer::getIp, ip);
        queryWrapper.eq(MediaServer::getHttpPort, httpPort);
        return this.getOne(queryWrapper, false);
    }

    /**
* By Name Query
*
* @param name
* @return
*/
    @Override
    public MediaServer getByName(String name) {
        LambdaQueryWrapper<MediaServer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaServer::getName, name);
        return this.getOne(queryWrapper, false);
    }

    /**
* Init Data
*/
    @Override
    public void initData() {
        MediaServer mediaServer = this.getByIpAndPort(mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort());
        if(mediaServer == null) {
            mediaServer = new MediaServer();
            mediaServer.setName(mediaNodeConfig.getName());
            mediaServer.setIp(mediaNodeConfig.getHttpIp());
            mediaServer.setHttpPort(mediaNodeConfig.getHttpPort());
            mediaServer.setRtspPort(mediaNodeConfig.getRtspPort());
            mediaServer.setRtcPort(mediaNodeConfig.getRtspPort());
            mediaServer.setRtpPortRange(mediaNodeConfig.getRtpPortRange());
            mediaServer.setSendRtpPortRange(mediaNodeConfig.getSendRtpPortRange());
            mediaServer.setSecret(mediaNodeConfig.getSecret());
            this.save(mediaServer);
        }
    }

    /**
* Save Data
*
* @param mediaServer
*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(MediaServer mediaServer) throws BizException {
        //Add or Update Data
this.saveOrUpdate(mediaServer);

// Sync to GB Standard Media Node, for What not Make Complete One Media Node Management? Consider with after part Away
if(projectConfig.isWvpGbEnable()) {
Map<String, Object> params = new HashMap<>();
params.put("id", mediaServer.getName());
params.put("ip", mediaServer.getIp());
params.put("hookIp", wvpConfig.getWvpIp());
params.put("sdpIp", mediaServer.getIp());
params.put("streamIp", mediaServer.getIp());
params.put("httpPort", mediaServer.getHttpPort());
params.put("httpSSlPort", 0);
params.put("rtmpPort", 1935);
params.put("rtmpSSlPort", 0);
params.put("flvPort", mediaServer.getHttpPort());
params.put("flvSSLPort", 0);
params.put("wsFlvPort", mediaServer.getHttpPort());
params.put("wsFlvSSLPort", 0);
params.put("rtpProxyPort", 10000);
params.put("rtspPort", mediaServer.getRtspPort());
params.put("rtspSSLPort", 0);
params.put("autoConfig", 0);
params.put("secret", mediaServer.getSecret());
params.put("hookAliveInterval", 10);
params.put("rtpEnable", 1);
params.put("status", 0);
params.put("rtpPortRange", mediaServer.getRtpPortRange());
params.put("sendRtpPortRange", mediaServer.getSendRtpPortRange());
params.put("recordAssistPort", 0);
params.put("createTime", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
params.put("updateTime", DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
params.put("lastKeepaliveTime", 0);
params.put("defaultServer", 0);
params.put("recordDay", 7);
params.put("recordPath","");
params.put("type","zlm");
params.put("transcodeSuffix","");

String url = String.format("http://%s:%s/api/server/media_server/save", wvpConfig.getWvpIp(), wvpConfig.getWvpPort());

try {
String result = HttpUtil.post(url, JSON.toJSONString(params), 5000);
JSONObject jsonObject = JSON.parseObject(result);
if(!jsonObject.containsKey("code") || jsonObject.getIntValue("code")!= 0) {
throw new BizException("Sync GB Standard Service Media Node Failed, Please Check GB Standard Service Whether Normal");
}
} catch (Exception e) {
throw new BizException("Sync GB Standard Service Media Node Failed, Please Check GB Standard Service Whether Normal");
}

}
}

/**
* Delete Data
*
* @param id
*/
@Override
public void removeData(Long id) throws BizException {
MediaServer mediaServer = this.getById(id);

// Delete Node
this.removeById(id);

// Sync to GB Standard Media Node, for What not Make Complete One Media Node Management? Consider with after part Away
if(projectConfig.isWvpGbEnable() && mediaServer!= null) {
Map<String, Object> params = new HashMap<>();
params.put("id", mediaServer.getName());

String url = String.format("http://%s:%s/api/server/media_server/delete1", wvpConfig.getWvpIp(), wvpConfig.getWvpPort());
try {
String result = HttpUtil.post(url, params, 5000);
log.info("Sync Delete GB Standard Service Media Node, result: {}", result);
JSONObject jsonObject = JSON.parseObject(result);
if(!jsonObject.containsKey("code") || jsonObject.getIntValue("code")!= 0) {
throw new BizException("Sync Delete GB Standard Service Media Node Failed, Please Check GB Standard Service Whether Normal");
}
} catch (Exception e) {
throw new BizException("Sync Delete GB Standard Service Media Node Failed, Please Check GB Standard Service Whether Normal");
}

}
}
}
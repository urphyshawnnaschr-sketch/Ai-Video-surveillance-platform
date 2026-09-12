package com.yihecode.camera.ai.isapi;

import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Camera;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* Hikvision ISAPI API
*/
@Slf4j
@Component
public class ISAPIService {

    private static final String IP_REGEX =
            "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    /**
* Parse Thermal can Force, Parse Channel Value
* @param rtspInfo
* @return
*/
    private ThermalCapXmlResult getThermalCap(RtspInfo rtspInfo) {
        HttpGetResult httpGetResult = httpGet(rtspInfo, "/ISAPI/Thermal/capabilities");
        if(!httpGetResult.isSuccess()) {
            return ThermalCapXmlResult.builder().success(false).error("API Error").build();
        }
        return ThermalCapXmlParser.parse(httpGetResult.getXmlStr());
    }

    /**
* Get Temp Measure Basic Config, main need check View Test Function can Whether Enable
*/
    private BasicParamXmlResult getThermometryBasicParam(RtspInfo rtspInfo, String channelID) {
        HttpGetResult httpGetResult = httpGet(rtspInfo, String.format("/ISAPI/Thermal/channels/%s/thermometry/basicParam", channelID));
        if(!httpGetResult.isSuccess()) {
            return BasicParamXmlResult.builder().success(false).error(httpGetResult.getError()).build();
        }
        return BasicParamXmlParser.readEnabled(httpGetResult.getXmlStr());
    }

    /**
* Set Temp Measure Basic Config, main need Set Enable Test Function can
*/
    private HttpPutResult setThermometryBasicParam(RtspInfo rtspInfo, String xml, String channelID) {
        return httpPut(rtspInfo, String.format("/ISAPI/Thermal/channels/%s/thermometry/basicParam", channelID), xml);
    }

    /**
* Get Temp Measure Linkage Mode, main need check View Report in Core Linkage Mode Whether via Set
*/
    private NotificationMethodXmlResult getThermometryNotificationMethod(RtspInfo rtspInfo, String channelID) {
        HttpGetResult httpGetResult = httpGet(rtspInfo, String.format("/ISAPI/Event/triggers/thermometry-%s/preset/1", channelID));
        if(!httpGetResult.isSuccess()) {
            return NotificationMethodXmlResult.builder().success(false).error(httpGetResult.getError()).xmlStr("empty XML").build();
        }
        return NotificationMethodXmlParser.checkCenter(httpGetResult.getXmlStr());
    }

    /**
* Set Temp Measure Linkage Mode, main need Set Linkage Mode for Report in Core
*/
    private HttpPutResult setThermometryNotificationMethod(RtspInfo rtspInfo, String xml, String channelID) {
        return httpPut(rtspInfo, String.format("/ISAPI/Event/triggers/thermometry-%s/preset/1", channelID), xml);
    }

    /**
* Get Network Alarm Server Config
*/
    private AlarmHostsXmlResult getNetworkAlarmHosts(RtspInfo rtspInfo) {
        HttpGetResult httpGetResult = httpGet(rtspInfo, "/ISAPI/Event/notification/httpHosts");
        if(!httpGetResult.isSuccess()) {
            return AlarmHostsXmlResult.builder().success(false).error(httpGetResult.getError()).xmlStr("").build();
        }
        return AlarmHostsXmlParser.check(httpGetResult.getXmlStr());
    }

    /**
* Set Network Alarm Server
*/
    private void setNetworkAlarmHosts() {

    }

    /**
* Common HTTP GET Request
* @param rtspInfo
* @param url
* @return
*/
    private HttpGetResult httpGet(RtspInfo rtspInfo, String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            String reqUrl = String.format("http://%s%s", rtspInfo.getIp(), url);
            HttpGet request = new HttpGet(reqUrl);

            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(rtspInfo.getUsername(), rtspInfo.getPassword()));

            httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(provider)
                    .build();
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return HttpGetResult.builder().success(true).error("OK").xmlStr(EntityUtils.toString(entity)).build();
                } else {
                    return HttpGetResult.builder().success(false).error("no Result Entity Info").xmlStr("").build();
                }
            }
        } catch (ConnectException e) {
            return HttpGetResult.builder().success(false).error("Camera no Method Connection").xmlStr("").build();
        } catch (Exception e) {
            //e.printStackTrace();
return HttpGetResult.builder().success(false).error("Camera Connection Exception, ex:"+ e.getMessage()).xmlStr("").build();
}
}

/**
* Common HTTP PUT Request
* @param rtspInfo
* @param url
* @param xml
* @return
*/
private HttpPutResult httpPut(RtspInfo rtspInfo, String url, String xml) {
CloseableHttpClient httpClient = HttpClients.createDefault();
try {
String reqUrl = String.format("http://%s%s", rtspInfo.getIp(), url);
HttpPut request = new HttpPut(reqUrl);

CredentialsProvider provider = new BasicCredentialsProvider();
provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(rtspInfo.getUsername(), rtspInfo.getPassword()));

// List<Header> defaultHeaders = Arrays.asList(
// new BasicHeader("Content-Type","application/json")
//);

StringEntity stringEntity = new StringEntity(xml, StandardCharsets.UTF_8);
request.setEntity(stringEntity);

httpClient = HttpClients.custom()
.setDefaultCredentialsProvider(provider)
//.setDefaultHeaders(defaultHeaders)
.build();
try (CloseableHttpResponse response = httpClient.execute(request)) {
HttpEntity entity = response.getEntity();
if (entity!= null) {
String returnXml = EntityUtils.toString(entity);
boolean success = HttpPutResultParser.parse(returnXml);
if(success) {
return HttpPutResult.builder().success(true).returnXml(returnXml).build();
} else {
return HttpPutResult.builder().success(false).returnXml(returnXml).build();
}
} else {
return HttpPutResult.builder().success(false).returnXml("no Back XML").build();
}
} catch (Exception e) {
log.error("put Operation Exception 1, rtspInfo: {}", rtspInfo, e);
return HttpPutResult.builder().success(false).returnXml(e.getMessage()).build();
}
} catch (Exception e) {
// e.printStackTrace();
log.error("put Operation Exception 2, rtspInfo: {}", rtspInfo, e);
return HttpPutResult.builder().success(false).returnXml(e.getMessage()).build();
}
}

/**
* Set Temp Measure Phase close Config
* @param camera
*/
public boolean setConfig(Camera camera, String hostIp, String hostPort, String hostUrl) {
RtspInfo rtspInfo = parseRtspInfo(camera.getRtspUrl());
if(rtspInfo == null) {
log.error("Set Camera Temp Measure Config Error,RTSP Address Parse Error, no Method Parse out User, Password,IP Address, Please Check RTSP, cameraID: {}, cameraName: {}, RTSP{}", camera.getId(), camera.getName(), camera.getRtspUrl());
return false;
}

// Parse Thermal can Force, Get Channel
ThermalCapXmlResult thermalCapXmlResult = getThermalCap(rtspInfo);
if(!thermalCapXmlResult.isSuccess()) {
log.error("Parse Camera Thermal can Force XML Error, cameraID: {}, cameraName: {}, error: {}, xml: {}", camera.getId(), camera.getName(), thermalCapXmlResult.getError(), thermalCapXmlResult.getXmlStr());
return false;
}

// check View Temp Measure Function can Whether Enable
BasicParamXmlResult basicParamXmlResult = getThermometryBasicParam(rtspInfo, thermalCapXmlResult.getChannelID());
if(!basicParamXmlResult.isSuccess()) {
log.error("Parse Camera Temp Measure Config XML Error, cameraID: {}, cameraName: {}, error: {}, xml: {}", camera.getId(), camera.getName(), basicParamXmlResult.getError(), basicParamXmlResult.getXmlStr());
return false;
}

// Modify Enable Temp Measure Function can
if(!"true".equalsIgnoreCase(basicParamXmlResult.getEnabled())) {
// Get Modify XML
BasicParamXmlResult basicParamXmlResult1 = BasicParamXmlParser.modifyXml(basicParamXmlResult.getXmlStr());
if(basicParamXmlResult1 == null) {
log.error("Modify Camera Temp Measure Config XML Error, cameraID: {}, cameraName: {}, error: {}, xml: {}", camera.getId(), camera.getName(), basicParamXmlResult.getError(), basicParamXmlResult.getXmlStr());
return false;
}

if(!basicParamXmlResult1.isSuccess()) {
log.error("Modify Camera Temp Measure Config XML Error, cameraID: {}, cameraName: {}, error: {}, xml: {}", camera.getId(), camera.getName(), basicParamXmlResult.getError(), basicParamXmlResult.getXmlStr());
return false;
}

// Update Temp Measure Basic Config
HttpPutResult putResult = setThermometryBasicParam(rtspInfo, basicParamXmlResult1.getModifyXml(), thermalCapXmlResult.getChannelID());
if(!putResult.isSuccess()) {
log.error("Update Temp Measure Basic Config Error, cameraID: {}, cameraName: {}, returnXml: {}", camera.getId(), camera.getName(), putResult.getReturnXml());
return false;
}
log.error("Update Temp Measure Basic Config Success, cameraID: {}, cameraName: {}, returnXml: {}", camera.getId(), camera.getName(), putResult.getReturnXml());
} else {
log.error("Temp Measure Enable Function can via Enable, cameraID: {}, cameraName: {}", camera.getId(), camera.getName());
}

// via Enable Test, down One Step, Check Whether Set Linkage Mode

// Check Linkage Mode Whether via Set Upload in Core
NotificationMethodXmlResult notificationMethodXmlResult = getThermometryNotificationMethod(rtspInfo, thermalCapXmlResult.getChannelID());
if(!notificationMethodXmlResult.isSuccess()) {
log.error("Check Linkage Mode Failed, Back,cameraID: {}, cameraName: {},error: {}, xml: {}", camera.getId(), camera.getName(), notificationMethodXmlResult.getError(), notificationMethodXmlResult.getXmlStr());
return false;
}

// not has Set' Upload in Core' Linkage Mode, rule in Line Set
if(!notificationMethodXmlResult.isHasCenter()) {
// Get Modify XML
NotificationMethodXmlResult notificationMethodXmlResult1 = NotificationMethodXmlParser.modifyXml(notificationMethodXmlResult.getXmlStr());
if(!notificationMethodXmlResult1.isSuccess()) {
log.error("Linkage Mode Set Error, cameraID: {}, cameraName: {}, error: {}, xml: {}", camera.getId(), camera.getName(), notificationMethodXmlResult1.getError(), notificationMethodXmlResult1.getXmlStr());
return false;
}

// Update Temp Measure Basic Config
HttpPutResult putResult = setThermometryNotificationMethod(rtspInfo, notificationMethodXmlResult1.getModifyXml(), thermalCapXmlResult.getChannelID());
if(!putResult.isSuccess()) {
log.error("Linkage Mode Set Error, cameraID: {}, cameraName: {}, returnXml: {}", camera.getId(), camera.getName(), putResult.getReturnXml());
return false;
}
log.error("Linkage Mode Set Success, cameraID: {}, cameraName: {}, returnXml: {}", camera.getId(), camera.getName(), putResult.getReturnXml());
} else {
log.error("Linkage Mode via Set, cameraID: {}, cameraName: {}", camera.getId(), camera.getName());
}

// Linkage Mode Set Complete Finish, down One Step Set Network -> Alarm Server

AlarmHostsXmlResult alarmHostsXmlResult = getNetworkAlarmHosts(rtspInfo);
if(!alarmHostsXmlResult.isSuccess()) {
log.error("Check Network Alarm Server Failed, Parse XML Error, Back,cameraID: {}, cameraName: {},error: {}, xml: {}", camera.getId(), camera.getName(), alarmHostsXmlResult.getError(), alarmHostsXmlResult.getXmlStr());
return false;
}

// not has Parse out Alarm Server Node
if(alarmHostsXmlResult.getAlarmHostsList() == null || alarmHostsXmlResult.getAlarmHostsList().isEmpty()) {
log.error("Check Network Alarm Server Failed, not has Parse to Alarm Server Node, Back,cameraID: {}, cameraName: {},error: {}, xml: {}", camera.getId(), camera.getName(), alarmHostsXmlResult.getError(), alarmHostsXmlResult.getXmlStr());
return false;
}

//
List<AlarmHosts> alarmHostsList = alarmHostsXmlResult.getAlarmHostsList();
for(AlarmHosts alarmHosts: alarmHostsList) {
if(hostIp.equalsIgnoreCase(alarmHosts.getIpAddress()) && hostPort.equalsIgnoreCase(alarmHosts.getPortNo()) && hostUrl.equalsIgnoreCase(alarmHosts.getUrl())) {
log.info("Network Alarm Server via Set, Back,cameraID: {}, cameraName: {}", camera.getId(), camera.getName());
return true;
}
}

// Determine Whether Need Update
String updateId = null;
for(AlarmHosts alarmHosts: alarmHostsList) {
// only need IP and Address has One Item Match allocate, rule Recognize for is Need Modify
if(hostIp.equalsIgnoreCase(alarmHosts.getIpAddress()) || hostUrl.equalsIgnoreCase(alarmHosts.getUrl())) {
updateId = alarmHosts.getId();
break;
}
}

// not has Match allocate History Node
if(updateId == null) {
// find out One All for 0.0.0.0 Address in Line Config
for(AlarmHosts alarmHosts: alarmHostsList) {
if("0.0.0.0".equalsIgnoreCase(alarmHosts.getIpAddress())) {
updateId = alarmHosts.getId();
break;
}
}
}

// not has Node
if(updateId == null) {
log.error("Alarm Server All by Occupy Use, no Method Confirm Config, Back,cameraID: {}, cameraName: {}", camera.getId(), camera.getName());
return false;
}

//
AlarmHostsXmlResult alarmHostsXmlResult1 = AlarmHostsXmlParser.modifyXml(alarmHostsXmlResult.getXmlStr(), updateId, hostIp, hostPort, hostUrl);
if(!alarmHostsXmlResult1.isSuccess()) {
log.error("Modify Alarm Server XML Failed, Modify XML Error, Back,cameraID: {}, cameraName: {},error: {}, xml: {}", camera.getId(), camera.getName(), alarmHostsXmlResult1.getError(), alarmHostsXmlResult1.getXmlStr());
}

HttpPutResult putResult = httpPut(rtspInfo,"/ISAPI/Event/notification/httpHosts", alarmHostsXmlResult1.getModifyXml());
if(!putResult.isSuccess()) {
log.error("Modify Alarm Server Failed, cameraID: {}, cameraName: {}, returnXml: {}", camera.getId(), camera.getName(), putResult.getReturnXml());
return false;
}
log.error("Modify Alarm Server Success, cameraID: {}, cameraName: {}, returnXml: {}", camera.getId(), camera.getName(), putResult.getReturnXml());
return true;
}

/**
* Get rtsp in ip Address
* @param rtspUrl
* @return
*/
private static RtspInfo parseRtspInfo(String rtspUrl) {
if(StrUtil.isBlank(rtspUrl)) {
return null;
}

// Parse IP Address
String ip = null;
Pattern pattern = Pattern.compile(IP_REGEX);
Matcher matcher = pattern.matcher(rtspUrl);
if(matcher.find()) {
ip = matcher.group();
}

// not has Parse out IP Address
if(StrUtil.isBlank(ip)) {
return null;
}

// Non rtsp Protocol, Direct connect Back
if (!rtspUrl.toLowerCase().startsWith("rtsp://")) {
return null;
}

// Parse out rtsp:// User Name and Password
int pos = rtspUrl.lastIndexOf("@");
if (pos <= 0) {
return null;
}

// rtsp:// User Name: Password
String prefixString = rtspUrl.substring(0, pos);

// Parse out User Name: Password
String usernameAndPassword = prefixString.substring(7);
int pos1 = usernameAndPassword.indexOf(":");

// Parse User Name Error
if (pos1 <= 0) {
return null;
}

// Parse User
String username = usernameAndPassword.substring(0, pos1);

// Parse out Password
String password = usernameAndPassword.substring(pos1 + 1);

// not has Parse out User Name and Password
if(StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
return null;
}

//
RtspInfo rtspInfo = new RtspInfo();
rtspInfo.setIp(ip);
rtspInfo.setUsername(username);
rtspInfo.setPassword(password);
return rtspInfo;
}

public static void main(String[] args) {
log.info("{}", parseRtspInfo("rtsp://admin:hkws@1234@192.168.3.64/Streaming/Channels/101"));
log.info("{}", parseRtspInfo("rtsp://admin@10.0.0.1:554/Streaming/Channels/101"));
log.info("{}", parseRtspInfo("rtsp://10.0.0.1:554/Streaming/Channels/101"));
}
}

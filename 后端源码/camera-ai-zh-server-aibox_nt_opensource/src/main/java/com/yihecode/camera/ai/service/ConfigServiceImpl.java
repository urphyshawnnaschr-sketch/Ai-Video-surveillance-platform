package com.yihecode.camera.ai.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.Config;
import com.yihecode.camera.ai.mapper.ConfigMapper;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* System Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    /**
*
* @param tag
* @return
*/
    @Override
    @Cacheable(value = "configs", key = "#tag")
    public String getByValTag(String tag) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getTag, tag);

        //
Config config = this.getOne(queryWrapper, false);
if(config == null || config.getVal() == null) {
return"";
}
return config.getVal();
}

/**
* Clear Divide Cache
*
* @param tag
*/
@Override
@CacheEvict(value ="configs", key ="#tag")
public void evictByTag(String tag) {
//
}

/**
* Init Data
*/
@Override
public void initData(String hostIp, String hostIpFile) {
if(StrUtil.isBlank(hostIp)) {
hostIp ="127.0.0.1";
logger.info("not Get HOST_IP Config, Fixed Meaning ip for 127.0.0.1");

}

if(!hasTag("ipAddr")) {
addTag("IP Address + Port","ipAddr", hostIp +":8021");
}

if(!hasTag("webUrl")) {
addTag("in Station WS Address","webUrl","http://"+ hostIp +":8021");
}

if(!hasTag("wsUrl")) {
addTag("in Station WS Address","wsUrl","ws://"+ hostIp +":8021");
}

if(!hasTag("streamType")) {
addTag("Video Stream Type","streamType","rtsp");
}

if(!hasTag("mediaUrl")) {
addTag("Media Server Address","mediaUrl","http://"+ hostIp +":80");
}

if(!hasTag("playUrl")) {
addTag("Media Play Put Address","playUrl","http://"+ hostIp +":80");
}

if(!hasTag("pushIp")) {
addTag("Stream Media Push Stream IP","pushIp", hostIp);
}

if(!hasTag("pushPort")) {
addTag("Stream Media Push Stream Port","pushPort","554");
}

if(!hasTag("nginxEnable")) {
addTag("Media nginx Forward","nginxEnable","false");
}

if(!hasTag("nginxPort")) {
addTag("Media nginx Port","nginxPort","");
}

if(!hasTag("maxWindow")) {
addTag("Play Put most big Window Number","maxWindow","6");
}

if(!hasTag("weworkUrl")) {
addTag("WeWork Bot Address","weworkUrl","");
}

if(!hasTag("weworkEnable")) {
addTag("WeWork Bot Enable","weworkEnable","false");
}

if(!hasTag("dingdingUrl")) {
addTag("DingTalk Group Bot Address","dingdingUrl","");
}

if(!hasTag("dingdingSign")) {
addTag("DingTalk Group Bot Add Sign","dingdingSign","");
}

if(!hasTag("dingdingEnable")) {
addTag("DingTalk Group Bot Enable","dingdingEnable","false");
}

if(!hasTag("algorithmUrl")) {
addTag("Algorithm Remote Call Address","algorithmUrl","http://"+ hostIp +":8000/api/safety/predict");
}

if(!hasTag("smsEnable")) {
addTag("Third Party Alert Push Address","reportPushUrl","");
}

if(!hasTag("smsEnable")) {
addTag("Third Party Alert Push Image","reportPushImage","false");
}

if(!hasTag("smsEnable")) {
addTag("Whether Push SMS","smsEnable","false");
}

if(!hasTag("appName")) {
addTag("should Use Name","appName","AI Video Monitor Platform");
}

if(!hasTag("screenName")) {
addTag("big Screen can View Change Name","screenName","AI Video Monitor can View Change Platform");
}

if(!hasTag("aiboxKey")) {
addTag("Box key Value","aiboxKey","n2S8jh*V5");
}

if(!hasTag("TRACKER_ENABLE")) {
addTag("Whether Enable Person Stream Quantity","TRACKER_ENABLE","false");
}

if(!hasTag("HTTP_FRAME_ENABLE")) {
addTag("Whether Enable Frame Extract","HTTP_FRAME_ENABLE","false");
}

if(!hasTag("SERVER_TYPE")) {
addTag("Server Type (1-PC Server, 2- Calculate Box)","SERVER_TYPE","2");
}

if(!hasTag("secret")) {
addTag("Stream Media Server Password","secret","123456");
}
if(!hasTag("mediaServer")) {
addTag("Stream Media Server Type","mediaServer","zlm");
}
if(!hasTag("INNER_IP")) {
addTag("inner net IP","INNER_IP", hostIp);
} else {
update(getUpdateWapper(hostIp,"INNER_IP"));
}

// Delete User Hour Determine Whether Use at this Some Menu Permission, has Word, not Allow Delete
String delAccountCheckMenus ="algorithmManagement,noticeManagement,systemManagement";
if(!hasTag("delAccountCheckMenus")) {
addTag("Delete User Hour Validate Menu","delAccountCheckMenus", delAccountCheckMenus);
} else {
Config config = this.getByTag("delAccountCheckMenus");
if(StrUtil.isBlank(config.getVal())) {// cannot be empty, can with Modify Complete,,,
addTag("Delete User Hour Validate Menu","delAccountCheckMenus", delAccountCheckMenus);
}
}

// By config.js File Modify
String configJSIp = getHostIp(hostIpFile);
if(StrUtil.isNotBlank(configJSIp)){

update(getUpdateWapper(configJSIp +":8021","ipAddr"));
logger.info("IP Address + Port ipAddr Update");

update(getUpdateWapper("http://"+ configJSIp +":8021","webUrl"));
logger.info("in Station WS Address webUrl Update");

update(getUpdateWapper("ws://"+ configJSIp +":8021","wsUrl"));
logger.info("in Station WS Address wsUrl Update");

update(getUpdateWapper("http://"+ configJSIp +":80","mediaUrl"));
logger.info("Media Server Address mediaUrl Update");

update(getUpdateWapper("http://"+ configJSIp +":80","playUrl"));
logger.info("Media Play Put Address playUrl Update");

update(getUpdateWapper(configJSIp,"pushIp"));
logger.info("Stream Media Push Stream IPpushIp Update");

update(getUpdateWapper("http://"+ configJSIp +":8000/api/safety/predict","algorithmUrl"));
logger.info("Algorithm Remote Call Address algorithmUrl Update");

update(getUpdateWapper(configJSIp,"INNER_IP"));
logger.info("inner net IP");
}

}

/**
* Config Whether Exist
* @param tag
* @return
*/
private boolean hasTag(String tag) {
LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Config::getTag, tag);
Config config = this.getOne(queryWrapper, false);
if(config == null) {
return false;
}
return true;
}

/**
* Add Config
* @param name
* @param tag
* @param val
*/
private void addTag(String name, String tag, String val) {
Config config = new Config();
config.setName(name);
config.setTag(tag);
config.setVal(val);
this.save(config);
}

/**
* By tag Query
*
* @param tag
* @return
*/
@Override
public Config getByTag(String tag) {
LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Config::getTag, tag);
return this.getOne(queryWrapper, false);
}

/**
* Save Data
*
* @param name
* @param tag
* @param val
*/
@Override
public void saveData(String name, String tag, String val) {
Config config = this.getByTag(tag);
if(config == null) {
config = new Config();
config.setName(name);
config.setTag(tag);
config.setVal(val);
this.save(config);
} else {
config.setVal(val);
this.saveOrUpdate(config);
}
}

// Read config.js Get ip
public String getHostIp(String hostIpFile){
File config = new File(hostIpFile);
String hostIp = null;
try {
BufferedReader reader = new BufferedReader(new FileReader(config));
StringBuilder stringBuilder=new StringBuilder();
String line=null;
while((line=reader.readLine())!=null){
stringBuilder.append(line);
break;
}
logger.info(stringBuilder.toString());
reader.close();
// Fixed Meaning URL Correct rule table Reach style Mode
String regex ="(\\d{1,3}\\.){3}\\d{1,3}";
Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(stringBuilder);
while (matcher.find()) {
hostIp = matcher.group();
break;
}
logger.info("Pass config.js Get to ip:"+hostIp);
} catch (Exception e) {
logger.error(e.getMessage());
logger.error("not Pass config.js Get to ip");
return"";
}
return hostIp;
}

public UpdateWrapper<Config> getUpdateWapper(String val, String tag){
UpdateWrapper<Config> updateWrapper = Wrappers.update();
updateWrapper.set("val",val);
updateWrapper.eq("tag",tag);
return updateWrapper;
}

/**
* Get outer net IP
*
* @return
*/
@Override
public String getOutIp() {
String ipAddr = this.getByValTag("ipAddr");
String[] sp = ipAddr.split(":");
if(sp.length!= 2) {
return"127.0.0.1";
}
return sp[0];
}
}
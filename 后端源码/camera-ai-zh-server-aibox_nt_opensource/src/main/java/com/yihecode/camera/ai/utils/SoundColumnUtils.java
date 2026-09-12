package com.yihecode.camera.ai.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
* IP Speaker Pole Utils
*/
@Slf4j
public class SoundColumnUtils {

    /**
* Adjust whole Audio Quantity
*
* @param server
* @param sn
* @param vol
*/
    public static void sendVol(String server, String sn, Integer vol) {
        //
log.info("Speaker Pole Audio Quantity Request Param: server: {}, sn: {}, vol: {}", server, sn, vol);
String[] serverInfo = server.split(":");
String ip = serverInfo[0];
String port = serverInfo[1];
if (StrUtil.isBlank(ip) || StrUtil.isBlank(port)) {
log.error("Audio Quantity Adjust whole - Speaker Pole Server Set Error, ip: {}, port: {}", ip, port);
return;
}
Socket socket = null;
InputStream input = null;
OutputStream output = null;
//
try {
Integer _port = Integer.valueOf(port);
socket = new Socket(ip, _port);

// Send Command
output = socket.getOutputStream();
JSONObject object = new JSONObject();
object.put("mode", 1009);
object.put("vol", vol);
object.put("vols", vol);
object.put("snlist", Arrays.asList(sn));
log.info("Audio Quantity Adjust whole - Send Data to Speaker Pole Server: {}", object);
String params = object.toString();
String sendParams = params.length() +"\n"+ params;
output.write(sendParams.getBytes());

// connect Receive Reply
input = socket.getInputStream();
StringBuilder sb = new StringBuilder();
byte[] b = new byte[1024];
int nIdx = 0;
int nTotalLen = b.length;
// Read Receive Two in make Info Stream
int len = input.read(b, nIdx, nTotalLen - nIdx);
if (len > 0) {
String data = new String(b, 0, len);
sb.append(data);
log.info("Audio Quantity Adjust whole - receive to Server Message:"+ data);
}
log.info("Speaker Pole Audio Quantity Send Complete Finish");
} catch (Exception e) {
log.error("Audio Quantity Adjust whole - Speaker Pole Audio Quantity Adjust whole Failed, ex: {}", e.getMessage());
} finally {
if (output!= null) {
try {
output.close();
} catch (IOException e) {
e.printStackTrace();
}
}
if (input!= null) {
try {
input.close();
} catch (IOException e) {
e.printStackTrace();
}
}
if (socket!= null) {
try {
socket.close();
} catch (IOException e) {
e.printStackTrace();
}
}
}
}

/**
* Send Play Put
*
* @param server
* @param sn
* @param mp3file
*/
public static void sendPlay(String server, String sn, String mp3file) {
log.info("Speaker Pole Play Put Request Param: server: {}, sn: {}, mp3file: {}", server, sn, mp3file);
//
String[] serverInfo = server.split(":");
String ip = serverInfo[0];
String port = serverInfo[1];
if (StrUtil.isBlank(ip) || StrUtil.isBlank(port)) {
log.error("Send Play Put - Speaker Pole Server Set Error, ip: {}, port: {}", ip, port);
System.out.println("Speaker Pole Server Set Error");
return;
}
Socket socket = null;
InputStream input = null;
OutputStream output = null;
//
try {
Integer _port = Integer.valueOf(port);
socket = new Socket(ip, _port);

// Send Command
output = socket.getOutputStream();
JSONObject object = new JSONObject();
object.put("cmd","PLAYOFF");
object.put("filelist", Arrays.asList(mp3file));
object.put("snlist", Arrays.asList(sn));
log.info("Send Play Put - Send Data to Speaker Pole Server: {}", object);
System.out.println("Send Play Put - Send Data to Speaker Pole Server:"+ object.toString());
String params = object.toString();
String sendParams = params.length() +"\n"+ params;
output.write(sendParams.getBytes());

// connect Receive Reply
input = socket.getInputStream();
StringBuilder sb = new StringBuilder();
byte[] b = new byte[1024];
int nIdx = 0;
int nTotalLen = b.length;
// Read Receive Two in make Info Stream
int len = input.read(b, nIdx, nTotalLen - nIdx);
if (len > 0) {
String data = new String(b, 0, len);
sb.append(data);
log.info("Send Play Put - receive to Server Message:{}", data);
}
log.info("Speaker Pole Play Put Send End");
} catch (Exception e) {
log.error("Send Play Put - Play Put Failed, ex: {}", e.getMessage());
} finally {
if (output!= null) {
try {
output.close();
} catch (IOException e) {
//e.printStackTrace();
}
}
if (input!= null) {
try {
input.close();
} catch (IOException e) {
//e.printStackTrace();
}
}
if (socket!= null) {
try {
socket.close();
} catch (IOException e) {
//e.printStackTrace();
}
}
}
}

/**
* Send Yuelang Speaker Pole Play Put
*
* @param server
* @param sn
* @param mp3file
* @param userName
* @param password
*/
public static void sendYueLangPlay(String server, String sn, String mp3file, String userName, String password, Integer vol) {
log.info("Speaker Pole Play Put Request Param: server: {}, sn: {}, mp3file: {}", server, sn, mp3file);
if (StrUtil.isBlank(server) || StrUtil.isBlank(userName) || StrUtil.isBlank(password)) {
log.error("Send Play Put - Speaker Pole Server Set Error, server: {}, userName: {}, password: {}", server, userName, password);
return;
}
try {
// Call Login
Map<String, Object> params = new HashMap<String, Object>();
params.put("username", userName);
params.put("password", password);
String result = HttpPostUtils.doPostJson("http://"+ server +"/v1/login", params, null);
log.info("Speaker Pole Login Result: username: {}, password: {}, result: {}", userName, password, result);
JSONObject resultObject = JSONObject.parseObject(result);
int code = Integer.parseInt(resultObject.get("code").toString());
if (code!= 200) {
return;
}
JSONObject valueResult = (JSONObject) resultObject.get("value");
String token = valueResult.get("token").toString();
if (StrUtil.isBlank(token)) {
return;
}
// Call Send
Map<String, String> speechHeaderParams = new HashMap<String, String>();
speechHeaderParams.put("access_token", token);
Map<String, Object> speechParams = new HashMap<String, Object>();
List<String> codeList = new ArrayList<String>();
codeList.add(sn);
speechParams.put("device_codes", codeList);
speechParams.put("url", mp3file);
speechParams.put("sync", false);
speechParams.put("queue", true);
speechParams.put("volume", vol);
speechParams.put("prompt", false);
String speechResult = HttpPostUtils.doPostJson("http://"+ server +"/v1/speech", speechParams, speechHeaderParams);
log.info("Speaker Pole Send Result: speechResult: {}", speechResult);
} catch (Exception e) {
log.error("Send Play Put - Play Put Failed, ex: {}", e.getMessage());
}
}

}

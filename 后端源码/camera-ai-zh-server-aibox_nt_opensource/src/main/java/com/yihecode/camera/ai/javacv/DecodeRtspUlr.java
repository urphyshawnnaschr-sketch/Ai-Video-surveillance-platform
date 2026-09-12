package com.yihecode.camera.ai.javacv;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
* rtsp Password Protect Reason
*
* @author zhoumingxing
* @since 2025/2/12
*/
public class DecodeRtspUlr {

    public static String processRtspUrl(String rtspUrl) {
        try {
            if (StrUtil.isBlank(rtspUrl)) {
                return rtspUrl;
            }

            //Non rtsp Protocol, Direct connect Back
if (!rtspUrl.toLowerCase().startsWith("rtsp://")) {
return rtspUrl;
}

// Parse out rtsp:// User Name and Password
int pos = rtspUrl.lastIndexOf("@");
if (pos <= 0) {
return rtspUrl;
}

// rtsp:// User Name: Password
String prefixString = rtspUrl.substring(0, pos);

// Parse out User Name: Password
String usernameAndPassword = prefixString.substring(7);
int pos1 = usernameAndPassword.indexOf(":");

// Parse User Name Error
if (pos1 <= 0) {
return rtspUrl;
}

// Parse out Password
String password = usernameAndPassword.substring(pos1 + 1);

// Password in Line Code
String decodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());

// Splice connect new Address
return"rtsp://"+ usernameAndPassword.substring(0, pos1) +":"+ decodedPassword + rtspUrl.substring(pos);
} catch (Exception e) {
return rtspUrl;
}
}

/**
* for Camera User and Password in Line Desensitize Process
* @param rtspUrl
* @return
*/
public static String processSensitive(String rtspUrl) {
try {
if (StrUtil.isBlank(rtspUrl)) {
return"";
}

// Non rtsp Protocol, Direct connect Back
if (!rtspUrl.toLowerCase().startsWith("rtsp://")) {
return rtspUrl;
}

// Parse out rtsp:// User Name and Password
int pos = rtspUrl.lastIndexOf("@");
if (pos <= 0) {
return rtspUrl;
}

// rtsp:// User Name: Password
String prefixString = rtspUrl.substring(0, pos);

// Parse out User Name: Password
String usernameAndPassword = prefixString.substring(7);
int pos1 = usernameAndPassword.indexOf(":");

// Parse User Name Error
if (pos1 <= 0) {
return rtspUrl;
}

// Parse out Password
String password = usernameAndPassword.substring(pos1 + 1);

// Password in Line Code
String decodedPassword = URLUtil.encode(password, StandardCharsets.UTF_8);

// Splice connect new Address
return"rtsp://******:******"+ rtspUrl.substring(pos);
} catch (Exception e) {
return rtspUrl;
}
}

public static void main(String[] args) throws Exception {

String url1 ="rtsp://admin:p#ssw0rd@192.168.1.100/stream";
String url2 ="rtsp://user:pass@word@10.0.0.1/live";
String url3 ="rtsp://aadmin:j2wj@#w@host";
String url4 ="rtsp://invalid:pass+@host";

System.out.println(processRtspUrl(url1));
System.out.println();
System.out.println(processRtspUrl(url2));
System.out.println();
System.out.println(processRtspUrl(url3));
System.out.println();
System.out.println(processRtspUrl(url4));

String encodedPassword = URLEncoder.encode("abc+", StandardCharsets.UTF_8.toString());
System.out.println(encodedPassword);
}
}

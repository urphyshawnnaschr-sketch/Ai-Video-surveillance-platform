package com.yihecode.camera.ai.notify.wework;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
* Description: WeWork Bot Send Message https://open.work.weixin.qq.com/help2/pc/18401
* https://developer.work.weixin.qq.com/document/path/91770?version=4.1.0.70174&platform=mac
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
public class WeWorkRobotSendUtils {

    /**
* Send Text Message
*
* @param content
*/
    public static void sendText(String reqUrl, String content) {
        //
Map<String, Object> contentMap = new HashMap<>();
contentMap.put("content", content);

//
Map<String, Object> params = new HashMap<>();
params.put("msgtype","text");
params.put("text", contentMap);

try {
String response = HttpUtil.post(reqUrl, JSON.toJSONString(params));
//log.info("Group Bot Send Text Message", response);
} catch (Exception e) {
log.error("Group Bot Send Text Message Exception", e);
}

}

/**
* Send image Text Message
*
* @param title
* @param url
* @param picurl
*/
public static void sendTextAndImage(String reqUrl, String title, String description, String url, String picurl) {
List<Map<String, Object>> articles = new ArrayList<>();

//
Map<String, Object> article = new HashMap<>();
article.put("title", title);
if (StringUtils.isNotBlank(description)) {
article.put("description", description);
}
article.put("url", url);
article.put("picurl", picurl);
articles.add(article);

//
Map<String, Object> news = new HashMap<>();
news.put("articles", articles);

//
Map<String, Object> params = new HashMap<>();
params.put("msgtype","news");
params.put("news", news);

//
try {
//log.info("WeWork Group Send image Text Message:{}", params);
String response = HttpUtil.post(reqUrl, JSON.toJSONString(params), 5000);
//log.info("WeWork Group Bot Send image Text Message Reply: {}", response);
} catch (Exception e) {
log.info("WeWork Group Group Bot Send Image Message Exception: {}", e.getMessage());
}
}

public static void main(String[] args) {
WeWorkRobotSendUtils.sendTextAndImage(
"https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=a67300d7-xxxxx",
"Camera Name:1 No database Pile Scene",
"Monitor Content: Object Occupies Lane,\n Alert Content: send current Object Occupies Lane,\n Alert Time:"
+ DateUtil.format(new Date(),"yyyy Year MM Month dd Day HH Hour mm part ss s"),
"http://xxxx.com/report/detail?id=1742413033878650882",
"http://xxxx.com/report/stream?id=1742413033878650882");
}
}

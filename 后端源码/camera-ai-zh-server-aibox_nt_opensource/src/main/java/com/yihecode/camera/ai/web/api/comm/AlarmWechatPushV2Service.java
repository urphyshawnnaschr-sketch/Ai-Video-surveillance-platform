package com.yihecode.camera.ai.web.api.comm;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.notify.wework.WeWorkRobotSendUtils;
import com.yihecode.camera.ai.service.SocialResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
* Alarm WeWork Push
*
* @author 465769438@qq.com
* @since 2025/3/27
*/
@Slf4j
@Component
public class AlarmWechatPushV2Service {

    @Autowired
    private SocialResultService socialResultService;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    public void send(SocialHook socialHook, String title, String content, String pageUrl, String imgUrl, String cameraName, String algorithmName, Long reportId) {
        CompletableFuture.runAsync(() -> {
            AlarmPushResult alarmPushResult = this.send(socialHook, title, content, pageUrl, imgUrl);

            //Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(socialHook.getId());
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl(imgUrl);
socialResult.setState(alarmPushResult.isSuccess()? 0: 1);
socialResult.setErrorDetail(alarmPushResult.getErrorDetail());
socialResult.setSendText(title +"###"+ content);
socialResult.setCameraName(cameraName);
socialResult.setAlgorithmName(algorithmName);
socialResult.setReportId(reportId);
socialResultService.save(socialResult);
}, executor);
}

private AlarmPushResult send(SocialHook socialHook, String title, String content, String pageUrl, String imgUrl) {
try {
List<Map<String, Object>> articles = new ArrayList<>();

//
Map<String, Object> article = new HashMap<>();
article.put("title", title);
if (StringUtils.isNotBlank(content)) {
article.put("description", content);
}
article.put("url", pageUrl);
article.put("picurl", imgUrl);
articles.add(article);

//
Map<String, Object> news = new HashMap<>();
news.put("articles", articles);

//
Map<String, Object> params = new HashMap<>();
params.put("msgtype","news");
params.put("news", news);

//
String response = HttpUtil.post(socialHook.getWebhook(), JSON.toJSONString(params), 5000);
JSONObject object = JSON.parseObject(response);
if(object.containsKey("errcode") && object.getIntValue("errcode") == 0) {
return AlarmPushResult.builder().success(true).build();
} else {
return AlarmPushResult.builder().success(false).error("WeWork Return Code Error").errorDetail(response).build();
}
} catch (Exception e) {
log.error("WeWork Send Exception", e);
return AlarmPushResult.builder().success(false).error("WeWork Send Failed").errorDetail(e.getMessage()).build();
}
}

}

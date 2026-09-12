package com.yihecode.camera.ai.web.api.face;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;


/**
* Face Alarm Third Party Push
*
* @author zhoumingxing
* @date 2024/8/14
*/
@Slf4j
@Component
public class ApiFaceReportThirdPushService {

    @Async
    public void push(String url, JSONObject params, boolean toBase64) {
        int statusCode = -1;
        HttpEntity httpEntity = null;
        try {
            //
if(toBase64) {
try {
File file = new File(params.getString("img_path"));
String imageBase64 = ImgUtil.toBase64(ImgUtil.read(file), FileUtil.extName(file));
params.put("imageBase64", imageBase64);
} catch (Exception e) {
params.put("imageBase64","");
}
} else {
params.put("imageBase64","");
}

CloseableHttpClient client = HttpClients.createDefault();
//
HttpPost httpPost = new HttpPost(url);
httpPost.addHeader("Accept-Encoding","gzip, deflate, br");
httpPost.addHeader("Content-Type","application/json");
httpPost.setEntity(new StringEntity(params.toString(),"UTF-8"));

//
RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(500).build();
httpPost.setConfig(requestConfig);

//
CloseableHttpResponse response = client.execute(httpPost);
statusCode = response.getStatusLine().getStatusCode();
httpEntity = response.getEntity();

//
if (statusCode!= 200) {
log.error("Call Third Party Report Face Info API Status Exception status:{}, url:{}, camera:{}, algorithm:{}, response:{}", statusCode, url, params.getString("camera_name"), params.getString("algorithm_name"), EntityUtils.toString(httpEntity));
} else {
log.info("Call Third Party Report Face Info API API Status Success status:{}, url:{}, camera:{}, algorithm:{}, response:{}", statusCode, url, params.getString("camera_name"), params.getString("algorithm_name"), EntityUtils.toString(httpEntity));
}

response.close();
client.close();
} catch (Exception e) {
log.error("Call Third Party Report Face Info API Exception {}, ex:{}", url, e.getMessage());
} finally {
try {
EntityUtils.consume(httpEntity);
} catch (Exception e) {}
}
}

}
package com.yihecode.camera.ai.web.api.aibox;

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

/**
* Person Stream Quantity Alarm Third Party Push
*
* @author zhoumingxing
* @date 2024/8/14
*/
@Slf4j
@Component
public class ApiTrackerReportThirdPushService {

    @Async
    public void push(String url, JSONObject params) {
        int statusCode = -1;
        HttpEntity httpEntity = null;
        try {
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
log.error("Call Third Party Report Data (tracker) API Status Exception status:{}, url:{}, camera:{}, algorithm:{}, response:{}", statusCode, url, params.getString("camera_name"), params.getString("algorithm_name"), EntityUtils.toString(httpEntity));
} else {
log.info("Call Third Party Report Data (tracker) API API Status Success status:{}, url:{}, camera:{}, algorithm:{}, response:{}", statusCode, url, params.getString("camera_name"), params.getString("algorithm_name"), EntityUtils.toString(httpEntity));
}

response.close();
client.close();
} catch (Exception e) {
log.error("Call Third Party Report Data (tracker) API API Exception {}, ex:{}", url, e.getMessage());
} finally {
try {
EntityUtils.consume(httpEntity);
} catch (Exception e) {}
}
}

}

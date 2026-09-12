package com.yihecode.camera.ai.notify.voice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsResponse;
import com.aliyun.sdk.service.dyvmsapi20170525.AsyncClient;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;

import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
* Description: Voice Notification Send Utils
*/
@Slf4j
public class SendVoiceUtil {

    private static final String ENCODING = "UTF-8";
    private static long lastCalledTime = 0;
    private static String endpoint = "dyvmsapi.aliyuncs.com";

    /**
* @param mobiles
* @param cameraName
* @param algorithmName
*/
    public static void send(String mobiles, String ttsCode, String content, String appId,
            String appSecret) {
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCalledTime < 3600000) {
                return;
            } else {
                Config config = new Config()
                        .setAccessKeyId(appId)
                        .setAccessKeySecret(appSecret);
                config.endpoint = endpoint;
                Client client = new Client(config);
                SingleCallByTtsRequest singleCallByTtsRequest = new SingleCallByTtsRequest()
                        .setTtsCode(ttsCode)
                        .setCalledNumber(mobiles)
                        .setTtsParam("{\"content\":" + content + "}");
                try {
                    //Copy Code Run Please self Line Print API Back Value
SingleCallByTtsResponse singleCallByTtsResponse =
client.singleCallByTtsWithOptions(singleCallByTtsRequest, new RuntimeOptions());
log.info("Voice Call Result:"+ singleCallByTtsResponse.getBody().toString());
} catch (TeaException error) {
// this Place Only Make Print show show, Please Cautious for Pending Exception Process, In work Process Project in Cut Do Not Direct connect Ignore Exception.
// Error message
// System.out.println(error.getMessage());
// Diagnose Break Address
// System.out.println(error.getData().get("Recommend"));
com.aliyun.teautil.Common.assertAsString(error.message);
} catch (Exception _error) {
TeaException error = new TeaException(_error.getMessage(), _error);
// this Place Only Make Print show show, Please Cautious for Pending Exception Process, In work Process Project in Cut Do Not Direct connect Ignore Exception.
// Error message
// System.out.println(error.getMessage());
// Diagnose Break Address
// System.out.println(error.getData().get("Recommend"));
com.aliyun.teautil.Common.assertAsString(error.message);
}
}
// Execute Method Operation
lastCalledTime = currentTime;
} catch (Exception e) {
log.error("voicePush error", e);
}

}

public static void sendAsync(List<String> mobiles, String ttsCode, String content, String appId, String appSecret) {
try {
long currentTime = System.currentTimeMillis();
if (currentTime - lastCalledTime < 3600000) {
return;
} else {
for (String mobile: mobiles) {
StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
.accessKeyId(appId)
.accessKeySecret(appSecret)
.build());
AsyncClient client = AsyncClient.builder()
.region("cn-zhangjiakou") // Region ID
.credentialsProvider(provider)
.overrideConfiguration(
ClientOverrideConfiguration.create()
.setEndpointOverride("dyvmsapi.aliyuncs.com"))
.build();
com.aliyun.sdk.service.dyvmsapi20170525.models.SingleCallByTtsRequest singleCallByTtsRequest =
com.aliyun.sdk.service.dyvmsapi20170525.models.SingleCallByTtsRequest.builder()
.ttsParam("{\"content\":\""+ content +"\"}")
.ttsCode(ttsCode)
.calledNumber(mobile)
.speed(-200)
.playTimes(4)
.build();
CompletableFuture<com.aliyun.sdk.service.dyvmsapi20170525.models.SingleCallByTtsResponse> response =
client.singleCallByTts(singleCallByTtsRequest);
com.aliyun.sdk.service.dyvmsapi20170525.models.SingleCallByTtsResponse resp = response.get();
// System.out.println(new Gson().toJson(resp));
}

}
} catch (Exception e) {
log.error("voicePush error: {}", e.getMessage());
}
}

public static void main(String[] args) {
List<String> phoneList = new ArrayList<String>();
phoneList.add("13393746311");
phoneList.add("18301606030");
sendAsync(phoneList,"acc","XXX Bit set, Alert Content: not Wear Wear Safe all Cap, Alert Time:2024 Year 3 Month 28 No 10 Point 01 part 45 s",
"acc",
"acc");
}

}

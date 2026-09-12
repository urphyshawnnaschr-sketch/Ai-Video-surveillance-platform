package com.yihecode.camera.ai.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

/**
* Push Data to net site Group
*/
public class HttpPostUtils {

    public static final String MEDIA_TYPE_MULTIPART = "multipart/form-data";
    public static final String MEDIA_TYPE_TEXT = "text/plain;charset=utf-8";

    public static String doPostFormData(String url, String fileKey, File file,
            Map<String, Object> paramMap, Map<String, String> headerMap) {
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(MEDIA_TYPE_MULTIPART));
            if (headerMap != null) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    httpHeaders.add(entry.getKey(), entry.getValue());
                }
            }
            params.add(fileKey, new FileSystemResource(file));
            if (paramMap != null) {
                for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                    params.add(entry.getKey(), entry.getValue());
                }
            }
            //Send Request
HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, httpHeaders);
RestTemplate rt = new RestTemplate();
String result = rt.exchange(url, HttpMethod.POST, request, String.class).getBody();
return result;
} catch (Exception e) {
e.printStackTrace();
}
return null;
}

public static String doPostJson(String url, Map<String, Object> paramMap, Map<String, String> headerMap) {
try {
HttpHeaders httpHeaders = new HttpHeaders();
httpHeaders.setContentType(MediaType.APPLICATION_JSON);
if (headerMap!= null) {
for (Map.Entry<String, String> entry: headerMap.entrySet()) {
httpHeaders.add(entry.getKey(), entry.getValue());
}
}
// Send Request
HttpEntity<Map<String, Object>> request = new HttpEntity<>(paramMap, httpHeaders);
RestTemplate rt = new RestTemplate();
String result = rt.exchange(url, HttpMethod.POST, request, String.class).getBody();
return result;
} catch (Exception e) {
e.printStackTrace();
}
return null;
}

public static void main(String[] args) {
// File file = new File("C:\\stream.jpg");
// Map<String, Object> params = new HashMap<String, Object>();
// params.put("point_x","0");
// params.put("point_y","123");
// params.put("imgId","stream.jpg");
// String result =
// HttpPostUtils.doPostFormData("http://192.168.1.102:36896/ai_infer","img_file", file, params, null);
// System.out.println(result);

Map<String, Object> params = new HashMap<String, Object>();
params.put("username","admin");
params.put("password","123456");

String result = HttpPostUtils.doPostJson("http://192.168.1.208:8080/v1/login", params, null);
System.out.println(result);
JSONObject resultObject = JSONObject.parseObject(result);
int code = Integer.parseInt(resultObject.get("code").toString());
System.out.println(code);
JSONObject valueResult = (JSONObject) resultObject.get("value");
String token = valueResult.get("token").toString();
System.out.println(token);

// Call Send
Map<String, String> speechHeaderParams = new HashMap<String, String>();
speechHeaderParams.put("access_token", token);
Map<String, Object> speechParams = new HashMap<String, Object>();
List<String> codeList = new ArrayList<String>();
codeList.add("EC5C55B9");
speechParams.put("device_codes", codeList);
speechParams.put("url","http://192.168.1.30:8021/algorithm/sound/stream?id=1696809711436365825");
speechParams.put("sync", false);
speechParams.put("queue", true);
speechParams.put("volume", 1);
speechParams.put("prompt", false);
String speechResult =
HttpPostUtils.doPostJson("http://192.168.1.208:8080/v1/speech", speechParams, speechHeaderParams);
System.out.println(speechResult);
}
}
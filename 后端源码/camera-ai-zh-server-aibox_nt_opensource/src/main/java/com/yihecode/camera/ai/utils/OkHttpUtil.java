package com.yihecode.camera.ai.utils;

import com.yihecode.camera.ai.entity.HttpResponse;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
* @Author lichangliang
* @Date 2023/6/21 23:58
* @Describe
* @Version 1.0
*/
@Component
public class OkHttpUtil {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=UTF-8");
    private static final MediaType MEDIA_TYPE_MULTIPART = MediaType.parse("multipart/form-data");
    private static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    @Autowired
    private OkHttpClient okHttpClient;


    public HttpResponse postForm(String url, Map<String, String> headerMap, Map<String, String> paramMap, Map<String, File> fileMap) throws RuntimeException {


        RequestBody requestBody = null;
        if (MapUtils.isNotEmpty(fileMap)) {
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();

            fileMap.forEach((key, file) ->
                    multipartBodyBuilder.addFormDataPart(
                            key, file.getName(), RequestBody.create(MEDIA_TYPE_MULTIPART, file)
                    )
            );

            if (MapUtils.isNotEmpty(paramMap)) {
                paramMap.forEach((key, value) ->
                        multipartBodyBuilder.addFormDataPart(key, value)
                );
            }

            requestBody = multipartBodyBuilder.build();
        } else if (MapUtils.isNotEmpty(paramMap)) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();

            paramMap.forEach((key, value) ->
                    formBodyBuilder.add(key, value)
            );

            requestBody = formBodyBuilder.build();
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();

            requestBody = formBodyBuilder.build();
        }

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);

        if (MapUtils.isNotEmpty(headerMap)) {
            headerMap.forEach((key, value) ->
                    requestBuilder.addHeader(key, value)
            );
        }

        Request request = requestBuilder
                .method("POST", requestBody)
                .build();
        return execute(request);
    }
    public HttpResponse postJson(String url, Map<String, String> headerMap, String paramJsonString) throws RuntimeException {

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, paramJsonString);

        if (MapUtils.isNotEmpty(headerMap)) {
            headerMap.forEach((key, value) ->
                    requestBuilder.addHeader(key, value)
            );
        }

        Request request = requestBuilder
                .post(requestBody)
                .build();
        return this.execute(request);
    }
    private HttpResponse execute(
            Request request
    ) {
        HttpResponse httpResponse = new HttpResponse();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response == null) {
                return null;
            }


            httpResponse.setHttpStatus(response.code());

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return httpResponse;
            }


            httpResponse.setStringData(responseBody.string());
            return httpResponse;

        } catch (SocketTimeoutException e) {
            throw new RuntimeException("Upload Timeout");
        } catch (Exception e) {
            throw new RuntimeException(StringUtils.isEmpty(e.getMessage()) ? "Upload Exception" : e.getMessage());
        }
    }
}

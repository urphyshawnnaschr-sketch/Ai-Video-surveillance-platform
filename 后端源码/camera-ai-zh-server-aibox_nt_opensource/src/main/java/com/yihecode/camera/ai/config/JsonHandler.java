package com.yihecode.camera.ai.config;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.util.ArrayList;
import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/21 14:28
* @Describe
* @Version 1.0
*/
public class JsonHandler extends JacksonTypeHandler {
    public JsonHandler (Class<?> type) {
        super(type);
    }


    @Override
    protected List<String> parse(String json) {
        List<String> jsons = new ArrayList<>();
        try {
            jsons = JSONObject.parseArray(json, String.class);
        } catch (JSONException e) {
            jsons.add(JSONObject.parseObject(json, String.class));
        }
        return  jsons;
    }
}

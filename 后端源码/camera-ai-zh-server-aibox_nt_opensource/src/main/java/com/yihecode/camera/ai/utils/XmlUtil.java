package com.yihecode.camera.ai.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

/**
* @Author lichangliang
* @Date 2023/7/26 22:53
* @Describe
* @Version 1.0
*/
public class XmlUtil {
    public static String objectToXml(Object object) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setDefaultUseWrapper(false);
        /*Field for null, self Dynamic Ignore, not again order Column Change*/
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        /*Set Convert Mode*/
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String resultXml = xmlMapper.writeValueAsString(object);

        return resultXml;
    }

}

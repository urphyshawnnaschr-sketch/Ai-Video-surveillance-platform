package com.yihecode.camera.ai.dto.xml;

import lombok.Data;

import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/26 22:58
* @Describe
* @Version 1.0
*/
@Data
public class XmlObject {
    private String folder;
    private String fileName;
    private Source source;
    private Size size;
    private List<AnnoObject> object;
}

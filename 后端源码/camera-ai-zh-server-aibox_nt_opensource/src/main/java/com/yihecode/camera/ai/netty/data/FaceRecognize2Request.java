package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

/**
* Face Recognition Search
*/
public class FaceRecognize2Request extends Request {

    @JSONField(name = "image_name")
    private String imageName;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}

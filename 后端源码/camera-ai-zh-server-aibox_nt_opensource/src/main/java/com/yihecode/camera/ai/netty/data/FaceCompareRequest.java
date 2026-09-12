package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

/**
* Face than for
*/
public class FaceCompareRequest extends Request {

    @JSONField(name = "image1")
    private String image1;

    @JSONField(name = "image2")
    private String image2;

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }
}

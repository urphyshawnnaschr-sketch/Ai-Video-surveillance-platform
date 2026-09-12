package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

/**
* Face Recognition Search
*/
public class FaceRecognizeRequest extends Request {

    @JSONField(name = "search_id")
    private Long searchId;

//public FaceRecognizeRequest() {
// super();
//}

public Long getSearchId() {
return searchId;
}

public void setSearchId(Long searchId) {
this.searchId = searchId;
}
}

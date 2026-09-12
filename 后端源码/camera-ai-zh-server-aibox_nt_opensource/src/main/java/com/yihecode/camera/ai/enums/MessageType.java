package com.yihecode.camera.ai.enums;

/**
* websocket Message Type
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public enum MessageType {

    HEART("HEART", "Heartbeat Message"),
    STREAM("STREAM", "Video Stream Message"),
    REPORT("REPORT", "Alert Message");

    private String type;

    private String content;

    MessageType(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

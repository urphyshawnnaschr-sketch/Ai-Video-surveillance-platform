package com.yihecode.camera.ai.netty.data;

/**
* Time Type
*/
public enum MessageType {

    REGISTER("register", "Register"),
    HEARTBEAT("heartbeat", "Heartbeat"),
    ADD_CAMERA("add_camera", "Add Camera"),
    DEL_CAMERA("del_camera", "Delete Camera"),
    UPGRADE_ALGO("algo_upgrade", "Algorithm Upgrade"),
    RESTART("restart_infer", "Restart Inference Service"),
    ACTIVATION_STATUS("activation_status", "Query Device Whether Activate"),
    DEVICE_SERIAL("device_serial", "Get Device order Column"),
    ACTIVATE_DEVICE("activate_device", "Activate Device"),
    IMAGE_CAPTURE("image_capture", "Get Image"),
    STREAM_PUSHER("stream_pusher", "to Cloud End Push Stream"),
    STREAM_CLOSE("stream_close", "Close Cloud End Push Stream"),
    STREAM_RECORD("stream_record", "Cloud End Record make"),
    ALGO_EXTRAS("algo_extras", "Expand Param Update"),
    DEL_FILES("del_files", "Delete Algorithm File"),
    FACE_RECOGNIZE("face_recognize", "Face Recognition"),
    FACE_RECOGNIZE2("face_recognize2", "Face Recognition"),
    FACE_COMPARE("face_compare", "Face than for");

    private String type;
    private String text;

    MessageType(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static MessageType getMessageType(String type) {
        MessageType[] messageTypes = MessageType.values();
        for(MessageType messageType : messageTypes) {
            if(messageType.getType().equalsIgnoreCase(type)) {
                return messageType;
            }
        }
        return null;
    }
}

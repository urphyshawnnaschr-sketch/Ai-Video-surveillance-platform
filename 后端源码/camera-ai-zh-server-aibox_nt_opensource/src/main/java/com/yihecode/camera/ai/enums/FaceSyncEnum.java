package com.yihecode.camera.ai.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Face Sync Type
*
* @author zhou
* @since 2025-07-10
*/
public enum FaceSyncEnum {

    ADD_FACE("add_face", "Add User"),
    CLEAR_FACE("clear_face", "Delete Face"),
    CLEAR_USER("clear_user", "Delete User"),
    CLEAR_GROUP("clear_group", "Delete Group"),
    QUERY_GROUP_LIST("query_group_list", "Query Group List"),
    QUERY_USER_LIST("query_user_list", "Query User List");

    //
private String code;
private String text;

FaceSyncEnum(String code, String text) {
this.code = code;
this.text = text;
}

public String getCode() {
return code;
}

public void setCode(String code) {
this.code = code;
}

public String getText() {
return text;
}

public void setText(String text) {
this.text = text;
}

}

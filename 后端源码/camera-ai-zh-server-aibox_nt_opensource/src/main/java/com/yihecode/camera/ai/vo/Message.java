package com.yihecode.camera.ai.vo;

import lombok.Data;

/**
* first page Alert Push socket Message Body
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
public class Message {

    private String type;

    private String content;

    private Object data;
}

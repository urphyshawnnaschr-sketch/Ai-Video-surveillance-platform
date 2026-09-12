package com.yihecode.camera.ai.utils;

import lombok.Data;

import java.util.List;

/**
* Tree result structure - jstree need Request Tree result structure
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Data
public class TreeResult {

    private String meId;

    private String text;

    private String icon;

    private String parent;

    private String type;

    private List<TreeResult> children;
}

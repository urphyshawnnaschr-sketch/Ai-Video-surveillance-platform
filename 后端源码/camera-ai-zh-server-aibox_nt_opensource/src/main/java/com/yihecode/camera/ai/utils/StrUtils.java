package com.yihecode.camera.ai.utils;

import cn.hutool.core.util.StrUtil;

/**
* Description: String Utils
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public class StrUtils {

    /**
* Hide String
* @return
*/
    public static String hide(String source) {
        if(StrUtil.isBlank(source)) {
            return "";
        }

        //
int len = source.length();
if(len == 1 || len == 2) {
return"**";
}
//
if(len == 3) {
return source.substring(0, 1) +"**";
}
//
if(len <= 5) {
return source.substring(0, 1) +"**"+ source.substring(len - 2);
}
//
if(len < 9) {
return source.substring(0, 2) +"***"+ source.substring(len - 2);
}
//
if(len >= 9) {
return source.substring(0, 3) +"****"+ source.substring(len - 3);
}

return"******";
}

/**
* Color 16 in make String turn rgb String
* @param hexStr eg.#fff4500
* @return
*/
public static String hex2rgb(String hexStr) {
return hex2rgb(hexStr, 1);
}

/**
* Color 16 in make String turn rgb String
* @param hexStr 16 in make Color Value,eg.#fff4500
* @param alpha Transparent Degree
* @return
*/
public static String hex2rgb(String hexStr, float alpha) {
if(hexStr!= null &&!"".equals(hexStr) && hexStr.length() == 7){
int r = Integer.valueOf(hexStr.substring(1, 3), 16);
int g = Integer.valueOf(hexStr.substring(3, 5), 16);
int b = Integer.valueOf(hexStr.substring(5, 7), 16);
return"rgba("+ r +","+ g +","+ b +","+ alpha +")";
}
return"rgba(0, 0, 0,"+ alpha +")";
}
}

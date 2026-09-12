package com.yihecode.camera.ai.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
* Login Password Encrypt Process
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
public class PassUtils {

    /**
* Encrypt original start Password
* @param str
* @return
*/
    public static String encrypt(String str) {
        if(StrUtil.isBlank(str)) {
            return "";
        }
        return DigestUtil.sha256Hex(str);
    }

    /**
* Decrypt Password
* @param str
* @param t
* @return
*/
    public static String decrypt(String str, String t) {
        String a1 = "c";
        String s2 = "$";
        String p3 = a1 + str + s2 + "#" + t;
        return encrypt(p3);
    }

    public static void main(String[] args) {
        System.out.println(PassUtils.encrypt("66$"));
        System.out.println(decrypt("5aac43e4518629d67e15985959b874fc10758b45ddaecca482bc71efa7829d09", "1735390285650"));
    }
}

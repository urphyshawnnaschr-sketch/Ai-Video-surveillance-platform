package com.yihecode.camera.ai.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
* External Access Utils
*
* @auhtor 465769438@qq.com
* @since 2025/8/14
*/
public class ExternalAccessUtils {

    private static final String AK = "&_(zGb6+GErD_P^^HL";

    /**
* For Alarm Generate Access key
* @return
*/
    public static String encrypt(Long reportId, Long timestamp) {
        return DigestUtil.md5Hex(String.format("%s|%s|%s", AK, reportId, timestamp));
    }

    /**
* Decrypt than for
* @param reportId
* @param timestamp
* @param encryptStr
* @return
*/
    public static boolean decrypt(Long reportId, Long timestamp, String encryptStr) {
        String en = encrypt(reportId, timestamp);
        return en.equals(encryptStr);
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        System.out.println(t);
        System.out.println(ExternalAccessUtils.encrypt(1955963151016325121L, t));
    }
}

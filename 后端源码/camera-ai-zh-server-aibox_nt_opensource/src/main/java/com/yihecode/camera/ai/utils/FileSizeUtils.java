package com.yihecode.camera.ai.utils;

/**
* Description: for File size in Line Format Change Display for B,KB,MB,GB etc
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public class FileSizeUtils {

    /**
* Format Change File Size
* @param filesize
* @return
*/
    public static String formatSize(long filesize) {
        if(filesize < 1024) {
            return filesize + "B";
        } else if((filesize / 1024) < 1024) {
            return Long.valueOf(filesize / 1024) + "KB";
        } else if((filesize / 1024 / 1024) < 1024) {
            return Long.valueOf(filesize / 1024 / 1024) + "MB";
        } else {
            return Long.valueOf(filesize / 1024 / 1024 / 1024) + "GB";
        }
    }

}

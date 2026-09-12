package com.yihecode.camera.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Video Play Put Stream
*
* @author 465769438@qq.com
* @since 2025/4/10
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamUrlDTO {

    /**
* Status, 200- Success,300- Camera Delete,301- Stream Address not Config,302- not Relate Box, 303- Pull Stream Failed, 304- turn code Failed
*/
    private int code;

    /**
* Stream Address
*/
    private String msg;

    /**
* Play Put Stream Format
*/
    private String codec;

    /**
* Play Put Stream Address
*/
    private String url;

    /**
* H264 Play Put
* @param url
* @return
*/
    public static StreamUrlDTO h264(int code, String msg, String url) {
        return new StreamUrlDTO(code, msg, "H264", url);
    }

    /**
* H265 Play Put
* @param url
* @return
*/
    public static StreamUrlDTO h265(int code, String msg, String url) {
        return new StreamUrlDTO(code, msg, "H265", url);
    }
}

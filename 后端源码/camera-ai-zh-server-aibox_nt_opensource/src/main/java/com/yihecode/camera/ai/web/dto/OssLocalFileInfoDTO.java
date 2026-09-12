package com.yihecode.camera.ai.web.dto;

import lombok.Data;

/**
* oss and Local File than for Info
*/
@Data
public class OssLocalFileInfoDTO {

    private String name;

    private String md5Str;

    private Long localLength;

    private String localSize;

    private Long length;

    private String size;
}

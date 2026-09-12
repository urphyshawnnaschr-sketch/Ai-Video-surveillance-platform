package com.yihecode.camera.ai.web.api.aibox.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
* Upload Recording File
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
@Data
public class UploadRecordFileVo {

    private Long recordId;

    private MultipartFile file;

    private String key;
}

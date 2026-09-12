package com.yihecode.camera.ai.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Collect Alarm Data Export
*
* @author 465769438@qq.com
* @since 2025/3/15
*/
@Data
public class ReportCollectExportVo {

    private Long cameraId;

    private Long algorithmId;

    private List<Long> departIds;

    private List<Long> reportIds;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}

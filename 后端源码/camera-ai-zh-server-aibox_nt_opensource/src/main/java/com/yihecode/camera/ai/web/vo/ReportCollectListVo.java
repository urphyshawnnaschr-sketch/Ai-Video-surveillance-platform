package com.yihecode.camera.ai.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
* Alarm Data Collect Query
*
* @author 465769438@qq.com
* @since 2025/3/15
*/
@Data
public class ReportCollectListVo {

    /**
* Camera ID
*/
    private Long algorithmId;

    /**
* Camera ID
*/
    private Long cameraId;

    /**
* Box IDs
*/
    private List<Long> locationIds;

    /**
* Start Time
*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Instant startTime;

    /**
* End Time
*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Instant endTime;

    /**
* Page Number
*/
    private Integer page = 1;

    /**
* Pagination Count
*/
    private Integer limit = 12;
}

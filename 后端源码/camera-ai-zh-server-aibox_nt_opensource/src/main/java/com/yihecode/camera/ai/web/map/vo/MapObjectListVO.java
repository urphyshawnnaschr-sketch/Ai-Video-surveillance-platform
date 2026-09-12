package com.yihecode.camera.ai.web.map.vo;

import lombok.Data;

import java.util.List;

@Data
public class MapObjectListVO {

    private Integer type = 0;

    private Long mapId;

    private List<Long> objectIds;

    private String objectName;

    private Integer level;

}

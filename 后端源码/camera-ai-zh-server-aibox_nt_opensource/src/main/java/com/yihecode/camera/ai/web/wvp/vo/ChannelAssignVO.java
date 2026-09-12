package com.yihecode.camera.ai.web.wvp.vo;

import lombok.Data;

import java.util.List;

/**
* Channel Assign to Box / Server
*/
@Data
public class ChannelAssignVO {

    /**
* Channel Ids
*/
    private List<Long> channelIds;

    /**
* Box / Server ID
*/
    private Long boxId;
}

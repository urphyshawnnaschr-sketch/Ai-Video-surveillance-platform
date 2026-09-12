package com.yihecode.camera.ai.web.wvp.dto;

import lombok.Data;

/**
* Box Info and Relate Camera Info
*/
@Data
public class LocationChannelCountDTO {

    //Box ID
private Long id;

// Box Name
private String name;

// Channel Count
private Integer channelCount;

}

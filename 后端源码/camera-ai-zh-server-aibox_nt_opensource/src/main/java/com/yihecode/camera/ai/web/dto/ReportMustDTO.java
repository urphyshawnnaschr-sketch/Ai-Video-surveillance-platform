package com.yihecode.camera.ai.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Alarm Data Process Status Query
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportMustDTO {

    //Whether Need Manual Process
private boolean mustAudit;

// Whether Need Manual Push
private boolean mustPush;
}

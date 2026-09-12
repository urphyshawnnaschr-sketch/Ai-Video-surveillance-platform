package com.yihecode.camera.ai.dto.hr;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@ApiModel(value = "hr User Info")
@Data
public class HrUserInfo {
    /**
* User ID
*/
    private String userId;

    /**
* User according Piece
*/
    private MultipartFile photo;

    /**
* work No
*/
    private String workCode;

    /**
* User Name
*/
    private String userName;

    /**
* Department ID
*/
    private String deptId;

    /**
* Department Code
*/
    private String deptCode;

    /**
* Department Name
*/
    private String deptName;

    /**
* Job Bit ID
*/
    private String positionId;

    /**
* Job Bit Name
*/
    private String positionName;

    /**
* Phone code
*/
    private String cellPhone;

    /**
* User Status (1- In Job 5- Away Job)
*/
    private String userStatus;
}

package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
* Face Person member - Face Recognition System
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Face Person member")
@Data
@TableName("tbl_biz_face_user")
public class FaceUser {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Person member Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Contact Phone")
    @TableField("tel")
    private String tel;

    @ApiModelProperty(value = "Remark")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "Group ID")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "Delete Status")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;

    /**
* Image List
*/
    @TableField(exist = false)
    private List<FaceImage> faceImages;

    /**
* The belong Group
*/
    @TableField(exist = false)
    private FaceGroup faceGroup;

    /**
* work No
*/
    @ApiModelProperty(value = "work No")
    @TableField("work_code")
    private String workCode;

    /**
* Department ID
*/
    @ApiModelProperty(value = "Department ID")
    @TableField("dept_id")
    private String deptId;

    /**
* Department Code
*/
    @ApiModelProperty(value = "Department Code")
    @TableField("dept_code")
    private String deptCode;

    /**
* Department Name
*/
    @ApiModelProperty(value = "Department Name")
    @TableField("dept_name")
    private String deptName;

    /**
* Job Bit ID
*/
    @ApiModelProperty(value = "Job Bit ID")
    @TableField("position_id")
    private String positionId;

    /**
* Job Bit Name
*/
    @ApiModelProperty(value = "Job Bit Name")
    @TableField("position_name")
    private String positionName;

    /**
* User Status (1- In Job 5- Away Job)
*/
    @ApiModelProperty(value = "User Status (1- In Job 5- Away Job)")
    @TableField("user_status")
    private String userStatus;
    /**
* User Status (1- In Job 5- Away Job)
*/
    @ApiModelProperty(value = "User Source:hr, aicv")
    @TableField("source")
    private String source;

}
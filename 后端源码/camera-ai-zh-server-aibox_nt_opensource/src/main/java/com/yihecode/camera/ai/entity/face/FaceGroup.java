package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Face Group - Face Recognition System
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Face Group")
@Data
@TableName("tbl_biz_face_group")
public class FaceGroup {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Group Name Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Delete Status")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @ApiModelProperty(value = "User Count")
    @TableField(exist = false)
    private Integer userCount;
}
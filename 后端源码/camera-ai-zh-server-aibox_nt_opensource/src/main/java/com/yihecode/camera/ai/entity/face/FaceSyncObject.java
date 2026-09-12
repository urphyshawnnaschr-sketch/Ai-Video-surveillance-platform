package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Face Sync Object table
* @author zhou
* @since 2025-07-110
*/
@ApiModel(value = "Face Sync Object table")
@Data
@TableName("tbl_biz_face_sync_object")
public class FaceSyncObject {

    @ApiModelProperty(value = "id", example = "1", dataType = "long")
    private Long id;

    @ApiModelProperty(value = "User I", example = "1", dataType = "long")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "Group ID", example = "1", dataType = "long")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "Image ID", example = "1", dataType = "long")
    @TableField("image_id")
    private Long imageId;

    @ApiModelProperty(value = "Operation Type, can select Value for add_face,clear_face,clear_user,clear_group,query_group_list,query_user_list", example = "add_face", dataType = "string")
    @TableField("op_type")
    private String opType;

    @ApiModelProperty(value = "Create Time", example = "2025-07-10 11:22:22", dataType = "date")
    @TableField("created_at")
    private Date createdAt;
}

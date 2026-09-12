package com.yihecode.camera.ai.entity.face;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Face and Box Sync Object table
* @author zhou
* @since 2025-07-110
*/
@ApiModel(value = "Face and Box Sync Object table")
@Data
@TableName("tbl_biz_face_sync_box")
public class FaceSyncBox {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Box ID", example = "1", dataType = "long")
    @TableField("box_id")
    private Long boxId;

    @ApiModelProperty(value = "Sync ID", example = "1", dataType = "long")
    @TableField("sync_id")
    private Long syncId;

    @ApiModelProperty(value = "Sync Status, 0- not Sync,1- Sync Complete Complete", example = "1", dataType = "int")
    @TableField("sync_status")
    private Integer syncStatus;

    @ApiModelProperty(value = "Sync Result Status,0- not Sync,1- Success,2- Failed", example = "1", dataType = "int")
    @TableField("result_status")
    private Integer resultStatus;

    @ApiModelProperty(value = "Sync Result Description", example = "2025-07-10 11:22:22", dataType = "string")
    @TableField("result_msg")
    private String resultMsg;

    @ApiModelProperty(value = "Sync Result Time", example = "2025-07-10 11:22:22", dataType = "date")
    @TableField("result_at")
    private Date resultAt;

    @ApiModelProperty(value = "Create Time", example = "2025-07-10 11:22:22", dataType = "date")
    @TableField("created_at")
    private Date createdAt;

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

    @ApiModelProperty(value = "Sync sub Number, super over 3 sub Failed Put Abandon", example = "2", dataType = "int")
    @TableField("sync_num")
    private Integer syncNum;


}

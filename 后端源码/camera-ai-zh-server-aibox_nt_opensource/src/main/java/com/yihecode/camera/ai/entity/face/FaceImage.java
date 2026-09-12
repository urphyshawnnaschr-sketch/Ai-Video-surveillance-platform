package com.yihecode.camera.ai.entity.face;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
* Person member Image - Face Recognition System
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Person member Image")
@Data
@TableName("tbl_biz_face_image")
public class FaceImage {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Person member ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "Image Address")
    @TableField("img_url")
    private String imgUrl;

    @ApiModelProperty(value = "Whether Image")
    @TableField("is_avatar")
    private Integer isAvatar;

    @ApiModelProperty(value = "Delete Status")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @ApiModelProperty(value = "Create Time")
    @TableField("created_at")
    private Date createdAt;
}
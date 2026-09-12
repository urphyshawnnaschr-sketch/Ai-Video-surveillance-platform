package com.yihecode.camera.ai.entity.map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* Ground image or image Layer Config Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiModel(value = "Ground image or image Layer Config Management")
@Data
@TableName(value = "tbl_biz_map_config", autoResultMap = true)
public class MapConfig {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "Ground image or image Layer Name")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "Ground image or image Layer English Name")
    @TableField("english_name")
    private String englishName;

    @ApiModelProperty(value = "Cover image")
    @TableField("cover")
    private String cover;

    @ApiModelProperty(value = "Sort Value")
    @TableField("sort")
    private Integer sort;
}

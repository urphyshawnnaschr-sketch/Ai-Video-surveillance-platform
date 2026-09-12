package com.yihecode.camera.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.web.dto.CameraDTO;
import org.apache.ibatis.annotations.Param;

/**
* Camera Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface CameraMapper extends BaseMapper<Camera> {
    IPage<Camera> defaultPage(@Param("iPage") IPage<Camera> iPage, @Param("cameraDto") CameraDTO cameraDto);
}
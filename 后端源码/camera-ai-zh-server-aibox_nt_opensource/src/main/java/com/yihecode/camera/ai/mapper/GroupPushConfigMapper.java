package com.yihecode.camera.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihecode.camera.ai.entity.GroupPushConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupPushConfigMapper extends BaseMapper<GroupPushConfig> {
    List<Long> getChildGroupIds(@Param("parentId") Long groupId);

    Long getGroupIdByCameraId(@Param("cameraId")Long cameraId);

    Long getParentIdByGroupId(@Param("groupId")Long groupId);

    String getNameByGroupId(@Param("groupId")Long groupId);

    Long getLevelByGroupId(@Param("groupId")Long groupId);
}

package com.yihecode.camera.ai.mapper.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.dto.ApGroupQueryDTO;
import com.yihecode.camera.ai.dto.ApUserGroupDTO;
import com.yihecode.camera.ai.entity.ap.UserGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApUserGroupMapper extends BaseMapper<UserGroup> {

    /**
*
* @param groupQueryDTO
* @return
*/
    List<UserGroup> findGroupList(@Param("groupQueryDTO")ApGroupQueryDTO groupQueryDTO);

    /**
* Page Query
* @param page
* @param groupQueryDTO
* @return
*/
    IPage<ApUserGroupDTO> findGroupPage(@Param("page")Page<ApUserGroupDTO> page, @Param("groupQueryDTO") ApGroupQueryDTO groupQueryDTO);
}

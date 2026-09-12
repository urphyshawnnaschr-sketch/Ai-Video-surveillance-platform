package com.yihecode.camera.ai.mapper.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.dto.ApProjectQueryDTO;
import com.yihecode.camera.ai.entity.ap.ApProjectDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/19 22:56
* @Describe
* @Version 1.0
*/
public interface ApProjectMapper extends BaseMapper<ApProjectDO> {

    List<ApProjectDO> findpProjectList(@Param("projectQueryDTO") ApProjectQueryDTO projectQueryDTO);

    IPage<ApProjectDO> findpProjectPage(@Param("page") Page<ApProjectDO> page, @Param("projectQueryDTO") ApProjectQueryDTO projectQueryDTO);

    @Select("select max(id) from ap_project")
    Long selectMaxId();
}

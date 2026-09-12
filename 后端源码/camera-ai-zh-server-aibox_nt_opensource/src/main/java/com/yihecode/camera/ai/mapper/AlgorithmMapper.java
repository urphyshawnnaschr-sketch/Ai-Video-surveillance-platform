package com.yihecode.camera.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihecode.camera.ai.entity.Algorithm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* Algorithm Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface AlgorithmMapper extends BaseMapper<Algorithm> {
    /**
* Query nameEn
* @return
*/
    List<Algorithm> selectNameEn(String nameEn);
    /**
* Query make Use Algorithm List
* @return
*/
    List<Algorithm> selectUsed();

    @Update("update tbl_biz_algorithm set platform = #{platform}")
    void updatePlatform(@Param("platform") String platform);
}
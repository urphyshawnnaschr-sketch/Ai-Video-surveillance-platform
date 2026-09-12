package com.yihecode.camera.ai.mapper.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yihecode.camera.ai.entity.ap.Image;
import com.yihecode.camera.ai.web.ap.vo.ImageSearchRequestVo;

import java.util.List;

public interface ApImageMapper extends BaseMapper<Image> {

    List<Image> selectWithLabel(ImageSearchRequestVo request);

    Long countWithLabel(ImageSearchRequestVo request);
}

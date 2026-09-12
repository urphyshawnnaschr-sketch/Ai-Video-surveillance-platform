package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.Annotation;
import com.yihecode.camera.ai.mapper.ap.ApAnnotationMapper;
import com.yihecode.camera.ai.service.ap.ApAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/22 17:37
* @Describe
* @Version 1.0
*/
@Service
public class ApAnnotationServiceImpl extends ServiceImpl<ApAnnotationMapper, Annotation> implements ApAnnotationService {
    @Autowired
    private ApAnnotationMapper apAnnotationMapper;
    @Override
    public Long saveAnnotation(Annotation annotation) {
        apAnnotationMapper.insert(annotation);
        return annotation.getId();
    }

    @Override
    public List<Annotation> findAnnotationList(Annotation annotation) {
        return null;
    }
}

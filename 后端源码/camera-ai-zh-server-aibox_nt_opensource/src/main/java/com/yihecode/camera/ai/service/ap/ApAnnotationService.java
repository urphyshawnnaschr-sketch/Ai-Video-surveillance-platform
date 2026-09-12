package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.Annotation;

import java.util.List;

public interface ApAnnotationService extends IService<Annotation> {

    Long saveAnnotation(Annotation annotation);


    List<Annotation> findAnnotationList(Annotation annotation);
}
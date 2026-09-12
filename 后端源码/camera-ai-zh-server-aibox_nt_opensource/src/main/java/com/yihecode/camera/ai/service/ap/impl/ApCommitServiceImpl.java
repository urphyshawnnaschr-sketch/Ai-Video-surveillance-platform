package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.Annotation;
import com.yihecode.camera.ai.entity.ap.Commit;
import com.yihecode.camera.ai.mapper.ap.ApAnnotationMapper;
import com.yihecode.camera.ai.mapper.ap.ApCommitMapper;
import com.yihecode.camera.ai.service.ap.ApCommitService;
import org.springframework.stereotype.Service;

@Service
public class ApCommitServiceImpl extends ServiceImpl<ApCommitMapper, Commit>  implements ApCommitService {

}

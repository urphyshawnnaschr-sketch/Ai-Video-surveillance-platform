package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Tag;

import java.util.List;

public interface TagService extends IService<Tag> {

    List<Tag> getByType(Integer type);
}
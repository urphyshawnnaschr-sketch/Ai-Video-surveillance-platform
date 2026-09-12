package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Tag;
import com.yihecode.camera.ai.mapper.AccountMapper;
import com.yihecode.camera.ai.mapper.TagMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {


    @Override
    public List<Tag> getByType(Integer type) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getType, type);
        return this.list(queryWrapper);
    }
}
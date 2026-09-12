package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.UserGroupRelationships;
import com.yihecode.camera.ai.mapper.ap.ApUserGroupRelationshipsMapper;
import com.yihecode.camera.ai.service.ap.ApUserGroupRelationshipsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* real current class
*/
@Service
public class ApUserGroupRelationshipsServiceImpl extends ServiceImpl<ApUserGroupRelationshipsMapper, UserGroupRelationships> implements ApUserGroupRelationshipsService {

    /**
* By User id Query The In Annotation group
*
* @param userId User id
* @return
*/
    @Override
    public List<UserGroupRelationships> findByUser(Long userId) {
        LambdaQueryWrapper<UserGroupRelationships> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserGroupRelationships::getUserId, userId);
        List<UserGroupRelationships> userGroupRelationships = this.list(queryWrapper);
        if(userGroupRelationships == null) {
            return new ArrayList<>();
        }
        return userGroupRelationships;
    }
}
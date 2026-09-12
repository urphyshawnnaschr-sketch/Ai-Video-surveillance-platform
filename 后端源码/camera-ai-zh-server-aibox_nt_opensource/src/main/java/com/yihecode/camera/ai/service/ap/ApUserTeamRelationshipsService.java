package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.UserTeamRelationships;

/**
* ap Team Complete member real current class
* @author zhoumingxing
*/
public interface ApUserTeamRelationshipsService extends IService<UserTeamRelationships> {

    /**
* Create Account and Team Relate close System
* @param account
* @param teamId
*/
    void saveRelationships(Account account, Long teamId) throws Exception;

    /**
* By Team ID Query Complete member Count
* @param teamId
* @return
*/
    int countUserNum(Long teamId);

    /**
* Delete User
* @param teamId
* @param userId
*/
    void deleteUser(Long teamId, Long userId);
}

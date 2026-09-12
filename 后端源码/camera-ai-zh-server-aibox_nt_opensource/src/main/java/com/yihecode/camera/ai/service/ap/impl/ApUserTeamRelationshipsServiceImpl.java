package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.dto.ApTeamDTO;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.UserTeam;
import com.yihecode.camera.ai.entity.ap.UserTeamRelationships;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApUserTeamMapper;
import com.yihecode.camera.ai.mapper.ap.ApUserTeamRelationshipsMapper;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.ApUserTeamRelationshipsService;
import com.yihecode.camera.ai.service.ap.ApUserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* ap Team real current class
* @author zhoumingxing
*/
@Service
public class ApUserTeamRelationshipsServiceImpl extends ServiceImpl<ApUserTeamRelationshipsMapper, UserTeamRelationships> implements ApUserTeamRelationshipsService {

    @Autowired
    private AccountService accountService;

    /**
* Create Account and Team Relate close System
*
* @param account
* @param teamId
*/
    @Override
    public void saveRelationships(Account account, Long teamId) throws Exception {
        //
Account accountOld = accountService.getByAccount(account.getAccount());
if(accountOld!= null) {
throw new BizException("Account Name Exist");
}

// Save Account Entity
accountService.save(account);

// Save Account and Team Relate close System
UserTeamRelationships userTeamRelationships = new UserTeamRelationships();
userTeamRelationships.setUserId(account.getId());
userTeamRelationships.setTeamId(teamId);
userTeamRelationships.setCreatedAt(new Date());
userTeamRelationships.setUpdatedAt(new Date());
this.save(userTeamRelationships);
}

/**
* By Team ID Query Complete member Count
*
* @param teamId
* @return
*/
@Override
public int countUserNum(Long teamId) {
LambdaQueryWrapper<UserTeamRelationships> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(UserTeamRelationships::getTeamId, teamId);
return this.count(queryWrapper);
}

/**
* Delete User
*
* @param teamId
* @param userId
*/
@Override
public void deleteUser(Long teamId, Long userId) {
LambdaQueryWrapper<UserTeamRelationships> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(UserTeamRelationships::getTeamId, teamId);
queryWrapper.eq(UserTeamRelationships::getUserId, userId);
this.remove(queryWrapper);
}
}
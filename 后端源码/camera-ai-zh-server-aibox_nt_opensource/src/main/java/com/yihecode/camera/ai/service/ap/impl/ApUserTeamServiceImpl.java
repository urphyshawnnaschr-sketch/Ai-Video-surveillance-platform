package com.yihecode.camera.ai.service.ap.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.dto.ApGroupQueryDTO;
import com.yihecode.camera.ai.dto.ApTeamDTO;
import com.yihecode.camera.ai.dto.ApUserGroupDTO;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.*;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApRoleMapper;
import com.yihecode.camera.ai.mapper.ap.ApUserTeamMapper;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.ApRoleService;
import com.yihecode.camera.ai.service.ap.ApUserRoleService;
import com.yihecode.camera.ai.service.ap.ApUserTeamRelationshipsService;
import com.yihecode.camera.ai.service.ap.ApUserTeamService;
import com.yihecode.camera.ai.utils.PassUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* ap Team real current class
* @author zhoumingxing
*/
@Service
public class ApUserTeamServiceImpl extends ServiceImpl<ApUserTeamMapper, UserTeam> implements ApUserTeamService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApUserTeamRelationshipsService apUserTeamRelationshipsService;

    @Autowired
    private ApUserRoleService apUserRoleService;

    /**
* Page Query Group Queue table
* @param apTeamDTO
* @return
*/
    @Override
    public IPage<UserTeam> findTeamPage(ApTeamDTO apTeamDTO) {
        //
LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
if(apTeamDTO.getUserId()!= null) {
queryWrapper.eq(UserTeam::getUserId, apTeamDTO.getUserId());
}
if(StrUtil.isNotBlank(apTeamDTO.getName())) {
queryWrapper.like(UserTeam::getName, apTeamDTO.getName());
}
queryWrapper.isNull(UserTeam::getDeletedAt);
queryWrapper.orderByDesc(UserTeam::getCreatedAt);

//
Page<UserTeam> page = new Page<>();
page.setCurrent(apTeamDTO.getPageNum());
page.setSize(apTeamDTO.getPageSize());

//
return this.page(page, queryWrapper);
}

/**
* Save Team Info
* @param apTeamDTO
* @return
*/
@Override
@Transactional(rollbackFor = Exception.class)
public Long saveTeam(ApTeamDTO apTeamDTO) {
// TODO: 2023/8/1 Validate Whether Platform Management member / Annotation Management member, Annotation Management member only can Create One Team

//
apTeamDTO.setCreatedAt(new Date());
apTeamDTO.setUpdatedAt(new Date());
//
UserTeam userTeam = new UserTeam();
BeanUtils.copyProperties(apTeamDTO, userTeam);
this.save(userTeam);
return userTeam.getId();
}

/**
* Modify Team Info
* @param teamDTO
*/
@Override
@Transactional(rollbackFor = Exception.class)
public void updateTeamById(ApTeamDTO teamDTO) {
//
teamDTO.setUpdatedAt(new Date());
UserTeam userTeam = new UserTeam();
BeanUtils.copyProperties(teamDTO, userTeam);
this.updateById(userTeam);
}

/**
* Delete Team Info
* @param teamId
*/
@Override
@Transactional(rollbackFor = Exception.class)
public void deleteTeamById(Long teamId) {
this.removeById(teamId);
}

/**
* Query All Data
*
* @return
*/
@Override
public List<UserTeam> listData() {
List<UserTeam> userTeamList = this.list();
return userTeamList == null? new ArrayList<>(): userTeamList;
}

/**
* Batch Add User
*
* @param teamId
* @param roleIds
* @param userNum
* @param shortTeamName
* @param roleNameEnStr
*/
@Override
public void saveBatchUsers(Long teamId, List<Long> roleIds, Integer userNum, String shortTeamName, String roleNameEnStr) {
// find out most big Index
Integer maxSeq = accountService.findMaxSeq(shortTeamName);
for(int i = maxSeq + 1; i <= maxSeq + userNum; i++) {
// Create User
Account account = new Account();
account.setAccount(shortTeamName + i);
account.setPassword(PassUtils.encrypt("AP@321001"));
account.setName(shortTeamName + i);
account.setState(0);
account.setCreatedAt(new Date());
account.setUpdatedAt(new Date());
accountService.save(account);
// Save User and Role close System
for(Long roleId: roleIds) {
UserRole userRole = new UserRole();
userRole.setUserId(account.getId());
userRole.setRoleId(roleId);
userRole.setCreatedAt(new Date());
userRole.setUpdatedAt(new Date());
apUserRoleService.save(userRole);
}
// Save Team and User close System
UserTeamRelationships userTeamRelationships = new UserTeamRelationships();
userTeamRelationships.setTeamId(teamId);
userTeamRelationships.setUserId(account.getId());
userTeamRelationships.setCreatedAt(new Date());
userTeamRelationships.setUpdatedAt(new Date());
apUserTeamRelationshipsService.save(userTeamRelationships);
}
}
}
package com.yihecode.camera.ai.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.dto.ApAccountDTO;
import com.yihecode.camera.ai.dto.ApAccountModifyDTO;
import com.yihecode.camera.ai.dto.ApAccountQueryDTO;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.UserRole;
import com.yihecode.camera.ai.entity.ap.UserTeamRelationships;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.AccountMapper;
import com.yihecode.camera.ai.mapper.ap.ApUserTeamRelationshipsMapper;
import com.yihecode.camera.ai.service.ap.ApUserRoleService;
import com.yihecode.camera.ai.utils.PassUtils;
import io.swagger.models.auth.In;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
* Account Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Autowired
    private ApUserRoleService apUserRoleService;

    @Autowired
    private ApUserTeamRelationshipsMapper apUserTeamRelationshipsMapper;

    /**
* By Account Query
*
* @param account
* @return
*/
    @Override
    public Account getByAccount(String account) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getAccount, account);
        return this.getOne(queryWrapper, false);
    }

    @Override
    public IPage<ApAccountDTO> findAccountPage(ApAccountQueryDTO accountQueryDTO) {
        Page<ApAccountDTO> page = new Page<>();
        page.setCurrent(accountQueryDTO.getPageNum());
        page.setSize(accountQueryDTO.getPageSize());
        IPage<ApAccountDTO> apAccountDTOIPage = baseMapper.findAccountPage(page, accountQueryDTO);
        return apAccountDTOIPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAccount(ApAccountModifyDTO apAccountModifyDTO) {
        validateAccount(apAccountModifyDTO, true);
        apAccountModifyDTO.setCreatedAt(new Date());
        apAccountModifyDTO.setUpdatedAt(new Date());

        Account account = new Account();
        BeanUtils.copyProperties(apAccountModifyDTO, account);
        //
account.setPassword(PassUtils.encrypt(account.getPassword()));
account.setIsSuper(0); // Default 0, not Allow Modify
this.save(account);
// Update User Role
updateUserRole(account.getId(), apAccountModifyDTO.getRoleIds());
return account.getId();
}

@Override
@Transactional(rollbackFor = Exception.class)
public void updateAccountById(ApAccountModifyDTO apAccountModifyDTO) {
validateAccount(apAccountModifyDTO, false);
apAccountModifyDTO.setUpdatedAt(new Date());
Account account = new Account();
BeanUtils.copyProperties(apAccountModifyDTO, account);
// Update User Role
updateUserRole(apAccountModifyDTO.getId(), apAccountModifyDTO.getRoleIds());
// Update Account
account.setPassword(null); // not Change Password
this.updateById(account);
}

@Override
@Transactional(rollbackFor = Exception.class)
public void deleteAccountById(Long accountId) {
// Delete Role Relate
LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
userRoleWrapper.eq(UserRole::getUserId, accountId);
apUserRoleService.remove(userRoleWrapper);
// Delete Team Relate
LambdaQueryWrapper<UserTeamRelationships> teamRelationshipsWrapper = new LambdaQueryWrapper<>();
teamRelationshipsWrapper.eq(UserTeamRelationships::getUserId, accountId);
apUserTeamRelationshipsMapper.delete(teamRelationshipsWrapper);
// Delete Account
removeById(accountId);


// Modify Account Status
// Account account = new Account();
// account.setId(accountId);
// account.setState(1);
// account.setUpdatedAt(new Date());
// this.updateById(account);
}

/**
* Update User Role
*
* @param userId
* @param roleIds
*/
private void updateUserRole(Long userId, List<Long> roleIds) {
// Delete original has
LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
userRoleWrapper.eq(UserRole::getUserId, userId);
apUserRoleService.remove(userRoleWrapper);
// Update Role
List<UserRole> userRoles = new ArrayList<>();
for (Long roleId: roleIds) {
UserRole userRole = new UserRole();
userRole.setRoleId(roleId);
userRole.setUserId(userId);
userRole.setUpdatedAt(new Date());
userRoles.add(userRole);
}
apUserRoleService.saveBatch(userRoles);
}

@SneakyThrows
private void validateAccount(ApAccountModifyDTO apAccountDTO, boolean isCreate) {
if (ObjectUtil.isNull(apAccountDTO)) {
throw new Exception("Info cannot be empty");
}
if (StrUtil.isEmpty(apAccountDTO.getAccount())) {
throw new Exception("Login Account cannot be empty");
}
if (StrUtil.isEmpty(apAccountDTO.getName())) {
throw new Exception("Account Name cannot be empty");
}
if(isCreate) {
if (StrUtil.isEmpty(apAccountDTO.getPassword())) {
throw new Exception("Account Password cannot be empty");
}
}
if(apAccountDTO.getDepartId() == null) {
throw new Exception("must Select belong belong Department");
}
// if (apAccountDTO.getRoleIds() == null || apAccountDTO.getRoleIds().isEmpty()) {
// throw new BizException("Role cannot be empty");
//}
LambdaUpdateWrapper<Account> query = new LambdaUpdateWrapper<>();
query.eq(Account::getAccount, apAccountDTO.getAccount())
.ne(ObjectUtil.isNotNull(apAccountDTO.getId()), Account::getId, apAccountDTO.getId());
List<Account> accounts = list(query);
if (CollectionUtils.isNotEmpty(accounts)) {
throw new Exception("Login Account not can re reply");
}
}

/**
* By Creator Query The Create Team All Complete member
*
* @param createUserId
* @return
*/
@Override
public List<ApAccountDTO> findAllTeamUser(Long createUserId) {
List<ApAccountDTO> apAccountDTOList = baseMapper.findAllTeamUser(createUserId);
return apAccountDTOList == null? new ArrayList<>(): apAccountDTOList;
}

/**
* By Team ID Query All Complete member
*
* @param teamId
* @return
*/
@Override
public List<ApAccountDTO> findTeamUser(Long teamId) {
List<ApAccountDTO> apAccountDTOList = baseMapper.findTeamUser(teamId);
return apAccountDTOList == null? new ArrayList<>(): apAccountDTOList;
}

@Override
public List<ApAccountDTO> findTeamUser(Long teamId, String name) {
List<ApAccountDTO> apAccountDTOList = baseMapper.findTeamUserLikeName(teamId, name);
return apAccountDTOList == null? new ArrayList<>(): apAccountDTOList;
}

/**
* By Annotation group Id Query All Complete member
*
* @param groupId
* @return
*/
@Override
public List<ApAccountDTO> findGroupUser(Long groupId) {
List<ApAccountDTO> apAccountDTOList = baseMapper.findGroupUser(groupId);
return apAccountDTOList == null? new ArrayList<>(): apAccountDTOList;
}

/**
* By Simple form Name Query most big Index
*
* @param shortAccount
* @return
*/
@Override
public Integer findMaxSeq(String shortAccount) {
LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.like(Account::getAccount, shortAccount);
List<Account> accounts = this.list(queryWrapper);
if(accounts == null || accounts.isEmpty()) {
return 0;
}
//
Integer maxSeq = 0;
for(Account account: accounts) {
String ac = account.getAccount();
String seqStr = ac.replace(shortAccount,"");
try {
int seq = Integer.valueOf(seqStr);
if(seq > maxSeq) {
maxSeq = seq;
}
} catch (Exception e) {
//
}
}
return maxSeq;
}

/**
* Reset Account Password
* @param accountId
*/
@Override
public void updateResetPassword(Long accountId) {
Account account = new Account();
account.setId(accountId);
account.setPassword(PassUtils.encrypt("123456"));
account.setUpdatedAt(new Date());
this.updateById(account);
}

@Override
public void updatePassword(Long accountId, String password) {
Account account = new Account();
account.setId(accountId);
account.setPassword(PassUtils.encrypt(password));
account.setUpdatedAt(new Date());
this.updateById(account);
}

/**
* By Role Count Person member Count
*
* @return
*/
@Override
public Map<Long, Integer> listGroupByRole() {
List<Map<String, Object>> results = this.getBaseMapper().selectGroupByRole();
if(results == null) {
return new HashMap<>();
}
//
Map<Long, Integer> resultMap = new HashMap();
for(Map<String, Object> result: results) {
Object roleId = result.get("role_id");
Object cnt = result.get("cnt");
resultMap.put(Convert.toLong(roleId), Convert.toInt(cnt));
}
return resultMap;
}

/**
* By Account ID Query
*
* @param accountId
* @return
*/
@Override
public ApAccountDTO findAccountById(Long accountId) {
return this.getBaseMapper().findAccountById(accountId);
}

@Override
public List<Account> listByDepartId(Long departId) {
LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
if (departId!= null) {
queryWrapper.eq(Account::getDepartId, departId).or().eq(Account::getIsSuper, 1);
}
return this.list(queryWrapper);
}


}
package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.dto.ApAccountDTO;
import com.yihecode.camera.ai.dto.ApAccountModifyDTO;
import com.yihecode.camera.ai.dto.ApAccountQueryDTO;
import com.yihecode.camera.ai.entity.Account;

import java.util.List;
import java.util.Map;

/**
* Account Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface AccountService extends IService<Account> {

    /**
* By Account Query
* @param account
* @return
*/
    Account getByAccount(String account);

    /**
* Page Query Account
* @param accountQueryDTO
* @return
*/
    IPage<ApAccountDTO> findAccountPage(ApAccountQueryDTO accountQueryDTO);

    /**
* Save Account
* @param apAccountModifyDTO
* @return
*/
    Long saveAccount(ApAccountModifyDTO apAccountModifyDTO);

    /**
* Modify Account
* @param apAccountModifyDTO
*/
    void updateAccountById(ApAccountModifyDTO apAccountModifyDTO);

    /**
* Delete Account
* @param accountId
*/
    void deleteAccountById(Long accountId);

    /**
* By Creator Query The Create Team All Complete member
* @param createUserId
* @return
*/
    List<ApAccountDTO> findAllTeamUser(Long createUserId);

    /**
* By Team ID Query All Complete member
* @param teamId
* @return
*/
    List<ApAccountDTO> findTeamUser(Long teamId);

    /**
* By Team ID Query All Complete member name Blur Query
* @author Abyss
* @date 2024/1/9 14:11
* @param teamId
* @param name
* @return java.util.List<com.yihecode.camera.ai.dto.ApAccountDTO>
*/
    List<ApAccountDTO> findTeamUser(Long teamId, String name);


    /**
* By Annotation group Id Query All Complete member
* @param groupId
* @return
*/
    List<ApAccountDTO> findGroupUser(Long groupId);

    /**
* By Simple form Name Query most big Index
* @param shortAccount
* @return
*/
    Integer findMaxSeq(String shortAccount);

    /**
* Reset Account Password
* @param accountId
*/
    void updateResetPassword(Long accountId);

    /**
* Change Password
* @param accountId
* @param password
*/
    void updatePassword(Long accountId, String password);

    /**
* By Role Count Person member Count
* @return
*/
    Map<Long, Integer> listGroupByRole();

    /**
* By Account ID Query
* @param accountId
* @return
*/
    ApAccountDTO findAccountById(Long accountId);

    /**
* By Company ID Query Account, Contain super Level Management member
* @param departId
* @return
*/
    List<Account> listByDepartId(Long departId);

}
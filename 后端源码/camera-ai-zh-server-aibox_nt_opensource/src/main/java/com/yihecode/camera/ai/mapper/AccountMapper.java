package com.yihecode.camera.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.dto.ApAccountDTO;
import com.yihecode.camera.ai.dto.ApAccountQueryDTO;
import com.yihecode.camera.ai.entity.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* Account Management
*/
public interface AccountMapper extends BaseMapper<Account> {
    /**
* Page Query
* @param page
* @param accountQueryDTO
* @return
*/
    IPage<ApAccountDTO> findAccountPage(@Param("page")Page<ApAccountDTO> page, @Param("accountQueryDTO")ApAccountQueryDTO accountQueryDTO);

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

    List<ApAccountDTO> findTeamUserLikeName(@Param("teamId") Long teamId, @Param("name")String name);

    /**
* By Annotation group Id Query All Complete member
* @param groupId
* @return
*/
    List<ApAccountDTO> findGroupUser(Long groupId);

    /**
* By Role Count Person member Count
* @return
*/
    List<Map<String, Object>> selectGroupByRole();

    /**
* By Account id Query
* @param accountId
* @return
*/
    ApAccountDTO findAccountById(@Param("accountId") Long accountId);
}

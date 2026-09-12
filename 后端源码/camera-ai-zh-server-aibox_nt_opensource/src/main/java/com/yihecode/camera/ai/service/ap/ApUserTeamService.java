package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.dto.ApTeamDTO;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.entity.ap.UserTeam;

import java.util.List;

/**
* ap Team real current class
* @author zhoumingxing
*/
public interface ApUserTeamService extends IService<UserTeam> {

    /**
* Query Group Queue table
* @param teamDTO
* @return
*/
    IPage<UserTeam> findTeamPage(ApTeamDTO teamDTO);

    /**
* Save Team Info
* @param teamDTO
* @return
*/
    Long saveTeam(ApTeamDTO teamDTO);

    /**
* Modify Team Info
* @param teamDTO
*/
    void updateTeamById(ApTeamDTO teamDTO);

    /**
* Delete Team Info
* @param teamId
*/
    void deleteTeamById(Long teamId);

    /**
* Query All Data
* @return
*/
    List<UserTeam> listData();

    /**
* Batch Add User
* @param teamId
* @param roleIds
* @param userNum
* @param shortTeamName
* @param roleNameEnStr
*/
    void saveBatchUsers(Long teamId, List<Long> roleIds, Integer userNum, String shortTeamName, String roleNameEnStr);
}

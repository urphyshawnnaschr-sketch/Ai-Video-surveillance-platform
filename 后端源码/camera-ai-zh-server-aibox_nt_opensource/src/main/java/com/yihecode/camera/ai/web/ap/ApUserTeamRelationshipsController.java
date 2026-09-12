package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ApTeamDTO;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.ap.UserTeam;
import com.yihecode.camera.ai.enums.CommState;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.ApUserTeamRelationshipsService;
import com.yihecode.camera.ai.service.ap.ApUserTeamService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.ap.vo.UserTeamRelationshipsRequestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;

@ApiIgnore
@Api(tags = "Team Complete member Management")
@SaCheckLogin
@Controller
@RequestMapping({"/ap/userTeam/relationships"})
public class ApUserTeamRelationshipsController {

    @Autowired
    private ApUserTeamRelationshipsService apUserTeamRelationshipsService;

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "Save Team Complete member")
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult<Long> save(@RequestBody UserTeamRelationshipsRequestVo requestVo) throws Exception {
        //
if(requestVo.getTeamId() == null) {
return JsonResultUtils.fail("Please select in Annotation Team");
}
//
if(StrUtil.isBlank(requestVo.getAccount())) {
return JsonResultUtils.fail("Please enter Login Account");
}
//
if(StrUtil.isBlank(requestVo.getName())) {
return JsonResultUtils.fail("Please enter User Name");
}

//
if(requestVo.getUserId() == null) {
//
if(StrUtil.isBlank(requestVo.getPassword())) {
return JsonResultUtils.fail("Please enter Login Password");
}

// Create Account
Account account = new Account();
account.setAccount(requestVo.getAccount());
account.setPassword(PassUtils.encrypt(requestVo.getPassword()));
account.setName(requestVo.getName());
account.setState(CommState.NORMAL.getType());
account.setCreatedAt(new Date());
account.setUpdatedAt(new Date());
//
apUserTeamRelationshipsService.saveRelationships(account, requestVo.getTeamId());
} else {
// Update Account Info
Account account = new Account();
account.setId(requestVo.getUserId());
//account.setAccount(requestVo.getAccount()); // not can Modify
account.setName(requestVo.getName());
account.setUpdatedAt(new Date());
//
if(StrUtil.isNotBlank(requestVo.getPassword())) {
account.setPassword(PassUtils.encrypt(requestVo.getPassword()));
}
accountService.updateById(account);
}

//
return JsonResultUtils.success();
}
}

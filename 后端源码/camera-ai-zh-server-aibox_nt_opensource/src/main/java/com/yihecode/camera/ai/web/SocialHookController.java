package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.SocialHook;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.SocialConfigService;
import com.yihecode.camera.ai.service.SocialHookService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.vo.SocialFeishuConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
* Social Platform Push, Feishu, WeWork, DingTalk
*/
@RestController
@RequestMapping("social/hook")
public class SocialHookController {

    @Autowired
    private SocialHookService socialHookService;

    @Autowired
    private SocialConfigService socialConfigService;

    @Autowired
    private ConfigService configService;

    //Query Data
@SaCheckPermission(value = {"edgePlatform-boxManagement","noticeManagement-wx"}, mode = SaMode.OR)
@GetMapping("list")
public JsonResult<List<SocialHook>> list() {
List<SocialHook> socialHookList = socialHookService.list();
return JsonResultUtils.success(socialHookList);
}

// Edit Data
@SaCheckPermission("noticeManagement-wx")
@PostMapping("save")
public JsonResult<Void> save(@RequestBody SocialHook socialHook) {
if(StrUtil.isBlank(socialHook.getName())) {
return JsonResultUtils.fail("Please enter Group Name");
}
if(StrUtil.isBlank(socialHook.getWebhook())) {
return JsonResultUtils.fail("Please enter Webhook Address");
}

socialHookService.saveOrUpdate(socialHook);
return JsonResultUtils.success();
}

// Cut change Status
@SaCheckPermission("noticeManagement-wx")
@PostMapping("switch")
public JsonResult<Void> switchState(Long id) {
if(id == null) {
return JsonResultUtils.fail("Param Error");
}

SocialHook socialHook = socialHookService.getById(id);
if(socialHook == null) {
return JsonResultUtils.fail("Data Not Exist");
}

Integer state = socialHook.getState();
socialHook.setState((state == null || state == 0)? 1: 0);
socialHookService.updateById(socialHook);
return JsonResultUtils.success();
}

// Delete Data
@SaCheckPermission("noticeManagement-wx")
@PostMapping("delete")
public JsonResult<Void> delete(Long id) {
if(id == null) {
return JsonResultUtils.fail("Param Error");
}

SocialHook socialHook = socialHookService.getById(id);
if(socialHook == null) {
return JsonResultUtils.fail("Data Not Exist");
}

socialHookService.removeById(id);

// Delete and Camera Relate Config
socialConfigService.deleteBySocial(id);
return JsonResultUtils.success();
}

// Delete Data
@SaCheckPermission("noticeManagement-wx")
@GetMapping("info")
public JsonResult<SocialHook> get(Long id) {
return JsonResultUtils.success(socialHookService.getById(id));
}

// Delete Data
@SaCheckPermission("XXXXXXX")
@GetMapping("esb/config")
public JsonResult<SocialFeishuConfigVo> getEsbConfig() {
SocialFeishuConfigVo configVo = new SocialFeishuConfigVo();
configVo.setEsbBaseUrl(configService.getByValTag("esbBaseUrl"));
configVo.setEsbTokenUrl(configService.getByValTag("esbTokenUrl"));
configVo.setEsbUploadUrl(configService.getByValTag("esbUploadUrl"));
configVo.setEsbSendUrl(configService.getByValTag("esbSendUrl"));
configVo.setEsbAppKey(configService.getByValTag("esbAppKey"));
configVo.setEsbAk(configService.getByValTag("esbAk"));
configVo.setEsbSk(configService.getByValTag("esbSk"));
configVo.setEsbFsTemplatId(configService.getByValTag("esbFsTemplatId"));
configVo.setEsbFsClientId(configService.getByValTag("esbFsClientId"));
return JsonResultUtils.success(configVo);
}

// Delete Data
@SaCheckPermission("XXXXXXX")
@PostMapping("esb/config")
public JsonResult<Void> saveEsbConfig(@RequestBody SocialFeishuConfigVo configVo) {
configService.saveData("esbBaseUrl","esbBaseUrl", configVo.getEsbBaseUrl());
configService.evictByTag("esbBaseUrl");

configService.saveData("esbTokenUrl","esbTokenUrl", configVo.getEsbTokenUrl());
configService.evictByTag("esbTokenUrl");

configService.saveData("esbUploadUrl","esbUploadUrl", configVo.getEsbUploadUrl());
configService.evictByTag("esbUploadUrl");

configService.saveData("esbSendUrl","esbSendUrl", configVo.getEsbSendUrl());
configService.evictByTag("esbSendUrl");

configService.saveData("esbAppKey","esbAppKey", configVo.getEsbAppKey());
configService.evictByTag("esbAppKey");

configService.saveData("esbAk","esbAk", configVo.getEsbAk());
configService.evictByTag("esbAk");

configService.saveData("esbSk","esbSk", configVo.getEsbSk());
configService.evictByTag("esbSk");

configService.saveData("esbFsTemplatId","esbFsTemplatId", configVo.getEsbFsTemplatId());
configService.evictByTag("esbFsTemplatId");

configService.saveData("esbFsClientId","esbFsClientId", configVo.getEsbFsClientId());
configService.evictByTag("esbFsClientId");

return JsonResultUtils.success();
}
}

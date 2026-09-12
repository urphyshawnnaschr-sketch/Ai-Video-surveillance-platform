package com.yihecode.camera.ai.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yihecode.camera.ai.dto.VoicePhoneConfigDTO;
import com.yihecode.camera.ai.entity.VoicePhone;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.VoicePhoneService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "Voice Notification Config Management")
@SaCheckLogin
@Controller
@Slf4j
@RequestMapping({ "/voicePhone" })
public class VoicePhoneController {

    @Autowired
    private VoicePhoneService voicePhoneService;

    @Autowired
    private ConfigService configService;

    /**
* save Save Voice Alert Config
*/
    @ApiOperation("Save Voice Alert Config")
    @SaCheckPermission(value = {"noticeManagement-voice"}, mode = SaMode.OR)
    @PostMapping({ "/saveAlarmVoiceConfig" })
    @ResponseBody
    public JsonResult<?> saveAlarmVoiceConfig(@RequestBody VoicePhoneConfigDTO voicePhoneConfig) {
        String accountId = String.valueOf(StpUtil.getLoginIdAsLong());
        configService.saveData("Whether Push Voice Notification", "voiceEnable" + accountId, voicePhoneConfig.getVoiceEnable());
        configService.saveData("Voice Platform APP_ID", "voiceAppId" + accountId, voicePhoneConfig.getVoiceAppId());
        configService.saveData("Voice Platform APP_SECRET", "voiceAppSecret" + accountId, voicePhoneConfig.getVoiceAppSecret());
        configService.saveData("Voice Platform Template ID", "voiceTemplateId" + accountId, voicePhoneConfig.getVoiceTemplateId());
        this.configService.evictByTag("voiceEnable" + accountId);
        this.configService.evictByTag("voiceAppId" + accountId);
        this.configService.evictByTag("voiceAppSecret" + accountId);
        this.configService.evictByTag("voiceTemplateId" + accountId);
        return JsonResultUtils.success();
    }

    /**
* query Query Voice Alert Config
*/
    @ApiOperation("Query Voice Alert Config")
    @SaCheckPermission(value = {"noticeManagement-voice"}, mode = SaMode.OR)
    @GetMapping({ "/queryAlarmVoiceConfig" })
    @ResponseBody
    public JsonResult<?> queryAlarmVoiceConfig() {
        String accountId = String.valueOf(StpUtil.getLoginIdAsLong());
        Map<String, Object> map = buildInfos("voiceEnable" + accountId, "voiceAppId" + accountId, "voiceAppSecret" + accountId, "voiceTemplateId" + accountId);
        return JsonResultUtils.success(map);
    }

    /**
* By Tag Name Query Value and Organization Complete map Back
*
* @param tags
* @return
*/
    private Map<String, Object> buildInfos(String...tags) {
        Map<String, Object> infos = new HashMap<>();
        String accountId = String.valueOf(StpUtil.getLoginIdAsLong());
        for (String tag : tags) {
            String val = configService.getByValTag(tag);
            if(tag.contains(accountId)){
                infos.put(tag.replace(accountId, ""), StrUtil.isBlank(val) ? "" : val);
            } else {
                infos.put(tag, StrUtil.isBlank(val) ? "" : val);
            }
        }
        return infos;
    }

    /**
*
* @return
*/
    @ApiOperation("Query Data List")
    @SaCheckPermission(value = {"noticeManagement-voice"}, mode = SaMode.OR)
    @GetMapping({ "/listData" })
    @ResponseBody
    public PageResult<?> listData() {
        Long accountId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<VoicePhone> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoicePhone::getAccountId, accountId);
        List<VoicePhone> voicePhoneList = voicePhoneService.list(queryWrapper);
        if (voicePhoneList == null) {
            voicePhoneList = new ArrayList<>();
        }
        return PageResultUtils.success(null, voicePhoneList);
    }

    /**
*
* @param voicePhone
* @return
*/
    @ApiOperation("Add Voice Notification Phone")
    @ApiImplicitParam(name = "voicePhone", value = "SMS Entity")
    @SaCheckPermission(value = {"noticeManagement-voice"}, mode = SaMode.OR)
    @PostMapping({ "/savePhone" })
    @ResponseBody
    public JsonResult<?> save(@RequestBody VoicePhone voicePhone) {
        if (StrUtil.isBlank(voicePhone.getPhone())) {
            return JsonResultUtils.fail("Please enter Phone code");
        }
        if (!Validator.isMobile(voicePhone.getPhone())) {
            return JsonResultUtils.fail("Phone code not Correct");
        }
        Long accountId = StpUtil.getLoginIdAsLong();
        voicePhone.setAccountId(accountId);
        voicePhoneService.saveOrUpdate(voicePhone);
        return JsonResultUtils.success();
    }

    /**
*
* @param voicePhone
* @return
*/
    @ApiOperation("Delete Voice Notification Phone")
    @ApiImplicitParam(name = "id", value = "Data ID")
    @SaCheckPermission(value = {"noticeManagement-voice"}, mode = SaMode.OR)
    @PostMapping({ "/delete" })
    @ResponseBody
    public JsonResult<?> delete(@RequestBody VoicePhone voicePhone) {
        voicePhoneService.removeById(voicePhone.getId());
        return JsonResultUtils.success();
    }
}

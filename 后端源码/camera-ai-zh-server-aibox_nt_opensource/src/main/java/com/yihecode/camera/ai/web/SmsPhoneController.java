package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.SmsPhone;
import com.yihecode.camera.ai.service.SmsPhoneService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
* SMS Push Phone code Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
import cn.hutool.core.util.IdUtil;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "SMS Management")
@SaCheckLogin
@Controller
@RequestMapping({"/smsphone"})
public class SmsPhoneController {

    @Autowired
    private SmsPhoneService smsPhoneService;

    /**
*
* @return
*/
    @ApiOperation("Query Data List")
    @SaCheckPermission(value = {"noticeManagement-message"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public PageResult listData() {

        List<SmsPhone> smsPhoneList = this.smsPhoneService.listByAccountId(StpUtil.getLoginIdAsLong());
        if (smsPhoneList == null) {
            smsPhoneList = new ArrayList<>();
        }
        return PageResultUtils.success(null, smsPhoneList);
    }

    /**
*
* @param smsPhone
* @return
*/
    @ApiOperation("Query Data List")
    @ApiImplicitParam(name = "smsPhone", value = "SMS Entity")
    @SaCheckPermission(value = {"noticeManagement-message", "noticeManagement-message"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult save(SmsPhone smsPhone) {
        if (StrUtil.isBlank(smsPhone.getPhone())) {
            return JsonResultUtils.fail("Please enter Phone code");
        }
        //
if(!Validator.isMobile(smsPhone.getPhone())) {
return JsonResultUtils.fail("Phone code not Correct");
}

smsPhone.setAccountId(StpUtil.getLoginIdAsLong());

smsPhoneService.save(smsPhone);
//
smsPhoneService.evictPhoneStr("test");
return JsonResultUtils.success();
}

/**
*
* @param id
* @return
*/
@ApiOperation("Delete SMS")
@ApiImplicitParam(name ="id", value ="Data ID")
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) {
this.smsPhoneService.removeById(id);
//
smsPhoneService.evictPhoneStr("test");
return JsonResultUtils.success();
}
}
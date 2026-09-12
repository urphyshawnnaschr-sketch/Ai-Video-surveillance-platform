package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihecode.camera.ai.entity.AlarmLevel;
import com.yihecode.camera.ai.enums.AlarmShowType;
import com.yihecode.camera.ai.service.AlarmLevelService;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* Alert Level Management
* User Algorithm Alert Level Config
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Alert Level Management")
@SaCheckLogin
@Controller
@RequestMapping({"/alarmLevel"})
public class AlarmLevelController {

    @Autowired
    private AlarmLevelService alarmLevelService;
    @Autowired
    private AlgorithmService algorithmService;

    /**
* Query Data List
* @return
*/
    @ApiOperation(value = "Query Data List")
    @SaCheckPermission(value = {"noticeManagement-voice", "algorithmManagement"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public PageResult listData() {
        LambdaQueryWrapper<AlarmLevel> queryWrapper = new LambdaQueryWrapper<>();
        Long accountId = StpUtil.getLoginIdAsLong();
        queryWrapper.eq(AlarmLevel::getAccountId, accountId);
        List<AlarmLevel> alarmLevelList = this.alarmLevelService.list(queryWrapper);
        if (alarmLevelList == null) {
            alarmLevelList = new ArrayList<>();
        }
        //Enum Name Convert
alarmLevelList.stream().map(data -> {
List<String> showTypes = data.getShowTypes();
List<String> showTypeNames = new ArrayList<>();
if(showTypes!= null) {
for(String showType: showTypes) {
showTypeNames.add(AlarmShowType.getText(showType));
}
}
data.setShowTypeNames(String.join(",", showTypeNames));
return data;
}).collect(Collectors.toList());
//
return PageResultUtils.success(null, alarmLevelList);
}

@ApiOperation(value ="Save Data")
@SaCheckPermission(value = {"algorithm-alarm-level"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult save(@RequestBody AlarmLevel alarmLevel) {
if (StrUtil.isBlank(alarmLevel.getName())) {
return JsonResultUtils.fail("Please enter Alert Level Name");
}
Long accountId = StpUtil.getLoginIdAsLong();
if (null == alarmLevel.getId()) {
LambdaQueryWrapper<AlarmLevel> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(AlarmLevel::getName, alarmLevel.getName());
queryWrapper.eq(AlarmLevel::getAccountId, accountId);
if (alarmLevelService.count(queryWrapper) > 0) {
return JsonResultUtils.fail("Alert Level Name duplicated");
}
}
if(alarmLevel.getShowTypes() == null) {
alarmLevel.setShowTypes(new ArrayList<>());
}
//
alarmLevel.setShowColorAlpha(StrUtils.hex2rgb(alarmLevel.getShowColor(), 0.3f));
alarmLevel.setAccountId(accountId);
alarmLevelService.saveOrUpdate(alarmLevel);
return JsonResultUtils.success();
}

@ApiOperation(value ="Delete Data")
@ApiImplicitParam(name ="id", value ="id")
@SaCheckPermission(value = {"algorithm-alarm-level"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) {
this.alarmLevelService.removeById(id);
this.algorithmService.clearLevel(id);
return JsonResultUtils.success();
}

@ApiOperation(value ="Query Data")
@ApiImplicitParam(name ="id", value ="id")
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@PostMapping({"/detail"})
@ResponseBody
public JsonResult detail(Long id) {
AlarmLevel alarmLevel = this.alarmLevelService.getById(id);
return JsonResultUtils.success(alarmLevel);
}

@ApiOperation(value ="Query Alert Type show show List")
@ApiImplicitParam(name ="id", value ="id")
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@PostMapping({"/listAlarmShowTypes"})
@ResponseBody
public JsonResult<List<Map<String, Object>>> listAlarmShowTypes() {
//
List<Map<String, Object>> alarmShowTypes = AlarmShowType.toList();
return JsonResultUtils.success(alarmShowTypes);
}

}
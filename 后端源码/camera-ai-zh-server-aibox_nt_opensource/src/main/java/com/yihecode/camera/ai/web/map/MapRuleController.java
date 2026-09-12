package com.yihecode.camera.ai.web.map;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import com.yihecode.camera.ai.entity.map.MapRule;
import com.yihecode.camera.ai.service.map.MapRuleService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* Camera or Box Rule rule Config Management
*/
@Slf4j
@RestController
@RequestMapping("map/rule")
public class MapRuleController {

    @Autowired
    private MapRuleService mapRuleService;

    /**
* Edit Data
* @param mapRule
* @return
*/
    @SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
    @PostMapping("save")
    public JsonResult<?> save(@RequestBody MapRule mapRule) {
        mapRuleService.saveOrUpdate(mapRule);
        return JsonResultUtils.success();
    }

    @SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
    @GetMapping("info")
    public JsonResult<?> info(Integer type) {
        MapRule mapRule = mapRuleService.getLatest(type);
        return JsonResultUtils.success(mapRule);
    }
}

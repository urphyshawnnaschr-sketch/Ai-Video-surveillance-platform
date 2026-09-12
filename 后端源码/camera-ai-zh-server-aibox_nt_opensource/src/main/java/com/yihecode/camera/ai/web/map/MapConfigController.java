package com.yihecode.camera.ai.web.map;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import com.yihecode.camera.ai.entity.map.MapConfig;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.service.map.MapConfigService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
* Ground image or image Layer Config Management
*/
@Api(tags = "System Management _ Base image image Layer Management")
@Slf4j
@RestController
@RequestMapping("map/config")
public class MapConfigController {

    @Autowired
    private MapConfigService mapConfigService;

    /**
* List Data
* @return
*/
    @ApiOperation("List Data")
    //@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@GetMapping("list")
public PageResult<?> list(@RequestHeader("Lang") String language) {
List<MapConfig> mapConfigList = mapConfigService.listData();
if(mapConfigList == null) {
mapConfigList = new ArrayList<>();
}
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
mapConfigList.forEach(mapConfig -> mapConfig.setName(mapConfig.getEnglishName()));
}
return PageResultUtils.success(null, mapConfigList);
}

/**
* Save
* @param mapConfig
* @return
*/
@ApiOperation("Add / Modify Data")
@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@PostMapping("save")
public JsonResult<?> save(@RequestBody MapConfig mapConfig) {
mapConfigService.saveOrUpdate(mapConfig);
return JsonResultUtils.success(mapConfig.getId());
}

/**
* Delete
* @param id
* @return
*/
@ApiOperation("Delete Data")
@ApiImplicitParam(name ="id", value ="Base image image Layer ID", example ="1")
@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@GetMapping("delete")
public JsonResult<?> delete(Long id) {
mapConfigService.deleteData(id);
return JsonResultUtils.success();
}

/**
* Detail
* @return
*/
@ApiOperation("Detail Data")
@ApiImplicitParam(name ="id", value ="Base image image Layer ID", example ="1")
@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@GetMapping("info")
public JsonResult<?> info(Long id) {
return JsonResultUtils.success(mapConfigService.getById(id));
}
}

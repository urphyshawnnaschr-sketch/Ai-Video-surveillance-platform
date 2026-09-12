package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaIgnore;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.*;

/**
* Cache Management
*/
@Slf4j
@ApiIgnore
@SaIgnore
@RestController
@RequestMapping("/cache")
public class CacheController {

    @Resource
    private ConfigService configService;

    private final List<String> PUBLICK_TAGS = Arrays.asList("afterSalesQRCodes", "clearFaceReportDay");

    /**
* Clear Divide Cache
* @param tag
* @return
*/
    @SaIgnore
    @GetMapping("/get")
    public JsonResult<String> get(String tag) {
        if(tag == null) {
            return JsonResultUtils.fail("lack Missing tag Param");
        }

        if(!PUBLICK_TAGS.contains(tag)) {
            return JsonResultUtils.success("X");
        }

        String val = configService.getByValTag(tag);
        return JsonResultUtils.success(val);
    }
}

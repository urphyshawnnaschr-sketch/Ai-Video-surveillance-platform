package com.yihecode.camera.ai.web.wvp;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.yihecode.camera.ai.config.SipConfig;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "GB Standard Platform Info Management")
@Controller
@RequestMapping({"/wvp/platform"})
public class GBPlatformController {

    @Autowired
    private SipConfig sipConfig;

    @SaCheckPermission("gb-platform-info")
    @ApiOperation("Get GB Standard Config Info")
    @GetMapping("info")
    @ResponseBody
    public JsonResult<Map<String, Object>> getInfo() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("sipId", sipConfig.getId());
        dataMap.put("sipDomain", sipConfig.getDomain());
        dataMap.put("sipIp", sipConfig.getIp());
        dataMap.put("sipPort", sipConfig.getPort());
        dataMap.put("sipPassword", sipConfig.getPassword());
        return JsonResultUtils.success(dataMap);
    }

}

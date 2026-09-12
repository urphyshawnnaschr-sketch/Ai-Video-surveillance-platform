package com.yihecode.camera.ai.web.api.aibox;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.api.aibox.vo.LoggerVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;

/**
* Edge Box Push Log Info
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@SaIgnore
@ApiIgnore
@Api(tags = "Edge Box Log Info Report Management")
@Slf4j
@RestController
@RequestMapping({ "/api/aibox/logger" })
public class AiBoxLoggerController {

    @Autowired
    private ConfigService configService;

    @Value("${uploadDir}")
    private String baseDir;

    @ApiOperation("Edge Box Log Info Report")
    @PostMapping({"", "/"})
    public JsonResult<Void> report(@RequestBody LoggerVo loggerVo) {
        try {
            //
if(StrUtil.isBlank(loggerVo.getMsg())) {
return JsonResultUtils.success();
}
// Validate key
String aiboxKey = configService.getByValTag("aiboxKey");
if (StrUtil.isBlank(aiboxKey)) {
log.error("Edge Box Report Exception: aiboxKey not Config {}", loggerVo);
return JsonResultUtils.fail("sss");
}
// key not Consistent
if (!SecureUtil.md5(aiboxKey).equals(loggerVo.getKey())) {
log.error("Edge Box Report Exception: key Value Error {}", loggerVo);
return JsonResultUtils.fail("key Value Error");
}
//
String filepath = baseDir + File.separator +"box_log"+ File.separator;
String filename ="error.log";
File tar = new File(filepath);
if(!tar.exists()) {
tar.mkdirs();
}

File file = new File(filepath + filename);
if(file.exists() && file.length() >= 30 * 1024 * 1024) {// most big 30m
FileUtil.del(file);
}
//
FileUtil.appendUtf8String(loggerVo.getMsg() +"\n", file);
} catch (Exception e) {
log.error("Edge Box Log Info Report Data Exception: {}", e);
}
return JsonResultUtils.success();
}
}
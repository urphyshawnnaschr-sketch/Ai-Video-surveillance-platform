package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
* Image Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Image Management")
@Controller
@RequestMapping({"/image"})
public class ImageController {

    /**
* Image Directory
*/
    @Value("${uploadDir}")
    private String uploadDir;

    /**
* Upload Image
* @param file
* @return
*/
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping({"/upload"})
    @ResponseBody
    public JsonResult doUpload(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            return JsonResultUtils.fail("Please select File");
        }
        String extName = FileUtil.extName(file.getOriginalFilename().toLowerCase());
        if (StrUtil.isBlank(extName)) {
            return JsonResultUtils.fail("not Support Image Format");
        }
        String extName2 = extName.toLowerCase();
        if (!"jpg".equals(extName2) && !"jpeg".equals(extName2) && !"png".equals(extName2)) {
            return JsonResultUtils.fail(extName2 + "not Support Image Format");
        }
        try {
            String newFileName = IdUtil.fastSimpleUUID() + "." + extName2;
            file.transferTo(new File(this.uploadDir + newFileName));
            return JsonResultUtils.success(newFileName);
        } catch (Exception e) {
            //e.printStackTrace();
return JsonResultUtils.fail("Image Upload Exception, Please Retry Later");
}
}

/**
* Image input out Stream, web show show Image
* @param fileName
* @param response
* @throws Exception
*/
// @ApiOperation("Image input out Stream")
// @ApiImplicitParam(name ="fileName", value ="Image Name")
// //@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
// @GetMapping({"/stream"})
// public void getImageAsByteArray(String fileName, HttpServletResponse response) throws Exception {
// if (!StrUtil.isBlank(fileName)) {
// try {
// String realName = FileUtil.getName(fileName);
// BufferedInputStream in = new BufferedInputStream(Files.newInputStream(new File(FileUtils.pathTo(uploadDir +"/"+ realName)).toPath()));
// response.setContentType("image/jpeg");
// IOUtils.copy(in, response.getOutputStream());
//} catch (Exception e) {
// //e.printStackTrace();
//}
//}
//}

/**
* Image input out Stream, web show show Image
* @param fileName
* @param response
* @throws Exception
*/
@ApiOperation("Image input out Stream")
@ApiImplicitParam(name ="fileName", value ="Image Name")
@SaCheckPermission(value = {"edgePlatform-groupView","box-algorithm-overview"}, mode = SaMode.OR)
@GetMapping({"camera/stream"})
public void cameraImage(String fileName, HttpServletResponse response) throws Exception {
if (!StrUtil.isBlank(fileName)) {
try {
String realName = FileUtil.getName(fileName);
BufferedInputStream in = new BufferedInputStream(Files.newInputStream(new File(FileUtils.pathTo(uploadDir +"/"+ realName)).toPath()));
response.setContentType("image/jpeg");
IOUtils.copy(in, response.getOutputStream());
} catch (Exception e) {
//e.printStackTrace();
}
}
}
}
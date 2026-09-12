package com.yihecode.camera.ai.web.map;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;

/**
* Phase close File Upload
*/
@Slf4j
@Controller
@RequestMapping("map/file")
public class MapUploadController {

    @Value("${uploadDir}")
    private String uploadDir;

    /**
* Upload File
* @param file
* @return
*/
    @SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
    @PostMapping("image/upload")
    @ResponseBody
    public JsonResult<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null) {
                return JsonResultUtils.fail("File Is Empty");
            }
            String originalFilename = file.getOriginalFilename();
            if (StrUtil.isBlank(originalFilename)) {
                return JsonResultUtils.fail("File Name Error");
            }
            String ext = FileUtil.extName(originalFilename);
            if (StrUtil.isBlank(ext)) {
                return JsonResultUtils.fail("not Support File Type");
            }
            if (!("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext) || "png".equalsIgnoreCase(ext))) {
                return JsonResultUtils.fail("Only Support jpg,jpeg,png Type File");
            }

            String newFileName = IdUtil.fastSimpleUUID() + "." + ext;
            file.transferTo(new File(FileUtils.pathTo(this.uploadDir + "/" + newFileName)));
            return JsonResultUtils.success(newFileName);
        } catch (Exception e) {
            //
}
return JsonResultUtils.fail("File Upload Error");
}

/**
* Upload File
* @param file
* @return
*/
@SaCheckPermission(value = {"map:mgr"}, mode = SaMode.OR)
@PostMapping("svg/upload")
@ResponseBody
public JsonResult<?> uploadSvg(MultipartFile file) throws Exception {
try {
if (file == null) {
return JsonResultUtils.fail("File Is Empty");
}

String originalFilename = file.getOriginalFilename();
if (StrUtil.isBlank(originalFilename)) {
return JsonResultUtils.fail("File Name Error");
}

String ext = FileUtil.extName(originalFilename);
if (StrUtil.isBlank(ext)) {
return JsonResultUtils.fail("not Support File Type");
}

if (!"svg".equalsIgnoreCase(ext)) {
return JsonResultUtils.fail("Only Support svg Type");
}

String newFileName = IdUtil.fastSimpleUUID() +".svg";
file.transferTo(new File(FileUtils.pathTo(this.uploadDir +"/"+ newFileName)));
return JsonResultUtils.success(newFileName);
} catch (Exception e) {
//
}
return JsonResultUtils.fail("File Upload Error");
}

/**
* Image input out Stream, web show show Image
* @param filename
* @param response
* @throws Exception
*/
//@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@GetMapping({"/stream"})
public void getImageAsByteArray(String filename, HttpServletResponse response) throws Exception {
if (StrUtil.isNotBlank(filename)) {
try {
String ext = FileUtil.extName(filename);
if("svg".equalsIgnoreCase(ext)) {
response.setContentType("image/svg+xml");
} else {
response.setContentType("image/jpeg");
}

BufferedInputStream in = new BufferedInputStream(Files.newInputStream(new File(this.uploadDir + filename).toPath()));
IOUtils.copy(in, response.getOutputStream());
} catch (Exception e) {
// e.printStackTrace();
}
}
}
}

package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ApProjectQueryDTO;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.entity.ap.ApFileDO;
import com.yihecode.camera.ai.service.ap.ApFileService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PageResult;
import com.yihecode.camera.ai.utils.PageResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

@ApiIgnore
@Api(tags = "Data Set Management")
@Controller
@RequestMapping({"/ap/file"})
@Slf4j
public class ApFileController {

    private static String BASE_PATH = "/data/dataset/";
    private static String FILE_PATH = "/data/file/";

    @Autowired
    private ApFileService apFileService;

    @ApiOperation(value = "Upload Data Set,")
    @PostMapping({"/upload/dataset"})
    //@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="file", value ="File Cut Cut File Name for ****.zip_ Number Char.tmp"),
//@ApiImplicitParam(name ="end", value ="1 for most after One Piece"),
//@ApiImplicitParam(name ="projectId", value ="Project Id"),
//})
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@ResponseBody
public JsonResult<Long> uploadDataset(@RequestParam("file") MultipartFile file,
@RequestParam(value ="projectId",required = true) Long projectId,
@RequestParam("end") String end) throws Exception {
String originalFilename = file.getOriginalFilename();
log.info("File Name {} Start Upload", originalFilename);
// File Name
String name = StringUtils.substringBeforeLast(StringUtils.substringBeforeLast(file.getOriginalFilename(),"."),".");
String mergeDir = BASE_PATH + projectId +"/merge";
String splitDir = BASE_PATH + projectId +"/"+ name +"/temp/split/";
String splitFile = splitDir + file.getOriginalFilename();
String mergeFile = mergeDir +"/"+"upload.zip";
boolean temp = false;
if (FileUtil.exist(splitFile)) {
BufferedInputStream tempFileIo = FileUtil.getInputStream(splitFile);
if (IoUtil.contentEquals(file.getInputStream(), tempFileIo)) {
temp = true;
} else {

FileUtil.del(splitFile);
}
tempFileIo.close();
}
if (!temp) {
if (!FileUtil.exist(splitDir)) {
FileUtil.mkdir(splitDir);
}
FileUtil.writeFromStream(file.getInputStream(), splitFile);
}
if ("1".equals(end)) {
log.info("File Name {} combine and File Start", originalFilename);
List<String> fileList = FileUtil.listFileNames(splitDir);
Collections.sort(fileList, (o1, o2) -> {
return Integer.valueOf(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(o1,"."),"_"))
.compareTo(Integer.valueOf(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(o2,"."),"_")));
});
if (!FileUtil.exist(mergeDir)) {
FileUtil.mkdir(mergeDir);
}
FileOutputStream fos = new FileOutputStream(mergeFile);
for (String filename: fileList) {
log.info("File Name {} combine and File Start", originalFilename);
FileUtil.writeToStream(splitDir + filename, fos);
log.info("File Name {} combine and File End", originalFilename);
}
fos.close();
log.info("File Name {} Delete Temp Hour File Start", originalFilename);
FileUtil.del(new File(splitDir));
log.info("File Name {} Delete Temp Hour File End", originalFilename);
Long userId = StpUtil.getLoginIdAsLong();
return JsonResultUtils.success(apFileService.saveFile(mergeFile, 1,null,userId));
}
return JsonResultUtils.success(1L);
}

@ApiOperation(value ="Upload Annotation Example")
//@ApiImplicitParams(value = {
//@ApiImplicitParam(name ="file", value ="File Cut Cut File for ****.zip_ Number Char.tmp"),
//@ApiImplicitParam(name ="fileType", value ="File Type,2-excel table Grid,3-doc"),
//@ApiImplicitParam(name ="projectId", value ="Project Id")
//})
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@PostMapping({"/upload"})
@ResponseBody
public JsonResult<Long> upload(@RequestParam("file") MultipartFile file,
@RequestParam("fileType") Integer fileType,
@RequestParam("projectId") Long projectId) throws Exception {
String fileDir = FILE_PATH + projectId;
String filePath = fileDir +"/"+ file.getOriginalFilename();
if (!FileUtil.exist(fileDir)) {
FileUtil.mkdir(fileDir);
}
FileUtil.writeFromStream(file.getInputStream(), filePath);
Long userId = StpUtil.getLoginIdAsLong();
return JsonResultUtils.success(apFileService.saveFile(filePath, fileType,projectId,userId));
}

@ApiOperation(value ="Get Annotation Example")
@SaIgnore
@GetMapping({"/stream"})
public void getImageAsByteArray(@RequestParam(defaultValue ="0") Long id, HttpServletResponse response) {
ApFileDO apFileDO = apFileService.getById(id);
if(apFileDO!= null && StrUtil.isNotBlank(apFileDO.getRawData())) {
try {
BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(apFileDO.getRawData())));
response.setHeader("Content-Type","application/word");
response.setHeader("Content-Disposition","attachment;filename="+ java.net.URLEncoder.encode(
StringUtils.substringAfterLast(apFileDO.getRawData(),"/"),"UTF-8"));
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
e.printStackTrace();
}
}
}
}

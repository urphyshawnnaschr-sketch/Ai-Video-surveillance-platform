package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.yihecode.camera.ai.entity.CameraBatchImport;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.CameraBatchImportService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.vo.CameraBatchErrorExportVo;
import com.yihecode.camera.ai.web.vo.CameraBatchImportVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
* Function can: Camera Batch Import
* 1. First Import Temp Hour table tbl_biz_camera_import, Validate Field Whether Correct, Whether can Enough Splice connect rtsp Address;
* 2. JobCron Scheduled Task Schedule, will tbl_biz_camera_import Record Import tbl_biz_camera, main need Verify Get image Whether Correct;
* tips: Get image Operation Compare Consume Consume Property can, form Data Process
*
* @author zhoumingxing
* @date 2024/3/23
*/
@Slf4j
@Api(tags = "Camera Batch Import Management")
@RestController
@RequestMapping("/camera/batch/import")
public class CameraBatchImportController {

    @Resource
    private CameraBatchImportService cameraBatchImportService;

    @Resource
    private ConfigService configService;

    @Value("${uploadDir}")
    private String uploadDir;

    /**
* Batch excel File Upload
*
* @param files
* @return
* @throws Exception
*/
    @SaCheckPermission(value = {"box-batch-add"}, mode = SaMode.OR)
    @PostMapping("/uploads")
    public JsonResult<Map<String, Object>> uploads(List<MultipartFile> files) throws Exception {
        if (files == null || files.size() == 0) {
            return JsonResultUtils.fail("Please select Upload File");
        }
        //Validate File Format Whether Error
List<String> extErrs = new ArrayList<>();
for (MultipartFile file: files) {
String filename = file.getOriginalFilename();
String extName = FileUtil.extName(filename);
if (StrUtil.isBlank(extName) ||!"xlsx".equals(extName.toLowerCase())) {
extErrs.add(filename);
}
}
// part part File Format Exist Problem
if (!extErrs.isEmpty()) {
return JsonResultUtils.fail(String.join(",", extErrs) +"not is Excel File");
}
// Clear Data
cameraBatchImportService.deleteAll();
// Server Type (1-PC Server, 2- Calculate Box)
String serverType = configService.getByValTag("SERVER_TYPE");
// Import Batch Label
String tag = UUID.randomUUID().toString();
// Import Error File
List<String> fileErrs = new ArrayList<>();
for (MultipartFile file: files) {
try {
EasyExcel.read(file.getInputStream(), CameraBatchImportVo.class, new CameraBatchImportListener(cameraBatchImportService, StpUtil.getLoginIdAsLong(), serverType, file.getOriginalFilename(), tag)).sheet().headRowNumber(2).doRead();
} catch (Exception e) {
fileErrs.add(file.getName());
}
}
// Exist Export File Exception, Clear Data
if (!fileErrs.isEmpty()) {
cameraBatchImportService.deleteAll();
}
// Execute Import
new Thread(new Runnable() {
@Override
public void run() {
List<CameraBatchImport> cameraBatchImports = cameraBatchImportService.listByTag(tag, 1);
for (CameraBatchImport cameraBatchImport: cameraBatchImports) {
cameraBatchImportService.saveImport(cameraBatchImport);
}
}
}).start();

//
Map<String, Object> outMap = new HashMap<>();
outMap.put("fileErrs", fileErrs);
outMap.put("tag", tag);
return JsonResultUtils.success(outMap);
}

/**
* By Import Batch Label Query Import Status
*
* @return
*/
@SaCheckPermission(value = {"edgePlatform-boxManagement","box-batch-add"}, mode = SaMode.OR)
@GetMapping("/statusByTag")
public JsonResult<Map<String, Object>> statusByTag(String tag) {
//
if (StrUtil.isBlank(tag)) {
//return JsonResultUtils.fail("Import Batch Label Error");
tag = cameraBatchImportService.getLastImportTag();
}
// find not to Label
if (StrUtil.isBlank(tag)) {
Map<String, Object> retMap = new HashMap<>();
retMap.put("totalNum", 0);
retMap.put("failNum", 0);
retMap.put("successNum", 0);
retMap.put("finishNum", 0);
retMap.put("checkNum", 0);
retMap.put("status", 0); // 0- not has Import 1- In Progress Import 2- Import Complete Complete (All Success) 3- Import Complete Complete (Exist Import Error)
retMap.put("text","no Import"); // 0- not has Import 1- In Progress Import 2- Import Complete Complete (All Success) 3- Import Complete Complete (Exist Import Error)
retMap.put("percentage", 0);
return JsonResultUtils.success(retMap);
}
//
int totalNum = cameraBatchImportService.countByTag(tag, null);
int failNum = cameraBatchImportService.countByTag(tag, 2);
int successNum = cameraBatchImportService.countByTag(tag, 3);
int checkNum = cameraBatchImportService.countByTag(tag, 1);
int finishNum = failNum + successNum;
//
int percentage = 100;
if (totalNum > 0) {
percentage = Double.valueOf((failNum + successNum) * 1.0 / totalNum * 100).intValue();
}
//
Map<String, Object> retMap = new HashMap<>();
retMap.put("totalNum", totalNum);
retMap.put("failNum", failNum);
retMap.put("successNum", successNum);
retMap.put("finishNum", finishNum);
retMap.put("checkNum", checkNum);
retMap.put("status", totalNum == 0? 0: (checkNum > 0? 1: (successNum == totalNum? 2: 3))); // 0- not has Import 1- In Progress Import 2- Import Complete Complete (All Success) 3- Import Complete Complete (Exist Import Error)
retMap.put("text", totalNum == 0?"no Import": (checkNum > 0? ("In Progress Import"+ percentage +"%"): (successNum == totalNum? ("Import Complete Complete,"+ successNum +"Success"):"Import Complete Complete,"+ failNum +"Error"))); // 0- not has Import 1- In Progress Import 2- Import Complete Complete (All Success) 3- Import Complete Complete (Exist Import Error)
retMap.put("percentage", percentage);
return JsonResultUtils.success(retMap);
}

@ApiOperation("Alarm Data Export")
@ApiImplicitParam(name ="tag", value ="Import Batch Label")
@SaCheckPermission(value = {"box-batch-add"}, mode = SaMode.OR)
@GetMapping("/export")
public void export(String tag, HttpServletResponse response) throws Exception {
if (StrUtil.isBlank(tag)) {
throw new BizException("Import Batch Label Error");
}
// Detection Export Root Directory Whether Exist
File tarRoot = new File(uploadDir);
if (!tarRoot.exists()) {
throw new BizException("Export Directory not Set");
}
// Create Export Directory
File tar = new File(uploadDir + File.separator +"camera_import_errors"+ File.separator + tag + File.separator);
if (tar.exists()) {
FileUtil.del(tar);
}
tar.mkdirs();
// Query Export Data
List<CameraBatchImport> cameraBatchImports = cameraBatchImportService.listByTag(tag, 2);
//
List<String> filenames = new ArrayList<>();
for (CameraBatchImport cameraBatchImport: cameraBatchImports) {
if (filenames.contains(cameraBatchImport.getFilename())) {
continue;
}
filenames.add(cameraBatchImport.getFilename());
}
// by File Name Export
for (String filename: filenames) {
//
int idx = 1;
List<CameraBatchErrorExportVo> cameraBatchErrorExportVos = new ArrayList<>();
for (CameraBatchImport cameraBatchImport: cameraBatchImports) {
if (cameraBatchImport.getFilename().equals(filename)) {
CameraBatchErrorExportVo cameraBatchErrorExportVo = new CameraBatchErrorExportVo();
cameraBatchErrorExportVo.setIdx(idx++);
cameraBatchErrorExportVo.setBrand(cameraBatchImport.getBrand());
cameraBatchErrorExportVo.setName(cameraBatchImport.getName());
cameraBatchErrorExportVo.setIpHost(cameraBatchImport.getIpHost());
cameraBatchErrorExportVo.setPort(cameraBatchImport.getPort());
cameraBatchErrorExportVo.setChannel(cameraBatchImport.getChannel());
cameraBatchErrorExportVo.setAccount(cameraBatchImport.getAccount());
cameraBatchErrorExportVo.setPassword(cameraBatchImport.getPassword());
cameraBatchErrorExportVo.setAlgorithmNames(cameraBatchImport.getAlgorithms());
cameraBatchErrorExportVo.setLocationName(cameraBatchImport.getLocation());
cameraBatchErrorExportVo.setAlarmInterval(cameraBatchImport.getAlarmInterval() == null?"": cameraBatchImport.getAlarmInterval() +"");
cameraBatchErrorExportVo.setIntervalTime(cameraBatchImport.getIntervalTime() == null?"": cameraBatchImport.getIntervalTime() +"");
cameraBatchErrorExportVo.setRtspUrl(cameraBatchImport.getRtspUrl2()); // input out customer account Manual input in Stream Address
cameraBatchErrorExportVo.setMistakeDesc(cameraBatchImport.getMistakeDesc());
cameraBatchErrorExportVos.add(cameraBatchErrorExportVo);
}
}
// Generate Import Error excel
String fileName = tar.getAbsolutePath() + File.separator + filename;
EasyExcel.write(fileName, CameraBatchErrorExportVo.class)
.sheet("Error Data")
.doWrite(() -> {
return cameraBatchErrorExportVos;
});
}
// Type Package zip
File zipfile = ZipUtil.zip(tar, StandardCharsets.UTF_8);
// Delete File
FileUtil.del(tar);
// Execute Export
try (FileInputStream fis = new FileInputStream(zipfile)) {
// Export File
String chName ="Import Error Data Export _ Download.zip";
response.setContentType("application/octet-stream");
response.setContentLengthLong(zipfile.length());
response.addHeader("Content-Disposition","attachment; filename="+ URLEncoder.encode(chName,"UTF-8"));
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));

// Export success, Delete Import Record
cameraBatchImportService.deleteAll();

// Delete Compress Package
FileUtil.del(zipfile);
} catch (Exception e) {
throw new BizException("Export Hour send produce Error");
}
}

/**
* Delete All
*
* @return
*/
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping("/deleteAll")
public JsonResult<Boolean> deleteAll() {
cameraBatchImportService.deleteAll();
return JsonResultUtils.success(true);
}

/**
* Download Import Mode Version
*
* @param request
* @param response
*/
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@GetMapping("/downloadTemplateFile")
public void downloadTemplateFile(HttpServletRequest request, HttpServletResponse response) {
try {
response.reset();
response.setContentType("application/octet-stream");
response.addHeader("Access-Control-Allow-Origin","*");
response.addHeader("Content-Disposition","attachment; filename="+ URLEncoder.encode("Import Mode Version.xlsx","utf-8"));
FileCopyUtils.copy(ModelController.class.getResourceAsStream("/templateFile/cameraBatchImport2.xlsx"), response.getOutputStream());
} catch (Exception e) {
log.error("Download Export Template Exception", e);
}
}
}
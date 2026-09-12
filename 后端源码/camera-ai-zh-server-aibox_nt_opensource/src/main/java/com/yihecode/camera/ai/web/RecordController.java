package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.yihecode.camera.ai.config.MediaNodeConfig;
import com.yihecode.camera.ai.config.MinioConfig;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.Record;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.service.RecordService;
import com.yihecode.camera.ai.service.ReportService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("record")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private ProjectConfig projectConfig;

    @SaCheckPermission(value = {"alarmManagement", "edgePlatform-videoPreview"}, mode = SaMode.OR)
    @GetMapping("play")
    @ResponseBody
    public JsonResult<?> play(Long reportId) {
        Report report = reportService.getById(reportId);
        if(report.getRecordId() == null || report.getRecordId() == 0) {
            return JsonResultUtils.success("");
        }

        Record record = recordService.getById(report.getRecordId());
        if(record == null || record.getUploadFlag() == null || record.getUploadFlag() != 1) {
            return JsonResultUtils.success("");
        }

       //String playUrl = String.format("http://%s:%s/%s", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort(), record.getFileUrl());
String playUrl = String.format("%s/%s", minioConfig.getUrl(), record.getRecordPath());
return JsonResultUtils.success(playUrl);
}

@SaCheckPermission("XXXXXX")
@GetMapping("list")
@ResponseBody
public JsonResult<?> list(@RequestParam(defaultValue ="4") Integer limit) {
if(limit > 20) {
limit = 20;
}

List<String> recordUrls = new ArrayList<>();
List<Report> reportList = reportService.listRecordByLimit(50);
if(reportList == null || reportList.isEmpty()) {
return JsonResultUtils.success(recordUrls);
}

for(Report report: reportList) {
Record record = recordService.getById(report.getRecordId());
if(record == null || record.getUploadFlag() == null || record.getUploadFlag()!= 1) {
continue;
}

//
// String playUrl = String.format("http://%s:%s/%s", mediaNodeConfig.getHttpIp(), mediaNodeConfig.getHttpPort(), record.getFileUrl());
String playUrl = String.format("%s/%s", minioConfig.getUrl(), record.getRecordPath());
recordUrls.add(playUrl);

//
if (recordUrls.size() >= limit) {
break;
}
}
return JsonResultUtils.success(recordUrls);
}
}

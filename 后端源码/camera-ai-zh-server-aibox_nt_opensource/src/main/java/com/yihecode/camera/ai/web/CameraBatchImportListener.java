package com.yihecode.camera.ai.web;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.yihecode.camera.ai.entity.CameraBatchImport;
import com.yihecode.camera.ai.service.CameraBatchImportService;
import com.yihecode.camera.ai.web.vo.CameraBatchImportVo;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
* Camera Batch Import Process
*/
@Slf4j
public class CameraBatchImportListener implements ReadListener<CameraBatchImportVo> {

    /**
* Each Separate 5 Storage Database, real International make Use in can with 100, after Clear Reason list, Method Convenient inner Store return receive
*/
    private static final int BATCH_COUNT = 500;

    /**
* Cache Data
*/
    private List<CameraBatchImportVo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    //Operator member
private Long accountId;

// Region Type 1- Server Version 2- Box Version
private String locationType;

// Import File Name
private String filename;

// Batch Label
private String tag;

//
private CameraBatchImportService cameraBatchImportService;

/**
* false set this is One DAO, when has Business Logic this Also can with is One service. when like Result not Use Storage this Object not Use.
*/
public CameraBatchImportListener(CameraBatchImportService cameraBatchImportService, Long accountId, String locationType, String filename, String tag) {
this.cameraBatchImportService = cameraBatchImportService;
this.accountId = accountId;
this.locationType = locationType;
this.filename = filename;
this.tag = tag;
}

/**
* this Each One Data Parse all will Incoming Call
*
* @param data one row value. Is is same as {@link AnalysisContext#readRowHolder()}
* @param context
*/
@Override
public void invoke(CameraBatchImportVo data, AnalysisContext context) {
cachedDataList.add(data);
// Reach to BATCH_COUNT, Need Remove Storage One sub Database, Prevent Data Several Ten Thousand Data In inner Store, Capacity Easy OOM
if (cachedDataList.size() >= BATCH_COUNT) {
saveData();
// Storage Complete Complete Clear Reason list
cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
}
}

/**
* All Data Parse Complete Complete all will Incoming Call
*
* @param context
*/
@Override
public void doAfterAllAnalysed(AnalysisContext context) {
// Here Also need Save Data, Ensure most after Left Object Data Also Storage to Database
saveData();
log.info("All Data Parse Complete Complete!");
}

/**
* Add up Storage Database
*/
private void saveData() {
log.info("{} Data, Start Storage Database!", cachedDataList.size());

//
for(CameraBatchImportVo cameraBatchImportVo: cachedDataList) {
CameraBatchImport cameraBatchImport = new CameraBatchImport();
cameraBatchImport.setBrand(cameraBatchImportVo.getBrand());
cameraBatchImport.setName(cameraBatchImportVo.getName());
cameraBatchImport.setIpHost(cameraBatchImportVo.getIpHost());
cameraBatchImport.setPort(cameraBatchImportVo.getPort());
cameraBatchImport.setChannel(cameraBatchImportVo.getChannel());
cameraBatchImport.setAccount(cameraBatchImportVo.getAccount());
cameraBatchImport.setPassword(cameraBatchImportVo.getPassword());
cameraBatchImport.setAlgorithms(cameraBatchImportVo.getAlgorithmNames());
cameraBatchImport.setLocation(cameraBatchImportVo.getLocationName());
cameraBatchImport.setAlarmInterval(parse(cameraBatchImportVo.getAlarmInterval()));
cameraBatchImport.setIntervalTime(parse(cameraBatchImportVo.getIntervalTime()));
cameraBatchImport.setAccountId(String.valueOf(this.accountId));
cameraBatchImport.setMistake("");
cameraBatchImport.setCheckState(3); // Validate Complete Complete
cameraBatchImport.setImportState(1); // Import in
cameraBatchImport.setCreatedAt(new Date());
cameraBatchImport.setLocationType(locationType);
cameraBatchImport.setRtspUrl(cameraBatchImportVo.getRtspUrl());
cameraBatchImport.setFilename(this.filename);
cameraBatchImport.setTag(this.tag);
cameraBatchImport.setRtspUrl2(cameraBatchImportVo.getRtspUrl());
cameraBatchImportService.saveData(cameraBatchImport);
}
//
log.info("Storage Database Success!");
}

/**
* Convert for Float Point Number
* @param str
* @return
*/
private Float parse(String str) {
if(StrUtil.isBlank(str)) {
return null;
}
//
try {
return Float.parseFloat(str);
} catch (Exception e) {
return null;
}
}
}

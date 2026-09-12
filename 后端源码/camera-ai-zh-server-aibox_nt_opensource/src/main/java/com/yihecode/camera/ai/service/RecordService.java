package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Record;

import java.util.List;

/**
* Recording Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface RecordService extends IService<Record> {

    List<Record> listEarlyByFlag(Integer flag, long mills);

    Record getRecordId(String app, String stream, long mills);

    List<Record> listByUploadFlag(Long locationId, Integer uploadFlag, long mills);

    /**
* Delete Recording
* @param recordId
*/
    void deleteRecord(Long recordId);
}
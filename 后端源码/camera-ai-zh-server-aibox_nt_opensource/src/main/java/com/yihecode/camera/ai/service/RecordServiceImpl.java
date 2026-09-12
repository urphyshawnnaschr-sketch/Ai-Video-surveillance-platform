package com.yihecode.camera.ai.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.config.MinioConfig;
import com.yihecode.camera.ai.entity.Record;
import com.yihecode.camera.ai.mapper.RecordMapper;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Recording Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public List<Record> listEarlyByFlag(Integer flag, long mills) {
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Record::getFlag, flag);
        queryWrapper.lt(Record::getEndTime, mills);
        return this.list(queryWrapper);
    }

    @Override
    public Record getRecordId(String app, String stream, long mills) {
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Record::getApp, app);
        queryWrapper.eq(Record::getStream, stream);
        queryWrapper.le(Record::getStartTime, mills);
        queryWrapper.ge(Record::getEndTime, mills);
        return this.getOne(queryWrapper, false);
    }

    /**
* Query Recent Bind Alarm, But not has Upload Recording File Record
* @param locationId
* @param uploadFlag
* @param mills
* @return
*/
    @Override
    public List<Record> listByUploadFlag(Long locationId, Integer uploadFlag, long mills) {
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Record::getId, Record::getFileUrl);
        queryWrapper.eq(Record::getLocationId, locationId);
        queryWrapper.eq(Record::getUploadFlag, uploadFlag);
        queryWrapper.eq(Record::getFlag, 1);
        queryWrapper.gt(Record::getEndTime, mills);
        return this.list(queryWrapper);
    }

    /**
* Delete Recording
*
* @param recordId
*/
    @Override
    public void deleteRecord(Long recordId) {
        if(recordId == null || recordId == 0) {
            return ;
        }

        //Query
Record record = this.getById(recordId);
if(record == null || StrUtil.isBlank(record.getRecordPath())) {
return;
}

// Delete Record
this.removeById(recordId);

// from minio Delete
try {
String recordPath = record.getRecordPath();
recordPath = recordPath.replace("record/","");
minioConfig.getMinioClient().removeObject(RemoveObjectArgs.builder().bucket("record").object(recordPath).build());
log.info("minio Recording Delete success, {}", recordPath);
} catch (Exception e) {
log.error("Delete minio Recording Record Exception, ex:", e);
}
}
}
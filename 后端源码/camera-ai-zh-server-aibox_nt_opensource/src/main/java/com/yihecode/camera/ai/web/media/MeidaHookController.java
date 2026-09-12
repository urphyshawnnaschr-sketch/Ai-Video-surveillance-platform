package com.yihecode.camera.ai.web.media;

import cn.dev33.satoken.annotation.SaIgnore;
import com.yihecode.camera.ai.entity.Record;
import com.yihecode.camera.ai.service.RecordService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.media.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SaIgnore
@Slf4j
@RestController
@RequestMapping("media/hook")
public class MeidaHookController {

    @Autowired
    private RecordService recordService;

    /**
* Access http File Server up hls of outer File Hour Trigger
* @return
*/
    @PostMapping("on_http_access")
    public JsonResult<?> onHttpAccess(@RequestBody OnHttpAccessVO httpAccessVO) {
        log.info("onHttpAccess Req: {}", httpAccessVO);
        return JsonResultUtils.success();
    }

    /**
* Play Put Device Auth Event
* @return
*/
    @PostMapping("on_play")
    public JsonResult<?> onPlay(@RequestBody OnPlayVO playVO) {
        log.info("onPlay Req: {}", playVO);
        return JsonResultUtils.success();
    }

    /**
* rtsp/rtmp/rtp Push Stream Auth Event
* @return
*/
    @PostMapping("on_publish")
    public JsonResult<?> onPublish(@RequestBody OnPublishVO publishVO) {
        log.info("onPublish Req: {}", publishVO);
        return JsonResultUtils.success();
    }

    /**
* Record make mp4 Complete Complete after Notification Event
* @return
*/
    @PostMapping("on_record_mp4")
    public JsonResult<?> onRecordMp4(@RequestBody OnRecordMp4VO recordMp4VO) {
        Record record = new Record();
        record.setMediaServerId(recordMp4VO.getMediaServerId());
        record.setApp(recordMp4VO.getApp());
        record.setStream(recordMp4VO.getStream());
        record.setFileName(recordMp4VO.getFileName());
        record.setFileSize(recordMp4VO.getFileSize());
        record.setFileUrl(recordMp4VO.getUrl());
        record.setStartTime(recordMp4VO.getStartTime());
        record.setEndTime(System.currentTimeMillis());
        record.setTimeLen(Double.valueOf(recordMp4VO.getTimeLen()).intValue());
        record.setFlag(0);
        recordService.save(record);
        return JsonResultUtils.success();
    }

    /**
* rtsp/rtmp Stream Register or Note Sell Hour Trigger this Event
* @return
*/
    @PostMapping("on_stream_changed")
    public JsonResult<?> onStreamChanged(@RequestBody OnStreamChangedVO streamChangedVO) {
        log.info("onStreamChanged Req: {}", streamChangedVO);
        return JsonResultUtils.success();
    }

    /**
* Stream no Person View Hour Event
* @return
*/
    @PostMapping("on_stream_none_reader")
    public JsonResult<?> onStreamNoneReader(@RequestBody OnStreamNoneReaderVO streamNoneReaderVO) {
        log.info("onStreamNoneReader Req: {}", streamNoneReaderVO);
        return JsonResultUtils.success();
    }

    /**
* Stream not find to Event
* @return
*/
    @PostMapping("on_stream_not_found")
    public JsonResult<?> onStreamNotFound(@RequestBody OnStreamNotFoundVO streamNotFoundVO) {
        log.info("onStreamNotFound Req: {}", streamNotFoundVO);
        return JsonResultUtils.success();
    }

    /**
* Server Start Event
* @return
*/
    @PostMapping("on_server_started")
    public JsonResult<?> onServerStarted(@RequestBody OnServerStartedVO serverStartedVO) {
        log.info("onServerStarted Req: {}", serverStartedVO);
        return JsonResultUtils.success();
    }

    /**
* Server Timing Report Time
* @return
*/
    @PostMapping("on_server_keepalive")
    public JsonResult<?> onServerKeeplive(@RequestBody OnServerKeepliveVO serverKeepliveVO) {
        //log.info("onServerKeeplive Req: {}", serverKeepliveVO);
return JsonResultUtils.success();
}

/**
* Call openRtpServer API,rtp server long Time not receive to Data
* @return
*/
@PostMapping("on_rtp_server_timeout")
public JsonResult<?> onRtpServerTimeout() {
return JsonResultUtils.success();
}
}
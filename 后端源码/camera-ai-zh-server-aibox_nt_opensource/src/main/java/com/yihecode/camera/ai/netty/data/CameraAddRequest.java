package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class CameraAddRequest extends Request {

    @JSONField(name = "camera_id")
    private Long cameraId;

    @JSONField(name = "rtsp_url")
    private String rtspUrl;

    @JSONField(name = "interval_time")
    private Float intervalTime;

    @JSONField(name = "video_fps")
    private Integer videoFps;

    @JSONField(name = "algorithms")
    private List<Algo> algorithms;

    @Data
    public static class Algo {

        @JSONField(name = "algorithm_id")
        private Long algoId;

        @JSONField(name = "algorithm_name")
        private String algoName;

        @JSONField(name = "algorithm_name_en")
        private String algoNameEn;

        @JSONField(name = "algorithm_confidence")
        private Float algoConfidence;

        @JSONField(name = "algorithm_ver")
        private String algoVer;

        @JSONField(name = "algorithm_rois")
        private String algoRois;

        @JSONField(name = "algorithm_lines")
        private String algoLines;

        @JSONField(name = "zip_file")
        private String zipFile;

        @JSONField(name = "zip_name")
        private String zipName;

        @JSONField(name = "md5")
        private String md5;

        @JSONField(name = "share_mode")
        private Integer shareMode;

        @JSONField(name = "extras")
        private String extras;

    }
}

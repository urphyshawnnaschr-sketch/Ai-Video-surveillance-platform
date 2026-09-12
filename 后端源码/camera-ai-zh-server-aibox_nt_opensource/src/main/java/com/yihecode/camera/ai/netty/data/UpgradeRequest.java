package com.yihecode.camera.ai.netty.data;

import com.alibaba.fastjson.annotation.JSONField;

public class UpgradeRequest extends Request {

    @JSONField(name = "file_md5")
    private String fileMd5;

    @JSONField(name = "algo_code")
    private String algoCode;

    @JSONField(name = "algo_ver")
    private String algoVer;

    @JSONField(name = "algo_id")
    private Long algoId;

    @JSONField(name = "file_name")
    private String fileName;

    @JSONField(name = "file_url")
    private String fileUrl;

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getAlgoCode() {
        return algoCode;
    }

    public void setAlgoCode(String algoCode) {
        this.algoCode = algoCode;
    }

    public String getAlgoVer() {
        return algoVer;
    }

    public void setAlgoVer(String algoVer) {
        this.algoVer = algoVer;
    }

    public Long getAlgoId() {
        return algoId;
    }

    public void setAlgoId(Long algoId) {
        this.algoId = algoId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}

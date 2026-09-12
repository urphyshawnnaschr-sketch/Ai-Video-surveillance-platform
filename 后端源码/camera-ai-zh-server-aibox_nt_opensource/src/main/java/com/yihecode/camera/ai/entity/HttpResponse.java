package com.yihecode.camera.ai.entity;

import java.io.InputStream;

/**
* Http Response Info
*/
public class HttpResponse {

    /**
* HTTP Status Code
*/
    private Integer httpStatus;

    /**
* String Data
*/
    private String stringData;

    /**
* Byte Number group Data
*/
    private byte[] byteArrayData;

    /**
* Stream Data
*/
    private InputStream inputStream;

    /**
* getters and setters
*/

    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public byte[] getByteArrayData() {
        return byteArrayData;
    }

    public void setByteArrayData(byte[] byteArrayData) {
        this.byteArrayData = byteArrayData;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }
}

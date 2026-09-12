package com.yihecode.camera.ai.config;

/**
* HttpClient Bridge connect Config
*/
public class HttpClientConfigInfo {

    /**
* Connection timeout Time
*/
    private Integer connectTimeout;

    /**
* read Timeout Time
*/
    private Integer readTimeout;

    /**
* write Timeout Time
*/
    private Integer writeTimeout;

    /**
* Connection Pool most big Number
*/
    private Integer maxConnections;

    /*getters and setters*/

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Integer writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }
}

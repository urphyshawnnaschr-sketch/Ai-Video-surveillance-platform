package com.yihecode.camera.ai.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpClientConfig {

    private static final String KEY_PREFIX = "httpClient.okHttpClient.";

    private static final String KEY_CONNECT_TIMEOUT = KEY_PREFIX + "connectTimeout";
    private static final int DEFAULT_CONNECT_TIMEOUT = 20000;

    private static final String KEY_READ_TIMEOUT = KEY_PREFIX + "readTimeout";
    private static final int DEFAULT_READ_TIMEOUT = 20000;

    private static final String KEY_WRITE_TIMEOUT = KEY_PREFIX + "writeTimeout";
    private static final int DEFAULT_WRITE_TIMEOUT = 10000;

    private static final String KEY_MAX_CONNECTION_POOL = KEY_PREFIX + "maxConnectionPool";
    private static final int DEFAULT_MAX_CONNECTION_POOL = 500;


    private static final String KEY_KEEP_ALIVE_DURATION = KEY_PREFIX + "keepAliveDuration";
    private static final int DEFAULT_KEEP_ALIVE_DURATION = 1;

    /**
* OKHTTP Base Layer socket Chain connect Timeout Time
*/
    @Value("${" + KEY_CONNECT_TIMEOUT + ":" + DEFAULT_CONNECT_TIMEOUT + "}")
    private Integer connectTimeout;

    /**
* OKHTTP Base Layer socket Read Timeout Time
*/
    @Value("${" + KEY_READ_TIMEOUT + ":" + DEFAULT_READ_TIMEOUT + "}")
    private Integer readTimeout;

    /**
* OKHTTP Base Layer socket Write Timeout Time
*/
    @Value("${" + KEY_WRITE_TIMEOUT + ":" + DEFAULT_WRITE_TIMEOUT + "}")
    private Integer writeTimeout;

    /**
* Connection Pool Connection Number
*/
    @Value("${" + KEY_MAX_CONNECTION_POOL + ":" + DEFAULT_MAX_CONNECTION_POOL + "}")
    private Integer maxConnections;

    /**
* Connection Pool Connection Store Work Time
*/
    @Value("${" + KEY_KEEP_ALIVE_DURATION + ":" + DEFAULT_KEEP_ALIVE_DURATION + "}")
    private Integer keepAliveDuration;

    @Bean
    @Lazy
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public OkHttpClient okHttpClient() {
        return this.getInstance(new HttpClientConfigInfo());
    }

    /**
* Get new Instance
*/
    public OkHttpClient newInstance(HttpClientConfigInfo configInfo) {
        return this.getInstance(configInfo);
    }

    /*class inner Aux assist Method*/

    private OkHttpClient getInstance(HttpClientConfigInfo configInfo) {
        if (configInfo == null) {
            return null;
        }

        Integer connectTimeout = configInfo.getConnectTimeout();
        if (connectTimeout == null) {
            connectTimeout = this.connectTimeout;
        }

        Integer readTimeout = configInfo.getReadTimeout();
        if (readTimeout == null) {
            readTimeout = this.readTimeout;
        }

        Integer writeTimeout = configInfo.getWriteTimeout();
        if (writeTimeout == null) {
            writeTimeout = this.writeTimeout;
        }

        Integer maxConnections = configInfo.getMaxConnections();
        if (maxConnections == null) {
            maxConnections = this.maxConnections;
        }
        TrustManager[] trustAllCerts = buildTrustManagers();
        SSLSocketFactory sslSocketFactory=null;
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new OkHttpClient().newBuilder()
                .sslSocketFactory(sslSocketFactory,(X509TrustManager) trustAllCerts[0])
                .hostnameVerifier(new AllowAllHostnameVerifier())
                .connectionPool(new ConnectionPool(maxConnections, keepAliveDuration, TimeUnit.MINUTES))
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .build();
    }

    /*getters*/

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public Integer getWriteTimeout() {
        return writeTimeout;
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }
}

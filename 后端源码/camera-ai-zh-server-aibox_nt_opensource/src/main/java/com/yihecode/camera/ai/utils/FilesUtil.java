package com.yihecode.camera.ai.utils;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.yihecode.camera.ai.enums.FileReturnFlagEnum;
import com.yihecode.camera.ai.exception.FileDecodeException;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component
public class FilesUtil {
    private static final Logger log = LoggerFactory.getLogger(FilesUtil.class);
    private static FilesUtil fileUtils;
    @Value("${file.isDecode:true}")
    private Boolean isDecode;

    public FilesUtil() {
    }

    @PostConstruct
    public void init() {
        fileUtils = this;
        fileUtils.isDecode = this.isDecode;
    }

    public static File decode(MultipartFile file) throws Exception {
        try {
            File inFile = File.createTempFile("in_tmp_", file.getOriginalFilename());
            file.transferTo(inFile);
            return !fileUtils.isDecode ? inFile : decodeFile(inFile);
        } catch (Throwable var2) {
            throw var2;
        }
    }

    public static InputStream decodeStream(MultipartFile file) throws Exception {
        try {
            return Files.newInputStream(decode(file).toPath());
        } catch (Throwable var2) {
            throw var2;
        }
    }

    private static File decodeFile(File file) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyManagementException {
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
            sslcontext.init((KeyManager[])null, new TrustManager[]{new MeX509TrustManager()}, new SecureRandom());
            HttpRequest request = HttpUtil.createRequest(Method.POST, "http://172.24.64.118/interface/api-rs/uni").setSSLSocketFactory(sslcontext.getSocketFactory()).setFollowRedirects(true).setHostnameVerifier((s, sslSession) -> {
                return true;
            }).timeout(-1).body(Files.readAllBytes(file.toPath())).contentType("application/x-www-form-urlencoded");
            request.header("method~name", "fileDecryptionRest");
            request.header("data~fileOffset", "0");
            request.header("data~counSize", String.valueOf(file.length()));
            log.info("Start send Start Decrypt Request");
            HttpResponse httpResponse = request.execute();
            String result = httpResponse.header("data~returnFlag");
            log.info("Decrypt Complete Complete, Decrypt Result Status for {}", result);
            if (!"0".equals(result) && !"2".equals(result)) {
                if (file.exists()) {
                    file.delete();
                }

                throw new FileDecodeException(String.format("File Decrypt Failed, Decrypt Result Status:%s, Description:[%s]", result, FileReturnFlagEnum.getNameByCode(result)));
            } else if (ObjectUtil.isNotEmpty(httpResponse.bodyBytes())) {
                File toFile = File.createTempFile("out_tmp_", file.getName());
                FileUtil.writeBytes(httpResponse.bodyBytes(), toFile);
                log.info("Decrypt Success, Write File Address: {}", toFile.toPath());
                if (file.exists()) {
                    file.delete();
                }

                return toFile;
            } else {
                return file;
            }
        } catch (Throwable var6) {
            throw var6;
        }
    }

    public static class MeX509TrustManager implements X509TrustManager {
        public MeX509TrustManager() {
        }

        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}

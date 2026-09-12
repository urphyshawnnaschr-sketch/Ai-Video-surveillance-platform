package com.yihecode.camera.ai.utils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FtpUtils {

    private FTPClient ftpClient;

    /**
* Instance Change
*
* @param hostName FTP Server Address
* @param port FTP Server Port
* @param userName FTP Login Account account
* @param password FTP Login Password
* @throws IOException
*/
    public FtpUtils(String hostName, int port, String userName, String password) throws IOException {
        ftpClient = new FTPClient();
        //Set transmit input Command Timeout
ftpClient.setDefaultTimeout(60000);// ms
// Set Two Service Connection timeout Time
ftpClient.setConnectTimeout(60000);// ms
// by Dynamic Mode down Set Data transmit input Timeout Time
ftpClient.setDataTimeout(60000);// ms
// Connection FTP
ftpClient.connect(hostName, port);
// more Add Account account Password Login Service
ftpClient.login(userName, password);
// by Dynamic Mode (Need Set In Connection After, Especially its linux Environment)
ftpClient.enterLocalPassiveMode();
//ftpClient.enterLocalActiveMode();
ftpClient.setControlEncoding("UTF-8");
}


public String[] getFileNameList(String ftpDirectory)
{
String[] names = {};
try {
names = ftpClient.listNames(ftpDirectory);
} catch (IOException e) {
e.printStackTrace();
}

return names;
}
/**
* FTP Upload File
*
* @param remoteUploadDirectory need Upload Directory (FTP Server up Directory)
* @param localUploadFilePathName Local Upload File Complete whole Path (Local Path)
* @return
*/
public Pair<Boolean, String> uploadFile(String remoteUploadDirectory, String localUploadFilePathName) {
FileInputStream fis = null;
try {
// like Result not can in in dir down, Note this Directory does not exist!
System.out.println("FTP Response code:"+ftpClient.getReplyCode());
System.out.println("FTP Response Info:"+ftpClient.getReplyString());
if (!ftpClient.changeWorkingDirectory(remoteUploadDirectory)) {
log.info("not has Directory:{}", remoteUploadDirectory);
if (!ftpClient.makeDirectory(remoteUploadDirectory)) {
log.info("Create File Directory [{}] Failed!", remoteUploadDirectory);
return Pair.of(false,"Create File Directory ["+ remoteUploadDirectory +"] Failed");
}
}
// in in File Directory
ftpClient.changeWorkingDirectory(remoteUploadDirectory);
// Create File Stream
fis = new FileInputStream(new File(localUploadFilePathName));
// Set Upload Directory
ftpClient.setBufferSize(1024);
// Set File Type (Two in make)
ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//FTP Server up most end Name
String uploadFileName = localUploadFilePathName.substring(localUploadFilePathName.lastIndexOf(File.separator) + 1);
// File Upload
boolean b = ftpClient.storeFile(uploadFileName, fis);
int replyCode = ftpClient.getReplyCode();
log.info("Upload File Response code:{}", replyCode);
log.info("Upload File Response Info:{}", ftpClient.getReplyString());
return Pair.of(b, b?"Upload success":"Upload failed");
} catch (Exception e) {
log.error("FTP Upload File Exception!:", e);
return Pair.of(false,"Upload File Exception");
} finally {
try {
if (fis!= null) {
fis.close();
}
} catch (IOException e) {
log.error("Close Stream send produce Exception!", e);
}
}
}

/**
* FTP File Download
*
* @param remoteDownloadDirectory need Download Directory (FTP Server Directory)
* @param localDirectory Local Download File Path
* @param downloadFileName Download File Name
* @return
*/
public Pair<Boolean, String> downloadFile(String remoteDownloadDirectory, String localDirectory, String downloadFileName) {
OutputStream out = null;
try {
if (StringUtils.isBlank(downloadFileName)) {
return Pair.of(false,"need Download File cannot be empty");
}
// work Make Directory Cut change to Download File Directory down
if (!ftpClient.changeWorkingDirectory(remoteDownloadDirectory)) {
log.info("Directory does not exist:{}", remoteDownloadDirectory);
return Pair.of(false,"Directory does not exist");
}
// Get Directory down All File
FTPFile[] files = ftpClient.listFiles();
if (files.length < 1) {
return Pair.of(false,"Directory Is Empty");
}
boolean fileExist = false;
boolean downloadFlag = false;
// Iterate File List
for (FTPFile ftpFile: files) {
String localFile = localDirectory + File.separator + downloadFileName;
// Whether Exist need Download File
if (downloadFileName.equals(ftpFile.getName())) {
fileExist = true;
out = new FileOutputStream(localFile);
// Download
ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
downloadFlag = ftpClient.retrieveFile(downloadFileName, out);
int replyCode = ftpClient.getReplyCode();
log.info("Download File Response code:{}", replyCode);
break;
}
}
if (!fileExist) {
return Pair.of(false,"FTP Server up File does not exist");
}
return Pair.of(downloadFlag, downloadFlag?"Download success":"Download failed");
} catch (Exception e) {
log.error("FTP Download File Exception!:", e);
return Pair.of(false,"Download File Exception");
} finally {
try {
if (out!= null) {
out.flush();
out.close();
}
} catch (IOException e) {
e.printStackTrace();
}
}
}

public void close() {
try {
if (ftpClient!= null && ftpClient.isConnected()) {
ftpClient.disconnect();
}
} catch (IOException e) {
e.printStackTrace();
}
}
/**
* Download File
*/
public static void download(String path, HttpServletResponse response) {
if (path == null) {
return;
}
FileInputStream fis = null;
ServletOutputStream out = null;
try {
File file = new File(path);
response.addHeader("Content-Length",""+ file.length());
fis = new FileInputStream(file);
out = response.getOutputStream();
IOUtils.copy(fis, out);
response.flushBuffer();
} catch (IOException e) {
e.printStackTrace();
} finally {
if (fis!= null) {
try {
fis.close();
} catch (IOException e) {
e.printStackTrace();
}
}
// this Place Record Get Close input out Servlet Stream
IoUtil.close(out);
}
}

public static void download(String path, String downloadName, HttpServletResponse response, HttpServletRequest request) {
// Request Type, Request Param, Request Header By Need Request make Fixed Immediate can
// Get to Need Download File
// like Any Generate File, By real International Business Need Request modify build Immediate can
log.debug("Pending Download File:{}", path);
// Download File
// Set Response Content Type, let Browser Knows Download is One File
ServletContext context = request.getServletContext();
// get MIME type of the file
String mimeType = context.getMimeType(path);
if (mimeType == null) {
// set to binary type if MIME mapping not found
mimeType ="application/octet-stream";
log.debug("context getMimeType is null");
}
log.debug("MIME type:"+ mimeType);
// Set Response Header Info, Tell Browser File Name and long Degree
// set content attributes for the response
response.setContentType(mimeType);
response.setContentLength((int) new File(path).length());
// Set Code
response.setCharacterEncoding("utf-8");
// Set Request Header Param with and Download File Name Name, in Text Name turn Prevent Messy code
String headerValue = String.format("attachment; filename=%s",
UriUtils.encode(downloadName, StandardCharsets.UTF_8));
response.setHeader(HttpHeaders.CONTENT_DISPOSITION, headerValue);
// Copy the stream to the response's output stream.
try {
InputStream myStream = new FileInputStream(path);
org.apache.commons.compress.utils.IOUtils.copy(myStream, response.getOutputStream());
response.flushBuffer();
myStream.close();
} catch (IOException e) {
e.printStackTrace();
}

}
}

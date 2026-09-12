package com.yihecode.camera.ai.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import com.yihecode.camera.ai.web.dto.OssFileDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OssUtils {
    private static final String ENDPOINT = "https://oss-cn-beijing.aliyuncs.com";
    private static final String ACCESS_KEY_ID = System.getenv("OSS_ACCESS_KEY_ID");
    private static final String ACCESS_KEY_SECRET = System.getenv("OSS_ACCESS_KEY_SECRET");
    private static final String BUCKET_NAME = "yihecode-models";

    private OSS ossClient;

    public OssUtils() {
        ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    //Upload File
public void uploadFile(String key, InputStream inputStream) {
PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, inputStream);
ossClient.putObject(putObjectRequest);
}

// Delete File
public void deleteFile(String key) {
ossClient.deleteObject(BUCKET_NAME, key);
}

// Close OSS Client Connection
public void shutdown() {
if (ossClient!= null) {
ossClient.shutdown();
}
}

// Download File to Local
public void download(String objectName, String pathName) {
try {
// Download Object to Local File, and Save to Refer Fixed Local Path in. like Result Refer Fixed Local File Exist will Override, does not exist rule new build.
// like Result not Refer Fixed Local Path, rule Download after File Default Save to Example Process order The belong Project Corresponding Local Path in.
ossClient.getObject(new GetObjectRequest(BUCKET_NAME, objectName), new File(pathName));
} catch (OSSException oe) {
System.out.println("Caught an OSSException, which means your request made it to OSS,"
+"but was rejected with an error response for some reason.");
System.out.println("Error Message:"+ oe.getErrorMessage());
System.out.println("Error Code:"+ oe.getErrorCode());
System.out.println("Request ID:"+ oe.getRequestId());
System.out.println("Host ID:"+ oe.getHostId());
} catch (ClientException ce) {
System.out.println("Caught an ClientException, which means the client encountered"
+"a serious internal problem while trying to communicate with OSS,"
+"such as not being able to access the network.");
System.out.println("Error Message:"+ ce.getMessage());
} finally {
shutdown();
}
}

// File List
public ObjectListing fileList(String path) {
try {
// structure build ListObjectsRequest Request.
ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
// Set prefix Incoming check find Special Fixed front Concat File.
listObjectsRequest.setPrefix(path);
// Set delimiter Incoming part Separate File Clip.
listObjectsRequest.setDelimiter("/");

// Column out File.
ObjectListing objectListing;
do {
objectListing = ossClient.listObjects(listObjectsRequest);
// Column out All File.
//List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
//for (OSSObjectSummary objectSummary: objectSummaries) {
// System.out.println("-"+ objectSummary.getKey());
//}

// Column out All File Clip.
//List<String> commonPrefixes = objectListing.getCommonPrefixes();
//for (String commonPrefix: commonPrefixes) {
// System.out.println("-"+ commonPrefix);
//}

// like Result Column out File Count Reach to One page most big Number (Default for 100), can with Set listObjectsRequest marker in Line down One page Column out.
listObjectsRequest.setMarker(objectListing.getNextMarker());
} while (objectListing.isTruncated());
return objectListing;
} catch (Exception e) {
e.printStackTrace();
}
return null;
}

public Long getFileLength(String path) {
ObjectListing objectListing = fileList(path);
if (objectListing.getObjectSummaries().size() > 0) {
OSSObjectSummary summary = objectListing.getObjectSummaries().get(0);
return summary.getSize();
} else {
return 0L;
}

}

// File Whether Exist
public boolean hasFile(String objectName) {
try {
// Determine File Whether Exist. like Result Back Value for true, rule File Exist, No rule Storage empty between or File does not exist.
// Set Whether in Line re Fixed to or Mirror return source. Default Value for true, table show Ignore 302 re Fixed to and Mirror return source; like Result Set isINoss for false, rule in Line 302 re Fixed to or Mirror return source.
//boolean isINoss = true;
boolean found = ossClient.doesObjectExist(BUCKET_NAME, objectName);
//boolean found = ossClient.doesObjectExist(bucketName, objectName, isINoss);
System.out.println(found);
return found;
} catch (OSSException oe) {
System.out.println("Caught an OSSException, which means your request made it to OSS,"
+"but was rejected with an error response for some reason.");
System.out.println("Error Message:"+ oe.getErrorMessage());
System.out.println("Error Code:"+ oe.getErrorCode());
System.out.println("Request ID:"+ oe.getRequestId());
System.out.println("Host ID:"+ oe.getHostId());
} catch (ClientException ce) {
System.out.println("Caught an ClientException, which means the client encountered"
+"a serious internal problem while trying to communicate with OSS,"
+"such as not being able to access the network.");
System.out.println("Error Message:"+ ce.getMessage());
}
return false;
}


public static void main2(String[] args) {
// OssUtils ossUtils = new OssUtils();
// OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
// ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
// listObjectsRequest.setPrefix("lynxi/");
// listObjectsRequest.setDelimiter("/");
//
//
// ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
// log.info("{}", objectListing.getCommonPrefixes());
//// List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
//// for (OSSObjectSummary s: sums) {
//// log.info("{}", s.getOwner());
////}
//// log.info("{}", objectListing);

System.out.println(OssUtils.getOssNames("chaoxing"));
}

/**
* By Algorithm Code Get Algorithm Name List
* @param platform
* @return
*/
public static List<String> getOssNames(String platform) {
List<String> names = new ArrayList<>();
OSS ossClient = null;
try {
ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
listObjectsRequest.setPrefix(platform +"/");
listObjectsRequest.setDelimiter("/");
ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
List<String> commonPrefixes = objectListing.getCommonPrefixes();
if(commonPrefixes == null) {
return names;
}

for(String commonPrefixe: commonPrefixes) {
String[] parts = commonPrefixe.split("/");
if(parts.length!= 2) {
continue;
}
names.add(parts[1]);
}
} catch (Exception e) {
//
} finally {
if(ossClient!= null) {
ossClient.shutdown();
}
}
return names;
}

/**
* By Algorithm Code Get Algorithm Name List
* @param platform
* @return
*/
public static Map<String, List<String>> getOssFiles(String platform, boolean ossNet) {
Map<String, List<String>> dataMap = new HashMap<>();
List<String> names = new ArrayList<>();
List<String> files = new ArrayList<>();

//
if(!ossNet) {
dataMap.put("names", names);
dataMap.put("files", files);
return dataMap;
}

//
if(StrUtil.isBlank(platform)) {
dataMap.put("names", names);
dataMap.put("files", files);
return dataMap;
}
//
OSS ossClient = null;
try {
ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
listObjectsRequest.setPrefix(platform +"/");
//listObjectsRequest.setDelimiter("/");
ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
List<OSSObjectSummary> summaries = objectListing.getObjectSummaries();
if(summaries!= null) {
for(OSSObjectSummary summary: summaries) {
String key = summary.getKey();
String[] parts = key.split("/");
if(key.toLowerCase().endsWith("zip")) {// Algorithm File Package
files.add(parts[parts.length - 1]);
names.add(parts[parts.length - 2]);
}
}
}
} catch (Exception e) {
//
} finally {
if(ossClient!= null) {
ossClient.shutdown();
}
}
dataMap.put("names", names);
dataMap.put("files", files);
return dataMap;
}

/**
* By Platform Type and Algorithm Code Query Algorithm Package
* @param platform
* @param nameEn
* @return
*/
public static List<OssFileDTO> getOssFiles(String platform, String nameEn, boolean ossNet) {
List<OssFileDTO> ossFileDTOS = new ArrayList<>();

// merchant City Offline
if(!ossNet) {
return ossFileDTOS;
}

//
if(StrUtil.isBlank(platform) || StrUtil.isBlank(nameEn)) {
return ossFileDTOS;
}
//
OSS ossClient = null;
try {
ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
listObjectsRequest.setPrefix(platform +"/"+ nameEn +"/");
//listObjectsRequest.setDelimiter("/");
ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
List<OSSObjectSummary> summaries = objectListing.getObjectSummaries();
if(summaries!= null) {
for(OSSObjectSummary summary: summaries) {
String key = summary.getKey();
String mainName = FileUtil.mainName(key);
String extName = FileUtil.extName(key);
if("zip".equalsIgnoreCase(extName)) {
if(StrUtil.isNotBlank(mainName)) {
String filename = FileUtil.getName(key);
String[] parts = mainName.split("-");
if(parts.length!= 3) {
continue;
}
String version = parts[2];
try {
double ver = Double.parseDouble(version);
OssFileDTO ossFileDTO = new OssFileDTO();
ossFileDTO.setFilename(filename);
ossFileDTO.setFilesize(summary.getSize());
ossFileDTO.setVersion(version);
ossFileDTO.setVer(ver);
ossFileDTOS.add(ossFileDTO);
} catch (Exception e) {
//
}
}
}
}
}
} catch (ClientException e) {
return null; // Back empty Value table show Network not via
} finally {
if(ossClient!= null) {
ossClient.shutdown();
}
}
return ossFileDTOS;
}

/**
* Get Refer Fixed File
* @param platform
* @param nameEn
* @param fileName
* @return
*/
public static OssFileDTO getOssFile(String platform, String nameEn, String fileName) {
//
OSS ossClient = null;
try {
ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
listObjectsRequest.setPrefix(platform +"/"+ nameEn +"/");
//listObjectsRequest.setDelimiter("/");
ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
List<OSSObjectSummary> summaries = objectListing.getObjectSummaries();
if(summaries!= null) {
for(OSSObjectSummary summary: summaries) {
String key = summary.getKey();
String mainName = FileUtil.mainName(key);
String extName = FileUtil.extName(key);
if("zip".equalsIgnoreCase(extName)) {
if(StrUtil.isNotBlank(mainName)) {
String filename = FileUtil.getName(key);
if(!filename.equals(fileName)) {
continue;
}
String[] parts = mainName.split("-");
if(parts.length!= 3) {
continue;
}
String version = parts[2];
try {
double ver = Double.parseDouble(version);
OssFileDTO ossFileDTO = new OssFileDTO();
ossFileDTO.setFilename(filename);
ossFileDTO.setFilesize(summary.getSize());
ossFileDTO.setVersion(version);
ossFileDTO.setVer(ver);
return ossFileDTO;
} catch (Exception e) {
//
}
}
}
}
}
} catch (ClientException e) {
return null; // Back empty Value table show Network not via
} finally {
if(ossClient!= null) {
ossClient.shutdown();
}
}
return null;
}

/**
* Get Refer Fixed File Size
* @param platform
* @param nameEn
* @param fileName
* @return
*/
public static long getOssFileLength(String platform, String nameEn, String fileName) {
OSS ossClient = null;
try {
ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET_NAME);
listObjectsRequest.setPrefix(platform +"/"+ nameEn +"/");
//listObjectsRequest.setDelimiter("/");
ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
List<OSSObjectSummary> summaries = objectListing.getObjectSummaries();
if(summaries!= null) {
for(OSSObjectSummary summary: summaries) {
String key = summary.getKey();
String mainName = FileUtil.mainName(key);
String extName = FileUtil.extName(key);
if("zip".equalsIgnoreCase(extName)) {
if(StrUtil.isNotBlank(mainName)) {
String filename = FileUtil.getName(key);
if(filename.equals(fileName)) {
return summary.getSize();
}
}
}
}
}
} catch (ClientException e) {
return 0; // Back empty Value table show Network not via
} finally {
if(ossClient!= null) {
ossClient.shutdown();
}
}
return 0;
}

/**
* Download Algorithm Package
* @param objectName
* @param pathName
*/
public static String downloadOssFile(String objectName, String pathName) {
OSS ossClient = null;
try {
// Download Object to Local File, and Save to Refer Fixed Local Path in. like Result Refer Fixed Local File Exist will Override, does not exist rule new build.
ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
ossClient.getObject(new GetObjectRequest(BUCKET_NAME, objectName), new File(pathName));
return"";
} catch (OSSException oe) {
return"OSS Download Error, Please Retry Later";
} catch (ClientException ce) {
return"Network Exception, Please Retry Later";
} finally {
if(ossClient!= null) {
ossClient.shutdown();
}
}
}
}

package com.yihecode.camera.ai.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* File Utils
*/
@Slf4j
public class FileUtils {

    /**
* Column out All File
* @param path Directory Complete whole Path
* @return
*/
    public static List<String> listFiles(String path) {
        List<String> filePaths = new ArrayList<>();
        if(StrUtil.isBlank(path)) {
            return filePaths;
        }
        if(!FileUtil.exist(path) || !FileUtil.isDirectory(path)) {
            return filePaths;
        }
        //
File[] files = new File(path).listFiles();
if(files == null) {
return filePaths;
}
//
for(File file: files) {
filePaths.add(file.getAbsolutePath());
}
return filePaths;
}

/**
* Column out Refer Fixed Format File
* @param path Directory Complete whole Path
* @param endWiths result Tail Type, i.e jpg,jpeg...
* @return
*/
public static List<String> listFiles(String path, List<String> endWiths) {
List<String> filePaths = new ArrayList<>();
if(StrUtil.isBlank(path) || endWiths == null || endWiths.isEmpty()) {
return filePaths;
}
if(!FileUtil.exist(path) &&!FileUtil.isDirectory(path)) {
return filePaths;
}
//
File[] files = new File(path).listFiles();
if(files == null) {
return filePaths;
}
//
List<String> endWithsLow = endWiths.stream().map(String::toLowerCase).collect(Collectors.toList());
for(File file: files) {
String extName = FileUtil.extName(file);
if(!StrUtil.isBlank(extName) && endWithsLow.contains(extName.toLowerCase())) {
filePaths.add(file.getAbsolutePath());
}
}
return filePaths;
}

/**
* Column out Contain Refer Fixed Name File
* @param path Directory Complete whole Path
* @param conatinString Contain String
* @return
*/
public static List<String> listFiles(String path, String conatinString) {
List<String> filePaths = new ArrayList<>();
if(StrUtil.isBlank(path) || StrUtil.isBlank(conatinString)) {
return filePaths;
}
if(!FileUtil.exist(path) &&!FileUtil.isDirectory(path)) {
return filePaths;
}
//
File[] files = new File(path).listFiles();
if(files == null) {
return filePaths;
}
//
for(File file: files) {
String mainName = FileUtil.mainName(file);
if(StrUtil.isNotBlank(mainName) && mainName.contains(conatinString)) {
filePaths.add(file.getAbsolutePath());
}
}
return filePaths;
}

/**
* Column out Contain Refer Fixed Name and Refer Fixed Format File
* @param path Directory Complete whole Path
* @param containString Contain String
* @param endWiths result Tail Type, i.e jpg,jpeg...
* @return
*/
public static List<String> listFiles(String path, String containString, List<String> endWiths) {
List<String> filePaths = new ArrayList<>();
if(StrUtil.isBlank(path) || StrUtil.isBlank(containString) || endWiths == null || endWiths.isEmpty()) {
return filePaths;
}
if(!FileUtil.exist(path) &&!FileUtil.isDirectory(path)) {
return filePaths;
}
//
File[] files = new File(path).listFiles();
if(files == null) {
return filePaths;
}
//
List<String> endWithsLow = endWiths.stream().map(String::toLowerCase).collect(Collectors.toList());
for(File file: files) {
String mainName = FileUtil.mainName(file);
String extName = FileUtil.extName(file);
if(StrUtil.isNotBlank(mainName) && StrUtil.isNotBlank(extName) && mainName.contains(containString) && endWithsLow.contains(extName.toLowerCase())) {
filePaths.add(file.getAbsolutePath());
}
}
return filePaths;
}

/**
* Parse Algorithm Package Name, Get Version No
* @param filepath Algorithm File Package Complete whole Path
* @return
*/
public static String getVersion(String filepath) {
if(StrUtil.isBlank(filepath)) {
return null;
}

String mainName = FileUtil.mainName(filepath);
if(StrUtil.isBlank(mainName)) {
return null;
}
String[] parts = mainName.split("-");
if(parts.length!= 3) {
return null;
}
//
try {
Double.parseDouble(parts[2]);
return parts[2];
} catch (Exception e) {
return null;
}
}

/**
* File Path Convert Complete
* @param path
* @return
*/
public static String pathTo(String path) {
if(StrUtil.isBlank(path)) {
return"";
}
// windows
if(FileUtil.isWindows()) {
path = path.replaceAll("\\\\","/");
path = path.replaceAll("//","/");
return path;
}
// linux
return path.replaceAll("//","/");
}

/**
* File Size Convert Complete Text Char
* @param bytes
* @return
*/
public static String convertBytes(long bytes) {
String result ="";
int unit = 1024;
if (bytes < unit)
return bytes +"B";
int exp = (int) (Math.log(bytes) / Math.log(unit));
String pre = String.valueOf("KMGTPE".charAt(exp - 1));
result += String.format("%.2f", bytes / Math.pow(unit, exp)) + pre +"B";
return result;
}
}

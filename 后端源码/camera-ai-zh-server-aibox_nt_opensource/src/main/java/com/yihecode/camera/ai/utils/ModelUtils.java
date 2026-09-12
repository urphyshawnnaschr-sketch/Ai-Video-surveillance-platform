package com.yihecode.camera.ai.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
* Algorithm Model Utils
*/
public class ModelUtils {

    private static final String MODEL_DIR = "/data/models/"; //"e:/models/"; //

/**
* Type Package
* @param model
*/
public static void zip(String model) {
File tar = new File(MODEL_DIR + model);
if(!tar.exists()) {
return;
}
// Column out All File
File[] files = tar.listFiles();
if(files == null) {
return;
}
// find out All Model Phase close File
List<File> tarfiles = new ArrayList<>();
String md5 = null;
for(File file: files) {
//
if(file.isDirectory()) {
continue;
}
//
String fileName = file.getName();
String extName = FileUtil.extName(file);
// zip File, or mp4, Delete
if(StrUtil.isBlank(extName) ||"zip".equals(extName.toLowerCase()) ||"mp4".equals(extName.toLowerCase())) {
continue;
}
//
if("md5.txt".equals(fileName)) {
md5 = FileUtil.readString(file,"utf-8");
}
tarfiles.add(file);
}
// find not to md5 Value
if(StrUtil.isBlank(md5)) {
return;
}
// zip File already exists, Skip
File zip = new File(MODEL_DIR + model + File.separator + md5 +".zip");
if(zip.exists()) {
return;
}
// Create Directory
File ziptar = FileUtil.mkdir(MODEL_DIR + model + File.separator + md5 + File.separator);
if(ziptar == null) {
return;
}
// Copy File to Directory
for(File file: tarfiles) {
FileUtil.copy(file, ziptar, true);
}
// Type Package
ZipUtil.zip(ziptar);
// Delete Directory
FileUtil.del(ziptar);
}

/**
* Type Package All Model
*/
public static void zipAll() {
File tar = new File(MODEL_DIR);
if(!tar.exists()) {
return;
}
//
File[] modeltars = tar.listFiles();
if(modeltars == null) {
return;
}
//
for(File modeltar: modeltars) {
if(modeltar.isDirectory()) {
String model = modeltar.getName();
zip(model);
}
}
}
}

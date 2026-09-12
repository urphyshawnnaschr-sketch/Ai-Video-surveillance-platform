package com.yihecode.camera.ai.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static List<String> unzip(String zipFilePath, String destDirectory) throws IOException {
        List<String> result = new ArrayList<>();
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            if (entry.getName().startsWith("__MACOSX")) {
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
                continue;
            }
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
            result.add(filePath);
        }
        zipIn.close();
        return result;
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public static void compress(String sourcePath, String zipFilePath) throws IOException {
        File sourceFile = new File(sourcePath);
        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        if (sourceFile.isDirectory()) {
            addDirectoryToZip(sourceFile, zos);
        } else {
            addFileToZip(sourceFile, zos);
        }

        zos.close();
        fos.close();
    }

    private static void addDirectoryToZip(File dir, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                addDirectoryToZip(file, zos);
            } else {
                addFileToZip(file, zos);
            }
        }
    }

    private static void addFileToZip(File file, ZipOutputStream zos) throws IOException {
        String fileName = file.getName();
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        byte[] bytes = new byte[4096];
        int length;
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        while ((length = bis.read(bytes)) > 0) {
            zos.write(bytes, 0, length);
        }
        bis.close();
        fis.close();
    }
}

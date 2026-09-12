package com.yihecode.camera.ai.utils;

import java.io.*;
import java.security.MessageDigest;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
*MD5 Calculate work Tool
*/
public class Md5FileUtils {
    public static  String SPLIT_PATH = "/temp/split/";

    /**
* Get One File md5 Value (can Process big File)
* @return md5 value
*/
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
* Request One String md5 Value
* @param target String
* @return md5 value
*/
    public static String MD5(String target) {
        return DigestUtils.md5Hex(target);
    }
    /**
* File Split Method
*/
    public static void getSplitFile(File file, String basePath) {

        long size = 1 * 1024 * 1024; //File Split copy Number
RandomAccessFile raf = null;

try {
// Get Target File pre Assign File The Occupy empty between In Disk in Create One Refer Fixed big small File r is only read
raf = new RandomAccessFile(file,"r");
long length = raf.length();// File total long Degree
long count = length / size;// File Cut Piece after long Degree

long yu = length % size;
if (yu!= 0) {
++count;
}

long offSet = 0L;// Init bias move Quantity
for (int i = 0; i < count - 1; i++) {// most after One Piece form Single Process
long begin = offSet;
long end = (i + 1) * size;
// offSet = writeFile(file, begin, end, i);
offSet = getWrite(file, i, begin, end, basePath);
}
if (length - offSet > 0) {
getWrite(file, (int) count - 1, offSet, length, basePath);
}

} catch (FileNotFoundException e) {
System.out.println("not has find to File");
e.printStackTrace();
} catch (IOException e) {
e.printStackTrace();
} finally {
try {
raf.close();
} catch (IOException e) {
e.printStackTrace();
}
}
}

/**
* Refer Fixed File Each One copy Boundary, Write not same File in
*
* @param file source File
* @param index source File Smooth order ID
* @param begin Start Refer For Bit set
* @param end End Refer For Bit set
* @return long
*/
public static long getWrite(File file, int index, long begin, long end, String basePath) {
String name = StringUtils.substringBeforeLast(file.getName(),".");
long endPointer = 0L;
try {
// Apply Bright File Cut Cut after File Disk
RandomAccessFile in = new RandomAccessFile(file,"r");
String targetPath = basePath + SPLIT_PATH;
if (!FileUtil.exist(new File(targetPath))) {
FileUtil.mkdir(targetPath);
}
// Fixed Meaning One can read, can write File and and after Concat Name for.tmp Two in make File
RandomAccessFile out = new RandomAccessFile(new File(targetPath + file.getName() +"_"+ index +".tmp"),"rw");

// Apply Bright Tool Body Each One File Byte Number group
byte[] b = new byte[1024];
int n = 0;
// from Refer Fixed Bit set Read File Byte Stream
in.seek(begin);
// Determine File Stream Read Boundary
while (in.getFilePointer() <= end && (n = in.read(b))!= -1) {
// from Refer Fixed Each One copy File Range, Write not same File
out.write(b, 0, n);
}

// Fixed Meaning Current Read File Refer For
endPointer = in.getFilePointer();

// Close input in Stream
in.close();
// Close input out Stream
out.close();
} catch (Exception e) {
e.printStackTrace();
}

return endPointer;
}
}

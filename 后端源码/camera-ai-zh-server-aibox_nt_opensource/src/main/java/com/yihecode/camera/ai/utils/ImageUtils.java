package com.yihecode.camera.ai.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageUtils {



    public static BufferedImage compress2Buffer (String input, double scale) throws IOException {
        BufferedImage image = ImageIO.read(new File(input));
        int width = image.getWidth();
        int height = image.getHeight();
        //new Width and high Degree
int newWidth = (int)(width * scale);
int newHeight = (int)(height * scale);
// Create new Zoom Put after Image
BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
Graphics2D g2d = newImage.createGraphics();
g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
g2d.dispose();
return newImage;
}

public static BufferedImage compress2Buffer (File inputFile, double scale) throws IOException {
BufferedImage image = ImageIO.read(inputFile);
int width = image.getWidth();
int height = image.getHeight();
// new Width and high Degree
int newWidth = (int)(width * scale);
int newHeight = (int)(height * scale);
// Create new Zoom Put after Image
BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
Graphics2D g2d = newImage.createGraphics();
g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
g2d.dispose();
return newImage;
}

public static byte[] compress2Byte (File inputFile, double scale) throws IOException {
// Read Image File
BufferedImage image = ImageIO.read(inputFile);
int width = image.getWidth();
int height = image.getHeight();
// new Width and high Degree
int newWidth = (int)(width * scale);
int newHeight = (int)(height * scale);
// Create new Zoom Put after Image
BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
Graphics2D g2d = newImage.createGraphics();
g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
g2d.dispose();
// Create Byte Number group input out Stream
ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
// will Image Write Byte Number group input out Stream
ImageIO.write(newImage,"jpg", byteStream);
// Get Byte Number group
return byteStream.toByteArray();
}

public static String compress(String input, String output, double scale) throws IOException {
BufferedImage image = ImageIO.read(new File(input));
int width = image.getWidth();
int height = image.getHeight();
// new Width and high Degree
int newWidth = (int)(width * scale);
int newHeight = (int)(height * scale);
// Create new Zoom Put after Image
BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
// will original Image Zoom Put To new Image
Graphics2D g2d = newImage.createGraphics();
g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
g2d.dispose();
// input out Image
ImageIO.write(newImage,"jpg", new File(output));
return output;
}

//xy Width high
public static String croppedImage (String filePath, String outPath, int x, int y, int width, int height) throws Exception{
BufferedImage originalImage = ImageIO.read(new File(filePath));
String exam = filePath.substring(filePath.lastIndexOf('.') + 1);
// Create new BufferedImage, only Contain Crop Region
BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

// will Crop after Image Write new File
ImageIO.write(croppedImage, exam, new File(outPath));
return outPath;
}

// left up xy right down x2y2
public static String croppedImage2 (String filePath, String outPath, int x, int y, int x2, int y2) throws Exception{
BufferedImage originalImage = ImageIO.read(new File(filePath));
String exam = filePath.substring(filePath.lastIndexOf('.') + 1);
// Create new BufferedImage, only Contain Crop Region
BufferedImage croppedImage = originalImage.getSubimage(x, y, x2-x, y2-y);

// will Crop after Image Write new File
ImageIO.write(croppedImage, exam, new File(outPath));
return outPath;
}

// left Margin x, up Margin y, right Margin x2, down Margin y2
public static String croppedImage3(String filePath, String outPath, int x, int y, int x2, int y2) throws Exception{
BufferedImage originalImage = ImageIO.read(new File(filePath));

String exam = filePath.substring(filePath.lastIndexOf('.') + 1);
// Create new BufferedImage, only Contain Crop Region
BufferedImage croppedImage = originalImage.getSubimage(x, y, originalImage.getWidth() - x - x2, originalImage.getHeight() - y - y2);

// will Crop after Image Write new File
ImageIO.write(croppedImage, exam, new File(outPath));
return outPath;
}

public static String croppedImage2SafeBox(String filePath, String outPath, int x, int y, int x2, int y2) throws Exception{
BufferedImage originalImage = ImageIO.read(new File(filePath));
String exam = filePath.substring(filePath.lastIndexOf('.') + 1);
// Calculate Safe all Box
double proportion = 2; // Safe all Box Zoom Put Times Rate
int width = x2-x;
int height = y2-y;
int safeBoxWidth = (int) (width * proportion);
int safeBoxHeight = (int) (height * proportion);

int safeBoxX = x - (safeBoxWidth - width) / 2;
int safeBoxY = y - (safeBoxHeight - height) / 2;
int safeBoxRight = safeBoxX + safeBoxWidth;
int safeBoxBottom = safeBoxY + safeBoxHeight;
// Ensure Safe all Box In original start Image Boundary inner
safeBoxX = Math.max(0, safeBoxX);
safeBoxY = Math.max(0, safeBoxY);
safeBoxRight = Math.min(originalImage.getWidth(), safeBoxRight);
safeBoxBottom = Math.min(originalImage.getHeight(), safeBoxBottom);

// Create new BufferedImage, only Contain Crop Region
BufferedImage croppedImage = originalImage.getSubimage(safeBoxX, safeBoxY, safeBoxRight - safeBoxX, safeBoxBottom - safeBoxY);

// will Crop after Image Write new File
ImageIO.write(croppedImage, exam, new File(outPath));
return outPath;
}

public static String croppedImage3SafeBox(String filePath, String outPath, int x, int y, int x2, int y2) throws Exception{
BufferedImage originalImage = ImageIO.read(new File(filePath));
String exam = filePath.substring(filePath.lastIndexOf('.') + 1);
// Calculate Safe all Box
double proportion = 2; // Safe all Box Zoom Put Times Rate
int width = originalImage.getWidth() - x - x2;
int height = originalImage.getHeight() - y - y2;
int safeBoxWidth = (int) (width * proportion);
int safeBoxHeight = (int) (height * proportion);

int safeBoxX = x - (safeBoxWidth - width) / 2;
int safeBoxY = y - (safeBoxHeight - height) / 2;
int safeBoxRight = safeBoxX + safeBoxWidth;
int safeBoxBottom = safeBoxY + safeBoxHeight;
// Ensure Safe all Box In original start Image Boundary inner
safeBoxX = Math.max(0, safeBoxX);
safeBoxY = Math.max(0, safeBoxY);
safeBoxRight = Math.min(originalImage.getWidth(), safeBoxRight);
safeBoxBottom = Math.min(originalImage.getHeight(), safeBoxBottom);

// Create new BufferedImage, only Contain Crop Region
BufferedImage croppedImage = originalImage.getSubimage(safeBoxX, safeBoxY, safeBoxRight - safeBoxX, safeBoxBottom - safeBoxY);

// will Crop after Image Write new File
ImageIO.write(croppedImage, exam, new File(outPath));
return outPath;
}
}

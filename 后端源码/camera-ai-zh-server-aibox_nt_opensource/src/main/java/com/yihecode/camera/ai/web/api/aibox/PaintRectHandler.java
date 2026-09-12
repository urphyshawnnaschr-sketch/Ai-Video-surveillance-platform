package com.yihecode.camera.ai.web.api.aibox;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
* Image Draw make Alarm Box
*/
@Slf4j
public class PaintRectHandler {

    //all Bureau Font Instance
public static Font font = null;

// Quiet state Init Block
static {
try (InputStream is = Objects.requireNonNull(PaintRectHandler.class.getResourceAsStream("/fonts/Microsoft YaHei.ttf"))) {
font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 16).deriveFont(Font.BOLD); // Set Default big small for 12pt
} catch (Exception e) {
// throw new RuntimeException("Load Font Failed: /fonts/Microsoft YaHei.ttf", e);
}
}

/**
* Draw make Rectangle Box and Back Draw make Result File Path
* @param filepath
* @param boxsJsonStr
* @return
*/
public String paintRect2File(String filepath, String boxsJsonStr) {
log.error("---------------------------------->>>>>>>>>>>>>>>>>>>>>> Start Draw make");

try {
String mainName = FileUtil.mainName(filepath);
String extName = FileUtil.extName(filepath);
String mainPath = FileUtil.getParent(filepath, 1);
String newFile = FileUtils.pathTo(mainPath +"/"+ mainName +"_draw."+ extName);
if(FileUtil.exist(newFile)) {
return newFile;
}

String copyFile = FileUtils.pathTo(mainPath +"/"+ mainName +"_copy."+ extName);
FileUtil.copyFile(filepath, copyFile, StandardCopyOption.REPLACE_EXISTING);

BufferedImage image = ImgUtil.read(copyFile);

BufferedImage bufferedImage = paintRectAndTitle(image, boxsJsonStr);
if(bufferedImage == null) {
throw new RuntimeException("Draw make Failed");
}
log.info("Draw make Success, Compress front width:{},heigh:{}", bufferedImage.getWidth(), bufferedImage.getHeight());
if(bufferedImage.getWidth() > 1000){
// Compress
bufferedImage = compressImg(bufferedImage);
}
log.info("Compress after width:{},heigh:{}", bufferedImage.getWidth(), bufferedImage.getHeight());
boolean success = ImageIO.write(bufferedImage, extName, new File(newFile));
if(success) {
return newFile;
}
return filepath;
} catch (Exception e) {
log.error("---------------------------------->>>>>>>>>>>>>>>>>>>>>> Draw make Exception", e);
}
return filepath;
}

private static BufferedImage compressImg(BufferedImage bufferedImage) {
int targetWidth = 640;
int targetHeight = 360;
// 2. Get Zoom Put after Image Object
Image scaledImage = bufferedImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

// 3. will Zoom Put after Image Object Convert return BufferedImage
bufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
Graphics2D g2d = bufferedImage.createGraphics();
g2d.drawImage(scaledImage, 0, 0, null);
g2d.dispose();
return bufferedImage;
}

public BufferedImage paintRect2Buffer(String filepath, String boxsJsonStr) {
try {
String mainName = FileUtil.mainName(filepath);
String extName = FileUtil.extName(filepath);
String mainPath = FileUtil.getParent(filepath, 1);
String newFile = FileUtils.pathTo(mainPath +"/"+ mainName +"_draw."+ extName);
if(FileUtil.exist(newFile)) {
return ImgUtil.read(newFile);
}

BufferedImage image = ImgUtil.read(filepath);
if(StrUtil.isBlank(boxsJsonStr)) {
return image;
}
return paintRectAndTitle(image, boxsJsonStr);
} catch (Exception e) {
//
}

try {
return ImgUtil.read(filepath);
} catch (Exception e) {
//
}
return null;
}

/**
* Draw make Rectangle Box and Title
* @param image
* @param boxsJsonStr, Format: [{type:'peopel', position: [100, 100, 200, 200]}]
* @return
*/
private BufferedImage paintRectAndTitle(BufferedImage image, String boxsJsonStr) {
if(image == null) {
return null;
}

try {
JSONArray boxsJson = JSON.parseArray(boxsJsonStr);
int len = boxsJson.size();
if(len == 0) {
return image;
}

//
Graphics2D g = image.createGraphics();
g.setColor(Color.RED);
g.setStroke(new BasicStroke(2));

// Draw make Rectangle Box and Title
for (int i = 0; i < len; i++) {
JSONObject row = boxsJson.getJSONObject(i);
String title = row.getString("type");
JSONArray position = row.getJSONArray("position");
//
g.setPaint(Color.RED); // Background
if (position.size() == 4) {
int x = position.getIntValue(0);
int y = position.getIntValue(1);
int width = position.getIntValue(2) - x;
int height = position.getIntValue(3) - y;
g.drawRect(x, y, width, height);

int textY = y - 10; // up Method 10 Pixel Bit set
//Font font = getFont();
if(font!= null) {
//font = font.deriveFont(16f);
//font = font.deriveFont(Font.BOLD);
g.setFont(font);
}
// Set Text Char Background big small By Text Char long Degree Incoming, and Set for half Transparent Red
FontMetrics fm = g.getFontMetrics();
int textWidth = fm.stringWidth(title); // Calculate Text Width

int backgroundColorWidth = textWidth + 35; // Background Width than Text Width Later big One Some
int backgroundColorHeight = 10 + 14; // Background high Degree can with By Need Adjust whole
g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // Set half Transparent combine Complete Rule rule
g.fillRect(x, y - backgroundColorHeight, backgroundColorWidth, backgroundColorHeight); // small Method Block Make for Background Mark, and Set for half Transparent

g.setPaint(Color.white);
g.drawString(title, x + 4, textY + 4);
}
}
g.dispose();
return image;
} catch (Exception e) {
return null;
}
}

/**
* Get System Font, Optimize First make Use in Text Font
* @return
*/
private Font getFont() {
GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
Font[] fonts = ge.getAllFonts();
if(fonts == null || fonts.length == 0) {
log.error("no Method Get System Font Set");
return null;
}

for(Font font: fonts) {
if(font.canDisplay('\u4e2d')) {
return font;
}
}
return fonts[0];
}
}

package com.yihecode.camera.ai.web.api;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.web.ModelController;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Base64;

/**
* Alert Third Party Push, will most new for Alert same Hour Push to Third Party Platform
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@ApiIgnore
@Slf4j
@Component
@EnableAsync
public class ReportPushService {

    //
@Resource
private ConfigService configService;

/**
* Call API
* @param url
* @param params
*/
@Async
public void request(String url, JSONObject params, boolean toBase64, String fileName) {
int statusCode = -1;
HttpEntity httpEntity = null;
try {
//
if(toBase64) {
// File still not write Complete, still not can Read?
int count = 0;
while(true) {
File file = new File(fileName);
if(file.exists() && file.canRead()) {
break;
}
count++;

if(count >= 10) {
break;
}

Thread.sleep(20);
}

try {
File file = new File(fileName);
BufferedImage image = ImgUtil.read(file);
// Whether Draw make Matrix Shape and Title
String draw = configService.getByValTag("reportPushDraw");
if("true".equals(draw)) {
image = paintRectAndTitle(image, params.getString("params"));
}
String imageBase64 = ImgUtil.toBase64(image, FileUtil.extName(file));
params.put("imageBase64", imageBase64);
} catch (Exception e) {
params.put("imageBase64","");
}
} else {
params.put("imageBase64","");
}

CloseableHttpClient client = HttpClients.createDefault();
//
HttpPost httpPost = new HttpPost(url);
httpPost.addHeader("Accept-Encoding","gzip, deflate, br");
httpPost.addHeader("Content-Type","application/json");
httpPost.setEntity(new StringEntity(params.toString(),"UTF-8"));

//
RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(500).build();
httpPost.setConfig(requestConfig);

//
CloseableHttpResponse response = client.execute(httpPost);
statusCode = response.getStatusLine().getStatusCode();
httpEntity = response.getEntity();

//
if (statusCode!= 200) {
log.error("Call Third Party Report API Status Exception status:{}, url:{}, camera:{}, algorithm:{}, response:{}", statusCode, url, params.getString("camera_name"), params.getString("algorithm_name"), EntityUtils.toString(httpEntity));
} else {
log.info("Call Third Party Report API Status Success status:{}, url:{}, camera:{}, algorithm:{}, response:{}", statusCode, url, params.getString("camera_name"), params.getString("algorithm_name"), EntityUtils.toString(httpEntity));
}

response.close();
client.close();
} catch (Exception e) {
log.error("Call Third Party Report API Exception {}, ex:{}", url, e);
} finally {
try {
EntityUtils.consume(httpEntity);
} catch (Exception e) {}
}
}



/**
* Image turn base64 String
* @param file
* @return
*/
private String getImgStrToBase64(String file) {
ByteArrayOutputStream bos = null;
FileInputStream fis = null;
try {
//
System.out.println(file);
File fileObj = new File(file);
System.out.println(fileObj.getAbsolutePath());
System.out.println(fileObj.getName());
if(!fileObj.exists()) {
System.out.println("not found");
return"";
}

//
int count = 0;
while(!fileObj.canRead()) {// Image can can Horse up Read not to
count++;
try {
Thread.sleep(5);
} catch (Exception e) {}
//
if(count > 3) {
break;
}
}

//
bos = new ByteArrayOutputStream(1000);
fis = new FileInputStream(file);
byte[] b = new byte[1000];
int n;
while ((n = fis.read(b))!= -1) {
bos.write(b, 0, n);
}
return Base64.getEncoder().encodeToString(bos.toByteArray());
} catch (Exception e) {
System.out.println(e.getMessage());
} finally{
try {
if (fis!= null) {
fis.close();
}
} catch (Exception e) {}

try {
if (bos!= null) {
bos.close();
}
} catch (Exception e) {}
}
return"";
}

@Async
public void testbase64(String fileName) {
//
File test = new File("/data/fis/");
if(!test.exists()) {
test.mkdirs();
}

// File still not write Complete, still not can Read?
int count = 0;
while(true) {
File file = new File(fileName);
if(file.exists() && file.canRead()) {
break;
}
count++;

if(count >= 10) {
break;
}

try {
Thread.sleep(20);
} catch (InterruptedException e) {
e.printStackTrace();
}
}

try {
File file = new File(fileName);
String imageBase64_1 = ImgUtil.toBase64(ImgUtil.read(file), FileUtil.extName(file));
String imagebase64_2 = getImgStrToBase64(fileName);

String uu = IdUtil.randomUUID();
FileUtil.writeString(imageBase64_1,"/data/fis/"+ uu +"_1.txt", Charset.forName("utf-8"));
FileUtil.writeString(imagebase64_2,"/data/fis/"+ uu +"_2.txt", Charset.forName("utf-8"));

} catch (Exception e) {
e.printStackTrace();
}
}

/**
* Draw make Rectangle Box and Title
* @param image
* @param boxsJsonStr
* @return
*/
private BufferedImage paintRectAndTitle(BufferedImage image, String boxsJsonStr) {
try {
Graphics2D g = image.createGraphics();
g.setColor(Color.RED);
g.setStroke(new BasicStroke(2));

// Draw make Rectangle Box and Title
JSONArray boxsJson = JSON.parseArray(boxsJsonStr);
int len = boxsJson.size();
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

int textX = x;
int textY = y - 10; // up Method 10 Pixel Bit set
Font font = Font.createFont(Font.TRUETYPE_FONT, ModelController.class.getResourceAsStream("/fonts/Microsoft YaHei.ttf"));
font = font.deriveFont(16f);
// Set Text Char Background big small By Text Char long Degree Incoming, and Set for half Transparent Red
FontMetrics fm = g.getFontMetrics();
int textWidth = fm.stringWidth(title); // Calculate Text Width

int backgroundColorWidth = textWidth + 35; // Background Width than Text Width Later big One Some
int backgroundColorHeight = 10 + 14; // Background high Degree can with By Need Adjust whole
g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // Set half Transparent combine Complete Rule rule
g.fillRect(textX, y - backgroundColorHeight, backgroundColorWidth, backgroundColorHeight); // small Method Block Make for Background Mark, and Set for half Transparent

g.setPaint(Color.white);
g.setFont(font);
g.drawString(title, textX, textY);
}
}
g.dispose();
// ImageIO.write(image,"jpg", new File("e:/draw/1b1.jpg"));
return image;
} catch (Exception e) {
return null;
}
}
}

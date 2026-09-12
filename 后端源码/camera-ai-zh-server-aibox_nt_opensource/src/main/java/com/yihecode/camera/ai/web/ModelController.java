package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.vo.ModelPredictVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
* Algorithm Model Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Model Test Management")
@Slf4j
@Controller
@RequestMapping({"/model"})
public class ModelController {

    //
@Autowired
private AlgorithmService algorithmService;

//
@Autowired
private ConfigService configService;

//
@Value("${modelDir}")
private String uploadDir;

/**
* Model Test - Image Upload - Fit allocate VUE, same testPredict Method
* @return
*/
@ApiOperation("Model Inference")
@SaCheckPermission("XXXXXX")
@PostMapping("/test/detect")
@ResponseBody
public JsonResult testDetect(@RequestBody ModelPredictVo modelPredictVo) {
String file = modelPredictVo.getFile();
String algorithms = modelPredictVo.getAlgorithms();
String cameraId = modelPredictVo.getCameraId();
Double imgHeight = modelPredictVo.getImgHeight();
String marks = modelPredictVo.getMarks();

//
if(StrUtil.isBlank(file)) {
return JsonResultUtils.fail("Please Upload Need Test Image");
}
//
if(StrUtil.isBlank(algorithms)) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
List<Long> algorithmIdList = new ArrayList<>();
try {
//
JSONArray array = JSON.parseArray(algorithms);
int len = array.size();
if(len == 0) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
for(int i = 0; i < len; i++) {
algorithmIdList.add(array.getLong(i));
}
} catch (Exception e) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
if(algorithmIdList.isEmpty()) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
if(StrUtil.isBlank(cameraId)) {
cameraId = IdUtil.randomUUID();
}
//
if(StrUtil.isBlank(marks)) {
marks ="";
}
//
try {
//
String filepath = uploadDir +"/"+ file;
File imageFile = new File(filepath);
if(!imageFile.exists()) {
return JsonResultUtils.fail("Image File does not exist");
}

//
String algorithmUrl = configService.getByValTag("algorithmUrl");
if(StrUtil.isBlank(algorithmUrl)) {
return JsonResultUtils.fail("Algorithm Address not Config");
}

//
BufferedImage image = ImgUtil.read(filepath);
String imageBase64 = ImgUtil.toBase64(image, FileUtil.extName(filepath));

// Convert Coordinate
marks = parseMarks(marks, filepath, imgHeight);

//
JSONArray algorithmArray = new JSONArray();
JSONArray algorithmJsonArray = JSON.parseArray(algorithms);
int algorithmJsonArraySize = algorithmJsonArray.size();
for(int i = 0; i < algorithmJsonArraySize; i++) {
Long algorithmId = algorithmJsonArray.getLong(i);
Algorithm algorithm = algorithmService.getById(algorithmId);
if(algorithm!= null) {
JSONObject algorithmObj = new JSONObject();
algorithmObj.put("algorithm_id", algorithm.getId());
algorithmObj.put("algorithm_confidence", 0.5);
algorithmObj.put("algorithm_name", algorithm.getNameEn());
algorithmObj.put("algorithm_name_en", algorithm.getNameEn());
algorithmObj.put("algorithm_rois", marks);
algorithmArray.add(algorithmObj);
}
}

//
JSONObject params = new JSONObject();
params.put("image_base64", imageBase64);
params.put("param", algorithmArray);
params.put("area", marks);
params.put("camera_id", cameraId); // Temp Hour not need transmit

// [{"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}, {"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}]
JSONArray predictArray = this.requestAlgorithm(algorithmUrl, params);
if(predictArray == null || predictArray.isEmpty()) {
return JsonResultUtils.fail("Algorithm Call Exception");
}

// Call Alert API
boolean isPredict = false;
int len = predictArray.size();
for(int i = 0; i < len; i++) {
JSONObject predictObj = predictArray.getJSONObject(i);
Long algorithmId = predictObj.getLong("algorithm_id");
JSONArray data = predictObj.getJSONArray("data");
if(data == null || data.isEmpty()) {
continue;
}

//
isPredict = true;
}

String json = JSON.toJSONString(predictArray, SerializerFeature.PrettyFormat,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);


//
Map<String, Object> retMap = new HashMap<>();
//retMap.put("file", dateDir + saveName);
retMap.put("json", json);

return JsonResultUtils.success(retMap);
} catch (Exception e) {
log.error("Call Inference API Exception", e);
return JsonResultUtils.fail("Inference Exception:"+ e.getMessage());
}
}
/**
* Video Stream
* @author Abyss
* @date 2023/12/5 12:35
*/
@SaCheckPermission("XXXXXX")
@GetMapping("/test/steamMp4")
@ResponseBody
public void steamMp4(String file, HttpServletResponse response) throws Exception {
String filepath = uploadDir +"/"+ file;
File videoFile = new File(filepath);
FileInputStream fis = new FileInputStream(videoFile);
byte[] videoData = new byte[(int) videoFile.length()];
fis.read(videoData);
response.setContentType("video/mp4");
response.getOutputStream().write(videoData);
response.getOutputStream().flush();
response.getOutputStream().close();
fis.close();
}
/**
* Video Download Export
* @author Abyss
* @date 2023/12/5 12:35
*/
@ApiOperation(value ="Export Download")
@SaCheckPermission("XXXXXX")
@GetMapping({"/test/download"})
@ResponseBody
public JsonResult download(@RequestParam("file") String file, HttpServletResponse response, HttpServletRequest request) throws IOException, BizException {
String filepath = uploadDir +"/"+ file;
if(StrUtil.isBlank(filepath)){
return JsonResultUtils.fail("mp4 File does not exist, Please Retry Later");
}
FtpUtils.download(filepath,"Model Inference Result Export.mp4", response, request);
return JsonResultUtils.success();
}
@ApiOperation(value ="Export Download")
@GetMapping({"/test/downloadZip"})
@ResponseBody
public JsonResult downloadZip(@RequestParam("file") String file, HttpServletResponse response, HttpServletRequest request) throws IOException, BizException {
String filepath = uploadDir +"/"+ file;
if(StrUtil.isBlank(filepath)){
return JsonResultUtils.fail("zip File does not exist, Please Retry Later");
}
FtpUtils.download(filepath,"Model Inference Result Export.zip", response, request);
return JsonResultUtils.success();
}

/**
* Model Inference Mp4
* @author Abyss
* @date 2023/12/5 03:55
*/
@ApiOperation("MP4 Model Inference (VUE)")
@SaCheckPermission("XXXXXX")
@PostMapping("/test/detectMp4")
@ResponseBody
public JsonResult testDetectMp4(@RequestBody ModelPredictVo modelPredictVo) {
String file = modelPredictVo.getFile();
String algorithms = modelPredictVo.getAlgorithms();
String cameraId = modelPredictVo.getCameraId();
Double imgHeight = modelPredictVo.getImgHeight();
String marks = modelPredictVo.getMarks();

//
if(StrUtil.isBlank(file)) {
return JsonResultUtils.fail("Please Upload Need Test Video");
}
//
if(StrUtil.isBlank(algorithms)) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
List<Long> algorithmIdList = new ArrayList<>();
try {
//
JSONArray array = JSON.parseArray(algorithms);
int len = array.size();
if(len == 0) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
for(int i = 0; i < len; i++) {
algorithmIdList.add(array.getLong(i));
}
} catch (Exception e) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
if(algorithmIdList.isEmpty()) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
if(StrUtil.isBlank(cameraId)) {
cameraId = IdUtil.randomUUID();
}
//
if(StrUtil.isBlank(marks)) {
marks ="";
}
//
try {
//
String filepath = uploadDir +"/"+ file;
File imageFile = new File(filepath);
if(!imageFile.exists()) {
return JsonResultUtils.fail("Video File does not exist");
}
//
String algorithmUrl = configService.getByValTag("algorithmUrl");
if(StrUtil.isBlank(algorithmUrl)) {
return JsonResultUtils.fail("Algorithm Address not Config");
}

// Frame Extract
List<String> imageList = extractFrames(filepath);

// Convert Coordinate
if(imageList!= null &&!imageList.isEmpty()) {
String imgpath = imageList.get(0);
marks = parseMarks(marks, imgpath, imgHeight);
}

// Algorithm Data
JSONArray algorithmArray = new JSONArray();
JSONArray algorithmJsonArray = JSON.parseArray(algorithms);
int algorithmJsonArraySize = algorithmJsonArray.size();
for(int i = 0; i < algorithmJsonArraySize; i++) {
Long algorithmId = algorithmJsonArray.getLong(i);
Algorithm algorithm = algorithmService.getById(algorithmId);
if(algorithm!= null) {
JSONObject algorithmObj = new JSONObject();
algorithmObj.put("algorithm_id", algorithm.getId());
algorithmObj.put("algorithm_confidence", 0.5);
algorithmObj.put("algorithm_name", algorithm.getNameEn());
algorithmObj.put("algorithm_name_en", algorithm.getNameEn());
algorithmObj.put("algorithm_rois", marks);
algorithmArray.add(algorithmObj);
}
}

//
for (String imagePath: imageList) {
//
BufferedImage image = ImgUtil.read(imagePath);
String imageBase64 = ImgUtil.toBase64(image, FileUtil.extName(imagePath));
//
JSONObject params = new JSONObject();
params.put("image_base64", imageBase64);
params.put("param", algorithmArray);
params.put("area", marks);
params.put("camera_id", cameraId); // Temp Hour not need transmit

// [{"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}, {"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}]
JSONArray predictArray = this.requestAlgorithm(algorithmUrl, params);
if(predictArray == null || predictArray.isEmpty()) {
continue;
// return JsonResultUtils.fail("Algorithm Call Exception");
}
// Call Alert API
boolean isPredict = false;
int len = predictArray.size();
for(int i = 0; i < len; i++) {
JSONObject predictObj = predictArray.getJSONObject(i);
Long algorithmId = predictObj.getLong("algorithm_id");
Algorithm algorithm = algorithmService.getById(algorithmId);
JSONArray data = predictObj.getJSONArray("data");
if(data == null || data.isEmpty()) {
continue;
}
//
isPredict = true;
for (int j=0; j<data.size(); j++) {
JSONObject positionData = data.getJSONObject(j);
// JSONObject positionData = positionArray.getJSONObject(j);
JSONArray position = positionData.getJSONArray("position");
double confidence = positionData.getDouble("confidence");
String type = positionData.getString("type");
// Draw make
// Color color;
// if (colors.containsKey(type)) {
// color = colors.get(type);
//} else {
// Random random = new Random();
// color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
// colors.put(type, color);
//}
String typeStr ="";
if ("hook_detect".equals(algorithm.getNameEn())) {
if ("no_hook".equals(type)) {
typeStr = algorithm.getName() +"_ not Check Stay";
drawPolygon(position, imagePath, Color.RED, String.format("%s %.2f", typeStr, confidence));
} else if ("hook".equals(type)) {
typeStr = algorithm.getName() +"_ Check Stay";
drawPolygon(position, imagePath, Color.GREEN, String.format("%s %.2f", typeStr, confidence));
}
} else {
typeStr = algorithm.getName();
drawPolygon(position, imagePath, Color.RED, String.format("%s %.2f", typeStr, confidence));
}
}
}

// String json = JSON.toJSONString(predictArray, SerializerFeature.PrettyFormat,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
// jsonList.add(json);
}

//
Map<String, Object> retMap = new HashMap<>();
// retMap.put("file", dateDir + saveName);
// retMap.put("json", jsonList);\
String fileExtension ="";
int dotIndex = filepath.lastIndexOf('.');
if (dotIndex >= 0) {
fileExtension = filepath.substring(dotIndex);
}
String outputPath = filepath.replace(fileExtension,"_out"+ fileExtension);
synthesizeVideo(imageList, outputPath, filepath);
retMap.put("file", outputPath.replace(uploadDir +"/",""));
return JsonResultUtils.success(retMap);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail("Storage File Exception");
}
}

/**
* Model Inference ZIP
* @author Abyss
* @date 2023/12/13 19:46
*/
@ApiOperation("zip Model Inference (VUE)")
@SaCheckPermission("XXXXXX")
@PostMapping("/test/detectZip")
@ResponseBody
public JsonResult testDetectZip(@RequestBody ModelPredictVo modelPredictVo) {
String file = modelPredictVo.getFile();
String algorithms = modelPredictVo.getAlgorithms();
//
if(StrUtil.isBlank(file)) {
return JsonResultUtils.fail("Please Upload Need Test Compress File");
}
//
if(StrUtil.isBlank(algorithms)) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
List<Long> algorithmIdList = new ArrayList<>();
try {
//
JSONArray array = JSON.parseArray(algorithms);
int len = array.size();
if(len == 0) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
for(int i = 0; i < len; i++) {
algorithmIdList.add(array.getLong(i));
}
} catch (Exception e) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
if(algorithmIdList.isEmpty()) {
return JsonResultUtils.fail("Please select Need Test Algorithm");
}
//
try {
//
String filepath = uploadDir +"/"+ file;
File zipFile = new File(filepath);
if(!zipFile.exists()) {
return JsonResultUtils.fail("zip File does not exist");
}
//
String algorithmUrl = configService.getByValTag("algorithmUrl");
if(StrUtil.isBlank(algorithmUrl)) {
return JsonResultUtils.fail("Algorithm Address not Config");
}
// Algorithm Data
JSONArray algorithmArray = new JSONArray();
JSONArray algorithmJsonArray = JSON.parseArray(algorithms);
int algorithmJsonArraySize = algorithmJsonArray.size();
for(int i = 0; i < algorithmJsonArraySize; i++) {
Long algorithmId = algorithmJsonArray.getLong(i);
Algorithm algorithm = algorithmService.getById(algorithmId);
if(algorithm!= null) {
JSONObject algorithmObj = new JSONObject();
algorithmObj.put("algorithm_id", algorithm.getId());
algorithmObj.put("algorithm_confidence", 0.5);
algorithmObj.put("algorithm_name", algorithm.getNameEn());
algorithmObj.put("algorithm_name_en", algorithm.getNameEn());
algorithmObj.put("algorithm_rois","");
algorithmArray.add(algorithmObj);
}
}
// Decompress Image
String unzipPath = zipFile.getPath().replace(".zip","");
List<String> imageList = ZipUtils.unzip(filepath, unzipPath);
//
for (String imagePath: imageList) {
//
BufferedImage image = ImgUtil.read(imagePath);
String imageBase64 = ImgUtil.toBase64(image, FileUtil.extName(imagePath));
//
JSONObject params = new JSONObject();
params.put("image_base64", imageBase64);
params.put("param", algorithmArray);
params.put("area","");
// [{"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}, {"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}]
JSONArray predictArray = this.requestAlgorithm(algorithmUrl, params);
if(predictArray == null || predictArray.isEmpty()) {
continue;
// return JsonResultUtils.fail("Algorithm Call Exception");
}
// Call Alert API
boolean isPredict = false;
int len = predictArray.size();
for(int i = 0; i < len; i++) {
JSONObject predictObj = predictArray.getJSONObject(i);
Long algorithmId = predictObj.getLong("algorithm_id");
Algorithm algorithm = algorithmService.getById(algorithmId);
JSONArray data = predictObj.getJSONArray("data");
if(data == null || data.isEmpty()) {
continue;
}
//
isPredict = true;
for (int j=0; j<data.size(); j++) {
JSONObject positionData = data.getJSONObject(j);
JSONArray position = positionData.getJSONArray("position");
double confidence = positionData.getDouble("confidence");
String type = positionData.getString("type");
String typeStr ="";
if ("hook_detect".equals(algorithm.getNameEn())) {
if ("no_hook".equals(type)) {
typeStr = algorithm.getName() +"_ not Check Stay";
drawPolygon(position, imagePath, Color.RED, String.format("%s %.2f", typeStr, confidence));
} else if ("hook".equals(type)) {
typeStr = algorithm.getName() +"_ Check Stay";
drawPolygon(position, imagePath, Color.GREEN, String.format("%s %.2f", typeStr, confidence));
}
} else {
typeStr = algorithm.getName();
drawPolygon(position, imagePath, Color.RED, String.format("%s %.2f", typeStr, confidence));
}

}
}
// String json = JSON.toJSONString(predictArray, SerializerFeature.PrettyFormat,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty);
// jsonList.add(json);
}

//
Map<String, Object> retMap = new HashMap<>();
// retMap.put("file", dateDir + saveName);
// retMap.put("json", jsonList);\
String fileExtension ="";
int dotIndex = filepath.lastIndexOf('.');
if (dotIndex >= 0) {
fileExtension = filepath.substring(dotIndex);
}
String outputPath = filepath.replace(fileExtension,"_out"+ fileExtension);
// Compress Image
String zipOutPath = filepath.replace(".zip","_out.zip");
ZipUtils.compress(unzipPath, zipOutPath);
// synthesizeVideo(imageList, outputPath, filepath);
retMap.put("file", zipOutPath.replace(uploadDir +"/",""));
return JsonResultUtils.success(retMap);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail("Storage File Exception");
}
}

/**
* MP4 Get No One Frame Image
* @author Abyss
* @date 2023/12/13 19:24
* @param modelPredictVo
* @return com.yihecode.camera.ai.utils.JsonResult
*/
@ApiOperation("MP4 Get No One Frame Image (VUE)")
@SaCheckPermission("XXXXXX")
@PostMapping("/test/firstPicFromMp4")
@ResponseBody
public JsonResult getFirstPicFromMp4(@RequestBody ModelPredictVo modelPredictVo) {
String file = modelPredictVo.getFile();
if(StrUtil.isBlank(file)) {
return JsonResultUtils.fail("Please Upload Need Test Video");
}
try {
//
String filepath = uploadDir +"/"+ file;
File imageFile = new File(filepath);
if(!imageFile.exists()) {
return JsonResultUtils.fail("Video File does not exist");
}
// Frame Extract
String image = extractFramesFirst(filepath);
return JsonResultUtils.success(image.replace(uploadDir,""));
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail("Frame Extract Exception");
}
}
private static String extractFramesFirst(String inputFile) throws Exception {
String result ="";
FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputFile);
frameGrabber.start();
int count = 0;
while (true) {
if (frameGrabber.grabImage()!= null) {
// Skip front Several Frame
if (count == 5) {
// Extract Get Frame and Save for Image File
Frame frame = frameGrabber.grabImage();
if (null!= frame) {
String fileName = String.format(inputFile +".frame_first.jpg");
File file = new File(fileName);
ImageIO.write(FrameToBufferedImage(frame),"jpg", file);
result = fileName;
}
break;
}
count++;
} else {
// Video Ended, exit out Loop
break;
}
}
frameGrabber.stop(); // Stop Grab Get Video Frame
frameGrabber.release();
return result;
}
// Each x Frame Extract Get One Frame
private static int frameRate = 4;
/**
* Video Frame Extract and Save Image
* @author Abyss
* @date 2023/12/4 18:26
*/
private static List<String> extractFrames(String inputFile) throws Exception {
// String inputFile ="path/to/input.mp4"; // input in Video File Path
List<String> result = new ArrayList<>();
// int frameRate = frameRate; // Each x Frame Extract Get One Frame
FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputFile);
frameGrabber.start();
int count = 0;
while (true) {
if (frameGrabber.grabImage()!= null) {
// Check Whether Reach to Refer Fixed Frame Rate
if (count % frameRate == 0) {
// Extract Get Frame and Save for Image File
Frame frame = frameGrabber.grabImage();
if (null!= frame) {
String fileName = String.format(inputFile +".frame_%03d.jpg", count);
File file = new File(fileName);
ImageIO.write(FrameToBufferedImage(frame),"jpg", file);
result.add(fileName);
}
}
count++;
} else {
// Video Ended, exit out Loop
break;
}
}
frameGrabber.stop(); // Stop Grab Get Video Frame
frameGrabber.release();
return result;
}
private static RenderedImage FrameToBufferedImage(Frame frame) {
// Create BufferedImage Object
Java2DFrameConverter converter = new Java2DFrameConverter();
BufferedImage bufferedImage = converter.getBufferedImage(frame);
return bufferedImage;
}
/**
* Video combine and
* @author Abyss
* @date 2023/12/4 19:33
* @param imageFiles Image Address
* @param outputFile input out Path
* @param mp4Path Video Address
*/
private static void synthesizeVideo(List<String> imageFiles, String outputFile, String mp4Path) throws Exception {

// for Image File in Line Sort, with Ensure It They In Video in Smooth order Correct
// imageFiles.sort(null);
// Get original Video Info
FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(mp4Path);
grabber.start();
// Get Video FPS
double fps = grabber.getFrameRate();
System.out.println("FPS:"+ fps);
// Mat imageMat = Imgcodecs.imread(imageFiles.get(0));
fps = fps / frameRate;

// Video Width high most Good is According to Often View Video Width high 16:9 or 9:16
FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight());
// Set Video Code Layer Mode
recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
// Set Video fps
recorder.setFrameRate(fps);
// Set Video Image Data Format
recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
recorder.setFormat("mp4");
recorder.start();
for (String imageFile: imageFiles) {
Java2DFrameConverter converter = new Java2DFrameConverter();
BufferedImage read = ImageIO.read(new File(imageFile));
recorder.record(converter.getFrame(read)); // will Frame Record to input out Video in
}
recorder.stop(); // Stop Record Video Frame and Explain Put Resource
recorder.release();
grabber.stop();
grabber.release();

}
/**
* Video Draw make
* @author Abyss
* @date 2023/12/5 03:43
*/
private static void drawPolygon(JSONArray position, String imagePath, Color color, String title) throws Exception{
// Read Image
BufferedImage image = ImageIO.read(new File(imagePath));
// Get Image Draw image Environment
Graphics2D g = image.createGraphics();
// Set Draw Pen Color
g.setColor(color);
// Set Draw Thick Stroke Detail
g.setStroke(new BasicStroke(2));
if (position.size() == 4) {
// In Image up Draw One Rectangle Box, Param part Other for x, y, Width, high Degree
int x = position.getIntValue(0);
int y = position.getIntValue(1);
int width = position.getIntValue(2) - x;
int height = position.getIntValue(3) - y;
g.drawRect(x, y, width, height);

// In Box up Method outer Side Annotation Text Char
int textX = x;
int textY = y - 10; // up Method 10 Pixel Bit set
// Font font = new Font("Microsoft YaHei", Font.BOLD, 16); // Set Support in Text Font
Font font = Font.createFont(Font.TRUETYPE_FONT, ModelController.class.getResourceAsStream("/fonts/Microsoft YaHei.ttf"));
font = font.deriveFont(16f);
// Set Text Char Background big small By Text Char long Degree Incoming, and Set for half Transparent Red
FontMetrics fm = g.getFontMetrics();
int textWidth = fm.stringWidth(title); // Calculate Text Width

int backgroundColorWidth = textWidth + 35; // Background Width than Text Width Later big One Some
int backgroundColorHeight = 10 + 14; // Background high Degree can with By Need Adjust whole
g.setPaint(color); // Background
g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // Set half Transparent combine Complete Rule rule
g.fillRect(textX, y - backgroundColorHeight, backgroundColorWidth, backgroundColorHeight); // small Method Block Make for Background Mark, and Set for half Transparent

g.setPaint(Color.white);
g.setFont(font);
g.drawString(title, textX, textY);
}

// Explain Put Draw image Environment
g.dispose();
// will Modify after Image Save for new File
ImageIO.write(image,"jpg", new File(imagePath));
}

/**
* API Address: http://demo.chineseocr.com:5002/api/safety/predict
* Request Param:
* {"image_base64":"",
*"param": [{"algorithm_id": 1,"algorithm_name":"Smoke Fire Recognition","algorithm_confidence": 0.5},
* {"algorithm_id": 2,"algorithm_name":"Smoking Recognition","algorithm_confidence": 0.5}
*],
*"area":"",
*"camera_id":"333"
*}
* Call Algorithm Recognition API
* @param url
* @param params
*/
private JSONArray requestAlgorithm(String url, JSONObject params) {
int statusCode = -1;
HttpEntity httpEntity = null;
try {
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

String json = EntityUtils.toString(httpEntity);

if(statusCode == 200) {
JSONObject resultJson = JSON.parseObject(json);
if(resultJson.containsKey("code") && resultJson.get("code")!= null && resultJson.getInteger("code") == 200) {
if(resultJson.containsKey("data") && resultJson.get("data")!= null) {
return resultJson.getJSONArray("data");
}
}
} else {
log.error("Call Image Algorithm API Status Exception {}, {}", statusCode, url);
}
} catch (Exception e) {
log.error("Call Image Algorithm API Exception {}, ex:{}", url, e);
} finally {
try {
EntityUtils.consume(httpEntity);
} catch (Exception e) {}
}
return null;
}

/**
* Upload File
* @param file
* @return
*/
@ApiOperation("Upload File")
@SaCheckPermission("XXXXXX")
@PostMapping("/test/upload")
@ResponseBody
public JsonResult testUpload(@RequestParam(value ="file", required = false) MultipartFile file) {
//
if(file == null) {
return JsonResultUtils.fail("Please Upload Need Test Image");
}

// Create Directory
String dateDir = DateUtil.format(new Date(),"yyyy") +"/"+ DateUtil.format(new Date(),"MMdd") +"/";
String path = uploadDir + dateDir;
File pathFile = new File(path);
if(!pathFile.exists()) {
pathFile.mkdirs();
}

// Save File Name Name
String saveName = IdUtil.randomUUID() +"."+ FileUtil.extName(file.getOriginalFilename());

//
try {
//
File newFile = new File(path + saveName);
file.transferTo(newFile);
//
String fileName = file.getOriginalFilename();
int dotIndex = fileName.lastIndexOf(".");
if (dotIndex!= -1 && dotIndex < fileName.length() - 1) {
String extension = fileName.substring(dotIndex + 1).toLowerCase();
if (extension.equals("mp4")) {
try {
long duration = getVideoTime(newFile);
if (duration > 180) {
newFile.delete();
return JsonResultUtils.fail("Please Upload 3 min inner Video");
}
} catch (Exception e) {
return JsonResultUtils.fail("Storage File Exception");
}
}
}
return JsonResultUtils.success(dateDir + saveName);
} catch (Exception e) {
return JsonResultUtils.fail("Storage File Exception");
}
}

public static Long getVideoTime(File file){
Long times = 0L;
try {
FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file);
ff.start();
times = ff.getLengthInTime()/(1000*1000);
ff.stop();
} catch (Exception e) {
e.printStackTrace();
}
return times;
}

/**
* Model Test - Image show show
* @return
*/
@ApiOperation("Image show show")
@SaCheckPermission("XXXXXX")
@GetMapping("/test/stream")
public void testPicStream(String file, HttpServletResponse response) {
try {
BufferedInputStream in = new BufferedInputStream(new FileInputStream(uploadDir + file));
response.setContentType("image/jpeg");
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
e.printStackTrace();
}
}

@ApiOperation("Image Zoom Slightly image show show")
@SaCheckPermission("XXXXXX")
@GetMapping("/test/streamThumb")
public void testPicStreamThumb(String file, HttpServletResponse response) {
try {
File ff = new File(uploadDir + file);
byte[] bytes = ImageUtils.compress2Byte(ff, 0.5);
response.setContentType("image/jpeg");
response.getOutputStream().write(bytes);
response.getOutputStream().flush();
response.getOutputStream().close();
} catch (Exception e) {
e.printStackTrace();
}
}

@ApiOperation("Get Model Test Use Image List")
@SaCheckPermission("XXXXXX")
@GetMapping("/test/getTestImage")
@ResponseBody
public JsonResult getTestImage(String suanfa) {
try {
if(StringUtils.isBlank(suanfa)) {
return JsonResultUtils.fail("Please select Algorithm");
}
String path = uploadDir +"model_test/"+ suanfa +"/pic";
if (!new File(path).exists()) {
return JsonResultUtils.fail("Algorithm Test Image does not exist");
}
List<String> result = new ArrayList<>();
List<Path> fileList = Files.list(Paths.get(path))
.filter(file ->!file.getFileName().toString().startsWith("._")
&& (file.getFileName().toString().endsWith(".jpg")
|| file.getFileName().toString().endsWith(".jpeg")
|| file.getFileName().toString().endsWith(".png")))
.collect(Collectors.toList());
for (Path file: fileList) {
// System.out.println(file);
result.add(file.toString().replace(uploadDir,""));
}
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail();
}

}

@ApiOperation("Get Model Test Use Video")
@SaCheckPermission("XXXXXX")
@GetMapping("/test/getTestMp4")
@ResponseBody
public JsonResult getTestMp4(String suanfa) {
try {
System.out.println(uploadDir);
if(StringUtils.isBlank(suanfa)) {
return JsonResultUtils.fail("Please select Algorithm");
}
String path = uploadDir +"model_test/"+ suanfa +"/mp4";
if (!new File(path).exists()) {
return JsonResultUtils.fail("Algorithm Test Use Video does not exist");
}
List<String> result = new ArrayList<>();
List<Path> fileList = Files.list(Paths.get(path))
.filter(file ->!file.getFileName().toString().startsWith("._")
&&!file.getFileName().toString().endsWith("_out.mp4")
&& file.getFileName().toString().endsWith(".mp4"))
.collect(Collectors.toList());
for (Path file: fileList) {
// System.out.println(file);
result.add(file.toString().replace(uploadDir,""));
}
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail();
}

}

@ApiOperation("Get Model Test Use Video")
@SaCheckPermission("XXXXXX")
@GetMapping("/test/getTestAll")
@ResponseBody
public JsonResult getTestAll(String suanfa) {
try {
if(StringUtils.isBlank(suanfa)) {
return JsonResultUtils.fail("Please select Algorithm");
}
List<String> result = new ArrayList<>();
// Get Image
String path = uploadDir +"model_test/"+ suanfa +"/pic";
if (new File(path).exists()) {
List<Path> fileList = Files.list(Paths.get(path))
.filter(file ->!file.getFileName().toString().startsWith("._")
&& (file.getFileName().toString().endsWith(".jpg")
|| file.getFileName().toString().endsWith(".jpeg")
|| file.getFileName().toString().endsWith(".png")))
.collect(Collectors.toList());
for (Path file: fileList) {
// System.out.println(file);
result.add(file.toString().replace(uploadDir,""));
}
}
// Get Video
path = uploadDir +"model_test/"+ suanfa +"/mp4";
if (new File(path).exists()) {
List<Path> fileList = Files.list(Paths.get(path))
.filter(file ->!file.getFileName().toString().startsWith("._")
// &&!file.getFileName().toString().endsWith("_out.mp4")
&& file.getFileName().toString().endsWith(".mp4"))
.collect(Collectors.toList());
for (Path file: fileList) {
// System.out.println(file);
result.add(file.toString().replace(uploadDir,""));
}
}
return JsonResultUtils.success(result);
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail();
}
}

/**
* Coordinate Convert
* @param marks Frontend Mark Point
* @param filepath Upload File Path
* @param imgHeight Frontend show show Image high Degree
* @return
*/
private String parseMarks(String marks, String filepath, Double imgHeight) {
//
if(StrUtil.isBlank(marks)) {
return"";
}
//
File tar = new File(filepath);
if(!tar.exists()) {
return"";
}
//
try {
// Get original start high Degree
BufferedImage image = ImgUtil.read(filepath);
// turn for true real high Degree and Width, Change than Example
int orginalHeight = image.getHeight();
double rate = orginalHeight * 1.0 / imgHeight.intValue();
//
JSONArray newMarks = new JSONArray();
JSONArray rootMark = JSON.parseArray(marks);
JSONArray roiMark = rootMark.getJSONArray(0);
int len = roiMark.size();
for(int i = 0; i < len; i++) {
JSONObject mark = roiMark.getJSONObject(i);
int x = mark.getIntValue("x");
int y = mark.getIntValue("y");

JSONObject newMark = new JSONObject();
newMark.put("x", Double.valueOf(x * rate).intValue());
newMark.put("y", Double.valueOf(y * rate).intValue());
newMarks.add(newMark);
}

// Package One Layer, 2D Number group
JSONArray wrapper = new JSONArray();
wrapper.add(newMarks);
marks = JSON.toJSONString(wrapper);

return marks;
} catch (Exception e) {
return"";
}
}

}

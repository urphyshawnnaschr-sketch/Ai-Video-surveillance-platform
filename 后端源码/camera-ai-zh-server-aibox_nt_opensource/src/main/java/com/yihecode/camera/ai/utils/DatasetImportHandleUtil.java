/**
* Copyright 2020 Zhejiang Lab. All Rights Reserved.
* <p>
* Licensed under the Apache License, Version 2.0 (the"License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an"AS IS"BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* =============================================================
*/
package com.yihecode.camera.ai.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.extra.compress.CompressUtil;
import cn.hutool.extra.compress.extractor.Extractor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ModelPredictionDTO;
import com.yihecode.camera.ai.dto.coco.Coco;
import com.yihecode.camera.ai.dto.coco.CocoAnnotation;
import com.yihecode.camera.ai.dto.coco.CocoCategorie;
import com.yihecode.camera.ai.dto.coco.CocoImage;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.ap.*;
import com.yihecode.camera.ai.enums.ap.ImageStatus;
import com.yihecode.camera.ai.enums.ap.ProjectStatusEnum;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.ap.ApFileService;
import com.yihecode.camera.ai.service.ap.ApImageService;
import com.yihecode.camera.ai.service.ap.ApProjectService;
import com.yihecode.camera.ai.web.ap.vo.AnnotationVo;
import com.yihecode.camera.ai.web.ap.vo.CommitVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @description Import Data Set Utils
* @date 2020-10-12
*/
@Slf4j
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class DatasetImportHandleUtil {

    @Autowired
    private ApImageService apImageService;


    @Autowired
    private ConfigService configService;

    @Autowired
    private AlgorithmService algorithmService;

    /**
* can Support Image Format Set combine
*/
    public static final List<String> SUFFIX_LIST = new ArrayList<>();

    private ThreadPoolExecutor commonThreadPoolExecutor;

    @Autowired
    private ApFileService apFileService;

    @Autowired
    private ApProjectService apProjectService;

    public static String BATH_PATH = "/data/dataset/";

    public static String UPLOAD = "upload";

    public static String IMAGES = "images";

    public static String ANNOTATION = "annotations";

    /**
* Load Quiet state Set combine Data
*/
    static {
        SUFFIX_LIST.add(".jpg");
        SUFFIX_LIST.add(".png");
        SUFFIX_LIST.add(".json");
        SUFFIX_LIST.add(".jpeg");
        SUFFIX_LIST.add(".bmp");
    }

    /**
* Init Method
*/
    @PostConstruct
    private void initialize() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.commonThreadPoolExecutor =
                new ThreadPoolExecutor(availableProcessors * 2, availableProcessors * 2, 100, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(100000), new ThreadPoolExecutor.CallerRunsPolicy());

    }

    @Async
    //@Transactional(rollbackFor = Exception.class)
public void importFile(ApProjectDTO projectDTO, Long userId) throws BizException {
ApFileDO fileDO = apFileService.getById(projectDTO.getDataFileId());
if (ObjectUtil.isNull(fileDO)) {
return;
}

String temp = BATH_PATH + projectDTO.getId() +"/"+ projectDTO.getDataFileId() +"/";
try {
if (!unTarLocalPath(fileDO.getRawData(), temp + UPLOAD)) {
throw new BizException("Decompress Failed");
}
executeUploadAndSave(temp, projectDTO, userId);
} catch (Exception e) {
log.error(e.getMessage());
} finally {
projectDTO.setStatus(ProjectStatusEnum.ANN.getCode());
apProjectService.updatepProjectById(projectDTO);
//apProjectService.updataProjectStatusLableNum(projectDTO.getId());
}

}


/**
* @param filePath Local Root Directory
*/
@Transactional(rollbackFor = Exception.class)
public void executeUploadAndSave(String filePath, ApProjectDTO projectDTO, Long userId) throws Exception {

String imagesPath = filePath + UPLOAD +"/"+ IMAGES;

List<String> fileList = FileUtil.listFileNames(imagesPath);
fileList = fileList.stream().filter(readName -> isFile(readName)).collect(Collectors.toList());
projectDTO.setItemCount(fileList.size());
if (CollectionUtil.isNotEmpty(projectDTO.getAlgorithmIds())) {
projectDTO.setStatus(ProjectStatusEnum.AUTO_ANN.getCode());
}
apProjectService.updatepProjectById(projectDTO);
Integer tatio = 0;
// Quality Check
if (projectDTO.getNeedReview().compareTo(1) == 0) {
Integer reviewRatio = projectDTO.getReviewRatio();
if (reviewRatio == 100) {
tatio = 1;
}
tatio = 100 / reviewRatio;
}
String annPath = filePath +"/"+ UPLOAD +"/"+ ANNOTATION;
AtomicInteger atomicInteger = new AtomicInteger(0);
boolean ann = false;
if (FileUtil.exist(annPath)) {
ann = true;
}

boolean annJson = false;
Map<String,CocoImage> imageMap = new HashMap();
Map<Integer,CocoCategorie> cocoCategoriesMap = new HashMap();
Map<Long,List<CocoAnnotation>> annotationsMap = new HashMap();
if (FileUtil.exist(annPath+"/"+"stuff_images.json")) {
annJson = true;

String content = null;
try {
content = readFile(new File(annPath+"/"+"stuff_images.json"));
} catch (IOException e) {
e.printStackTrace();
}
Coco coco = JSONObject.parseObject(content, Coco.class);
List<CocoAnnotation> annotations = coco.getAnnotations();
List<CocoCategorie> categories = coco.getCategories();
List<CocoImage> images = coco.getImages();
imageMap = images.stream().collect(Collectors.toMap(CocoImage::getFile_name, obj -> obj));
annotationsMap= annotations.stream().collect(Collectors.groupingBy(CocoAnnotation::getImage_id));
cocoCategoriesMap = categories.stream().collect(Collectors.toMap(CocoCategorie::getId, obj -> obj));
}
List<List<String>> fileSplit = CollectionUtil.split(fileList, 100);

for (int i = 0; i < fileSplit.size(); i++) {
List<String> fileParts = fileSplit.get(i);
CompletableFuture[] completableFutures = new CompletableFuture[fileParts.size()];
for (int j = 0; j < completableFutures.length; j++) {
String fileName = fileParts.get(j);
Integer finalTatio = tatio;
boolean finalAnn = ann;
boolean finaAnnJson = annJson;
Map<Long, List<CocoAnnotation>> finalAnnotationsMap = annotationsMap;
Map<Integer, CocoCategorie> finalCocoCategoriesMap = cocoCategoriesMap;
Map<String, CocoImage> finalImageMap = imageMap;
completableFutures[j] = CompletableFuture.runAsync(() -> {
try {
int andIncrement = atomicInteger.getAndIncrement();
if (!isFile(fileName)) {
return;
}
Image imageDO = new Image();
// File all Path
String fileFullPath = imagesPath +"/"+ fileName;
imageDO.setStoragePath(fileFullPath);
File file = new File(fileFullPath);
BufferedImage image = ImageIO.read(file);
imageDO.setWidth(image.getWidth());
imageDO.setHeight(image.getHeight());
imageDO.setMd5(Md5FileUtils.getMD5(file));
imageDO.setStatus(ImageStatus.WAIT.getType());
imageDO.setProjectId(projectDTO.getId());
imageDO.setDataFileId(projectDTO.getDataFileId());
imageDO.setVersion(1);
if (finalTatio > 0 && andIncrement % finalTatio == 0) {
imageDO.setNeedReview(1);
}
Long imageId = apImageService.saveImage(imageDO);
if (finalAnn&&!finaAnnJson) {
annXmlOperationMethod(annPath, fileName, userId, imageDO, imageId);
}
if (finalAnn&&finaAnnJson) {
annJsonOperationMethod(finalImageMap, finalCocoCategoriesMap, finalAnnotationsMap,
fileName, userId, imageDO, imageId);
}
if (CollectionUtil.isNotEmpty(projectDTO.getAlgorithmIds())) {
autoAnnOperationMethod(annPath, fileName, imageDO, imageId, projectDTO.getAlgorithmIds());
}
} catch (Exception e) {
log.error(e.getMessage());
}
}, commonThreadPoolExecutor);
}
CompletableFuture.allOf(completableFutures).join();
}

}

/**
* Process Annotation Info
*
* @param annPath
* @param fileName
* @param userId
* @param imageDO
* @param imageId
* @throws BizException
*/
private void annXmlOperationMethod(String annPath, String fileName, Long userId, Image imageDO, Long imageId) throws BizException {
String annName = StringUtils.substringBeforeLast(fileName,".");
String annFullPath = annPath +"/"+ annName;

List<Annotation> annotationDataList = new ArrayList<>();
if (FileUtil.exist(new File(annFullPath +".xml"))) {
annotationDataList = analyXML(new File(annFullPath +".xml"));
} else if (FileUtil.exist(new File(annFullPath +".json"))) {
//annotationDataList = analyJSON(new File(annFullPath +".json"));
} else {
return;
}
List<AnnotationVo> annotationVoList = annotationDataList.stream().map(annotationData -> {
AnnotationVo annotationVo = AnnotationVo.builder()
.tagName(annotationData.getTagName())
.annotationType(2).annotation(annotationData.getAnnotation()).build();
return annotationVo;
}).collect(Collectors.toList());
CommitVo commitVo = CommitVo.builder().isValid((byte) 1).imageId(imageId).annotations(annotationVoList).build();

apImageService.commit(commitVo, imageDO, userId);

imageDO.setStatus(ImageStatus.IMPORT_PROGRESS_FINSH.getType());
apImageService.updateById(imageDO);
}

/**
* Process json Annotation File
* @param fileName
* @param userId
* @param imageDO
* @param imageId
* @throws BizException
*/
private void annJsonOperationMethod(Map<String,CocoImage> imageMap,Map<Integer,CocoCategorie> cocoCategoriesMap,
Map<Long,List<CocoAnnotation>> annotationsMap,String fileName, Long userId,
Image imageDO, Long imageId) throws BizException {
CocoImage cocoImage = imageMap.get(fileName);
if(ObjectUtil.isNull(cocoImage)){
return;
}
Long cocoImageId = cocoImage.getId();
List<CocoAnnotation> cocoAnnotations = annotationsMap.get(cocoImageId);

List<Annotation> annotationDataList = analyJSON(cocoAnnotations,cocoCategoriesMap);


List<AnnotationVo> annotationVoList = annotationDataList.stream().map(annotationData -> {
AnnotationVo annotationVo = AnnotationVo.builder()
.tagName(annotationData.getTagName())
.annotationType(2).annotation(annotationData.getAnnotation()).build();
return annotationVo;
}).collect(Collectors.toList());
CommitVo commitVo = CommitVo.builder().isValid((byte) 1).imageId(imageId).annotations(annotationVoList).build();

apImageService.commit(commitVo, imageDO, userId);

imageDO.setStatus(ImageStatus.IMPORT_PROGRESS_FINSH.getType());
apImageService.updateById(imageDO);
}
/**
* self Dynamic Annotation
*
* @param annPath
* @param fileName
* @param imageDO
* @param imageId
* @throws BizException
*/
public void autoAnnOperationMethod(String annPath, String fileName, Image imageDO, Long imageId, List<Long> algorithmIds) throws BizException {
String algorithmUrl = configService.getByValTag("algorithmUrl");
if (StrUtil.isBlank(algorithmUrl)) {
return;
}

BufferedImage image = ImgUtil.read(annPath +"/"+ fileName);
String imageBase64 = ImgUtil.toBase64(image, FileUtil.extName(annPath +"/"+ fileName));

JSONArray algorithmArray = new JSONArray();
String marks ="";
for (int i = 0; i < algorithmIds.size(); i++) {
Long algorithmId = algorithmIds.get(i);
Algorithm algorithm = algorithmService.getById(algorithmId);
if (algorithm!= null) {
JSONObject algorithmObj = new JSONObject();
algorithmObj.put("algorithm_id", algorithm.getId());
algorithmObj.put("algorithm_confidence", 0.5);
algorithmObj.put("algorithm_name", algorithm.getNameEn());
algorithmObj.put("algorithm_name_en", algorithm.getNameEn());
algorithmArray.add(algorithmObj);
}
}

JSONObject params = new JSONObject();
params.put("image_base64", imageBase64);
params.put("param", algorithmArray);
params.put("area", marks);
//params.put("camera_id", cameraId); // Temp Hour not need transmit

// [{"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}, {"confidence":0.96,"position":[270,207,335,295],"type":"nohelmet"}]
JSONArray predictArray = this.requestAlgorithm(algorithmUrl, params);
List<ModelPredictionDTO> modelPredictionDTOS = JSONArray.parseArray(predictArray.toJSONString(), ModelPredictionDTO.class);
for (ModelPredictionDTO modelPredictionDTO: modelPredictionDTOS) {
List<AnnotationVo> annotationVos = modelPredictionDTO.getData().stream().map(predictionDetailDTO -> {
AnnotationData annotationData = new AnnotationData();
annotationData.setBox(predictionDetailDTO.getPosition());
AnnotationVo annotationVo = AnnotationVo.builder()
.tagName(predictionDetailDTO.getType())
.annotationType(2).annotation(annotationData).build();
return annotationVo;
}).collect(Collectors.toList());
CommitVo commitVo = CommitVo.builder().isValid((byte) 1).imageId(imageId).annotations(annotationVos).build();

apImageService.commit(commitVo, imageDO, 0L);
}
imageDO.setStatus(ImageStatus.ALGORITHM.getType());
apImageService.updateById(imageDO);

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
*
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

System.out.println("statusCode"+ statusCode);
System.out.println("json"+ json);

if (statusCode == 200) {
JSONObject resultJson = JSON.parseObject(json);
if (resultJson.containsKey("code") && resultJson.get("code")!= null && resultJson.getInteger("code") == 200) {
if (resultJson.containsKey("data") && resultJson.get("data")!= null) {
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
} catch (Exception e) {
}
}
return null;
}

/**
* Read File Content
*
* @param file File Object
* @return String File Content
*/
public static String readFile(File file) throws IOException {
StringBuilder stringBuffer = new StringBuilder();
LineIterator fileContext = FileUtils.lineIterator(file,"UTF-8");
while (fileContext.hasNext()) {
stringBuffer.append(fileContext.nextLine());
}
return stringBuffer.toString();
}

public static List<Annotation> analyXML(File file) {
String content = null;
try {
content = readFile(file);
} catch (IOException e) {
e.printStackTrace();
}
Map<String, Object> result1 = new HashMap<>(50);
Map<String, Object> stringObject = XmlUtil.xmlToMap(content, result1);
List<Map<String, Object>> detailList = new ArrayList<>();
Object objectXML = stringObject.get("object");
if (ObjectUtil.isNull(objectXML)) {
return new ArrayList<>();
}
if (!(objectXML instanceof List)) {
detailList.add(BeanUtil.beanToMap(objectXML));
} else {
Object object = ListUtil.toList(objectXML).get(0);
List<Object> objects1 = objToList(object);
detailList = objects1.stream().map(BeanUtil::beanToMap).collect(Collectors.toList());
//detailList = ListUtil.toList(stringObject.get("object")).stream().map(BeanUtil::beanToMap).collect(Collectors.toList());
}
List<Annotation> annotations = new ArrayList<>();
for (Map<String, Object> detailMap: detailList) {
AnnotationData annotationData = new AnnotationData();
Annotation annotation = new Annotation();
annotation.setTagName(ObjectUtil.isNull(detailMap.get("name"))?"": String.valueOf(detailMap.get("name")));
Object bndbox = detailMap.get("bndbox");
if (ObjectUtil.isNull(bndbox)) {
continue;
}
List<Float> box = new ArrayList<>();
Map<String, String> bndboxMap = (Map<String, String>) bndbox;
box.add(Float.parseFloat(bndboxMap.get("xmin")));
box.add(Float.parseFloat(bndboxMap.get("ymin")));
box.add(Float.parseFloat(bndboxMap.get("xmax")));
box.add(Float.parseFloat(bndboxMap.get("ymax")));
annotationData.setBox(box);
annotation.setAnnotation(annotationData);
annotations.add(annotation);
}
return annotations;
}

public static List<Annotation> analyJSON(List<CocoAnnotation> cocoAnnotations,Map<Integer,CocoCategorie> cocoCategoriesMap) {

List<Annotation> annotationlist = new ArrayList<>();
if(CollectionUtil.isEmpty(cocoAnnotations)){
return annotationlist;
}
for (int j = 0; j < cocoAnnotations.size(); j++) {
Annotation annotation = new Annotation();

AnnotationData annotationData = new AnnotationData();
CocoAnnotation cocoAnnotation = cocoAnnotations.get(j);
Integer category_id = cocoAnnotation.getCategory_id();
CocoCategorie cocoCategorie = cocoCategoriesMap.get(category_id);
if(ObjectUtil.isNotNull(cocoCategorie)){
annotation.setTagName(cocoCategorie.getName());
}

annotationData.setBox(cocoAnnotation.getBbox());
annotation.setAnnotation(annotationData);
annotationlist.add(annotation);
}
return annotationlist;
}

/**
* Get File Name (Kick Divide after Concat Name)
*
* @param fileName File Name
* @return String File Name (Kick Divide after Concat Name)
*/
public static String readFileName(String fileName) {
fileName = StringUtils.substringAfter(fileName, IMAGES +"/");
return fileName.substring(0, fileName.lastIndexOf("."));
}

/**
* Local Decompress tar Package and Delete Compress File
*
* @param sourcePath zip source File For example:/abc/z.zip
* @param targetPath Decompress after Target File Clip For example:/abc/
* @return boolean
*/
public boolean unTarLocalPath(String sourcePath, String targetPath) {

Extractor extractor = CompressUtil.createExtractor(
CharsetUtil.defaultCharset(),
FileUtil.file(sourcePath));
extractor.extract(FileUtil.file(targetPath));
return true;
}


public static boolean isFile(String fileName) {
for (String suffix: SUFFIX_LIST) {
if (StringUtils.endsWith(fileName.toLowerCase(), suffix)) {
return true;
}
}
return false;
}

public static void main(String[] args) throws IOException {

}

public static List<Object> objToList(Object obj) {
List<Object> list = new ArrayList<Object>();
if (obj instanceof ArrayList<?>) {
for (Object o: (List<?>) obj) {
list.add(o);
}
return list;
}
return null;
}

}

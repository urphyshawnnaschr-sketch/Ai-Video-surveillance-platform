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
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.extra.compress.CompressUtil;
import cn.hutool.extra.compress.extractor.Extractor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihecode.camera.ai.dto.ApExportDTO;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.dto.ModelPredictionDTO;
import com.yihecode.camera.ai.dto.coco.*;
import com.yihecode.camera.ai.dto.xml.*;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.ap.Annotation;
import com.yihecode.camera.ai.entity.ap.AnnotationData;
import com.yihecode.camera.ai.entity.ap.ApFileDO;
import com.yihecode.camera.ai.entity.ap.Image;
import com.yihecode.camera.ai.enums.ap.ImageStatus;
import com.yihecode.camera.ai.enums.ap.ProjectStatusEnum;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.ap.*;
import com.yihecode.camera.ai.web.ap.vo.AnnotationVo;
import com.yihecode.camera.ai.web.ap.vo.CommitVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
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
import java.math.BigDecimal;
import java.util.*;
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
public class DatasetExportHandleUtil {

    @Autowired
    private ApImageService apImageService;

    @Autowired
    private ApAnnotationService apAnnotationService;

    @Autowired
    private ApExportService apExportService;

    /**
* can Support Image Format Set combine
*/
    public static final List<String> SUFFIX_LIST = new ArrayList<>();

    private ThreadPoolExecutor commonThreadPoolExecutor;

    @Autowired
    private ApFileService apFileService;

    @Autowired
    private ApProjectService apProjectService;

    private static String EXPORT_BATH_PATH = "/data/dataset/";

    private static String EXPORT = "export/";

    private static String IMAGES = "images";

    private static String ANNOTATION = "annotations";

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
    @Transactional(rollbackFor = Exception.class)
    public void exportFile(ApExportDTO apExportDTO) throws IOException {
        UUID uuid = UUID.randomUUID();
        String basePath = EXPORT_BATH_PATH + apExportDTO.getProjectId() + "/" + apExportDTO.getFileId() + "/" + EXPORT + uuid;
        try {

            //String imagePath = basePath +"/"+ IMAGES;
String annoPath = basePath +"/"+ ANNOTATION;
log.info("|annoPath {}", annoPath);
// if (!FileUtil.exist(imagePath)) {
// FileUtil.mkdir(imagePath);
//}
if (!FileUtil.exist(annoPath)) {
FileUtil.mkdir(annoPath);
}
List<Image> imageList = apImageService.getFinshAnnoList(apExportDTO.getProjectId());
//List<Image> imageList = getImageList(apExportDTO.getProjectId());
boolean exportImage = ObjectUtil.isNotNull(apExportDTO.getExportImage()) && apExportDTO.getExportImage() == 1?
true: false;
String sourcePath = DatasetImportHandleUtil.BATH_PATH + apExportDTO.getProjectId() +"/"+ apExportDTO.getFileId() +"/"
+ DatasetImportHandleUtil.UPLOAD +"/"+ DatasetImportHandleUtil.IMAGES;
log.info("sourcePath {}", sourcePath);
if (!FileUtil.exist(sourcePath)) {
// apExportDTO.setStatus(1);
// apExportService.update(apExportDTO);
// return;

FileUtil.mkdir(sourcePath);
}
if (exportImage) {
FileUtil.copy(sourcePath, basePath, true);
}

if (apExportDTO.getType() == 1) {
log.info("Export XML {}", true);
exportAnnToXML(imageList, annoPath, sourcePath);
} else if (apExportDTO.getType() == 2) {
log.info("Export JSON {}", true);
exportAnnToJSON(imageList, annoPath, sourcePath);
}
apExportDTO.setStatus(1);
if (!FileUtil.exist(annoPath)) {
FileUtil.mkdir(annoPath);
}
String path = EXPORT_BATH_PATH + apExportDTO.getProjectId() +"/"+ apExportDTO.getFileId() +"/"+ EXPORT + uuid +"_"+ apExportDTO.getProjectName() +".zip";
log.info("Export zip {}", path);
ZipUtil.zip(basePath, path);
apExportDTO.setStatus(1);
apExportDTO.setStoragePath(path);
apExportService.update(apExportDTO);
}finally {
FileUtil.del(basePath);
}

}

private List<Image> getImageList(Long projectId) {
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId);
return apImageService.list(queryWrapper);
}

private void exportAnnToXML(List<Image> imageList, String annoPath, String sourcePath) throws IOException {
for (int i = 0; i < imageList.size(); i++) {
Image image = imageList.get(i);
if (ObjectUtil.isNull(image.getCommitId())) {
continue;
}
List<Annotation> imageAnnoList = getImageAnnoList(image.getCommitId());

if (CollectionUtil.isEmpty(imageAnnoList)) {
continue;
}
// Image Path
String storagePath = image.getStoragePath();
// Image File Name
String fileName = StringUtils.substringAfter(storagePath, sourcePath +"/");
// Annotation File Name Name
String annNamePre = StringUtils.substringBeforeLast(fileName,".");
// Annotation File all Path
String annFullPath = annoPath +"/"+ annNamePre +".xml";

RequestXml requestXml = new RequestXml();
requestXml.setFolder("images");
requestXml.setFileName(fileName);
Source source = new Source();
source.setDatabase("Unknown");
Size size = new Size();
size.setWidth(image.getWidth());
size.setHeight(image.getHeight());
buildXmlObject(requestXml, imageAnnoList);
String xml = XmlUtil.objectToXml(requestXml);
FileUtil.writeString(xml, annFullPath, CharEncoding.UTF_8);
}
}

private void buildXmlObject(RequestXml requestXml, List<Annotation> imageAnnoList) {
List<AnnoObject> annoObjects = imageAnnoList.stream().map(annotation -> {
AnnoObject annoObject = new AnnoObject();
annoObject.setName(annotation.getTagName());
annoObject.setPose("Unspecified");
BndBox bndBox = new BndBox();
AnnotationData annotationData = annotation.getAnnotation();
List<Float> box = annotationData.getBox();
if(box == null || box.size()!= 4) {
bndBox.setXmin(0);
bndBox.setYmin(0);
bndBox.setXmax(0);
bndBox.setYmax(0);
} else {
bndBox.setXmin(box.get(0));
bndBox.setYmin(box.get(1));
bndBox.setXmax(box.get(2));
bndBox.setYmax(box.get(3));
}
annoObject.setBndbox(bndBox);
return annoObject;
}).collect(Collectors.toList());
requestXml.setObject(annoObjects);
}

private void exportAnnToJSON(List<Image> imageList, String annoPath, String sourcePath) throws IOException {

Coco coco = new Coco();
DeatilInfo deatilInfo = new DeatilInfo();
deatilInfo.setContributor("LabelFree");
deatilInfo.setDate_created(DateUtil.formatDateTime(new Date()));
deatilInfo.setVersion("1.0");
deatilInfo.setYear(DateUtil.year(new Date()));
coco.setInfo(deatilInfo);
List<CocoImage> images = new ArrayList<>();
// Label
List<CocoCategorie> cocoCategories = new ArrayList<>();
Map<String, Integer> cateMap = new HashMap<>();
List<CocoAnnotation> annotations = new ArrayList<>();
for (int i = 0; i < imageList.size(); i++) {
Image image = imageList.get(i);

if (ObjectUtil.isNull(image.getCommitId())) {
continue;
}
List<Annotation> imageAnnoList = getImageAnnoList(image.getCommitId());

if (CollectionUtil.isEmpty(imageAnnoList)) {
continue;
}
// Image Path
String storagePath = image.getStoragePath();
// Image File Name
String fileName = StringUtils.substringAfter(storagePath, sourcePath +"/");
CocoImage cocoImage = buildCocoImage(image, fileName);
images.add(cocoImage);
annotations.addAll(buildCocoAnnotation(image, imageAnnoList,cocoCategories,cateMap));
}
coco.setImages(images);
coco.setAnnotations(annotations);
coco.setCategories(cocoCategories);
// Annotation File all Path
String annFullPath = annoPath +"/"+"stuff_images.json";
FileUtil.writeString(JSONObject.toJSONString(coco), annFullPath, CharEncoding.UTF_8);
}

private CocoImage buildCocoImage(Image image, String fileName) {
CocoImage cocoImage = new CocoImage();
cocoImage.setId(image.getId());
cocoImage.setHeight(image.getHeight());
cocoImage.setWidth(image.getWidth());
cocoImage.setFile_name(fileName);
return cocoImage;
}

private List<CocoAnnotation> buildCocoAnnotation(Image image,
List<Annotation> imageAnnoList,List<CocoCategorie> cocoCategories,
Map<String, Integer> cateMap) {
return imageAnnoList.stream().map(annotation -> {
CocoAnnotation cocoAnnotation = new CocoAnnotation();
cocoAnnotation.setId(annotation.getId());
cocoAnnotation.setImage_id(image.getId());
cocoAnnotation.setIscrowd(0);
cocoAnnotation.setShape("box");
if(!cateMap.containsKey(annotation.getTagName())){
CocoCategorie cocoCategorie = new CocoCategorie();
int size = cocoCategories.size();
cocoCategorie.setId(size+1);
cocoCategorie.setName(annotation.getTagName());
cocoCategorie.setSupercategory(StringUtils.substringBefore(annotation.getTagName(),":"));
cocoCategories.add(cocoCategorie);
cateMap.put(annotation.getTagName(),size+1);
}
cocoAnnotation.setCategory_id(cateMap.get(annotation.getTagName()));

AnnotationData annotationData = annotation.getAnnotation();
List<Float> box = annotationData.getBox();

cocoAnnotation.setBbox(box);
return cocoAnnotation;
}).collect(Collectors.toList());

}

private List<Annotation> getImageAnnoList(Long commit) {
LambdaQueryWrapper<Annotation> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Annotation::getCommitId, commit);
return apAnnotationService.list(queryWrapper);
}

public static void main(String[] args) {
System.out.println(StringUtils.substringBefore("AA",":"));
//ZipUtil.zip("E:\\data\\dataset\\1682283940146008090\\1\\export\\4d70b4c2-4b25-4fa7-ba4a-e9173ddaeb25","E:\\data\\dataset\\1682283940146008090\\1\\export\\1.zip");
//FileUtil.copy("E:\\data\\dataset\\1682283940146008074\\1\\upload\\annotations","E:\\data\\dataset\\1682283940146008074\\1\\upload\\annotations1", true);
}

}

package com.yihecode.camera.ai.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.netty.data.UpgradeRequest;
import com.yihecode.camera.ai.netty.data.UpgradeResponse;
import com.yihecode.camera.ai.oss.OssUtils;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.dto.*;
import com.yihecode.camera.ai.web.vo.AlarmCollectAlgoVo;
import com.yihecode.camera.ai.web.vo.AlarmCollectVo;
import com.yihecode.camera.ai.web.vo.AlgorithmPushConfigModifyVO;
import com.yihecode.camera.ai.web.vo.AlgorithmPushConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
* Algorithm Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Api(tags = "Algorithm Management")
@SaCheckLogin
@Controller
@RequestMapping({ "/algorithm" })
public class AlgorithmController {

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private AlarmLevelService alarmLevelService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private AlgorithmFileService algorithmFileService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AlgorithmTaskService algorithmTaskService;

    @Autowired
    private AlgorithmAlarmLevelService algorithmAlarmLevelService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApDepartService apDepartService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AlgorithmBoxService algorithmBoxService;

    @Autowired
    private BoxVersionService boxVersionService;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    @Autowired
    private ProjectConfig projectConfig;

    @Value("${dataModelsDir}")
    public String MODEL_DIR;

    @Value("${modelDir}")
    public String MODEL_FILE_DIR;

    @Value("${proj-confs.oss-net:true}")
    public boolean ossNet;

    private final Tika tika = new Tika();
    private Long id;
    private String name;
    private String nameEn;
    private String platform;
    private Integer shareMode;
    private MultipartFile imageFile;
    private Long alarmLevelId;
    private String extras;

    /**
* Query All Algorithm
* @return
*/
    @SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
    @PostMapping("listAll")
    @ResponseBody
    public JsonResult<?> listAll() {
        //Query Algorithm List, and a few Algorithm Field
List<Algorithm> algorithms = algorithmService.listLess();
return JsonResultUtils.success(algorithms);
}

@ApiOperation(value ="Query Data List")
@ApiImplicitParams({
@ApiImplicitParam(name ="tagId", dataTypeClass = Long.class, value ="Label ID"),
@ApiImplicitParam(name ="name", dataTypeClass = String.class, value ="Name"),
@ApiImplicitParam(name ="hasLocalFile", dataTypeClass = String.class, value ="Exist Local File,1- Exist")
})
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@PostMapping({"/listData"})
@ResponseBody
public PageResult<List<Algorithm>> listData(Long tagId, String name, String hasLocalFile, @RequestHeader("Lang") String language) {
List<Algorithm> algorithmList = this.algorithmService.getByTagAndNameLike(tagId, name);
if(algorithmList ==null || algorithmList.isEmpty()) {
return PageResultUtils.success(null, new ArrayList<>());
}

// Query Alert Level List
Map<Long, AlarmLevel> alarmLevelMap = alarmLevelService.getDataMap();

//
// Map<String, List<String>> ossResult = OssUtils.getOssFiles(platform, ossNet);
// List<String> ossNames = ossResult.get("names");
// List<String> ossFiles = ossResult.get("files");
for (Algorithm algorithm: algorithmList) {
// Query For not same customer account Config Alert Level
algorithm.setAlarmLevelId(algorithmAlarmLevelService.getLevelIdByAlgorithmId(algorithm.getId()));
algorithm.setAlarmLevel(alarmLevelMap.get(algorithm.getAlarmLevelId()));
algorithm.setHasLocalFile(false);
algorithm.setHasGitFile(true);
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
algorithm.setMarks(algorithm.getDetail());
algorithm.setName(algorithm.getEnglishName());
}
// check find Local Algorithm File
List<String> platformList = projectConfig.getPlatform();
if(platformList!= null) {
for(String platform: platformList) {
String path = FileUtils.pathTo(MODEL_DIR + File.separator + platform + File.separator + algorithm.getNameEn());
if(FileUtil.exist(path)) {
List<String> files = FileUtils.listFiles(path, Collections.singletonList("zip"));
if(!files.isEmpty()) {
algorithm.setHasLocalFile(true);
break;
}
}
}
}

/**

// Whether Exist Local File
String path = FileUtils.pathTo(MODEL_DIR + File.separator + algorithm.getPlatform() + File.separator + algorithm.getNameEn());
List<String> filepaths = FileUtils.listFiles(path, algorithm.getNameEn(), Collections.singletonList("zip"));
algorithm.setHasLocalFile(!filepaths.isEmpty());

// oss Whether Exist Algorithm File (Here not Strict, only Detection to Directory)
algorithm.setHasGitFile(ossNames.contains(algorithm.getNameEn()));

//
List<CameraAlgorithm> caList = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
int algorithmTask = algorithmTaskService.getByFilePath(path);
if (algorithmTask > 0) {
algorithm.setDownloadState("Download in");
} else {
if(caList == null || caList.isEmpty()) {
algorithm.setDownloadState(algorithm.getHasLocalFile()?"Download not Enabled":"not Download");
} else {
algorithm.setDownloadState("Enabled");
}
}

// Get Algorithm Local most new Version, according according Version No in Line Determine
String localVersion ="0.0";
if(!filepaths.isEmpty()) {
for(String filepath: filepaths) {
String v = FileUtils.getVersion(filepath);
if (v!= null && Double.parseDouble(localVersion) < Double.parseDouble(v)) {
localVersion = v;
}
}
}

// Whether has Update
algorithm.setHasUpdate(false);
if(caList!= null) {
// find out OSS up most big Version No
List<String> filterFiles = ossFiles.stream().filter(v -> v.contains(algorithm.getNameEn())).collect(Collectors.toList());
double ossMax = 0;
for(String filterFile: filterFiles) {
String v = FileUtils.getVersion(filterFile);
if(v!= null) {
double v1 = Double.parseDouble(v);
if(ossMax < v1) {
ossMax = v1;
}
}
}
// Local Relate Camera most big Version No
double localMax = 0;
if(!filepaths.isEmpty()) {
for(String filepath: filepaths) {
String v = FileUtils.getVersion(filepath);
if(StrUtil.isNotBlank(v)) {
double v1 = Double.parseDouble(v);
if(localMax < v1) {
localMax = v1;
}
}
}
}
// Determine Whether has Update
if(localMax > 0 && ossMax > localMax) {
algorithm.setHasUpdate(true);
}
}
*/
}
return PageResultUtils.success(null, algorithmList);
}

@ApiOperation(value ="Delete Algorithm")
@ApiImplicitParam(name ="id", value ="Algorithm ID", dataType ="long", required = true)
@SaCheckPermission(value = {"algorithm-delete"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult<?> delete(Long id) {
Algorithm algorithm = algorithmService.getById(id);
if(algorithm == null) {
return JsonResultUtils.fail("Algorithm Card does not exist");
}

// Query Algorithm Relate Camera
List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByAlgorithm(id);
if(cameraAlgorithms!= null &&!cameraAlgorithms.isEmpty()) {
return JsonResultUtils.fail("Algorithm via In make Use in, not can Delete Card");
}

// Delete Algorithm File
List<String> platformList = projectConfig.getPlatform();
if(platformList!= null) {
// Delete Algorithm File
for(String platform: platformList) {
String path = FileUtils.pathTo(MODEL_DIR + File.separator + platform + File.separator + algorithm.getNameEn());
if(FileUtil.exist(path)) {
FileUtil.del(path);
}
}
}

// Delete Version
boxVersionService.deleteByAlgo(algorithm.getId());

// Delete Algorithm File Record
algorithmFileService.removeByNameEn(algorithm.getNameEn());

// Delete Card
algorithmService.removeById(id);

// Delete Algorithm Alert Level Record
algorithmAlarmLevelService.removeByAlgorithmId(id);

// increase Add Process Task
algorithmBoxService.saveData(algorithm, 0);

return JsonResultUtils.success();
}

/**
* Query Algorithm Detail
*
* @param id
* @return
*/
@ApiOperation(value ="Query Algorithm Detail")
@ApiImplicitParam(name ="id", value ="Algorithm id")
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@PostMapping({"/detail"})
@ResponseBody
public JsonResult<Algorithm> detail(Long id, @RequestHeader("Lang") String language) {
Algorithm algorithm = algorithmService.getById(id);
if (algorithm == null) {
return JsonResultUtils.fail("Algorithm Card find not to or Deleted");
}
algorithm.setAlarmLevelId(algorithmAlarmLevelService.getLevelIdByAlgorithmId(algorithm.getId()));

// By Resource Package Get Config Info, Local Development Need Manual Decompress to Corresponding Directory
algorithm.setDetailMap(getDetailMap(algorithm.getNameEn()));

// Box Chip Type
algorithm.setPlatforms(projectConfig.getPlatform());

// inner net / outer net Mode
algorithm.setOssNet(projectConfig.isOssNet());
// if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
// algorithm.setMarks(algorithm.getDetail());
//}
return JsonResultUtils.success(algorithm);
}

/**
* Read Resource Package Image File
* @param path
* @return
*/
private List<String> readAssetImage(String path) {
List<String> images = new ArrayList<>();
if(FileUtil.exist(path)) {
File[] files = new File(path).listFiles();
if (files!= null) {
String modelDir = MODEL_DIR.replace("\\","/");
for (File file: files) {
String filename = file.getName().toLowerCase();
if (!filename.startsWith("._") && (filename.endsWith("jpg") || filename.endsWith("jpeg") || filename.endsWith("png"))) {
String filepath = file.getAbsolutePath().replace("\\","/");
if (filepath.contains(modelDir)) {
filepath ="/"+ filepath.substring(modelDir.length());
}
images.add(filepath);
}
}
Collections.sort(images);
}
}
return images;
}

/**
* Read Resource Package Text
* @param path
* @return
*/
private String readAssetText(String path) {
if (FileUtil.exist(path)) {
return FileUtil.readString(path, StandardCharsets.UTF_8);
}
return"";
}

/**
* Get Resource Package Config Info, Local Development Need Manual Decompress to Corresponding Directory
* @param nameEn
* @return
*/
private Map<String, Object> getDetailMap(String nameEn) {
Map<String, Object> detailMap = new HashMap<>();
try {
String rootPath = MODEL_DIR +"detail/"+ nameEn;
// Valid Image Directory
String truePath = rootPath + File.separator +"true";

List<String> trueImages = readAssetImage(truePath);
if (trueImages.isEmpty()) {
detailMap.put("trueText","Resource Package not find to, Please Confirm Whether via Correct Install Resource Package");
return detailMap;
}
detailMap.put("trueImage", trueImages);

// Valid Text
String trueText = truePath + File.separator +"text.txt";
detailMap.put("trueText", readAssetText(trueText));

// Invalid Image Directory
String falsePath = rootPath + File.separator +"false";
List<String> falseImages = readAssetImage(falsePath);
detailMap.put("falseImage", falseImages);

// Invalid Text
String falseText = falsePath + File.separator +"text.txt";
detailMap.put("falseText", readAssetText(falseText));

// Basic Accuracy
String basePrecisionPath = rootPath + File.separator +"basePrecision.txt";
detailMap.put("basePrecision", readAssetText(basePrecisionPath));

// Scene scene Optimize Accuracy
String scenePrecisionPath = rootPath + File.separator +"scenePrecision.txt";
if (FileUtil.exist(scenePrecisionPath)) {
detailMap.put("scenePrecision", FileUtil.readString(scenePrecisionPath, StandardCharsets.UTF_8));
}
} catch (Exception e) {
detailMap.put("trueText","Resource Package Read Error");
}
return detailMap;
}

@ApiOperation("Image show show")
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@GetMapping("/picStream")
public void picStream(String file, HttpServletResponse response) {
try {
// File Is Empty
if(StrUtil.isBlank(file)) {
return;
}

String filename = FileUtil.getName(file);
if(StrUtil.isBlank(filename)) {
return;
}

// File Type
String ext = FileUtil.extName(filename);
if(StrUtil.isBlank(ext) ||!(ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png"))) {
return;
}

//
BufferedInputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(FileUtils.pathTo(MODEL_DIR +"/cover_image/"+ filename))));
response.setContentType("image/jpeg");
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
// e.printStackTrace();
}
}

/**
* Check Algorithm English Name Whether Exist, Exist Back true
*
* @author Abyss
* @date 2024/1/16 14:43
* @param id
* @param nameEn
* @return com.yihecode.camera.ai.utils.JsonResult
*/
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@RequestMapping({"/checkNameEn"})
@ResponseBody
public JsonResult checkNameEn(Long id, String nameEn) {
// Detection English Name not can re reply (Self Divide outer, Delete Divide outer)
List<Algorithm> listNameEn = algorithmService.listNameEn(nameEn);
Boolean isExist = false;
if (!listNameEn.isEmpty()) {
for (Algorithm algorithm1: listNameEn) {
if (!algorithm1.getId().equals(id)
&& algorithm1.getNameEn().equals(nameEn)) {
isExist = true;
break;
}
}
}
return JsonResultUtils.success(isExist);
}


@ApiOperation(value ="Save Algorithm")
@ApiImplicitParam(name ="algorithm", value ="Algorithm Entity Param")
@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@RequestMapping({"/save"})
@ResponseBody
public JsonResult<Void> save(Long id, String name, String nameEn, String platform, Integer shareMode, MultipartFile imageFile, Long alarmLevelId, String extras, String englishName, String marks, String detail) {
if (id == null) {
return JsonResultUtils.fail("not Select Algorithm");
}
if (StrUtil.isBlank(name)) {
return JsonResultUtils.fail("Please enter Algorithm Name");
}
if (StrUtil.isBlank(nameEn)) {
return JsonResultUtils.fail("Please enter Algorithm Code");
}
if (StrUtil.isBlank(platform)) {
return JsonResultUtils.fail("Please select Hardware Platform");
}
if(shareMode == null) {
return JsonResultUtils.fail("Please select Share Type");
}
//if(StringUtils.isBlank(englishName)) {
// return JsonResultUtils.fail("Please enter Algorithm English Name");
//}

Algorithm algorithm = algorithmService.getById(id);
if(algorithm == null) {
return JsonResultUtils.fail("Algorithm Card not find to or Deleted");
}

AlgorithmBox algorithmBox = algorithmBoxService.getByAlgoAndTypeAndState(algorithm.getId(), 1, 1);
if(algorithmBox!= null) {
return JsonResultUtils.fail("Expand Param In Progress Update in, Please Later Again again Try");
}

// Image Storage
if(imageFile!= null) {
try {
String dir = FileUtils.pathTo(MODEL_DIR +"/cover_image");
if(!FileUtil.exist(dir)) {
FileUtil.mkdir(dir);
}
String dest ="/cover_image/"+ nameEn +".png";
String path = FileUtils.pathTo(MODEL_DIR +"/"+ dest);
imageFile.transferTo(new File(path));

// Set Image Path
algorithm.setImage(dest);
} catch (Exception e) {
return JsonResultUtils.fail("Card Image Save Exception");
}
}

// Whether Need Notification Box in Line change more Process
boolean isChange = false;

// Determine Share Mode Whether change more
if(algorithm.getShareMode() == null ||!algorithm.getShareMode().equals(shareMode)) {
algorithm.setShareMode(shareMode);
isChange = true;
}

// Old Expand Param
Map<String, String> oldMap = new HashMap<>();
if(StrUtil.isNotBlank(algorithm.getExtras())) {
try {
JSONArray array = JSONArray.parseArray(algorithm.getExtras());
int len = array.size();
for(int i = 0; i < len; i++) {
JSONObject object = array.getJSONObject(i);
if(!object.containsKey("name") || StrUtil.isBlank(object.getString("name"))) {
continue;
}
oldMap.put(object.getString("name"), object.getString("value"));
}
} catch (Exception e) {
//
}
}

// new Expand Param
Map<String, String> newMap = new HashMap<>();
if(StrUtil.isNotBlank(extras)) {
try {
JSONArray array = JSONArray.parseArray(extras);
int len = array.size();
for(int i = 0; i < len; i++) {
JSONObject object = array.getJSONObject(i);
if(!object.containsKey("name") || StrUtil.isBlank(object.getString("name"))) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing name Field or not Config Value");
}
if(!object.containsKey("value") || StrUtil.isBlank(object.getString("value"))) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing value Field or not Config Value");
}
if(!object.containsKey("label") || StrUtil.isBlank(object.getString("label"))) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing label Field or not Config Value");
}
if(!object.containsKey("type") || StrUtil.isBlank(object.getString("type"))) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing type Field or not Config Value");
}
if(!object.containsKey("element") || StrUtil.isBlank(object.getString("element"))) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing element Field or not Config Value");
}
newMap.put(object.getString("name"), object.getString("value"));
}
} catch (Exception e) {
return JsonResultUtils.fail("Expand Param Format Error, Please Check Whether by need Request Fill");
}
}

// Determine Expand Param Whether change more
if(oldMap.size()!= newMap.size()) {
algorithm.setExtras(extras);
isChange = true;
} else {
for (String key: newMap.keySet()) {
// Determine key Value Whether Change
if (!oldMap.containsKey(key)) {
algorithm.setExtras(extras);
isChange = true;
break;
}

// Determine Value Whether Change
String newVal = newMap.getOrDefault(key,"");
String oldVal = oldMap.getOrDefault(key,"");
if (!newVal.equals(oldVal)) {
algorithm.setExtras(extras);
isChange = true;
break;
}
}
}

// supplement Charge Other Param
algorithm.setUpdatedAt(new Date());
algorithm.setName(name);
algorithm.setMarks(marks);
algorithm.setEnglishName(englishName);
algorithm.setDetail(detail);
algorithm.setId(id);
algorithmService.updateById(algorithm);

// Save Alert Level
if(alarmLevelId!= null) {
algorithmAlarmLevelService.save(id, alarmLevelId);
}

// increase Add Process Task
if(isChange) {
algorithmBoxService.deleteData(algorithm.getId(), 1);
algorithmBoxService.saveData(algorithm, 1);
}

return JsonResultUtils.success();
}

@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@PostMapping({"/saveOrUpdate"})
@ResponseBody
public JsonResult<?> saveOrUpdate(Long id, String name, String nameEn, String platform, String params, String marks, MultipartFile imageFile, Integer shareMode, String extras) throws IOException {
if (StrUtil.isBlank(name)) {
return JsonResultUtils.fail("Please enter Algorithm Name");
}

// Replace. / \
nameEn = nameEn.replace(".","").replace("/","").replace("\\","");

if (StrUtil.isBlank(nameEn)) {
return JsonResultUtils.fail("Please enter Algorithm Code");
}

// Image Storage
String dest ="/cover_image/"+ nameEn +".png";
if(imageFile!= null) {
// MIME Type Check
String detectedMimeType = tika.detect(imageFile.getBytes());
if (!Objects.equals(detectedMimeType,"image/jpeg") &&!Objects.equals(detectedMimeType,"image/png")) {
return JsonResultUtils.fail("Image Format Error");
}

String originalFileName = imageFile.getOriginalFilename();
if(StrUtil.isBlank(originalFileName)) {
return JsonResultUtils.fail("Image Name Error");
}

// Expand Format Name Check
String ext = FileUtil.extName(originalFileName);
if(StrUtil.isBlank(ext) ||!("jpg".equalsIgnoreCase(ext) ||"jpeg".equalsIgnoreCase(ext) ||"png".equalsIgnoreCase(ext))) {
return JsonResultUtils.fail("Image Format Error");
}

try {
String dir = FileUtils.pathTo(MODEL_DIR +"/cover_image");
if(!FileUtil.exist(dir)) {
FileUtil.mkdir(dir);
}
String path = FileUtils.pathTo(MODEL_DIR +"/"+ dest);
imageFile.transferTo(new File(path));
} catch (Exception e) {
//e.printStackTrace();
return JsonResultUtils.fail("Image Save Exception");
}
}

// Add
if(id == null) {
if (shareMode == null) {
return JsonResultUtils.fail("Please select Share Type");
}

List<Algorithm> algorithms = algorithmService.listNameEn(nameEn);
if(algorithms!= null &&!algorithms.isEmpty()) {
return JsonResultUtils.fail("Algorithm Code Exist, Please make Use Other Code");
}

if(StrUtil.isNotBlank(extras)) {
try {
JSONArray newJsonArray = JSONObject.parseArray(extras);
// Detection Param Whether all has
int aLen = newJsonArray.size();
for(int j = 0; j < aLen; j++) {
JSONObject newJson = newJsonArray.getJSONObject(j);
if(!newJson.containsKey("label")) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing label Field");
}
if(StrUtil.isBlank(newJson.getString("label"))) {
return JsonResultUtils.fail("Expand Param Format Error,label Field Value lack Missing");
}
if(!newJson.containsKey("name")) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing name Field");
}
if(StrUtil.isBlank(newJson.getString("name"))) {
return JsonResultUtils.fail("Expand Param Format Error,name Field Value lack Missing");
}
if(!newJson.containsKey("value")) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing value Field");
}
if(StrUtil.isBlank(newJson.getString("value"))) {
return JsonResultUtils.fail("Expand Param Format Error,value Field Value lack Missing");
}
if(!newJson.containsKey("type")) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing type Field");
}
if(StrUtil.isBlank(newJson.getString("type"))) {
return JsonResultUtils.fail("Expand Param Format Error,type Field Value lack Missing");
}
if(!newJson.containsKey("element")) {
return JsonResultUtils.fail("Expand Param Format Error, lack Missing element Field");
}
if(StrUtil.isBlank(newJson.getString("element"))) {
return JsonResultUtils.fail("Expand Param Format Error,element Field Value lack Missing");
}

String type = newJson.getString("type");
if("number".equalsIgnoreCase(type)) {
String value = newJson.getString("value");
if(StrUtil.isBlank(value)) {
String label = newJson.getString("label");
return JsonResultUtils.fail("Expand Param -"+ label +"must input in Value");
} else {
boolean isNumber = NumberUtil.isNumber(value);
if(!isNumber) {
String label = newJson.getString("label");
return JsonResultUtils.fail("Expand Param -"+ label +"must for Number Value");
}
}
}
}
} catch (Exception e) {
return JsonResultUtils.fail("Expand Param result structure Error");
}
}

Algorithm algorithm = new Algorithm();
algorithm.setId(id);
algorithm.setName(name);
algorithm.setNameEn(nameEn);
algorithm.setPlatform("pt");
algorithm.setParams(params);
algorithm.setImageFile(imageFile);
algorithm.setMarks(marks);
algorithm.setShareMode(shareMode);
algorithm.setImage(imageFile == null?"": dest);
algorithm.setModelPath(FileUtils.pathTo(MODEL_DIR + nameEn));
algorithm.setCreatedAt(new Date());
algorithm.setUpdatedAt(new Date());
algorithm.setFrequency(1000);
algorithm.setIntervalTime(100);
algorithm.setStaticsFlag(0); // not Relate Display deng
algorithm.setExtras(extras); // Expand Param
algorithmService.save(algorithm);
return JsonResultUtils.success();
}

// Modify
Algorithm modifyAlgorithm = new Algorithm();
modifyAlgorithm.setId(id);
modifyAlgorithm.setName(name);
modifyAlgorithm.setImage(imageFile == null?"": dest);
modifyAlgorithm.setUpdatedAt(new Date());
algorithmService.updateById(modifyAlgorithm);
return JsonResultUtils.success();
}

@ApiOperation(value ="Manual Upload Algorithm Complete whole Package")
@ApiImplicitParams({
@ApiImplicitParam(name ="file", value ="Algorithm Complete whole Package, Format:[Platform Type]-[Algorithm Code]-[Version No]- Complete whole Package.zip"),
@ApiImplicitParam(name ="name", value ="Algorithm Name")
})
@SaCheckPermission(value = {"algorithm-import"}, mode = SaMode.OR)
@PostMapping({"/uploadZipSave"})
@ResponseBody
public JsonResult<?> uploadZipSave(@RequestParam(value ="file", required = false) MultipartFile file, String name) {
// xxx.zip
// - /card/card-describe.txt Algorithm Description
// - /card/detial-describe.txt Algorithm Detail Content
// - /card/[Algorithm Code].png Algorithm Cover image
// - detail
// - detail/...
if (StrUtil.isBlank(name)) {
return JsonResultUtils.fail("Please enter Algorithm Name");
}

if(file == null) {
return JsonResultUtils.fail("not has Algorithm Package File, Please Upload");
}

String filename = file.getOriginalFilename();
String mainName = FileUtil.mainName(filename);
String extName = FileUtil.extName(filename);
if(StrUtil.isBlank(filename) || StrUtil.isBlank(mainName) || StrUtil.isBlank(extName) ||!extName.equalsIgnoreCase("zip")) {
return JsonResultUtils.fail("Only Support zip Compress Package");
}

if(!mainName.contains("Complete whole Package")) {
return JsonResultUtils.fail("Algorithm Compress Package File Name Format Error, Scope Example:[Platform Type]-[Algorithm Code]-[Version No]- Complete whole Package.zip");
}

String[] parts = mainName.split("-");
if(parts.length!= 4) {
return JsonResultUtils.fail("Algorithm Compress Package File Name Format Error, Scope Example:[Platform Type]-[Algorithm Code]-[Version No]- Complete whole Package.zip");
}

String platform = parts[0];
String nameEn = parts[1];
String version = parts[2];
String append = parts[3];
if(StrUtil.isBlank(platform) || StrUtil.isBlank(nameEn) || StrUtil.isBlank(version) || StrUtil.isBlank(append)) {
return JsonResultUtils.fail("Algorithm Compress Package File Name Format Error, Scope Example:[Platform Type]-[Algorithm Code]-[Version No]- Complete whole Package.zip");
}

try {
Double.parseDouble(version);
} catch (Exception e) {
return JsonResultUtils.fail("Version No Error, Scope Example:1.0");
}

try {
// Validate Algorithm Card Whether already exists
if (null!= algorithmService.getByNameEn(nameEn)) {
return JsonResultUtils.success("Algorithm Card Exist, not can re reply Create");
}
// Query Default Card, Determine
List<Algorithm> algorithms = algorithmService.list();
if(algorithms!= null &&!algorithms.isEmpty()) {
Algorithm algorithm = algorithms.get(0);
if(StrUtil.isNotBlank(algorithm.getPlatform()) &&!platform.equals(algorithm.getPlatform())) {
return JsonResultUtils.fail("Platform Type Only Support ["+ algorithm.getPlatform() +"]");
}
}

// Save File
String path = FileUtils.pathTo(MODEL_DIR + File.separator + file.getOriginalFilename());
file.transferTo(new File(path));

// Decompress File
String unZipPath = FileUtils.pathTo(MODEL_DIR + File.separator + mainName + File.separator);
ZipUtils.unzip(path, unZipPath);

// Algorithm Card Description
String describeTxt ="";
String describeTxtFile = unZipPath +"/card/card-describe.txt";
if (FileUtil.exist(describeTxtFile)) {
describeTxt = FileUtil.readString(describeTxtFile, StandardCharsets.UTF_8);
}

// Algorithm Card Description
String descriptionTxt ="";
String descriptionTxtFile = unZipPath +"/card/detial-describe.txt";
if (FileUtil.exist(descriptionTxtFile)) {
descriptionTxt = FileUtil.readString(descriptionTxtFile, StandardCharsets.UTF_8);
}

// Cover Image File Move
String imageFile = unZipPath +"/card/"+ nameEn +".png";
if (FileUtil.exist(imageFile)) {
String destPath = FileUtils.pathTo(MODEL_DIR +"/cover_image");
if(!FileUtil.exist(destPath)) {
FileUtil.mkdir(destPath);
}
FileUtil.move(new File(imageFile), new File(destPath), true);
}

// Detail File Move
String detailPath = unZipPath +"/detail/";
if (FileUtil.exist(detailPath)) {
String destPath = FileUtils.pathTo(MODEL_DIR +"detail/"+ nameEn);
if(!FileUtil.exist(destPath)) {
FileUtil.mkdir(destPath);
}
File[] detailFiles = new File(detailPath).listFiles();
if(detailFiles!= null) {
for(File detailFile: detailFiles) {
FileUtil.move(detailFile, new File(destPath), true);
}
}
}

// Test File Move
String modelTestPath = unZipPath +"/model_test";
if (FileUtil.exist(modelTestPath)) {
String destPath = FileUtils.pathTo(MODEL_FILE_DIR +"model_test/"+ nameEn);
if(!FileUtil.exist(destPath)) {
FileUtil.mkdir(destPath);
}
FileUtil.move(new File(modelTestPath), new File(destPath), true);
}

// Algorithm File Move
String fileListPath = unZipPath +"/gitlab";
if(!FileUtil.exist(fileListPath)) {
return JsonResultUtils.fail("find not to Algorithm Model Phase close File");
}

File[] fileList = new File(fileListPath).listFiles();
if(fileList == null || fileList.length == 0) {
return JsonResultUtils.fail("find not to Algorithm Model Phase close File");
}

// Parse Algorithm Package
String fileName = null;
for(File fileObj: fileList) {
String fileMainName = FileUtil.mainName(fileObj);
String fileExtName = FileUtil.extName(fileObj);
if(StrUtil.isBlank(fileMainName) || StrUtil.isBlank(fileExtName)) {
continue;
}
if(fileExtName.equalsIgnoreCase("zip")) {
String[] sParts = fileMainName.split("-");
if(sParts.length!= 3) {
continue;
}
String sPlatform = sParts[0];
String sNameEn = sParts[1];
String sVersion = sParts[2];
if(sPlatform.equals(platform) && sNameEn.equals(nameEn) && sVersion.equals(version)) {
String destPath = FileUtils.pathTo(MODEL_DIR + sPlatform + File.separator + sNameEn);
if(!FileUtil.exist(destPath)) {
FileUtil.mkdir(destPath);
}

FileUtil.move(fileObj, new File(destPath), true);
fileName = fileObj.getName();

break;
}
}
}

// find not to Algorithm Package
if(StrUtil.isBlank(fileName)) {
return JsonResultUtils.fail("find not to Algorithm Model Phase close File");
}

// Save Algorithm table
Algorithm algorithm = new Algorithm();
algorithm.setFrequency(1000);
algorithm.setIntervalTime(100);
algorithm.setStaticsFlag(0);
algorithm.setName(name);
algorithm.setNameEn(nameEn);
algorithm.setModelPath(FileUtils.pathTo(MODEL_DIR + nameEn));
algorithm.setPlatform(platform);
algorithm.setImage("/cover_image/"+ nameEn +".png");
algorithm.setCreatedAt(new Date());
algorithm.setUpdatedAt(new Date());
algorithm.setPushEnable(0);
algorithm.setMarks(describeTxt);
algorithm.setDescription(descriptionTxt);
algorithm.setShareMode(0);
algorithmService.save(algorithm);

// Save Algorithm File
AlgorithmFile algorithmFile = new AlgorithmFile();
algorithmFile.setFileName(fileName);
algorithmFile.setNameEn(nameEn);
algorithmFile.setPlatform(platform);
algorithmFileService.save(algorithmFile);

// Delete Temp Hour File
FileUtil.del(unZipPath);

return JsonResultUtils.success();
} catch (Exception e) {
log.error("Import Complete whole Algorithm Package Exception", e);
return JsonResultUtils.fail("Import Complete whole Algorithm Package Failed");
}
}

@ApiOperation(value ="Manual Upload Algorithm Model Package")
@ApiImplicitParams({
@ApiImplicitParam(name ="file", value ="Algorithm Package"),
@ApiImplicitParam(name ="platform", value ="Platform Type"),
@ApiImplicitParam(name ="nameEn", value ="Algorithm Code"),
})
@SaCheckPermission(value = {"algorithm-alarm-voice","algorithm-import"}, mode = SaMode.OR)
@PostMapping({"/uploadAlgorithmFile"})
@ResponseBody
public JsonResult<?> uploadAlgorithmFile(@RequestParam(value ="file", required = false) MultipartFile file, String platform, String nameEn) {
try {
Algorithm algorithm = algorithmService.getByNameEn(nameEn);
if(algorithm == null) {
return JsonResultUtils.fail("Algorithm Model Card not Config");
}
if(StrUtil.isBlank(algorithm.getPlatform())) {
return JsonResultUtils.fail("Algorithm Model Corresponding Device Type not Config");
}
//
String filename = file.getOriginalFilename();
if(StrUtil.isBlank(filename)) {
return JsonResultUtils.fail("Compress Package File Name Name Format Error, Scope Example:[Platform Type]-[Algorithm Code]-[Version No].zip");
}
//
String[] parts = filename.split("-");
if(parts.length!= 3) {
return JsonResultUtils.fail("Compress Package File Name Name Format Error, Scope Example:[Platform Type]-[Algorithm Code]-[Version No].zip");
}
//
String p = parts[0];
String n = parts[1];
if (!p.equals(platform)) {
return JsonResultUtils.fail("Compress Package File Name Device Type should Config for:"+ algorithm.getPlatform() +", Current for:"+ p);
}
if (!n.equals(nameEn)) {
return JsonResultUtils.fail("Compress Package File Name Algorithm Code should Config for:"+ algorithm.getNameEn() +", Current for:"+ n);
}
String path = FileUtils.pathTo(MODEL_DIR + platform + File.separator + nameEn + File.separator);
if (!FileUtil.exist(path))
FileUtil.mkdir(path);

String filepath = path + filename;
if (FileUtil.exist(filepath))
FileUtil.del(filepath);
file.transferTo(new File(filepath));

//
AlgorithmFile algorithmFile = new AlgorithmFile();
algorithmFile.setFileName(file.getOriginalFilename());
algorithmFile.setNameEn(nameEn);
algorithmFile.setPlatform(platform);
algorithmFileService.save(algorithmFile);
return JsonResultUtils.success();
} catch (Exception e) {
log.error("Algorithm Model Package Upload Exception: {}", e.getMessage());
return JsonResultUtils.fail("Algorithm Model Package Upload Error");
}
}

@ApiOperation(value ="Uninstall Algorithm File")
@ApiImplicitParam(name ="id", value ="Algorithm ID", dataType ="long", required = true)
@SaCheckPermission(value = {"algorithm-unload"}, mode = SaMode.OR)
@PostMapping({"/deleteFile"})
@ResponseBody
public JsonResult<?> deleteFile(Long id) {
Algorithm algorithm = algorithmService.getById(id);
if(algorithm == null) {
return JsonResultUtils.fail("Algorithm Card does not exist");
}

// Query Algorithm Relate All Camera
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
if(cameraAlgorithmList!= null &&!cameraAlgorithmList.isEmpty()) {
return JsonResultUtils.fail("Algorithm via make Use in, not can Direct connect Uninstall");
}

// Delete Algorithm File
List<String> platformList = projectConfig.getPlatform();
if(platformList!= null) {
for(String platform: platformList) {
String path = FileUtils.pathTo(MODEL_DIR + File.separator + platform + File.separator + algorithm.getNameEn());
if(FileUtil.exist(path)) {
FileUtil.del(path);
}
}
}

// Delete Version
boxVersionService.deleteByAlgo(algorithm.getId());

// Delete Algorithm File Record List
algorithmFileService.removeByNameEn(algorithm.getNameEn());

// increase Add Process Task
algorithmBoxService.saveData(algorithm, 0);

return JsonResultUtils.success();
}

@ApiOperation("Algorithm Update Get Box List and Algorithm History Version")
@SaCheckPermission(value = {"algorithm-version"}, mode = SaMode.OR)
@GetMapping("/getBoxAndHistoryVersion")
@ResponseBody
public JsonResult<?> getBoxAndHistoryVersion(Long id) {
Algorithm algorithm = algorithmService.getById(id);
if (null == algorithm) {
return JsonResultUtils.fail("Algorithm Card does not exist or Deleted");
}

Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return JsonResultUtils.fail("You not has Operation Permission");
}
//
List<Long> queryLocationIds = new ArrayList<>();
if(Integer.valueOf(1).equals(account.getIsSuper())) {// super Level Management member
List<Location> locations = locationService.listByType("2");
for(Location location: locations) {
queryLocationIds.add(location.getId());
}
} else {// Non Management member, Only Query belong belong Department down Box
List<Long> departIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
List<Long> locationIds = locationService.getLocationIdsByDeparts(departIds);
if(locationIds!= null) {
queryLocationIds.addAll(locationIds);
}
}
// not has Corresponding Box Info
if(queryLocationIds.isEmpty()) {
return JsonResultUtils.success(new HashMap<>());
}

//
Map<String, Object> resultMap = new HashMap<>();

// Query and Current Algorithm Relate Camera
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithm(id);

// Query All Camera
List<Camera> cameraList = cameraService.listData();
Map<Long, Camera> cameraMap = new HashMap<>();
for (Camera camera: cameraList) {
if(queryLocationIds.contains(camera.getLocationId())) {// In Current Box in
cameraMap.put(camera.getId(), camera);
}
}
//
List<Map<String, Object>> boxList = new ArrayList<>();
Map<Long, CameraAlgorithm> boxMap = new HashMap<>();
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
Camera camera = cameraMap.get(cameraAlgorithm.getCameraId());
if (camera == null) {
continue;
}
boxMap.put(camera.getLocationId(), cameraAlgorithm);
}
List<Location> locationList = locationService.listDataByType("2");
for (Location location: locationList) {
if (location.getIsDef()!= null && location.getIsDef() == 1) {
continue;
}

// Box not In Current User Range
if(!queryLocationIds.contains(location.getId())) {
continue;
}

Map<String, Object> map = new HashMap<>();
CameraAlgorithm cameraAlgorithm = boxMap.get(location.getId());
map.put("boxName", location.getName());
map.put("boxVersion", location.getBoxVersion());
map.put("algorithmId", id);
map.put("locationId", location.getId());
if (null!= cameraAlgorithm) {
map.put("algorithmVersion", cameraAlgorithm.getAlgorithmVersion());
if (null!= cameraAlgorithm.getBoxUpdateStatus() && cameraAlgorithm.getBoxUpdateStatus().equals(3)) {
cameraAlgorithm.setBoxUpdateStatus(0);
cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
map.put("boxUpdateStatus", 3);
} else {
map.put("boxUpdateStatus", cameraAlgorithm.getBoxUpdateStatus());
}
} else {
map.put("algorithmVersion", 0);
map.put("boxUpdateStatus", 0);
}

boxList.add(map);
}

resultMap.put("boxList", boxList);

//
List<AlgorithmFile> algorithmFiles = algorithmFileService.listByNameEn(algorithm.getNameEn());
resultMap.put("fileList", algorithmFiles);

// OSS up Algorithm Version List
List<OssFileDTO> ossFiles = OssUtils.getOssFiles(algorithm.getPlatform(), algorithm.getNameEn(), ossNet);
if(ossFiles == null) {// Network error
ossFiles = new ArrayList<>();
}

// oss and Local File than for Info
List<OssLocalFileInfoDTO> ossLocalFileInfoDTOS = new ArrayList<>();
for(OssFileDTO ossFile: ossFiles) {
String ossFileName = FileUtil.getName(ossFile.getFilename());

String md5Str ="";
long localFileLength = 0L;

AlgorithmTask algorithmTask = algorithmTaskService.getByFileName(ossFileName);
if (null!= algorithmTask) {
md5Str ="Download in";
String localPath = FileUtils.pathTo(MODEL_DIR +"/temp");
String localFilePath = localPath +"/"+ ossFileName;
File localFile = new File(localFilePath);
localFileLength = localFile.length();
} else {
String localPath = FileUtils.pathTo(MODEL_DIR +"/"+ algorithm.getPlatform() +"/"+ algorithm.getNameEn());
String localFilePath = localPath +"/"+ ossFileName;
File localFile = new File(localFilePath);
//log.info("localFile {} File Exist {}", localFile, localFile.exists());
localFileLength = localFile.length();

//log.info("{}, {}, {}", ossFileName, ossFile.getFilesize(), localFileLength);
if (localFileLength == ossFile.getFilesize()) {
md5Str ="Consistent";
} else if (localFileLength!= 0) {
md5Str ="not Consistent";
} else {
md5Str ="not Download";
}
}

OssLocalFileInfoDTO ossLocalFileInfoDTO = new OssLocalFileInfoDTO();
ossLocalFileInfoDTO.setName(ossFileName);
ossLocalFileInfoDTO.setMd5Str(md5Str);
ossLocalFileInfoDTO.setLocalLength(localFileLength);
ossLocalFileInfoDTO.setLocalSize(FileUtils.convertBytes(localFileLength));
ossLocalFileInfoDTO.setLength(ossFile.getFilesize());
ossLocalFileInfoDTO.setSize(FileUtils.convertBytes(ossFile.getFilesize()));
ossLocalFileInfoDTOS.add(ossLocalFileInfoDTO);
}

// Query History Version, Local File not In oss List?
for (AlgorithmFile algorithmFile: algorithmFiles) {
boolean flag = false;
for(OssFileDTO ossFile: ossFiles) {
String ossFileName = FileUtil.getName(ossFile.getFilename());
if(algorithmFile.getFileName().equalsIgnoreCase(ossFileName)) {
flag = true;
break;
}
}

if (flag) continue;

String localPath = FileUtils.pathTo(MODEL_DIR + algorithmFile.getPlatform() +"/"+ algorithm.getNameEn());
String localFilePath = localPath +"/"+ algorithmFile.getFileName();
File localFile = new File(localFilePath);
long localFileLength = localFile.length();

OssLocalFileInfoDTO ossLocalFileInfoDTO = new OssLocalFileInfoDTO();
ossLocalFileInfoDTO.setName(algorithmFile.getFileName());
ossLocalFileInfoDTO.setMd5Str("Consistent");
ossLocalFileInfoDTO.setLocalLength(localFileLength);
ossLocalFileInfoDTO.setLocalSize(FileUtils.convertBytes(localFileLength));
ossLocalFileInfoDTO.setLength(localFileLength);
ossLocalFileInfoDTO.setSize(FileUtils.convertBytes(localFileLength));
ossLocalFileInfoDTOS.add(ossLocalFileInfoDTO);
}

resultMap.put("fileList", ossLocalFileInfoDTOS);

// check find most new Version
double lastVersion = 0.0;
for (OssLocalFileInfoDTO ossLocalFileInfoDTO: ossLocalFileInfoDTOS) {
String mainName = FileUtil.mainName(ossLocalFileInfoDTO.getName());
if(!StrUtil.isBlank(mainName)) {
String[] parts = mainName.split("-");
if(parts.length == 3) {
String v = parts[2];
if (Double.parseDouble(v) > lastVersion) {
lastVersion = Double.parseDouble(v);
resultMap.put("lastVersionFile", ossLocalFileInfoDTO);
}
}
}
}
return JsonResultUtils.success(resultMap);
}

@ApiOperation("Box Cut change Version Task Start")
@SaCheckPermission(value = {"algorithm-version"}, mode = SaMode.OR)
@PostMapping("/changeBoxAlgorithmVersion")
@ResponseBody
public JsonResult<?> changeBoxAlgorithmVersion(String boxIds, String fileName, Long algorithmId) {
if (StrUtil.isBlank(boxIds)) {
return JsonResultUtils.fail("Please select Box");
}
if (StrUtil.isBlank(fileName)) {
return JsonResultUtils.fail("Please select Algorithm Version");
}
if (null == algorithmId) {
return JsonResultUtils.fail("Please select Algorithm");
}

// Box List
List<String> boxIdList = Arrays.asList(boxIds.split(","));
if(boxIdList.size() > 1) {
return JsonResultUtils.fail("same Hour only Allow Update One Box Device");
}

//
String boxId = boxIdList.get(0);
long _boxId = -1L;
try {
_boxId = Long.parseLong(boxId);
} catch (Exception e) {
return JsonResultUtils.fail("not has need Update Box Device, not can Direct connect Cut change Version");
}

Location location = locationService.getById(_boxId);
if(location == null) {
return JsonResultUtils.fail("not has need Update Box Device, not can Direct connect Cut change Version");
}

String[] parts = FileUtil.mainName(fileName).split("-");
if(parts.length!= 3) {
return JsonResultUtils.fail("Algorithm Package File Name Error");
}

String platform = parts[0]; //"denglin"
String nameEn = parts[1]; //"car"
String version = parts[2]; // 1.0

String localFile = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ nameEn +"/"+ fileName);
if(!FileUtil.exist(localFile)) {
String ossFilePath = platform +"/"+ nameEn +"/"+ fileName; // oss Corresponding Path
String tempFilePath = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ nameEn +"/"+ fileName); // Temp Hour File
String result = OssUtils.downloadOssFile(ossFilePath, tempFilePath);
if(StrUtil.isNotBlank(result)) {// Download failed
return JsonResultUtils.fail(result);
}
}

String fileMd5 = Md5FileUtils.getMD5(FileUtil.newFile(localFile));
String fileUrl ="/"+ platform +"/"+ nameEn +"/"+ fileName;

// Whether Offline
Long heartTime = location.getBoxHeartTime();
if(heartTime == null) {
return JsonResultUtils.fail("Box Device Offline, not can Update");
}

// Determine Whether Offline,10 min inner no Heartbeat
long timeSecs = (System.currentTimeMillis() - heartTime) / 1000;
if(timeSecs >= 10 * 60) {
return JsonResultUtils.fail("Box Device Offline, not can Update");
}

// Box Device not has Index
if(StrUtil.isBlank(location.getBoxNo())) {
return JsonResultUtils.fail("Box Device not has Index, not can Update");
}

UpgradeRequest request = new UpgradeRequest();
request.setType(MessageType.UPGRADE_ALGO.getType());
request.setRequestId(IdUtil.randomUUID());
request.setSn(location.getBoxNo());
request.setFileMd5(fileMd5);
request.setAlgoCode(nameEn);
request.setAlgoId(algorithmId);
request.setAlgoVer(version);
request.setFileName(fileName);
request.setFileUrl(fileUrl);
Response response = messageSenderAndWaiter.sendRequest(request);
if(response == null) {
return JsonResultUtils.fail("Update Algorithm Package Exception, Unknown Error");
}

UpgradeResponse upgradeResponse = (UpgradeResponse) response;
if(upgradeResponse.isStatus()) {
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithmAndBoxId(algorithmId, location.getId());
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
cameraAlgorithm.setBoxUpdateStatus(2);
cameraAlgorithm.setAlgorithmVersion(version);
cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
}
return JsonResultUtils.success("Algorithm Package Update success");
}

List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithmAndBoxId(algorithmId, location.getId());
for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
cameraAlgorithm.setBoxUpdateStatus(3);
cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
}
return JsonResultUtils.fail(upgradeResponse.getMsg());
}

/**
* Save Algorithm Alert Audio
* @author Abyss
* @date 2024/5/5 17:22
*/
@ApiOperation(value ="Storage or Update Audio File")
@SaCheckPermission(value = {"algorithm-alarm-voice"}, mode = SaMode.OR)
@PostMapping({"/uploadSoundFile"})
@ResponseBody
public JsonResult<?> uploadSoundFile(@RequestParam(value ="id",required = false) Long id,
@RequestParam(value ="oldId",required = false) Long oldId,
@RequestParam(value ="file", required = false) MultipartFile file){
try {
if(id == null) {
return JsonResultUtils.fail("Please select Relate Algorithm");
}
if(file == null) {
return JsonResultUtils.fail("Please select mp3 File");
}

String filename = file.getOriginalFilename();
String mainName = FileUtil.mainName(filename);
String extName = FileUtil.extName(filename);
if(StrUtil.isBlank(mainName) || StrUtil.isBlank(extName) ||!(extName.equalsIgnoreCase("mp3") || extName.equalsIgnoreCase("wav"))) {
return JsonResultUtils.fail("File Format Error, Please select mp3 File");
}

// Verify File Type
String mimeType = tika.detect(file.getBytes());
if (!Objects.equals(mimeType,"audio/mpeg")) {
return JsonResultUtils.fail("File Format Error, Please select mp3 File");
}

// Create Audio Storage Directory
String path = FileUtils.pathTo(MODEL_DIR +"/soundFile/");
if (!FileUtil.exist(path)) {
FileUtil.mkdir(path);
}

// Save Audio File
String soundFileName = UUID.randomUUID() +"."+ extName;
try {
file.transferTo(new File(path +"/"+ soundFileName));
} catch (Exception e) {
log.error("Audio File Storage Failed", e);
return JsonResultUtils.fail("Audio File Storage Failed");
}

//
Algorithm algorithm = algorithmService.getById(id);
if(algorithm == null) {
return JsonResultUtils.fail("Algorithm Card does not exist or Deleted");
}
//
Algorithm algorithm1 = new Algorithm();
algorithm1.setId(algorithm.getId());
algorithm1.setSoundFile(soundFileName);
algorithmService.updateById(algorithm1);
return JsonResultUtils.success();
} catch (Exception e) {
return JsonResultUtils.fail("Data Storage Exception");
}
}

@ApiOperation(value ="Delete Audio File")
@SaCheckPermission(value = {"algorithm-alarm-voice"}, mode = SaMode.OR)
@PostMapping({"/deleteSoundFile"})
@ResponseBody
public JsonResult<?> deleteSoundFile(@RequestParam(value ="id",required = false) Long id){
Algorithm algorithm = algorithmService.getById(id);

// Update
algorithm.setSoundFile("");
algorithmService.saveOrUpdate(algorithm);
return JsonResultUtils.success();
}

/**
* Algorithm Alert Audio Stream
* @author Abyss
* @date 2024/5/5 17:23
*/
@SaCheckPermission(value = {"algorithm-alarm-voice"}, mode = SaMode.OR)
@GetMapping("/sound/stream")
public void soundStream(Long id, HttpServletResponse response) {
try {
Algorithm algorithm = algorithmService.getById(id);
if (StringUtils.isBlank(algorithm.getSoundFile())) {
log.info("Alert Audio File does not exist");
return;
}
// About Fixed demo Video Name
String filepath = FileUtils.pathTo(MODEL_DIR +"/soundFile/"+ algorithm.getSoundFile());
if(!FileUtil.exist(filepath)) {
return;
}
BufferedInputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(filepath)));
response.setContentType("audio/mpeg");
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
e.printStackTrace();
}
}

@ApiOperation(value ="Query Third Party Push Config")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@GetMapping("pushConfig")
@ResponseBody
public JsonResult<?> pushConfig() {
List<AlgorithmPushConfigDTO> algorithmPushConfigDTOS = new ArrayList<>();
List<Long> algorithmIds = cameraAlgorithmService.listAllAlgorithmIds();
if(algorithmIds == null || algorithmIds.isEmpty()) {
return JsonResultUtils.success(algorithmPushConfigDTOS);
}

//
List<Algorithm> algorithms = algorithmService.list();
if(algorithms == null || algorithms.isEmpty()) {
return JsonResultUtils.success(algorithmPushConfigDTOS);
}
Map<Long, Algorithm> algorithmMap = algorithms.stream().collect(Collectors.toMap(Algorithm::getId, Function.identity(), (s1, s2) -> s1));
for(Long algorithmId: algorithmIds) {
Algorithm algorithm = algorithmMap.get(algorithmId);
if(algorithm == null) {
continue;
}
//
AlgorithmPushConfigDTO algorithmPushConfigDTO = new AlgorithmPushConfigDTO();
algorithmPushConfigDTO.setId(algorithm.getId());
algorithmPushConfigDTO.setName(algorithm.getName());
algorithmPushConfigDTO.setNameEn(algorithm.getNameEn());
algorithmPushConfigDTO.setPushEnable(algorithm.getPushEnable() == null? 0: algorithm.getPushEnable());
algorithmPushConfigDTOS.add(algorithmPushConfigDTO);
}
return JsonResultUtils.success(algorithmPushConfigDTOS);
}

@ApiOperation(value ="Query Third Party Push Config")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping("pushConfig")
@ResponseBody
public JsonResult<?> modifyPushConfig(@RequestBody AlgorithmPushConfigModifyVO modifyVO) {
List<AlgorithmPushConfigVO> algorithmPushConfigVOS = modifyVO.getList();
if(algorithmPushConfigVOS == null || algorithmPushConfigVOS.isEmpty()) {
return JsonResultUtils.fail("not has Submit Config Data");
}

for(AlgorithmPushConfigVO algorithmPushConfigVO: algorithmPushConfigVOS) {
if(algorithmPushConfigVO.getId() == null) {
return JsonResultUtils.fail("Param Error,[id] Is Empty");
}
}

for(AlgorithmPushConfigVO algorithmPushConfigVO: algorithmPushConfigVOS) {
Algorithm algorithm = new Algorithm();
algorithm.setId(algorithmPushConfigVO.getId());
algorithm.setPushEnable(algorithmPushConfigVO.getPushEnable() == null? 0: algorithmPushConfigVO.getPushEnable());
algorithmService.updateById(algorithm);
}
return JsonResultUtils.success();
}

/**
* Save Data Collect Set
* @param collectVo
* @return
*/
@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@PostMapping("saveAlarmCollect")
@ResponseBody
public JsonResult<?> saveAlarmCollect(@RequestBody AlarmCollectVo collectVo) {
List<Algorithm> algorithms = algorithmService.list();
if(algorithms == null) {
algorithms = new ArrayList<>();
}

if(collectVo.getCollectDay() == null) {
return JsonResultUtils.fail("Please select Data Collect day Number");
}

if(collectVo.getCollectAlgos() == null || collectVo.getCollectAlgos().isEmpty()) {
return JsonResultUtils.fail("not has Select Algorithm");
}

// Update Data Collect day Number
configService.saveData("Data Collect day Number","collectDay", collectVo.getCollectDay() +"");
configService.evictByTag("collectDay");
configService.getByValTag("collectDay");

long startTime = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH).getTime();
long endTime = DateUtil.truncate(DateUtil.offsetDay(new Date(), collectVo.getCollectDay()), DateField.DAY_OF_MONTH).getTime();

// Update Data Collect Set Info
List<AlarmCollectAlgoVo> alarmCollectAlgoVos = collectVo.getCollectAlgos();
for(Algorithm algorithm: algorithms) {
int collectFlag = 0;
float collectConfidence = 0.5f;
int pushEnable = 1;
for(AlarmCollectAlgoVo collectAlgoVo: alarmCollectAlgoVos) {
if(collectAlgoVo.getAlgoId().equals(algorithm.getId())) {
collectFlag = collectAlgoVo.getCollectFlag();
collectConfidence = collectAlgoVo.getCollectConfidence();
pushEnable = collectAlgoVo.getPushEnable();
break;
}
}

Algorithm updateAlgorithm = new Algorithm();
updateAlgorithm.setId(algorithm.getId());
updateAlgorithm.setCollectFlag(collectFlag);
updateAlgorithm.setCollectConfidence(collectConfidence);
updateAlgorithm.setCollectStartTime(startTime);
updateAlgorithm.setCollectEndTime(endTime);
updateAlgorithm.setPushEnable(pushEnable);
algorithmService.updateById(updateAlgorithm);
}
return JsonResultUtils.success();
}

/**
* Query Data Collect Set
* @return
*/
@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@PostMapping("getAlarmCollect")
@ResponseBody
public JsonResult<?> getAlarmCollect() {
List<Algorithm> algorithms = algorithmService.list();
if(algorithms == null) {
algorithms = new ArrayList<>();
}

AlarmCollectDTO alarmCollectDTO = new AlarmCollectDTO();
alarmCollectDTO.setCollectDay(1);

// Update Data Collect day Number
String collectDay = configService.getByValTag("collectDay");
if(StrUtil.isNotBlank(collectDay)) {
alarmCollectDTO.setCollectDay(Integer.valueOf(collectDay));
}

// Update Data Collect Set Info
List<AlarmCollectAlgoDTO> alarmCollectAlgoDTOS = new ArrayList<>();
for(Algorithm algorithm: algorithms) {
AlarmCollectAlgoDTO alarmCollectAlgoDTO = new AlarmCollectAlgoDTO();
alarmCollectAlgoDTO.setId(algorithm.getId());
alarmCollectAlgoDTO.setName(algorithm.getName());
alarmCollectAlgoDTO.setCollectFlag(algorithm.getCollectFlag() == null? 0: algorithm.getCollectFlag());
alarmCollectAlgoDTO.setCollectConfidence(algorithm.getCollectConfidence() == null? 0.5f: algorithm.getCollectConfidence());
alarmCollectAlgoDTO.setPushEnable(algorithm.getPushEnable() == null? 0: algorithm.getPushEnable());
alarmCollectAlgoDTOS.add(alarmCollectAlgoDTO);
}
alarmCollectDTO.setCollectAlgos(alarmCollectAlgoDTOS);
return JsonResultUtils.success(alarmCollectDTO);
}

/**
* Query Data Collect Set
* @return
*/
@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@PostMapping("getFiles")
@ResponseBody
public JsonResult<?> getFiles(String nameEn, String platform) {
List<Map<String, Object>> dataList = new ArrayList<>();

if(StrUtil.isBlank(nameEn) || StrUtil.isBlank(platform)) {
return JsonResultUtils.success(dataList);
}

// Query Local Directory File
String path = FileUtils.pathTo(MODEL_DIR + File.separator + platform + File.separator + nameEn);
List<String> filepaths = FileUtils.listFiles(path, nameEn, Collections.singletonList("zip"));
for(String filepath: filepaths) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", FileUtil.getName(filepath));
dataMap.put("size", FileUtils.convertBytes(FileUtil.size(FileUtil.newFile(filepath))));
dataMap.put("file_size", FileUtil.size(FileUtil.newFile(filepath)));
dataMap.put("md5Str","Consistent");
dataMap.put("source","Local");
dataMap.put("process", 100);
dataMap.put("isDownload", false); // not Download
dataMap.put("isReDownload", false); // not Again Download
dataMap.put("downloading", false); // Download in
dataMap.put("platform", platform);
dataMap.put("nameEn", nameEn);
dataList.add(dataMap);
}

if(!projectConfig.isOssNet()) {
return JsonResultUtils.success(dataList);
}

// Query OSS File
List<OssFileDTO> ossFiles = OssUtils.getOssFiles(platform, nameEn, true);
if(ossFiles!= null &&!ossFiles.isEmpty()) {
for(OssFileDTO ossFile: ossFiles) {
boolean found = false;
for(Map<String, Object> dataMap: dataList) {
String name = Convert.toStr(dataMap.get("name"),"");
if(name.equalsIgnoreCase(ossFile.getFilename())) {
found = true;
long fileSize = Convert.toLong(dataMap.get("file_size"), 0L);
if(fileSize!= ossFile.getFilesize()) {
dataMap.put("md5Str","not Consistent");
dataMap.put("isDownload", false); // not Download
dataMap.put("isReDownload", true); // Again Download
}
break;
}
}

if(!found) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", FileUtil.getName(ossFile.getFilename()));
dataMap.put("size", FileUtils.convertBytes(ossFile.getFilesize()));
dataMap.put("file_size", ossFile.getFilesize());
dataMap.put("md5Str","Consistent");
dataMap.put("source","OSS");
dataMap.put("process", 0);
dataMap.put("isDownload", true); // Download
dataMap.put("isReDownload", false); // not Again Download
dataMap.put("downloading", false); // Download in
dataMap.put("platform", platform);
dataMap.put("nameEn", nameEn);
dataList.add(dataMap);
}
}
}
return JsonResultUtils.success(dataList);
}

/**
* Query Data Collect Set
* @return
*/
@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@PostMapping("getLocalFiles")
@ResponseBody
public JsonResult<?> getLocalFiles(String nameEn, String platform) {
List<Map<String, Object>> dataList = new ArrayList<>();

if(StrUtil.isBlank(nameEn) || StrUtil.isBlank(platform)) {
return JsonResultUtils.success(dataList);
}

// Query Local Directory File
String path = FileUtils.pathTo(MODEL_DIR + File.separator + platform + File.separator + nameEn);
List<String> filepaths = FileUtils.listFiles(path, nameEn, Collections.singletonList("zip"));
for(String filepath: filepaths) {
Map<String, Object> dataMap = new HashMap<>();
dataMap.put("name", FileUtil.getName(filepath));
dataMap.put("size", FileUtils.convertBytes(FileUtil.size(FileUtil.newFile(filepath))));
dataMap.put("platform", platform);
dataMap.put("nameEn", nameEn);
dataMap.put("updated_at", DateUtil.format(FileUtil.lastModifiedTime(filepath),"yyyy-MM-dd HH:mm:ss"));
dataList.add(dataMap);
}
return JsonResultUtils.success(dataList);
}

/**
* Query Data Collect Set
* @return
*/
@SaCheckPermission(value = {"algorithm-add","algorithm-edit"}, mode = SaMode.OR)
@PostMapping("getPlatform")
@ResponseBody
public JsonResult<?> getPlatform() {
return JsonResultUtils.success(projectConfig.getPlatform());
}

@ApiOperation("Box Cut change Version Task Start")
@SaCheckPermission(value = {"algorithm-version"}, mode = SaMode.OR)
@PostMapping("/switchModel")
@ResponseBody
public JsonResult<?> switchModel(Long boxId, String fileName, String nameEn, String platform) {
Map<String, Object> resultMap = new HashMap<>();
resultMap.put("success", false);
resultMap.put("message","Update success");

if (boxId == null) {
resultMap.put("message","Box Device Param Error");
return JsonResultUtils.success(resultMap);
}
if (StrUtil.isBlank(fileName)) {
resultMap.put("message","Algorithm File Param Error");
return JsonResultUtils.success(resultMap);
}
if (StrUtil.isBlank(nameEn)) {
resultMap.put("message","Algorithm Code Param Error");
return JsonResultUtils.success(resultMap);
}

String[] parts = FileUtil.mainName(fileName).split("-");
if(parts.length!= 3) {
resultMap.put("message","Algorithm Package File Name Error");
return JsonResultUtils.success(resultMap);
}

//String platform = parts[0]; //"denglin"
//String nameEn = parts[1]; //"car"
String version = parts[2]; // 1.0

//
Algorithm algorithm = algorithmService.getByNameEn(nameEn);
if(algorithm == null) {
resultMap.put("message","Algorithm Card not find to");
return JsonResultUtils.success(resultMap);
}

Location location = locationService.getById(boxId);
if(location == null) {
resultMap.put("message","Box Device not find to");
return JsonResultUtils.success(resultMap);
}

if(StrUtil.isBlank(location.getPlatform())) {
resultMap.put("message","Box Device not Set Chip Type");
return JsonResultUtils.success(resultMap);
}

if(!location.getPlatform().equalsIgnoreCase(platform)) {
resultMap.put("message","Box Device Chip Type and Cut change Model not Match allocate");
return JsonResultUtils.success(resultMap);
}

if(location.getBoxHeartTime() == null) {
resultMap.put("message","Box Device Offline");
return JsonResultUtils.success(resultMap);
}

if(StrUtil.isBlank(location.getBoxNo())) {
resultMap.put("message","Box Device lack Missing Index");
return JsonResultUtils.success(resultMap);
}

BoxVersion boxVersion = boxVersionService.getData(boxId, algorithm.getId());
if(boxVersion == null) {
boxVersion = new BoxVersion();
boxVersion.setBoxId(boxId);
boxVersion.setAlgorithmId(algorithm.getId());
boxVersion.setVersionNum(version);
boxVersionService.save(boxVersion);
} else {
String versionNum = boxVersion.getVersionNum();
if(version.equalsIgnoreCase(versionNum)) {
resultMap.put("success", true);
resultMap.put("message","Version Consistent, no Need Cut change");
return JsonResultUtils.success(resultMap);
}
}

// Determine Whether Offline,10 min inner no Heartbeat
long timeSecs = (System.currentTimeMillis() - location.getBoxHeartTime()) / 1000;
if(timeSecs >= 10 * 60) {
resultMap.put("message","Box Device Offline");
return JsonResultUtils.success(resultMap);
}

String localFile = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ nameEn +"/"+ fileName);
if(!FileUtil.exist(localFile)) {
resultMap.put("message","Algorithm File not find to or Deleted");
return JsonResultUtils.success(resultMap);
}

// md5 and File Path
String fileMd5 = Md5FileUtils.getMD5(FileUtil.newFile(localFile));
String fileUrl ="/"+ platform +"/"+ nameEn +"/"+ fileName;

UpgradeRequest request = new UpgradeRequest();
request.setType(MessageType.UPGRADE_ALGO.getType());
request.setRequestId(IdUtil.randomUUID());
request.setSn(location.getBoxNo());
request.setFileMd5(fileMd5);
request.setAlgoCode(nameEn);
request.setAlgoId(algorithm.getId());
request.setAlgoVer(version);
request.setFileName(fileName);
request.setFileUrl(fileUrl);
Response response = messageSenderAndWaiter.sendRequest(request);
if(response == null) {
resultMap.put("message","Update Exception, Unknown Error");
return JsonResultUtils.success(resultMap);
}

// Update success
UpgradeResponse upgradeResponse = (UpgradeResponse) response;
if(upgradeResponse.isStatus()) {
// List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithmAndBoxId(algorithm.getId(), location.getId());
// for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
// cameraAlgorithm.setBoxUpdateStatus(2);
// cameraAlgorithm.setAlgorithmVersion(version);
// cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
//}

boxVersion.setVersionNum(version);
boxVersionService.updateById(boxVersion);

resultMap.put("success", true);
return JsonResultUtils.success(resultMap);
}

// Update failed
// List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listByAlgorithmAndBoxId(algorithm.getId(), location.getId());
// for (CameraAlgorithm cameraAlgorithm: cameraAlgorithmList) {
// cameraAlgorithm.setBoxUpdateStatus(3);
// cameraAlgorithmService.saveOrUpdate(cameraAlgorithm);
//}
resultMap.put("message", upgradeResponse.getMsg());
return JsonResultUtils.success(resultMap);
}

@ApiOperation(value ="Algorithm summary Total according", notes ="summary total Download Model Package Count, Run in Model Count, can Use Count etc")
@SaCheckPermission(value = {"algorithmManagement"}, mode = SaMode.OR)
@GetMapping("/summary")
@ResponseBody
public JsonResult<AlgorithmSummaryDTO> summary() {
List<Algorithm> algorithmList = algorithmService.list();

// can Use Count
int availableCount = algorithmList.size();

// Download Model Package Count
int downloadCount = 0;
for (Algorithm algorithm: algorithmList) {
if(hasDownloadFile(algorithm.getNameEn())) {
downloadCount++;
}
}

// Run in Model
int runningCount = 0;
for (Algorithm algorithm: algorithmList) {
List<CameraAlgorithm> cameraAlgorithmList = cameraAlgorithmService.listRunningByAlgorithm(algorithm.getId(), 1);
runningCount += cameraAlgorithmList.size();
}

// input out
AlgorithmSummaryDTO dto = new AlgorithmSummaryDTO();
dto.setAvailableCount(availableCount);
dto.setDownloadCount(downloadCount);
dto.setRunningCount(runningCount);
return JsonResultUtils.success(dto);
}

/**
* Algorithm Whether Exist Download File, only need has One Hardware up via Download Algorithm, just Compute Exist
* @param nameEn
* @return
*/
public boolean hasDownloadFile(String nameEn) {
List<String> platforms = projectConfig.getPlatform();
for(String platform: platforms) {
String path = FileUtils.pathTo(MODEL_DIR +"/"+ platform +"/"+ nameEn +"/"); // zip Store Put Directory
List<String> files = FileUtils.listFiles(path, nameEn, Collections.singletonList("zip")); // find to All zip File
return!files.isEmpty();
}
return false;
}
}

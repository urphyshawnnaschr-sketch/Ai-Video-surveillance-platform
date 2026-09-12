package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Config;
import com.yihecode.camera.ai.entity.SmsPhone;
import com.yihecode.camera.ai.enums.LanguageEnum;
import com.yihecode.camera.ai.media.MediaRestfulService;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.SmsPhoneService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.dto.ConfigFaceSimiliarityDTO;
import com.yihecode.camera.ai.web.vo.ConfigFaceSimiliarityVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
* System Config Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "System Config Management")
@SaCheckLogin
@Controller
@RequestMapping({"/config"})
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private SmsPhoneService phoneService;

    @Autowired
    private MediaRestfulService mediaRestfulService;

    @Value("${uploadDir}")
    private String uploadDir;

    /**
* Config Detail
* @param id
* @return
*/
    @ApiOperation("Query Config Data Detail")
    @ApiImplicitParam(name = "id", value = "Data ID")
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping({"/detail"})
    @ResponseBody
    public JsonResult<?> detail(Long id) {
        Config config = configService.getById(id);
        if(config == null) {
            return JsonResultUtils.fail("find not to Data");
        }
        return JsonResultUtils.success(config);
    }

    /**
* Query Config List
* @return
*/
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public PageResult<?> listData() {
        List<Config> configList = this.configService.list();
        if (configList == null) {
            configList = new ArrayList<>();
        }

        //
for(Config config: configList) {
if("wework_url".equals(config.getTag())) {
config.setVal(StrUtils.hide(config.getVal()));
}
}
return PageResultUtils.success(null, configList);
}

/**
* Save Config
* @param config
* @return
*/
@ApiOperation("Save Config Data")
@ApiImplicitParam(name ="config", value ="Config Entity")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult<?> save(Config config) {
if (StrUtil.isBlank(config.getName())) {
return JsonResultUtils.fail("Please enter Config Name");
}
if (StrUtil.isBlank(config.getTag())) {
return JsonResultUtils.fail("Please enter Config ID");
}
if (StrUtil.isBlank(config.getVal())) {
return JsonResultUtils.fail("Please enter Config Value");
}
this.configService.saveOrUpdate(config);

//
configService.evictByTag(config.getTag());

return JsonResultUtils.success();
}

/**
* Delete Config
* @param id
* @return
*/
@ApiOperation("Delete Config Data")
@ApiImplicitParam(name ="id", value ="Data ID")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult<?> delete(Long id) {
Config config = configService.getById(id);
if(config == null) {
return JsonResultUtils.fail("Data Not Exist");
}
this.configService.removeById(id);
this.configService.evictByTag(config.getTag());
return JsonResultUtils.success();
}

@ApiOperation("Save Basic Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="ipAddr", value ="IP Address + Port"),
@ApiImplicitParam(name ="algorithmUrl", value ="Inference Address"),
@ApiImplicitParam(name ="streamType", value ="Play Put Stream Type"),
@ApiImplicitParam(name ="mediaUrl", value ="Stream Media Server Address"),
@ApiImplicitParam(name ="playUrl", value ="Stream Media Play Put Address"),
@ApiImplicitParam(name ="pushIp", value ="Algorithm Push Stream IP"),
@ApiImplicitParam(name ="pushPort", value ="Algorithm Push Stream Port")
})
@SaCheckPermission(value = {"systemManagement-baseManagement"}, mode = SaMode.OR)
@PostMapping({"/saveBasicConfig"})
@ResponseBody
public JsonResult saveBasicConfig(String ipAddr, String algorithmUrl, String streamType, String mediaUrl, String playUrl, String pushIp, String pushPort, String isRecord, String recordDates, String recordTimes, String recordSecs) {
//
configService.saveData("IP Address + Port","ipAddr", ipAddr);
//
configService.saveData("should Use Access Address","webUrl","http://"+ ipAddr);
configService.saveData("should Use Message Address","wsUrl","ws://"+ ipAddr);
//
this.configService.evictByTag("ipAddr");
this.configService.evictByTag("webUrl");
this.configService.evictByTag("wsUrl");

//
configService.saveData("Inference Address","algorithmUrl", algorithmUrl);
//
this.configService.evictByTag("algorithmUrl");

// Cut change Stream Play Put Mode, First need Close All Stream
String _streamType = configService.getByValTag("streamType");
if(!streamType.equals(_streamType)) {
String mediaServer = configService.getByValTag("mediaServer");
if("zlm".equals(mediaServer)) {
//zl.close_streams(mediaUrl,"123456");
}
}

//
configService.saveData("Play Put Stream Type","streamType", streamType);
//
configService.saveData("Stream Media Server Address","mediaUrl", mediaUrl);
//
configService.saveData("Stream Media Play Put Address","playUrl", playUrl);
//
configService.saveData("Algorithm Push Stream IP","pushIp", pushIp);
//
configService.saveData("Algorithm Push Stream Port","pushPort", pushPort);

//
this.configService.evictByTag("streamType");
this.configService.evictByTag("mediaUrl");
this.configService.evictByTag("playUrl");
this.configService.evictByTag("pushIp");
this.configService.evictByTag("pushPort");

// Delete Config Push Stream Address
//if("algo".equals(streamType)) {
// videoPlayService.removeAll();
//}

// Whether Recording
configService.saveData("Whether Recording","isRecord", isRecord);
this.configService.evictByTag("isRecord");

configService.saveData("Recording Time","recordDates", recordDates);
this.configService.evictByTag("recordDates");

configService.saveData("Recording Hour Segment","recordTimes", recordTimes);
this.configService.evictByTag("recordTimes");

String recordSecsDb = configService.getByValTag("recordSecs");
this.configService.saveData("Recording Hour long","recordSecs", recordSecs);

// Close All Liu
if("false".equalsIgnoreCase(isRecord)) {
//mediaRestfulService.closeAllStream();
}

// Determine Whether need Update Recording Hour long
if(recordSecs!= null &&!recordSecs.equals(recordSecsDb)) {
//mediaRestfulService.closeAllStream();
}

configService.saveData("Recording Hour long","recordSecs", recordSecs);
this.configService.evictByTag("recordSecs");

return JsonResultUtils.success();
}

/**
* Save IP Speaker Pole Play Report Push Config
* @author Abyss
*/
@ApiOperation("Save IP Speaker Pole Play Report Push Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="soundColumnServer", value ="IP Speaker Pole Server"),
@ApiImplicitParam(name ="soundColumnSn", value ="IP Speaker Pole No"),
@ApiImplicitParam(name ="soundColumnVol", value ="IP Speaker Pole Audio Quantity")
})
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/saveSoundColumnConfig"})
@ResponseBody
public JsonResult saveSoundColumnConfig(String soundColumnServer, String soundColumnSn, String soundColumnVol) {
configService.saveData("IP Speaker Pole Server","soundColumnServer", soundColumnServer);
configService.saveData("IP Speaker Pole No","soundColumnSn", soundColumnSn);
configService.saveData("IP Speaker Pole Audio Quantity","soundColumnVol", soundColumnVol);
this.configService.evictByTag("soundColumnServer");
this.configService.evictByTag("soundColumnSn");
this.configService.evictByTag("soundColumnVol");

return JsonResultUtils.success();
}

/**
* Save Alert Push Config
* @author Abyss
*/
@ApiOperation("Save Alert Push Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="reportPushUrl", value ="Third-party API Address"),
@ApiImplicitParam(name ="reportPushImage", value ="Whether Push Image"),
@ApiImplicitParam(name ="reportPushLiveUrl", value ="Keep Alive API Address")
})
@SaCheckPermission(value = {"noticeManagement-api"}, mode = SaMode.OR)
@PostMapping({"/saveAlarmPushConfig"})
@ResponseBody
public JsonResult saveAlarmPushConfig(String reportPushUrl, String reportPushImage, String reportPushLiveUrl) {
configService.saveData("Third-party API Address","reportPushUrl", reportPushUrl);
configService.saveData("Whether Push Image","reportPushImage", reportPushImage);
configService.saveData("Keep Alive API Address","reportPushLiveUrl", reportPushLiveUrl);
this.configService.evictByTag("reportPushUrl");
this.configService.evictByTag("reportPushImage");
this.configService.evictByTag("reportPushLiveUrl");

return JsonResultUtils.success();
}

/**
* Save Alert WeWork Config
* @author Abyss
*/
@ApiOperation("Save Alert WeWork Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="weworkEnable", value ="Whether Push WeWork Group Bot"),
@ApiImplicitParam(name ="weworkUrl", value ="WeWork Group Bot Address"),
@ApiImplicitParam(name ="dingdingUrl", value ="DingTalk Group Bot Address"),
@ApiImplicitParam(name ="dingdingSign", value ="DingTalk Group Bot Signature"),
@ApiImplicitParam(name ="dingdingEnable", value ="DingTalk Group Bot Enabled")
})
@SaCheckPermission(value = {"noticeManagement-wx"}, mode = SaMode.OR)
@PostMapping({"/saveAlarmWeworkConfig"})
@ResponseBody
public JsonResult saveAlarmWeworkConfig(String weworkEnable, String weworkUrl, String dingdingUrl, String dingdingSign, String dingdingEnable) {
Long accountId = StpUtil.getLoginIdAsLong();
configService.saveData("Whether Push WeWork Group Bot","weworkEnable"+ accountId, weworkEnable);
configService.saveData("WeWork Group Bot Address","weworkUrl"+ accountId, weworkUrl);
configService.saveData("DingTalk Group Bot Address","dingdingUrl"+ accountId, dingdingUrl);
configService.saveData("DingTalk Group Bot Signature","dingdingSign"+ accountId, dingdingSign);
configService.saveData("DingTalk Group Bot Enabled","dingdingEnable"+ accountId, dingdingEnable);
this.configService.evictByTag("weworkEnable"+ accountId);
this.configService.evictByTag("weworkUrl"+ accountId);
this.configService.evictByTag("dingdingUrl"+ accountId);
this.configService.evictByTag("dingdingSign"+ accountId);
this.configService.evictByTag("dingdingEnable"+ accountId);
return JsonResultUtils.success();
}

/**
* Save Alert SMS Config
* @author Abyss
*/
@ApiOperation("Save Alert SMS Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="smsEnable", value ="Whether Push SMS Notification"),
@ApiImplicitParam(name ="smsAppKey", value ="SMS Platform APP_KEY"),
@ApiImplicitParam(name ="smsTplId", value ="SMS Platform Template ID")
})
@SaCheckPermission(value = {"noticeManagement-message"}, mode = SaMode.OR)
@PostMapping({"/saveAlarmSmsConfig"})
@ResponseBody
public JsonResult saveAlarmSmsConfig(String smsEnable, String smsAppKey, String smsTplId) {
Long accountId = StpUtil.getLoginIdAsLong();
configService.saveData("Whether Push SMS Notification","smsEnable"+ accountId, smsEnable);
configService.saveData("SMS Platform APP_KEY","smsAppKey"+ accountId, smsAppKey);
configService.saveData("SMS Platform Template ID","smsTplId"+ accountId, smsTplId);
this.configService.evictByTag("smsEnable"+ accountId);
this.configService.evictByTag("smsAppKey"+ accountId);
this.configService.evictByTag("smsTplId"+ accountId);
return JsonResultUtils.success();
}


@ApiOperation("Save should Use Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="logoUrl", value ="logo Address"),
@ApiImplicitParam(name ="appName", value ="should Use Name"),
@ApiImplicitParam(name ="screenLogoUrl", value ="big Screen logo Address"),
@ApiImplicitParam(name ="screenName", value ="big Screen should Use Name")
})
@SaCheckPermission(value = {"systemManagement-baseManagement"}, mode = SaMode.OR)
@PostMapping({"/saveAppConfig"})
@ResponseBody
public JsonResult<Void> saveAppConfig(String logoUrl, String appName, String screenLogoUrl, String screenName) {
//
configService.saveData("logo Address","logoUrl", logoUrl);
//
configService.saveData("should Use Name","appName", appName);
//
configService.saveData("big Screen logo Address","screenLogoUrl", screenLogoUrl);
//
configService.saveData("big Screen should Use Name","screenName", screenName);

//
this.configService.evictByTag("logoUrl");
this.configService.evictByTag("appName");
this.configService.evictByTag("screenLogoUrl");
this.configService.evictByTag("screenName");
return JsonResultUtils.success();
}

@ApiOperation("Save Face and Frame Extract should Use Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="FACE_HTTP_BASE_URL", value ="Face Recognition Address"),
@ApiImplicitParam(name ="FACE_HTTP_BASE_URL_2", value ="Face for than Address"),
@ApiImplicitParam(name ="HTTP_FRAME_API_URL", value ="Frame Extract Service Address"),
@ApiImplicitParam(name ="HTTP_FRAME_ENABLE", value ="Whether Enable Frame Extract Service (true/false)"),
@ApiImplicitParam(name ="HTTP_FRAME_CALLBACK_URL", value ="Frame Extract return Adjust Address")
})//"FACE_HTTP_BASE_URL","FACE_HTTP_BASE_URL_2","HTTP_FRAME_API_URL","HTTP_FRAME_ENABLE","HTTP_FRAME_CALLBACK_URL"
@SaCheckPermission(value = {"systemManagement-baseManagement"}, mode = SaMode.OR)
@PostMapping({"/saveFaceFrameConfig"})
@ResponseBody
public JsonResult saveFaceFrameConfig(String FACE_HTTP_BASE_URL, String FACE_HTTP_BASE_URL_2, String HTTP_FRAME_API_URL, String HTTP_FRAME_ENABLE, String HTTP_FRAME_CALLBACK_URL) {
//
configService.saveData("FACE_HTTP_BASE_URL","FACE_HTTP_BASE_URL", FACE_HTTP_BASE_URL);
//
configService.saveData("FACE_HTTP_BASE_URL_2","FACE_HTTP_BASE_URL_2", FACE_HTTP_BASE_URL_2);
//
configService.saveData("HTTP_FRAME_API_URL","HTTP_FRAME_API_URL", HTTP_FRAME_API_URL);
//
configService.saveData("HTTP_FRAME_ENABLE","HTTP_FRAME_ENABLE", HTTP_FRAME_ENABLE);
//
configService.saveData("HTTP_FRAME_CALLBACK_URL","HTTP_FRAME_CALLBACK_URL", HTTP_FRAME_CALLBACK_URL);

//
this.configService.evictByTag("FACE_HTTP_BASE_URL");
this.configService.evictByTag("FACE_HTTP_BASE_URL_2");
this.configService.evictByTag("HTTP_FRAME_API_URL");
this.configService.evictByTag("HTTP_FRAME_ENABLE");
this.configService.evictByTag("HTTP_FRAME_CALLBACK_URL");
return JsonResultUtils.success();
}

/**
* Query Config Info
* @return
*/
@ApiOperation("Query Config Info")
@PostMapping({"/info"})
@SaCheckPermission(value = {"systemManagement-baseManagement","noticeManagement-message","noticeManagement-wx","noticeManagement-api","noticeManagement-voice"}, mode = SaMode.OR)
@ResponseBody
public JsonResult<Map<String, Object>> info() {
Map<String, Object> forms = new HashMap<>(); // Form Set combine
long accountId = StpUtil.getLoginIdAsLong();
forms.put("form1", buildInfos("ipAddr","algorithmUrl","streamType","mediaUrl","playUrl","pushIp","pushPort","isRecord","recordDates","recordTimes","recordSecs")); // Basic Config
forms.put("form2", buildInfos("reportPushUrl","reportPushImage","smsEnable"+ accountId,"weworkEnable"+ accountId,"weworkUrl"+ accountId,"dingdingUrl"+ accountId,"dingdingSign"+ accountId,"dingdingEnable"+ accountId,"smsAppKey"+ accountId,"smsTplId"+ accountId,"reportPushLiveUrl","soundColumnServer","soundColumnSn","soundColumnVol")); // Alert Config
forms.put("form3", buildInfos("logoUrl","appName","screenLogoUrl","screenName")); // should Use Config
forms.put("form4", buildInfos("FACE_HTTP_BASE_URL","FACE_HTTP_BASE_URL_2","HTTP_FRAME_API_URL","HTTP_FRAME_ENABLE","HTTP_FRAME_CALLBACK_URL"));
//
List<SmsPhone> phoneList = phoneService.list();
if(phoneList == null) {
phoneList = new ArrayList<>();
}
//
Map<String, Object> retMap = new HashMap<>();
retMap.put("forms", forms);
retMap.put("phoneList", phoneList);
return JsonResultUtils.success(retMap);
}

/**
* By Tag Name Query Value and Organization Complete map Back
* @param tags
* @return
*/
private Map<String, Object> buildInfos(String...tags) {
Map<String, Object> infos = new HashMap<>();
for(String tag: tags) {
String val = configService.getByValTag(tag);
String accountId = String.valueOf(StpUtil.getLoginIdAsLong());

if(tag.contains(accountId)){
infos.put(tag.replace(accountId,""), val);
} else {
infos.put(tag, StrUtil.isBlank(val)?"": val);
}
}
return infos;
}

/**
* Upload File
* @param file
* @return
*/
@ApiOperation("Upload File")
@SaCheckPermission(value = {"systemManagement"}, mode = SaMode.OR)
@PostMapping("/upload")
@ResponseBody
public JsonResult<String> upload(@RequestParam(value ="file", required = false) MultipartFile file) {
if(file == null) {
return JsonResultUtils.fail("Please select Image");
}

// Create Directory
String path = uploadDir + File.separator +"configs";
if(!FileUtil.exist(path)) {
FileUtil.mkdir(path);
}

// Save File
try {
//
String saveName = IdUtil.randomUUID() +"."+ FileUtil.extName(file.getOriginalFilename());
file.transferTo(new File(path + File.separator + saveName));
return JsonResultUtils.success(saveName);
} catch (Exception e) {
return JsonResultUtils.fail("Upload File Exception");
}
}

/**
* Image show show
* @return
*/
@SaIgnore
@ApiOperation("Image show show")
// @SaCheckPermission(value = {"systemManagement"}, mode = SaMode.OR)
@GetMapping("/upload/stream")
public void uploadStream(String file, HttpServletResponse response) {
try {
BufferedInputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(uploadDir + File.separator +"configs"+ File.separator + FileUtil.getName(file))));
response.setContentType("image/jpeg");
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
//e.printStackTrace();
}
}

/**
* app Info
* @return
*/
@ApiOperation("app Info")
@SaIgnore
@PostMapping("/appInfo")
@ResponseBody
public JsonResult<Map<String, String>> appInfo(@RequestHeader("Lang") String language) throws Exception {
Map<String, String> appMap = new HashMap<>();
appMap.put("appName","AI Video Monitor Platform");
appMap.put("screenName","AI Video Monitor can View Change Platform");
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
appMap.put("appName","Safety Management Platform");
appMap.put("screenName","Safety Management Platform");
}
appMap.put("logoUrl","");
appMap.put("screenLogoUrl","");
//
Config config = configService.getByTag("appName");
if(ObjectUtil.isNotNull(config)) {
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
appMap.put("appName", config.getEnglishVal());
}else {
appMap.put("appName", config.getVal());
}
}
//
String logoUrl = configService.getByValTag("logoUrl");
appMap.put("logoUrl", logoUrl);
// if(StrUtil.isNotBlank(logoUrl)) {
// String url ="/config/upload/stream?file="+ logoUrl;
// appMap.put("logoUrl", url);
//}
//
Config screenName = configService.getByTag("screenName");
if(ObjectUtil.isNotNull(screenName)) {
if(StringUtils.equals(LanguageEnum.ENGLISH.getCode(), language)){
appMap.put("screenName", screenName.getEnglishVal());
}else {
appMap.put("screenName", screenName.getVal());
}
}
//
String screenLogoUrl = configService.getByValTag("screenLogoUrl");
appMap.put("screenLogoUrl", screenLogoUrl);
// if(StrUtil.isNotBlank(screenLogoUrl)) {
// String url ="/config/upload/stream?file="+ screenLogoUrl;
// appMap.put("screenLogoUrl", url);
//}
return JsonResultUtils.success(appMap);
}

/**
* URL Code
* @param val
* @return
*/
private String encode(String val) {
try {
return URLEncoder.encode(val,"utf-8");
} catch (Exception e) {
return"";
}
}

@ApiOperation("Save Sell after Technology 2D code Config")
@ApiImplicitParams({
@ApiImplicitParam(name ="afterSalesQRCodes", value ="Sell after Service Technology Support 2D code"),
})
@SaCheckPermission(value = {"systemManagement-baseManagement"}, mode = SaMode.OR)
@PostMapping({"/saveAfterSalesConfig"})
@ResponseBody
public JsonResult saveAfterSalesConfig(String afterSalesQRCodes) {
configService.saveData("Sell after Service Technology Support 2D code","afterSalesQRCodes", afterSalesQRCodes);
this.configService.evictByTag("afterSalesQRCodes");
return JsonResultUtils.success();
}

@Autowired
private AccountService accountService;

@ApiOperation("Query Scheduled Task Clear Divide Alert Info Keep day Number")
@SaCheckPermission(value = {"alarmManagement"}, mode = SaMode.OR)
@PostMapping({"/getClearReportDayConfig"})
@ResponseBody
public JsonResult<Map<String, Object>> getClearReportDayConfig() {
String clearReportDay = configService.getByValTag("clearReportDay");
String clearReportImageDay = configService.getByValTag("clearReportImageDay");

Map<String, Object> resultMap = new HashMap<>();
resultMap.put("clearReportDay", StrUtil.isBlank(clearReportDay)?"45": clearReportDay);
resultMap.put("clearReportImageDay", StrUtil.isBlank(clearReportImageDay)?"15": clearReportImageDay);
return JsonResultUtils.success(resultMap);
}

@ApiOperation("Save Scheduled Task Clear Divide Alert Info Keep day Number")
@ApiImplicitParams({
@ApiImplicitParam(name ="clearReportDay", value ="Scheduled Task Clear Divide Alert Info Keep day Number"),
})
@SaCheckPermission(value = {"alarm-clear-report"}, mode = SaMode.OR)
@PostMapping({"/saveClearReportDayConfig"})
@ResponseBody
public JsonResult<Void> saveClearReportDayConfig(String clearReportDay, String clearReportImageDay) {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if (account == null || account.getIsSuper() == null || account.getIsSuper().equals(0)) {
return JsonResultUtils.fail("Only super Level Management member can with Modify");
}
configService.saveData("Scheduled Task Clear Divide Alert Info Keep day Number","clearReportDay", clearReportDay);
this.configService.evictByTag("clearReportDay");

configService.saveData("Scheduled Task Clear Divide Alert Image Keep day Number","clearReportImageDay", clearReportImageDay);
this.configService.evictByTag("clearReportImageDay");
return JsonResultUtils.success();
}

@ApiOperation("Save Scheduled Task Clear Divide Face Alert Info Keep day Number")
@ApiImplicitParams({
@ApiImplicitParam(name ="clearReportDay", value ="Scheduled Task Clear Divide Alert Info Keep day Number"),
})
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/saveClearFaceReportDayConfig"})
@ResponseBody
public JsonResult saveClearFaceReportDayConfig(String clearFaceReportDay) {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if (account == null || account.getIsSuper() == null || account.getIsSuper().equals(0)) {
return JsonResultUtils.fail("Only super Level Management member can with Modify");
}
configService.saveData("Scheduled Task Clear Divide Face Alert Info Keep day Number","clearFaceReportDay", clearFaceReportDay);
this.configService.evictByTag("clearFaceReportDay");
return JsonResultUtils.success();
}

@ApiOperation("Face Similarity Threshold Config, Filter Threshold, Stranger produce Person Threshold")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/saveFaceSimiliarity"})
@ResponseBody
public JsonResult<Void> saveFaceSimiliarity(@RequestBody ConfigFaceSimiliarityVo similiarityVo) {
if(similiarityVo.getMinSimiliarity() == null) {
return JsonResultUtils.fail("most small Similarity Is Empty");
}

if(similiarityVo.getSameSimiliarity() == null) {
return JsonResultUtils.fail("near Like Similarity Is Empty");
}

configService.saveData("most small Similarity Is Empty","faceMinSimiliarity", similiarityVo.getMinSimiliarity() +"");
this.configService.evictByTag("faceMinSimiliarity");

configService.saveData("near Like Similarity Is Empty","faceSameSimiliarity", similiarityVo.getSameSimiliarity() +"");
this.configService.evictByTag("faceSameSimiliarity");

return JsonResultUtils.success();
}

@ApiOperation("Face Similarity Threshold Config, Filter Threshold, Stranger produce Person Threshold")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/getFaceSimiliarity"})
@ResponseBody
public JsonResult<ConfigFaceSimiliarityDTO> getFaceSimiliarity() {
String faceMinSimiliarity = configService.getByValTag("faceMinSimiliarity");
String faceSameSimiliarity = configService.getByValTag("faceSameSimiliarity");

ConfigFaceSimiliarityDTO dto = new ConfigFaceSimiliarityDTO();
dto.setMinSimiliarity(Convert.toFloat(faceMinSimiliarity, 0.3f));
dto.setSameSimiliarity(Convert.toFloat(faceSameSimiliarity, 0.8f));
return JsonResultUtils.success(dto);
}
}
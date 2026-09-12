package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraGroupItem;
import com.yihecode.camera.ai.entity.face.FaceGroup;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.service.CameraGroupItemService;
import com.yihecode.camera.ai.service.CameraGroupService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.face.FaceGroupService;
import com.yihecode.camera.ai.service.face.FaceReportService;
import com.yihecode.camera.ai.service.face.FaceUserService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.api.face.ApiFaceFilter;
import com.yihecode.camera.ai.web.face.dto.FaceReportCameraDTO;
import com.yihecode.camera.ai.web.face.dto.FaceReportInfoDTO;
import com.yihecode.camera.ai.web.face.dto.FaceReportPageCameraDTO;
import com.yihecode.camera.ai.web.face.dto.FaceReportUserInfoDTO;
import com.yihecode.camera.ai.web.face.vo.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Face Recognition Result - Face Recognition child Module
*/
@Api(tags = "Face Recognition _ Face Recognition Result Management")
@SaCheckLogin
@Slf4j
@RestController
@RequestMapping("/face/report")
public class FaceReportController {

    @Resource
    private FaceReportService faceReportService;

    @Resource
    private CameraService cameraService;

    @Autowired
    private FaceGroupService faceGroupService;

    @Autowired
    private FaceUserService faceUserService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CameraGroupService cameraGroupService;

    @Autowired
    private CameraGroupItemService cameraGroupItemService;

    /**
* Page Query
* @param page
* @param limit
* @param hasStranger
* @param cameraId
* @return
*/
    @ApiOperation("Page Query")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "page", value = "Page Number"),
            @ApiImplicitParam(name = "limit", value = "Pagination Number"),
            @ApiImplicitParam(name = "hasStranger", value = "Whether Contain Stranger produce Person 0- All 1- is 2- No"),
            @ApiImplicitParam(name = "cameraId", value = "Camera ID")
    })
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping({"/listPage"})
    @ResponseBody
    public PageResult<List<FaceReport>> listPage(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(name = "limit", defaultValue = "10") Integer limit,
                                                 @RequestParam(name = "hasStranger", defaultValue = "1") Integer hasStranger,
                                                 Long groupId,
                                                 Long cameraId) {
        //Whether Contain Stranger produce Person
if(hasStranger == 0) {// All
hasStranger = null;
} else if(hasStranger == 2) {// not Contain
hasStranger = 0;
}

IPage<FaceReport> pageResult = faceReportService.listPage(page, limit, hasStranger, cameraId, groupId);
//
List<FaceReport> faceReports = pageResult.getRecords();
if(faceReports == null) {
faceReports = new ArrayList<>();
}
//
List<Camera> cameras = cameraService.listData();
Map<Long, Camera> cameraMap = cameras.stream().collect(Collectors.toMap(Camera::getId, s1 -> s1));
List<FaceGroup> groups = faceGroupService.list();
Map<Long, FaceGroup> groupMap = groups.stream().collect(Collectors.toMap(FaceGroup::getId, s1 -> s1));
for(FaceReport faceReport: faceReports) {
faceReport.setCamera(cameraMap.get(faceReport.getCameraId()));
faceReport.setGroup(groupMap.get(faceReport.getGroupId()));
faceReport.setCreatedStr(DateUtil.format(faceReport.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
}
return PageResultUtils.success(pageResult.getTotal(), faceReports);
}

@ApiOperation("Page Query V2")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Pagination Number"),
@ApiImplicitParam(name ="hasStranger", value ="Whether Contain Stranger produce Person 0- All 1- is 2- No"),
@ApiImplicitParam(name ="cameraId", value ="Camera ID")
})
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/listPage2"})
@ResponseBody
public PageResult<List<FaceReport>> listPage2(@RequestParam(name ="page", defaultValue ="1") Integer page,
@RequestParam(name ="limit", defaultValue ="10") Integer limit,
@RequestParam(name ="hasStranger", defaultValue ="1") Integer hasStranger,
Long groupId,
Long cameraId,
String startDate,
String endDate,
String name,
String phone,
@RequestParam(defaultValue ="1") Integer desc) {
// Frontend Date Format Make Code Process, Backend Need Solve code
String startTime = null;
String endTime = null;
try {
if(StrUtil.isNotBlank(startDate)) {
startTime = URLDecoder.decode(startDate,"utf-8");
}
if(StrUtil.isNotBlank(endDate)) {
endTime = URLDecoder.decode(endDate,"utf-8");
}
} catch (Exception e) {
return PageResultUtils.fail("Query Date Format Error");
}

// Whether Contain Stranger produce Person
if(hasStranger == 0) {// All
hasStranger = null;
} else if(hasStranger == 1) {// not Contain
hasStranger = 1;
}

IPage<FaceReport> pageResult = faceReportService.listPage2(page, limit, hasStranger, cameraId, groupId, startTime, endTime, name, phone, desc);
//
List<FaceReport> faceReports = pageResult.getRecords();
if(faceReports == null) {
faceReports = new ArrayList<>();
}
//
List<Camera> cameras = cameraService.listData();
Map<Long, Camera> cameraMap = cameras.stream().collect(Collectors.toMap(Camera::getId, s1 -> s1));
List<FaceGroup> groups = faceGroupService.list();
Map<Long, FaceGroup> groupMap = groups.stream().collect(Collectors.toMap(FaceGroup::getId, s1 -> s1));
List<FaceUser> faceUsers = faceUserService.list();
Map<Long, FaceUser> faceUserMap = faceUsers.stream().collect(Collectors.toMap(FaceUser::getId, s1 -> s1));
for(FaceReport faceReport: faceReports) {
faceReport.setCamera(cameraMap.get(faceReport.getCameraId()));
faceReport.setGroup(groupMap.get(faceReport.getGroupId()));
faceReport.setFaceUser(faceUserMap.get(faceReport.getUserId()));
faceReport.setCreatedStr(DateUtil.format(faceReport.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
//faceReport.setFilePath(Base64.encode(faceReport.getFilePath()));
faceReport.setFilePath("");
faceReport.setSourceFile("");
}
return PageResultUtils.success(pageResult.getTotal(), faceReports);
}

@ApiOperation("By Person member ID Query Recognition Result")
@ApiImplicitParam(name ="userId", value ="Person member ID")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/listByUserId"})
@ResponseBody
public JsonResult<List<FaceReport>> listByUserId(Long userId) {
LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(FaceReport::getUserId, userId);
queryWrapper.ne(FaceReport::getCameraId, 0);
List<FaceReport> faceReports = faceReportService.list(queryWrapper);
if(faceReports == null) {
faceReports = new ArrayList<>();
}
//
List<Camera> cameras = cameraService.listData();
Map<Long, Camera> cameraMap = cameras.stream().collect(Collectors.toMap(Camera::getId, s1 -> s1));
List<FaceGroup> groups = faceGroupService.list();
Map<Long, FaceGroup> groupMap = groups.stream().collect(Collectors.toMap(FaceGroup::getId, s1 -> s1));
for(FaceReport faceReport: faceReports) {
faceReport.setCamera(cameraMap.get(faceReport.getCameraId()));
faceReport.setGroup(groupMap.get(faceReport.getGroupId()));
faceReport.setCreatedStr(DateUtil.format(faceReport.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
//faceReport.setFilePath(Base64.encode(faceReport.getFilePath()));
faceReport.setSourceFile("");
faceReport.setFilePath("");
}
return JsonResultUtils.success(faceReports);
}

@ApiOperation("By Person member ID Query Recognition Result")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/listUserInfo"})
@ResponseBody
public JsonResult<FaceReportUserInfoDTO> listUserInfo(@RequestBody FaceReportUserPageVo pageVo) {
FaceReportUserInfoDTO dto = new FaceReportUserInfoDTO();

// Person member Info
FaceUser faceUser = faceUserService.getById(pageVo.getUserId());
if(faceUser == null) {
return JsonResultUtils.success(dto);
}

// Group Info
FaceGroup faceGroup = faceGroupService.getById(faceUser.getGroupId());

// Query Alarm List
List<FaceReport> faceReportList = faceReportService.listUserAllV2(pageVo);
if(ObjectUtil.isEmpty(faceReportList)) {
return JsonResultUtils.success(dto);
}

// Iterate All Camera
List<Long> cameraIds = new ArrayList<>();
for(FaceReport faceReport: faceReportList) {
if(faceReport.getCameraId()!= null &&!cameraIds.contains(faceReport.getCameraId())) {
cameraIds.add(faceReport.getCameraId());
}
}

// No One and most after One Alarm
FaceReport firstReport = faceReportList.get(0);
FaceReport lastReport = faceReportList.get(faceReportList.size() - 1);

// Camera List
Map<Long, String> cameraMap = cameraService.toMap();

List<FaceReportCameraDTO> faceReportCameraDTOList = new ArrayList<>();
for(Long cameraId: cameraIds) {
faceReportCameraDTOList.add(FaceReportCameraDTO.builder().id(cameraId).name(cameraMap.getOrDefault(cameraId,"-")).build());
}

// Data
dto.setUserId(faceUser.getId());
dto.setUserName(faceUser.getName());
dto.setGroupName(faceGroup == null?"-": faceGroup.getName());
dto.setReportCount(faceReportList.size());
dto.setStartCameraDate(TimeUtils.toYmdhms(firstReport.getCreatedAt()));
dto.setEndCameraDate(TimeUtils.toYmdhms(lastReport.getCreatedAt()));
dto.setStartCameraName(cameraMap.getOrDefault(firstReport.getCameraId(),"-"));
dto.setEndCameraName(cameraMap.getOrDefault(lastReport.getCameraId(),"-"));
dto.setCameras(faceReportCameraDTOList);
return JsonResultUtils.success(dto);
}

@ApiOperation("By Person member ID Query Recognition Result")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/listUserPage"})
@ResponseBody
public PageResult<List<FaceReportInfoDTO>> listUserPage(@RequestBody FaceReportUserPageVo pageVo) {
List<FaceReportInfoDTO> dtoList = new ArrayList<>();

// Person member Info
FaceUser faceUser = faceUserService.getById(pageVo.getUserId());
if(faceUser == null) {
return PageResultUtils.success(0L, dtoList);
}

// Query Alarm List
IPage<FaceReport> pageResult = faceReportService.listUserPageV2(pageVo);
List<FaceReport> faceReportList = pageResult.getRecords();
if(ObjectUtil.isEmpty(faceReportList)) {
return PageResultUtils.success(0L, dtoList);
}

// Camera List
Map<Long, String> cameraMap = cameraService.toMap();

// Query Group
List<FaceGroup> groups = faceGroupService.list();
Map<Long, String> groupMap = groups.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(FaceGroup::getId, FaceGroup::getName));

// Data Back
for(FaceReport faceReport: faceReportList) {
FaceReportInfoDTO dto = new FaceReportInfoDTO();
dto.setId(faceReport.getId());
dto.setUserId(faceReport.getUserId());
dto.setCameraName(cameraMap.getOrDefault(faceReport.getCameraId(),"-"));
dto.setGroupName(groupMap.getOrDefault(faceUser.getGroupId(),"-"));
dto.setSimiliarity(faceReport.getSimilarity() +"");
dto.setReportTime(TimeUtils.toYmdhms(faceReport.getCreatedAt()));
dtoList.add(dto);
}
return PageResultUtils.success(pageResult.getTotal(), dtoList);
}


// /**
// * Alarm Image
// * @param filepath
// * @param response
// * @throws Exception
// */
// @ApiOperation("Person member Image show show")
// ////@ApiImplicitParam(name ="filepath", value ="File Path")
// @GetMapping({"/image"})
// public void getImageAsByteArray(String filepath, HttpServletResponse response) {
// boolean hasErr = true;
// if(StrUtil.isNotBlank(filepath)) {
// filepath = Base64.decodeStr(filepath);
// try (FileInputStream fis = new FileInputStream(new File(filepath))) {
// response.setContentType("image/jpeg");
// fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
// hasErr = false;
//} catch (Exception e) {
// e.printStackTrace();
//}
//}
// //
// if(hasErr) {
// try {
// response.setContentType("image/png");
// InputStream in = ImageController.class.getResourceAsStream("/images/img_404.png");
// IOUtils.copy(in, response.getOutputStream());
//} catch (Exception e) {
// e.printStackTrace();
//}
//}
//}

@ApiOperation("Person member Image show show")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="id", value ="Alarm ID"),
@ApiImplicitParam(name ="type", value ="Image Type,0- small image, Other - original image", example ="0", dataType ="int")
})
@SaCheckPermission(value = {"faceControl-faceHistory","faceControl-stranger"}, mode = SaMode.OR)
@GetMapping({"/image"})
public void image(@RequestParam(name ="id") Long id, @RequestParam(defaultValue ="0") Integer type, HttpServletResponse response) {
FaceReport faceReport = faceReportService.getById(id);
// Alarm does not exist
if(faceReport == null) {
return;
}

// File Path
String filepath = type == 0? faceReport.getFilePath(): faceReport.getSourceFile();

// Image does not exist
if(!FileUtil.exist(filepath) ||!FileUtil.isFile(filepath)) {
return;
}

// input out
try (FileInputStream fis = new FileInputStream(filepath)) {
response.setContentType("image/jpeg");
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
// e.printStackTrace();
}
}

@ApiOperation("Clear Divide All Recognition Result")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@GetMapping({"/clearReport"})
@ResponseBody
public JsonResult<Void> clearReport() {
List<FaceReport> reportList = faceReportService.list();
for (FaceReport report: reportList) {
FileUtil.del(report.getFilePath());
faceReportService.removeById(report.getId());
}
return JsonResultUtils.success();
}

@ApiOperation("Remove re reply Set")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/setting/duplicate"})
@ResponseBody
public JsonResult<Void> settingDuplicate(@RequestBody FaceReportSettingDuplicateModifyVo modifyVo) {
if(modifyVo.getMinute() == null || modifyVo.getMinute() <= 0) {
return JsonResultUtils.fail("Param Error");
}
configService.saveData("Face Alarm Remove re reply Set","faceReportDuplicate", String.valueOf(modifyVo.getMinute()));
configService.evictByTag("faceReportDuplicate");

// Update Threshold
ApiFaceFilter.getInst().setThreshold(modifyVo.getMinute());

return JsonResultUtils.success();
}

@ApiOperation("Remove re reply Set return show")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@GetMapping({"/setting/duplicate"})
@ResponseBody
public JsonResult<String> getSettingDuplicate() {
String faceReportDuplicate = configService.getByValTag("faceReportDuplicate");
if(StrUtil.isBlank(faceReportDuplicate)) {
faceReportDuplicate ="300";
}
return JsonResultUtils.success(faceReportDuplicate);
}

@ApiOperation("Face Recognition Detail")
//@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/info"})
@ResponseBody
public JsonResult<FaceReportInfoDTO> info(@RequestBody FaceReportInfoVo infoVo) {
if(infoVo.getId() == null) {
return JsonResultUtils.fail("Param Error");
}

FaceReport faceReport = faceReportService.getById(infoVo.getId());
if(faceReport == null) {
return JsonResultUtils.success(new FaceReportInfoDTO());
}

// Recognition Info
JSONObject resultJson = JSON.parseObject(faceReport.getResultJson());
String similiarity = resultJson.getString("similiarity");
similiarity = handleSimiliarity(similiarity);

// Camera Info
Camera camera = cameraService.getById(faceReport.getCameraId());
String cameraName = camera == null?"-": camera.getName();

// Person member Info
FaceUser faceUser = faceUserService.getById(faceReport.getUserId());
String userName = faceUser == null?"-": faceUser.getName();

// Group Info
String groupName ="-";
if(faceUser!= null && faceUser.getGroupId()!= null) {
FaceGroup faceGroup = faceGroupService.getById(faceUser.getGroupId());
groupName = faceGroup == null?"-": faceGroup.getName();
}

//
FaceReportInfoDTO faceReportInfoDTO = new FaceReportInfoDTO();
faceReportInfoDTO.setId(faceReport.getId());
faceReportInfoDTO.setCameraName(cameraName);
faceReportInfoDTO.setReportTime(DateUtil.format(faceReport.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
faceReportInfoDTO.setSimiliarity(similiarity);
faceReportInfoDTO.setUserName(userName);
faceReportInfoDTO.setUserId(faceReport.getUserId());
faceReportInfoDTO.setGroupName(groupName);
faceReportInfoDTO.setStranger(faceReport.getHasStranger() == 1?"Stranger produce Person":"");
return JsonResultUtils.success(faceReportInfoDTO);
}

@ApiOperation("Download original image and Snapshot image")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="id", value ="Alarm id", dataType ="Long", required = true, example ="1"),
@ApiImplicitParam(name ="type", value ="Download Type, can select Value:0- original image,1- Snapshot image", dataType ="Integer", required = true, example ="0"),

})
//@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@GetMapping({"/download/image"})
public void downloadImage(@RequestParam(name ="id", required = true) @ApiParam(hidden = true) Long id, @RequestParam(name ="type", required = true) @ApiParam(hidden = true) Integer type, HttpServletResponse response) {
// Query Alarm
FaceReport faceReport = faceReportService.getById(id);
if(faceReport == null) {
return;
}

try {
String file = null;

// original image
if(type == 0) {
file = faceReport.getSourceFile();
}

// Snapshot image
if(type == 1) {
file = faceReport.getFilePath();
}

// File Is Empty
if(StrUtil.isBlank(file)) {
return;
}

// File does not exist
if(!FileUtil.exist(file)) {
return;
}

//
response.setHeader("Content-Type","application/octet-stream");
response.setHeader("Content-Disposition","attachment;filename="+ java.net.URLEncoder.encode(FileUtil.getName(file), StandardCharsets.UTF_8.toString()));
BufferedInputStream in = new BufferedInputStream(Files.newInputStream(new File(file).toPath()));
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
//e.printStackTrace();
}
}

@ApiOperation("Get up One / down One Alarm ID")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/nearly"})
@ResponseBody
public JsonResult<Long> nearly(@RequestBody FaceReportPageVo pageVo) {
// Current Face Alarm ID
if(pageVo.getReportId() == null) {
return JsonResultUtils.fail("Param Error");
}

// up One or down One
if(pageVo.getType() == null) {
return JsonResultUtils.fail("Param Error");
}

// select in Group, But not Select Camera
if(ObjectUtil.isNotEmpty(pageVo.getGroupIds()) && ObjectUtil.isEmpty(pageVo.getCameraIds())) {
List<Long> cameraIds = new ArrayList<>();
for(Long groupId: pageVo.getGroupIds()) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(groupId);
if(ObjectUtil.isNotEmpty(cameraGroupItemList)) {
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).filter(Objects::nonNull).collect(Collectors.toList());
if(ObjectUtil.isNotEmpty(currentCameraIds)) {
cameraIds.addAll(currentCameraIds);
}
}
}

// no Camera
if(ObjectUtil.isEmpty(cameraIds)) {
return JsonResultUtils.success(0L);
}

//
pageVo.setCameraIds(cameraIds);
}

// not select in Group, But select in Camera
//if(ObjectUtil.isEmpty(pageVo.getGroupIds()) && ObjectUtil.isNotEmpty(pageVo.getCameraIds())) {
// not Use Process
//}

// not select in Group, same Hour not select in Camera
// not Use Process

// select Fixed User
if(StrUtil.isNotBlank(pageVo.getUserName())) {
List<FaceUser> faceUserList = faceUserService.listLikeName(pageVo.getUserName());
if(ObjectUtil.isNotEmpty(faceUserList)) {
List<Long> userIds = faceUserList.stream().map(FaceUser::getId).collect(Collectors.toList());
pageVo.setUserIds(userIds);
}
}

Long nearlyReportId = 0L;

//
FaceReport faceReport = faceReportService.getNearly(pageVo);
if(faceReport!= null) {
nearlyReportId = faceReport.getId();
}

// not has Record
return JsonResultUtils.success(nearlyReportId);
}

@ApiOperation("Page Query _V2")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/v2/page"})
@ResponseBody
public PageResult<List<FaceReportInfoDTO>> pageV2(@RequestBody FaceReportPageVo pageVo) {
// select in Group, But not Select Camera
if(ObjectUtil.isNotEmpty(pageVo.getGroupIds()) && ObjectUtil.isEmpty(pageVo.getCameraIds())) {
List<Long> cameraIds = new ArrayList<>();
for(Long groupId: pageVo.getGroupIds()) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(groupId);
if(ObjectUtil.isNotEmpty(cameraGroupItemList)) {
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).filter(Objects::nonNull).collect(Collectors.toList());
if(ObjectUtil.isNotEmpty(currentCameraIds)) {
cameraIds.addAll(currentCameraIds);
}
}
}

// no Camera
if(ObjectUtil.isEmpty(cameraIds)) {
return PageResultUtils.success(0L, new ArrayList<>());
}

//
pageVo.setCameraIds(cameraIds);
}

// not select in Group, But select in Camera
//if(ObjectUtil.isEmpty(pageVo.getGroupIds()) && ObjectUtil.isNotEmpty(pageVo.getCameraIds())) {
// not Use Process
//}

// not select in Group, same Hour not select in Camera
// not Use Process

// select Fixed User
if(StrUtil.isNotBlank(pageVo.getUserName())) {
List<FaceUser> faceUserList = faceUserService.listLikeName(pageVo.getUserName());
if(ObjectUtil.isNotEmpty(faceUserList)) {
List<Long> userIds = faceUserList.stream().map(FaceUser::getId).collect(Collectors.toList());
pageVo.setUserIds(userIds);
} else {
return PageResultUtils.success(0L, new ArrayList<>());
}
}

// Page Query
IPage<FaceReport> faceReportIPage = faceReportService.listPageV5(pageVo);
List<FaceReport> faceReportList = faceReportIPage.getRecords();
if(ObjectUtil.isEmpty(faceReportList)) {
return PageResultUtils.success(0L, new ArrayList<>());
}

// Query Camera
List<Camera> cameraList = cameraService.listData();
Map<Long, String> cameraMap = cameraList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Camera::getId, Camera::getName));

// Query Face Group
List<FaceGroup> faceGroupList = faceGroupService.list();
Map<Long, String> faceGroupMap = faceGroupList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(FaceGroup::getId, FaceGroup::getName));

List<FaceUser> faceUserList = faceUserService.list();
Map<Long, String> faceUserMap = faceUserList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(FaceUser::getId, FaceUser::getName));
Map<Long, Long> faceUserGroupMap = faceUserList.stream().filter(s -> ObjectUtil.isNotNull(s.getGroupId())).collect(Collectors.toMap(FaceUser::getId, FaceUser::getGroupId));

//
List<FaceReportInfoDTO> faceReportInfoDTOList = new ArrayList<>();
for(FaceReport faceReport: faceReportList) {
FaceReportInfoDTO dto = new FaceReportInfoDTO();
dto.setId(faceReport.getId());
dto.setCameraName(cameraMap.getOrDefault(faceReport.getCameraId(),"Unknown"));
dto.setGroupName(faceGroupMap.getOrDefault(faceUserGroupMap.get(faceReport.getUserId()),"Unknown"));
dto.setReportTime(DateUtil.format(faceReport.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
dto.setStrangerType(faceReport.getHasStranger());
dto.setStranger(dto.getStrangerType() == 1?"Stranger produce Person":"");
dto.setUserName(dto.getStrangerType() == 1?"Stranger produce Person": faceUserMap.getOrDefault(faceReport.getUserId(),"Unknown"));
dto.setUserId(faceReport.getUserId());
faceReportInfoDTOList.add(dto);
}
return PageResultUtils.success(faceReportIPage.getTotal(), faceReportInfoDTOList);
}

@ApiOperation("Camera Query _V2")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/v2/cameras"})
@ResponseBody
public JsonResult<List<FaceReportPageCameraDTO>> cameraV2(@RequestBody FaceReportPageCameraVo cameraVo) {
// Query All Camera
List<Camera> cameraList = cameraService.list();

//
List<FaceReportPageCameraDTO> dtoList = new ArrayList<>();

// not has Select Group
if(ObjectUtil.isEmpty(cameraVo.getGroupIds())) {

for(Camera camera: cameraList) {
FaceReportPageCameraDTO dto = new FaceReportPageCameraDTO();
dto.setCameraId(camera.getId());
dto.setCameraName(camera.getName());
dtoList.add(dto);
}
return JsonResultUtils.success(dtoList);
}

// Select group ID
List<Long> cameraIds = new ArrayList<>();
for(Long groupId: cameraVo.getGroupIds()) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(groupId);
if(ObjectUtil.isNotEmpty(cameraGroupItemList)) {
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).filter(Objects::nonNull).collect(Collectors.toList());
if(ObjectUtil.isNotEmpty(currentCameraIds)) {
cameraIds.addAll(currentCameraIds);
}
}
}

// not has can Use Camera?
if(ObjectUtil.isEmpty(cameraIds)) {
return JsonResultUtils.success(dtoList);
}

//
Map<Long, String> cameraMap = cameraList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Camera::getId, Camera::getName));
for(Long cameraId: cameraIds) {
FaceReportPageCameraDTO dto = new FaceReportPageCameraDTO();
dto.setCameraId(cameraId);
dto.setCameraName(cameraMap.getOrDefault(cameraId,"-"));
dtoList.add(dto);
}
return JsonResultUtils.success(dtoList);
}

/**
* Process Similarity Bit Number
* @param s
* @return
*/
private String handleSimiliarity(String s) {
if(StrUtil.isBlank(s)) {
return"0.00";
}

int i = s.indexOf(".");
if(i <= 0) {
return s;
}

String p = s.substring(0, i);
String e = s.substring(i + 1);
if(e.length() <= 2) {
return p +"."+ e;
}
return p +"."+ e.substring(0, 2);
}
}

package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.entity.CameraGroupItem;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.face.*;
import com.yihecode.camera.ai.netty.MessageSendHandler;
import com.yihecode.camera.ai.netty.data.FaceRecognizeResponse;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.face.*;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.face.dto.*;
import com.yihecode.camera.ai.web.face.vo.*;
import com.yihecode.camera.ai.web.vo.IdVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* Face Search
*
* @author zhou
* @since 2025.6.20
*/
@Api(tags = "Face Recognition _ Stranger produce Person Search")
@Slf4j
@RestController
@RequestMapping("face/track/search/")
public class FaceTrackSearchController {

    @Autowired
    private FaceTrackSearchService faceTrackSearchService;

    @Autowired
    private FaceTrackFlowService faceTrackFlowService;

    @Autowired
    private MessageSendHandler messageSendHandler;

    @Autowired
    private LocationService locationService;

    @Autowired
    private FaceReportService faceReportService;

    @Autowired
    private FaceUserService faceUserService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CameraGroupService cameraGroupService;

    @Autowired
    private CameraGroupItemService cameraGroupItemService;

    @Autowired
    private FaceTrackConfigService faceTrackConfigService;

    @Autowired
    private FaceTrackCameraService faceTrackCameraService;

    @Value("${uploadDir}")
    private String uploadDir;

    /**
* Query All Data
* @return
*/
    @ApiOperation("Search History")
    @SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
    @PostMapping("page")
    public PageResult<?> page(@RequestBody FaceSearchPageVo pageVo) {
        IPage<FaceTrackSearch> faceSearchIPage = faceTrackSearchService.listPage(pageVo);
        List<FaceTrackSearch> faceSearchList = faceSearchIPage.getRecords();
        if(ObjectUtil.isEmpty(faceSearchList)) {
            return PageResultUtils.success(0L, new ArrayList<>());
        }

        List<FaceSearchInfoDTO> faceSearchInfoDTOList = new ArrayList<>();
        for(FaceTrackSearch faceSearch : faceSearchList) {
            FaceSearchInfoDTO dto = new FaceSearchInfoDTO();
            dto.setId(faceSearch.getId());
            dto.setCreatedAt(TimeUtils.toYmdhms(faceSearch.getCreatedAt()));
            dto.setFilename(faceSearch.getFilename());
            faceSearchInfoDTOList.add(dto);
        }
        return PageResultUtils.success(faceSearchIPage.getTotal(), faceSearchInfoDTOList);
    }

    /**
* Upload File
* @return
*/
    @ApiOperation(value = "Search Face Upload")
    @ApiImplicitParam(name = "file", value = "Search Face Image File", required = true)
    @SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
    @PostMapping("upload")
    public JsonResult<?> upload(@RequestParam(name = "file") MultipartFile file) {
        if(file == null) {
            return JsonResultUtils.fail("File not Select");
        }

        String ext = FileUtil.extName(file.getOriginalFilename());
        if(StrUtil.isBlank(ext) || !("jpg".equalsIgnoreCase(ext) || "jpeg".equalsIgnoreCase(ext) || "png".equalsIgnoreCase(ext))) {
            return JsonResultUtils.fail("File Format Error, Only Support jpg,jpeg,png");
        }

        //Create File Directory
String dest = FileUtils.pathTo(uploadDir +"/face_track/");
if(!FileUtil.exist(dest)) {
FileUtil.mkdir(dest);
}

try {
String filename = IdUtil.fastSimpleUUID() +"."+ ext;
String filepath = FileUtils.pathTo(dest +"/"+ filename);
file.transferTo(new File(filepath));
return JsonResultUtils.success(filename);
} catch (Exception e) {
log.info("Face Search, Search Face File Upload Exception");
return JsonResultUtils.fail("File Upload Exception");
}
}

@ApiOperation(value ="Search Face Display")
@ApiImplicitParam(name ="filename", value ="Face Image File Name", required = true)
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@GetMapping("image")
public void image(@RequestParam(name ="filename") String filename, HttpServletResponse response) {
if(StrUtil.isBlank(filename)) {
return;
}

String ext = FileUtil.extName(filename);
if(StrUtil.isBlank(ext) ||!("jpg".equalsIgnoreCase(ext) ||"jpeg".equalsIgnoreCase(ext) ||"png".equalsIgnoreCase(ext))) {
return;
}

// Get File Name, Prevent../ this kind Situation
filename = FileUtil.getName(filename);

// Create File Directory
String dest = FileUtils.pathTo(uploadDir +"/face_track/"+ filename);
if(!FileUtil.exist(dest)) {
return;
}

try {
BufferedInputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(dest)));
response.setContentType("image/jpeg");
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
//
}
}

/**
* Add / Edit Data
* @return
*/
@ApiOperation(value ="Add / Edit Data")
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@PostMapping("save")
public JsonResult<Long> save(@RequestBody FaceSearchModifyVo modifyVo) throws Exception {
if(StrUtil.isBlank(modifyVo.getFilename())) {
return JsonResultUtils.fail("Face Image not Upload");
}
if(modifyVo.getStartDate() == null || modifyVo.getEndDate() == null) {
return JsonResultUtils.fail("Start or End Time not Select");
}

FaceTrackSearch faceTrackSearch = new FaceTrackSearch();
faceTrackSearch.setFilename(modifyVo.getFilename());
faceTrackSearch.setStartDate(modifyVo.getStartDate());
faceTrackSearch.setEndDate(modifyVo.getEndDate());
faceTrackSearch.setGroupIds(modifyVo.getGroupIds());
faceTrackSearch.setStatus(0);
faceTrackSearch.setCreatedAt(new Date());
faceTrackSearchService.save(faceTrackSearch);

// Async Search
CompletableFuture<FaceTrackSearchResult> future = CompletableFuture.supplyAsync(() -> {
List<Location> locationList = locationService.listFaceBox();
if(locationList.isEmpty()) {
FaceTrackSearch modify = new FaceTrackSearch();
modify.setId(faceTrackSearch.getId());
modify.setStatus(3); // Failed
modify.setResultMsg("not Use can Use Face Box");
modify.setResultAt(new Date());
faceTrackSearchService.updateById(modify);
return FaceTrackSearchResult.builder().success(false).msg("not Use can Use Face Box").build();
}

FaceTrackSearch modify = new FaceTrackSearch();
modify.setId(faceTrackSearch.getId());
modify.setStatus(1); // Search in
faceTrackSearchService.updateById(modify);

// Face Similarity Threshold
double faceSimiliarity = Convert.toDouble(configService.getByValTag("faceSimiliarity"), 0.8);

// Loop Process
boolean success = false;
StringBuilder sb = new StringBuilder();
for(Location location: locationList) {
try {
Response response = messageSendHandler.sendFaceRecognize(location, faceTrackSearch.getId());
if (response == null) {
sb.append(location.getName()).append("Recognition out current Unknown Error");
continue;
}

FaceRecognizeResponse faceRecognizeResponse = (FaceRecognizeResponse) response;
if (!faceRecognizeResponse.isStatus()) {
if (sb.length() > 0) {
sb.append(";");
}
sb.append(location.getName()).append(faceRecognizeResponse.getMsg());
continue;
}

// Parse Face Search Result
String resultJson = faceRecognizeResponse.getResultJson();
JSONObject json = JSON.parseObject(resultJson);
int faceNum = json.getIntValue("face_num");
if(faceNum == 0) {
sb.append(location.getName()).append("Recognition Success, But no Recognition Data");
continue;
}

// find out Confidence most high Person
long resultUserId = 0L;
long resultGroupId = 0L;
long resultImageId = 0L;
float resultSimiliarity = 0f;
float maxSimiliarity = 0f;
JSONArray faces = json.getJSONArray("faces");
for(int i = 0; i < faceNum; i++) {
JSONObject face = faces.getJSONObject(i);
float similarity = face.getFloatValue("similiarity");
if(similarity > maxSimiliarity) {
JSONObject userInfo = face.getJSONObject("user_info");
resultUserId = userInfo.getLongValue("user_id");
resultGroupId = userInfo.getLongValue("group_id");
resultImageId = userInfo.getLongValue("face_id");
resultSimiliarity = similarity;
}
}

// Mark Fixed Current Whether for Stranger produce Person, like Result is Stranger produce Person, that Query Result Also only can is Stranger produce Person
int resultIsStranger = 0;
if(resultSimiliarity < faceSimiliarity) {
resultIsStranger = 1;
}

// like Result Camera Group not Is Empty
List<Long> cameraIds = new ArrayList<>();
if(ObjectUtil.isNotEmpty(modifyVo.getGroupIds())) {
for(Long groupId: modifyVo.getGroupIds()) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(groupId);
if(ObjectUtil.isEmpty(cameraGroupItemList)) {
continue;
}
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).collect(Collectors.toList());;
cameraIds.addAll(currentCameraIds);
}

// not has Relate Camera?
if(cameraIds.isEmpty()) {
continue;
}
}

// Query Tracking Trajectory
List<FaceReport> faceReportList = faceReportService.listByUser(resultUserId, faceTrackSearch.getStartDate(), faceTrackSearch.getEndDate(), resultIsStranger, cameraIds);

// like Result Camera Group not Is Empty, Filter Drop no close Camera
int resultNum = faceReportList.size();
// if(ObjectUtil.isNotEmpty(modifyVo.getGroupIds())) {
// int num = 0;
// for(FaceReport faceReport: faceReportList) {
// if(faceReport.getCameraId()!= null && cameraIds.contains(faceReport.getCameraId())) {
// num++;
//}
//}
// resultNum = num;
//}

// No One Alarm and most after One Alarm Record
Date resultStartDate = null;
Date resultEndDate = null;
Long resultStartCameraId = 0L;
Long resultEndCameraId = 0L;
if(!faceReportList.isEmpty()) {
resultStartDate = faceReportList.get(0).getCreatedAt();
resultStartCameraId = faceReportList.get(0).getCameraId();
resultEndDate = faceReportList.get(faceReportList.size() - 1).getCreatedAt();
resultEndCameraId = faceReportList.get(faceReportList.size() - 1).getCameraId();

}

//
FaceTrackSearch modify2 = new FaceTrackSearch();
modify2.setId(faceTrackSearch.getId());
modify2.setStatus(2); // Success
modify2.setResultMsg("Recognition Success");
modify2.setResultUserId(resultUserId);
modify2.setResultJson(resultJson);
modify2.setResultAt(new Date());
modify2.setResultNum(resultNum);
modify2.setResultGroupId(resultGroupId);
modify2.setResultImageId(resultImageId);
modify2.setResultStartDate(resultStartDate);
modify2.setResultEndDate(resultEndDate);
modify2.setResultIsStranger(resultIsStranger);
modify2.setResultSimilarity(resultSimiliarity);
modify2.setResultStartCameraId(resultStartCameraId);
modify2.setResultEndCameraId(resultEndCameraId);
faceTrackSearchService.updateById(modify2);

// Save Search ID and Alarm ID Relate close System
if(!faceReportList.isEmpty()) {
List<FaceTrackFlow> faceTrackFlowList = new ArrayList<>();
for(FaceReport faceReport: faceReportList) {
// // like Result Camera Group not Is Empty, Filter Drop no close Camera
// if(ObjectUtil.isNotEmpty(modifyVo.getGroupIds())) {
// if(faceReport.getCameraId() == null ||!cameraIds.contains(faceReport.getCameraId())) {
// continue;
//}
//}

FaceTrackFlow faceTrackFlow = new FaceTrackFlow();
faceTrackFlow.setReportId(faceReport.getId());
faceTrackFlow.setReportAt(faceReport.getCreatedAt());
faceTrackFlow.setCameraId(faceReport.getCameraId());
faceTrackFlow.setSimilarity(faceReport.getSimilarity());
faceTrackFlow.setSearchId(faceTrackSearch.getId());
faceTrackFlow.setUserId(faceReport.getUserId());
faceTrackFlow.setGroupId(faceReport.getGroupId());
faceTrackFlowList.add(faceTrackFlow);
}
faceTrackFlowService.saveBatch(faceTrackFlowList);
}

success = true;
break;
} catch (Exception e) {
//
}
}

if (!success) {
FaceTrackSearch modify2 = new FaceTrackSearch();
modify2.setId(faceTrackSearch.getId());
modify2.setStatus(3); // Failed
modify2.setResultMsg(sb.toString());
modify2.setResultAt(new Date());
faceTrackSearchService.updateById(modify2);

return FaceTrackSearchResult.builder().success(false).msg(sb.toString()).build();
}

return FaceTrackSearchResult.builder().success(true).msg("Search Success").build();
});

FaceTrackSearchResult result = future.get();
if(result.isSuccess()) {
return JsonResultUtils.success(faceTrackSearch.getId());
} else {
return JsonResultUtils.fail(result.getMsg());
}
}

/**
* Delete Data
* @return
*/
@ApiOperation(value ="Delete Data")
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@PostMapping("delete")
public JsonResult<Void> delete(@RequestBody IdVo idVo) {
if(idVo.getId() == null) {
return JsonResultUtils.fail("Param Error");
}

faceTrackSearchService.removeData(idVo.getId());
return JsonResultUtils.success();
}

/**
* Detail
* @return
*/
@ApiOperation(value ="Detail Data")
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@PostMapping("info")
public JsonResult<FaceTrackSearchInfoDTO> info(@RequestBody IdVo idVo) {
if(idVo.getId() == null) {
return JsonResultUtils.fail("Param Error");
}

FaceTrackSearch faceTrackSearch = faceTrackSearchService.getById(idVo.getId());
if(faceTrackSearch == null) {
return JsonResultUtils.fail("Search does not exist");
}

FaceTrackSearchInfoDTO dto = new FaceTrackSearchInfoDTO();
dto.setId(faceTrackSearch.getId());
dto.setStatus(faceTrackSearch.getStatus());
dto.setResultAt(TimeUtils.toYmdhms(faceTrackSearch.getResultAt()));
dto.setResultMsg(faceTrackSearch.getResultMsg());
dto.setResultNum(faceTrackSearch.getResultNum());
dto.setResultStartDate(TimeUtils.toYmdhms(faceTrackSearch.getResultStartDate()));
dto.setResultEndDate(TimeUtils.toYmdhms(faceTrackSearch.getResultEndDate()));
dto.setResultStartCameraName(getCameraName(faceTrackSearch.getResultStartCameraId()));
dto.setResultEndCameraName(getCameraName(faceTrackSearch.getResultEndCameraId()));
return JsonResultUtils.success(dto);
}

/**
* Detail
* @return
*/
@ApiOperation(value ="Search Result Page List")
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@PostMapping("flow/page")
public PageResult<List<FaceTrackFlowListDTO>> flowPage(@RequestBody FaceTrackFlowPageListVo listVo) {
if(listVo.getSearchId() == null) {
return PageResultUtils.fail("Search ID Error");
}

IPage<FaceTrackFlow> pageResult = faceTrackFlowService.listPage(listVo);
List<FaceTrackFlow> records = pageResult.getRecords();

List<FaceTrackFlowListDTO> dtoList = new ArrayList<>();

if(ObjectUtil.isNotEmpty(records)) {
Map<Long, String> cameraMap = cameraService.toMap();
//
for(FaceTrackFlow faceTrackFlow: records) {
FaceTrackFlowListDTO dto = new FaceTrackFlowListDTO();
dto.setCameraName(cameraMap.getOrDefault(faceTrackFlow.getCameraId(),"-"));
dto.setReportAt(TimeUtils.toYmdhms(faceTrackFlow.getReportAt()));
dto.setReportId(faceTrackFlow.getReportId());
dto.setSimilarity(faceTrackFlow.getSimilarity());
dtoList.add(dto);
}
}
return PageResultUtils.success(pageResult.getTotal(), dtoList);
}

/**
* Detail
* @return
*/
@ApiOperation(value ="Search Result Path List")
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@PostMapping("flow/path")
public JsonResult<List<FaceTrackFlowPathDTO>> flowPath(@RequestBody FaceTrackFlowPathListVo listVo) {
if(listVo.getSearchId() == null) {
return JsonResultUtils.fail("Search ID Error");
}

// Query All Path Record
List<FaceTrackFlow> faceTrackFlowList = faceTrackFlowService.listData(listVo);

//
List<FaceTrackFlowPathDTO> dtoList = new ArrayList<>();
if(ObjectUtil.isNotEmpty(faceTrackFlowList)) {
// Camera
Map<Long, String> cameraMap = cameraService.toMap();

// All User
List<FaceUser> faceUserList = faceUserService.list();
Map<Long, String> faceUserMap = faceUserList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(FaceUser::getId, FaceUser::getName));

// All Group
// List<FaceGroup> faceGroupList = faceGroupService.list();
// Map<Long, String> faceGroupMap = faceGroupList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(FaceGroup::getId, FaceGroup::getName));

// Query Camera Group List
List<CameraGroup> cameraGroupList = cameraGroupService.list();
Map<Long, String> cameraGroupMap = cameraGroupList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(CameraGroup::getId, CameraGroup::getName));

// Query Camera Group
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.list();
Map<Long, Long> cameraGroupItemMap = cameraGroupItemList.stream().collect(Collectors.toMap(CameraGroupItem::getCameraId, CameraGroupItem::getGroupId));

// front One Camera ID, like Result front One Camera ID and after One Camera ID Consistent, rule only Keep No One Camera ID
Long prevCameraId = 0L;

//
Map<Long, List<Integer>> cameraPointsMap = new HashMap<>();
FaceTrackConfig faceTrackConfig = faceTrackConfigService.getPrimary();
if(faceTrackConfig!= null) {
List<FaceTrackCamera> faceTrackCameraList = faceTrackCameraService.listByConfig(faceTrackConfig.getId());
if(ObjectUtil.isNotEmpty(faceTrackCameraList)) {
for(FaceTrackCamera faceTrackCamera: faceTrackCameraList) {
cameraPointsMap.put(faceTrackCamera.getCameraId(), faceTrackCamera.getPosition());
}
}
}

// Data Process and Filter re reply Camera
for(FaceTrackFlow faceTrackFlow: faceTrackFlowList) {
// front after Two ID Consistent
if(prevCameraId.equals(faceTrackFlow.getCameraId())) {
continue;
}

//
prevCameraId = faceTrackFlow.getCameraId();

//
Long cameraGroupId = cameraGroupItemMap.get(faceTrackFlow.getCameraId());
String cameraGroupName ="-";
if(cameraGroupId!= null) {
cameraGroupName = cameraGroupMap.getOrDefault(cameraGroupId,"-");
}

//
List<Integer> cameraPoints = cameraPointsMap.get(faceTrackFlow.getCameraId());

//
FaceTrackFlowPathDTO dto = new FaceTrackFlowPathDTO();
dto.setCameraName(cameraMap.getOrDefault(faceTrackFlow.getCameraId(),"-"));
dto.setReportAt(TimeUtils.toYmdhms(faceTrackFlow.getReportAt()));
dto.setReportId(faceTrackFlow.getReportId());
dto.setSimilarity(faceTrackFlow.getSimilarity());
dto.setUserId(faceTrackFlow.getUserId());
dto.setUserName(faceUserMap.getOrDefault(faceTrackFlow.getUserId(),"-"));
dto.setCameraGroupName(cameraGroupName);
dto.setCameraPoints(cameraPoints == null? Arrays.asList(0, 0, 0, 0): cameraPoints);
dtoList.add(dto);
}
}
return JsonResultUtils.success(dtoList);
}

@ApiOperation(value ="Search Result Camera List")
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@PostMapping("flow/cameras")
public JsonResult<List<FaceTrackFlowCameraDTO>> flowPath(@RequestBody FaceTrackFlowCameraVo cameraVo) {
if(cameraVo.getSearchId() == null) {
return JsonResultUtils.fail("Search ID Error");
}

FaceTrackSearch faceTrackSearch = faceTrackSearchService.getById(cameraVo.getSearchId());
if(faceTrackSearch == null) {
return JsonResultUtils.fail("Search Result does not exist");
}

List<Long> groupIds = faceTrackSearch.getGroupIds();
if(ObjectUtil.isNotEmpty(groupIds)) {
List<Long> cameraIds = new ArrayList<>();
for(Long groupId: groupIds) {
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.listByGroup(groupId);
if(ObjectUtil.isEmpty(cameraGroupItemList)) {
continue;
}
List<Long> currentCameraIds = cameraGroupItemList.stream().map(CameraGroupItem::getCameraId).collect(Collectors.toList());;
cameraIds.addAll(currentCameraIds);
}

//
List<FaceTrackFlowCameraDTO> dtoList = new ArrayList<>();
Map<Long, String> cameraMap = cameraService.toMap();
for(Long cameraId: cameraIds) {
String cameraName = cameraMap.get(cameraId);

FaceTrackFlowCameraDTO dto = new FaceTrackFlowCameraDTO();
dto.setId(cameraId);
dto.setName(cameraName);
dtoList.add(dto);
}
return JsonResultUtils.success(dtoList);
} else {
List<FaceTrackFlowCameraDTO> dtoList = new ArrayList<>();
List<Camera> cameraList = cameraService.listData();
for(Camera camera: cameraList) {
FaceTrackFlowCameraDTO dto = new FaceTrackFlowCameraDTO();
dto.setId(camera.getId());
dto.setName(camera.getName());
dtoList.add(dto);
}
return JsonResultUtils.success(dtoList);
}
}

@ApiOperation("Get up One / down One Alarm ID")
@SaCheckPermission(value = {"faceControl-stranger"}, mode = SaMode.OR)
@PostMapping({"/nearly"})
@ResponseBody
public JsonResult<Long> nearly(@RequestBody FaceTrackSearchNearlyVo nearlyVo) {
if(nearlyVo.getSearchId() == null) {
return JsonResultUtils.fail("Param Error");
}

if(nearlyVo.getReportId() == null) {
return JsonResultUtils.fail("Param Error");
}

if(nearlyVo.getType() == null) {
return JsonResultUtils.fail("Param Error");
}

//
FaceTrackFlow faceTrackFlow = faceTrackFlowService.getNearly(nearlyVo.getSearchId(), nearlyVo.getReportId(), nearlyVo.getType());
if(faceTrackFlow == null) {
return JsonResultUtils.success(0L);
}

// not has Record
return JsonResultUtils.success(faceTrackFlow.getReportId());
}

/**
* Get Camera Name
* @param cameraId
* @return
*/
private String getCameraName(Long cameraId) {
if(cameraId == null || cameraId == 0) {
return"";
}
Camera camera = cameraService.getById(cameraId);
return camera == null?"": camera.getName();
}
}

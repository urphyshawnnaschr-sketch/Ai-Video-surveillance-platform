package com.yihecode.camera.ai.web.api.face;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.yihecode.camera.ai.comm.CommService;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.face.FaceImage;
import com.yihecode.camera.ai.entity.face.FaceSyncBox;
import com.yihecode.camera.ai.entity.face.FaceTrackSearch;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.face.FaceImageService;
import com.yihecode.camera.ai.service.face.FaceSyncBoxService;
import com.yihecode.camera.ai.service.face.FaceTrackSearchService;
import com.yihecode.camera.ai.service.face.FaceUserService;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.api.face.dto.FaceFullDataDTO;
import com.yihecode.camera.ai.web.api.face.dto.FaceSyncBoxDTO;
import com.yihecode.camera.ai.web.api.face.dto.FaceSyncUrlDTO;
import com.yihecode.camera.ai.web.api.face.vo.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

/**
* Face Data Sync
*
* @author zhou
* @since 2025-07-11
*/
@ApiIgnore
@SaIgnore
@Slf4j
@Controller
@RequestMapping("api/face/sync")
public class ApiFaceSyncController {

    @Autowired
    private FaceSyncBoxService faceSyncBoxService;

    @Autowired
    private FaceImageService faceImageService;

    @Autowired
    private FaceUserService faceUserService;

    @Autowired
    private FaceTrackSearchService faceTrackSearchService;

    @Autowired
    private CommService commService;

    @Autowired
    private LocationService locationService;

    @Value("${uploadDir}")
    private String uploadDir;

    @SaIgnore
    @PostMapping("full_data")
    @ResponseBody
    public JsonResult<List<FaceFullDataDTO>> syncFullData(@RequestBody FaceFullDataVO vo) {
        //Validate key
if(!commService.checkKey(vo.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

// Query Box
Location location = locationService.getBoxSnForRemote(vo.getSn());
if(location == null || location.getUseType() == null ||!(location.getUseType() == 1 || location.getUseType() == 2)) {
return JsonResultUtils.fail("Box does not exist or Non Face Service Box");
}

List<FaceUser> faceUserList = faceUserService.list();
Map<Long, Long> faceGroupMap = faceUserList.stream()
.filter(faceUser -> faceUser.getGroupId()!= null)
.collect(Collectors.toMap(
FaceUser::getId,
FaceUser::getGroupId
));

List<FaceImage> faceImageList = faceImageService.list();

List<FaceFullDataDTO> dtos = new ArrayList<>();
for(FaceImage faceImage: faceImageList) {
Long userId = faceImage.getUserId();
Long groupId = faceGroupMap.get(userId);
if(groupId == null) {
continue;
}

String rowId = groupId +"_"+ faceImage.getUserId() +"_"+ faceImage.getId();

FaceFullDataDTO dto = new FaceFullDataDTO();
dto.setRowId(rowId);
dto.setGroupId(groupId);
dto.setUserId(faceImage.getUserId());
dto.setFaceId(faceImage.getId());
dtos.add(dto);
}
return JsonResultUtils.success(dtos);
}

/**
* Sync Object List
* @return
*/
@SaIgnore
@PostMapping("/objects")
@ResponseBody
public JsonResult<List<FaceSyncBoxDTO>> syncObject(@RequestBody FaceSyncObjectListVO listVO) {
// Validate key
if(!commService.checkKey(listVO.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

// Query Box
Location location = locationService.getBoxSnForRemote(listVO.getSn());
if(location == null || location.getUseType() == null ||!(location.getUseType() == 1 || location.getUseType() == 2)) {
return JsonResultUtils.fail("Box does not exist or Non Face Service Box");
}

// up order Row Column Get Data
List<FaceSyncBox> faceSyncBoxList = faceSyncBoxService.listSyncByBox(location.getId());
List<FaceSyncBoxDTO> faceSyncBoxDTOList = new ArrayList<>();
for(FaceSyncBox faceSyncBox: faceSyncBoxList) {
FaceSyncBoxDTO faceSyncBoxDTO = new FaceSyncBoxDTO();
BeanUtils.copyProperties(faceSyncBox, faceSyncBoxDTO);
faceSyncBoxDTOList.add(faceSyncBoxDTO);
}
return JsonResultUtils.success(faceSyncBoxDTOList);
}

/**
* Sync Image BASE64
* @return
*/
@SaIgnore
@PostMapping("image")
@ResponseBody
public JsonResult<?> syncImage(@RequestBody FaceSyncImageVO imageVO) {
// Validate key
if(!commService.checkKey(imageVO.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

// Query Box
Location location = locationService.getBoxSnForRemote(imageVO.getSn());
if(location == null || location.getUseType() == null ||!(location.getUseType() == 1 || location.getUseType() == 2)) {
return JsonResultUtils.fail("Box does not exist or Non Face Service Box");
}

// Query Image
FaceImage faceImage = faceImageService.getById(imageVO.getImageId());
if(faceImage == null) {
return JsonResultUtils.fail("Image ID Param Error");
}

// File Path
String filepath = getImageFile(faceImage.getUserId(), faceImage.getImgUrl());
if(filepath == null) {
return JsonResultUtils.fail("Image does not exist");
}

// BASE64 String
BufferedImage image = ImgUtil.read(filepath);
return JsonResultUtils.success(ImgUtil.toBase64(image, FileUtil.extName(filepath)));
}

/**
* Sync Process Result
* @return
*/
@SaIgnore
@PostMapping("result")
@ResponseBody
public JsonResult<?> syncResult(@RequestBody FaceSyncResultVO resultVO) {
// Validate key
if(!commService.checkKey(resultVO.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

//
if(resultVO.getStatus() == null) {
return JsonResultUtils.fail("status Value Error");
}

// Query Box
Location location = locationService.getBoxSnForRemote(resultVO.getSn());
if(location == null || location.getUseType() == null ||!(location.getUseType() == 1 || location.getUseType() == 2)) {
return JsonResultUtils.fail("Box does not exist or Non Face Service Box");
}

FaceSyncBox faceSyncBox = faceSyncBoxService.getById(resultVO.getId());
if(faceSyncBox == null) {
return JsonResultUtils.fail("Data Not Exist");
}

// 1- Success
if(resultVO.getStatus() == 1) {
FaceSyncBox modifyFaceSyncBox = new FaceSyncBox();
modifyFaceSyncBox.setId(faceSyncBox.getId());
modifyFaceSyncBox.setSyncStatus(1); // Sync Complete Complete
modifyFaceSyncBox.setSyncNum(faceSyncBox.getSyncNum() + 1);
modifyFaceSyncBox.setResultStatus(1);
modifyFaceSyncBox.setResultMsg(resultVO.getMsg());
modifyFaceSyncBox.setResultAt(new Date());
faceSyncBoxService.updateById(modifyFaceSyncBox);
}

// 2- Failed
if(resultVO.getStatus() == 2) {
int syncStatus = (faceSyncBox.getSyncNum() + 1) >= 3? 2: 0; // Determine Whether super over 3 sub, super over rule not again Process

FaceSyncBox modifyFaceSyncBox = new FaceSyncBox();
modifyFaceSyncBox.setId(faceSyncBox.getId());
modifyFaceSyncBox.setSyncStatus(syncStatus); // Sync Complete Complete
modifyFaceSyncBox.setSyncNum(faceSyncBox.getSyncNum() + 1);
modifyFaceSyncBox.setResultStatus(2);
modifyFaceSyncBox.setResultMsg(resultVO.getMsg());
modifyFaceSyncBox.setResultAt(new Date());
faceSyncBoxService.updateById(modifyFaceSyncBox);
}

return JsonResultUtils.success();
}

/**
* Sync Process Result
* @return
*/
@SaIgnore
@PostMapping("url")
@ResponseBody
public JsonResult<?> syncUrl(@RequestBody FaceSyncUrlVO urlVO) {
// Validate key
if(!commService.checkKey(urlVO.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

// Query Box
Location location = locationService.getBoxSnForRemote(urlVO.getSn());
if(location == null) {
return JsonResultUtils.fail("Box does not exist");
}

// Query All Face Box
List<Location> locationList = locationService.listFaceBox();
if(locationList.isEmpty()) {
FaceSyncUrlDTO faceSyncUrlDTO = new FaceSyncUrlDTO();
faceSyncUrlDTO.setChanged(false);
faceSyncUrlDTO.setFaceHttpIp("");
faceSyncUrlDTO.setFaceSn("");
return JsonResultUtils.success(faceSyncUrlDTO);
}

// Determine Whether Exist Consistent Box
boolean found = false;
for(Location location1: locationList) {
if(StrUtil.isBlank(location1.getIpAddr()) || StrUtil.isBlank(location1.getBoxNo())) {
continue;
}

if(location1.getIpAddr().equalsIgnoreCase(urlVO.getFaceHttpIp()) && location1.getBoxNo().equalsIgnoreCase(urlVO.getFaceSn())) {
found = true;
break;
}
}

// Exist Consistent Record, Direct connect Back
if(found) {
FaceSyncUrlDTO faceSyncUrlDTO = new FaceSyncUrlDTO();
faceSyncUrlDTO.setChanged(false);
faceSyncUrlDTO.setFaceHttpIp("");
faceSyncUrlDTO.setFaceSn("");
return JsonResultUtils.success(faceSyncUrlDTO);
}

// select out One Load most small Box
List<Location> locations = new ArrayList<>();
for(Location location1: locationList) {
// lack Missing IP Address and Index, Skip
if(StrUtil.isBlank(location1.getIpAddr()) || StrUtil.isBlank(location1.getBoxNo())) {
continue;
}

// Department not One Direct, not In One Bureau Domain net, Skip
if(location1.getDepartId() == null || location1.getDepartId().equals(location.getDepartId())) {
continue;
}

locations.add(location1);
}

// No data
if(locations.isEmpty()) {
FaceSyncUrlDTO faceSyncUrlDTO = new FaceSyncUrlDTO();
faceSyncUrlDTO.setChanged(false);
faceSyncUrlDTO.setFaceHttpIp("");
faceSyncUrlDTO.setFaceSn("");
return JsonResultUtils.success(faceSyncUrlDTO);
}

// Sort
locations.sort(new Comparator<Location>() {
@Override
public int compare(Location o1, Location o2) {
return o1.getUseNum() - o2.getUseNum();
}
});

//
Location location1 = locations.get(0);
FaceSyncUrlDTO faceSyncUrlDTO = new FaceSyncUrlDTO();
faceSyncUrlDTO.setChanged(true);
faceSyncUrlDTO.setFaceHttpIp(location1.getIpAddr());
faceSyncUrlDTO.setFaceSn(location1.getBoxNo());

// Update Box make Use Plan Number +1
Location modifyLocation = new Location();
modifyLocation.setId(location1.getId());
modifyLocation.setUseNum(location1.getUseNum() + 1);
locationService.updateById(modifyLocation);

return JsonResultUtils.success(faceSyncUrlDTO);
}

/**
* Sync Image BASE64
* @return
*/
@SaIgnore
@PostMapping("search_image")
@ResponseBody
public JsonResult<?> searchImage(@RequestBody FaceSearchImageVO imageVO) {
// Validate key
if(!commService.checkKey(imageVO.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

// Query Box
Location location = locationService.getBoxSnForRemote(imageVO.getSn());
if(location == null || location.getUseType() == null ||!(location.getUseType() == 1 || location.getUseType() == 2)) {
return JsonResultUtils.fail("Box does not exist or Non Face Service Box");
}

// Query Image
FaceTrackSearch faceTrackSearch = faceTrackSearchService.getById(imageVO.getSearchId());
if(faceTrackSearch == null) {
return JsonResultUtils.fail("Search ID Param Error");
}

// File Path
String filepath = getSearchImageFile(faceTrackSearch.getFilename());
if(filepath == null) {
return JsonResultUtils.fail("Image does not exist");
}

// BASE64 String
BufferedImage image = ImgUtil.read(filepath);
return JsonResultUtils.success(ImgUtil.toBase64(image, FileUtil.extName(filepath)));
}

/**
* than for Image Sync Image BASE64
* @return
*/
@SaIgnore
@PostMapping("compare_image")
@ResponseBody
public JsonResult<?> compareImage(@RequestBody FaceCompareImageVO imageVO) {
// Validate key
if(!commService.checkKey(imageVO.getKey())) {
return JsonResultUtils.fail("key Value Error");
}

// Query Box
Location location = locationService.getBoxSnForRemote(imageVO.getSn());
if(location == null || location.getUseType() == null ||!(location.getUseType() == 1 || location.getUseType() == 2)) {
return JsonResultUtils.fail("Box does not exist or Non Face Service Box");
}

// File Path
String filepath = getCompareImageFile(imageVO.getImageName());
if(filepath == null) {
return JsonResultUtils.fail("Image does not exist");
}

// BASE64 String
BufferedImage image = ImgUtil.read(filepath);
return JsonResultUtils.success(ImgUtil.toBase64(image, FileUtil.extName(filepath)));
}

/**
* Get Image Path
* @param faceUserId
* @param imgName
* @return
*/
private String getImageFile(Long faceUserId, String imgName) {
if(faceUserId == null || StrUtil.isBlank(imgName)) {
return null;
}

String filepath = FileUtils.pathTo(uploadDir +"/face_imgs/"+ faceUserId +"/"+ imgName);
if(!FileUtil.exist(filepath)) {
return null;
}

if(!FileUtil.isFile(filepath)) {
return null;
}
return filepath;
}

/**
* Get Search Image Path
* @param imgName
* @return
*/
private String getSearchImageFile(String imgName) {
String filepath = FileUtils.pathTo(uploadDir +"/face_track/"+ imgName);
if(!FileUtil.exist(filepath)) {
return null;
}

if(!FileUtil.isFile(filepath)) {
return null;
}
return filepath;
}

/**
* Get Search Image Path
* @param imgName
* @return
*/
private String getCompareImageFile(String imgName) {
String filepath = FileUtils.pathTo(uploadDir +"/"+ imgName);
if(!FileUtil.exist(filepath)) {
return null;
}

if(!FileUtil.isFile(filepath)) {
return null;
}
return filepath;
}
}

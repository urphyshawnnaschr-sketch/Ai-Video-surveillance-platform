package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.*;
import com.yihecode.camera.ai.service.*;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import com.yihecode.camera.ai.service.map.MapObjectService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.dto.BoxCassInfoDTO;
import com.yihecode.camera.ai.web.dto.CameraCassInfoDTO;
import com.yihecode.camera.ai.web.dto.DepartCassInfoDTO;
import com.yihecode.camera.ai.web.dto.LocationDepartDTO;
import com.yihecode.camera.ai.web.vo.IdVo;
import com.yihecode.camera.ai.web.vo.LocationListData5Vo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Camera Region Node < Tree result structure > Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Api(tags = "Camera Region Node")
@SaCheckLogin
@Controller
@RequestMapping({"/location"})
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CameraAlgorithmService cameraAlgorithmService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApDepartService apDepartService;

    @Autowired
    private MapObjectService mapObjectService;

    @Autowired
    private BoxVersionService boxVersionService;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    @Value("${dataModelsDir}")
    public String MODEL_DIR;

    @ApiOperation("Query Region Detail")
    @SaCheckPermission(value = {"caske-view", "edgePlatform-casketManagement"}, mode = SaMode.OR)
    @RequestMapping({"/detail"})
    @ResponseBody
    public JsonResult<Location> editVue(@RequestBody IdVo idVo) {
        Long id = idVo.getId();
        Location location = locationService.getById(id);
        if (location == null) {
            location = new Location();
            location.setParentId(null);
            location.setParentName("");
        } else {
            //
if (location.getParentId() == null) {
location.setParentId(null);
location.setParentName("");
} else if (location.getParentId() == 0) {
location.setParentId(0L);
location.setParentName("all Bureau Node");
} else {
Location parentLocation = locationService.getById(location.getParentId());
location.setParentId(parentLocation.getId());
location.setParentName(parentLocation.getName());
}
}
return JsonResultUtils.success(location);
}

/**
* Save
*
* @param location
* @return
*/
@ApiOperation("Save Region Data")
@ApiImplicitParam(name ="location", value ="Region Entity")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult save(Location location) throws Exception {
locationService.saveNode(location);
return JsonResultUtils.success();
}

/**
* @param id
* @return
*/
@ApiOperation("Delete Region Data")
@ApiImplicitParam(name ="id", value ="Data id")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) {
Location location = locationService.getById(id);
if (location.getParentId().equals(0L)) return JsonResultUtils.fail("no Method Delete Top Level Node");
this.locationService.deleteNodes(id);
return JsonResultUtils.success();
}

/**
* tree
*
* @return
*/
@ApiOperation("Query Region Tree result structure Data")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@RequestMapping(value ="/listTree")
@ResponseBody
public List<TreeResult> listTree() {
List<Location> locationList = locationService.listData();

//
List<TreeResult> treeResultList = new ArrayList<>();
for (Location location: locationList) {
TreeResult treeResult = new TreeResult();
treeResult.setMeId(location.getId() +"");
treeResult.setText(location.getName());
treeResult.setParent(location.getParentId() +"");
treeResult.setChildren(new ArrayList<>());
treeResult.setIcon("layui-icon layui-icon-location");
treeResultList.add(treeResult);
}

//
List<TreeResult> trees = new ArrayList<>();
for (TreeResult treeResult: treeResultList) {
if (treeResult.getParent().equals("0")) {
treeResult.setParent("#");
trees.add(findChildren(treeResult, treeResultList));
}
}
return trees;
}

@ApiOperation("Query Heartbeat Info")
@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
@RequestMapping(value ="/heart")
@ResponseBody
public JsonResult<List<Location>> heart() {
List<Location> locations = locationService.listHeart();
return JsonResultUtils.success(locations);
}

/**
* tree
*
* @return
*/
@ApiOperation("Query Region Tree result structure Data")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="locationType", value ="Data Source 1 Camera Management 2 Box Management")
})
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@RequestMapping(value ="/listTree2")
@ResponseBody
public JsonResult<List<TreeResult>> listTree2(String locationType) {
List<Location> locationList = locationService.listDataByType(locationType);

//
List<TreeResult> treeResultList = new ArrayList<>();
for (Location location: locationList) {
TreeResult treeResult = new TreeResult();
treeResult.setMeId(location.getId() +"");
treeResult.setText(location.getName());
treeResult.setParent(location.getParentId() +"");
treeResult.setChildren(new ArrayList<>());
treeResult.setIcon("layui-icon layui-icon-location");
treeResult.setType(location.getType());
treeResultList.add(treeResult);
}

//
List<TreeResult> trees = new ArrayList<>();
for (TreeResult treeResult: treeResultList) {
if (treeResult.getParent().equals("0")) {
treeResult.setParent("#");
trees.add(findChildren(treeResult, treeResultList));
}
}
return JsonResultUtils.success(trees);
}

// Query child Node
private TreeResult findChildren(TreeResult tree, List<TreeResult> treeList) {
for (TreeResult node: treeList) {
if (tree.getMeId().equals(node.getParent())) {
if (tree.getChildren() == null) {
tree.setChildren(new ArrayList<>());
}
tree.getChildren().add(findChildren(node, treeList));
if (tree.getChildren()!= null && tree.getChildren().size() > 0) {
tree.setIcon("layui-icon layui-icon-home");
}
}
}
return tree;
}

@ApiOperation("Query Region Tree result structure Data, Belt Camera Data")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="locationType", value ="Data Source 1 Camera Management 2 Box Management")
})
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@RequestMapping(value ="/listTreeWithCamera")
@ResponseBody
public JsonResult<List<TreeResult>> listTreeWithCamera(String locationType, String cameraName) {
if (StringUtils.isNotBlank(cameraName)) {
List<Camera> cameraList = cameraService.listLikeName(cameraName);
List<TreeResult> cameraTreeResultList = new ArrayList<>();
for (Camera camera: cameraList) {
TreeResult treeResult = new TreeResult();
treeResult.setMeId(camera.getId() +"");
treeResult.setText(camera.getName());
treeResult.setChildren(new ArrayList<>());
treeResult.setParent(camera.getLocationId() +"");
treeResult.setIcon("layui-icon layui-icon-location");
treeResult.setType("camera");
cameraTreeResultList.add(treeResult);
}
return JsonResultUtils.success(cameraTreeResultList);
}
List<Location> locationList = locationService.listDataByType(locationType);
List<Camera> cameraList = cameraService.list();
//
List<TreeResult> treeResultList = new ArrayList<>();
for (Location location: locationList) {
TreeResult treeResult = new TreeResult();
treeResult.setMeId(location.getId() +"");
treeResult.setText(location.getName());
treeResult.setParent(location.getParentId() +"");
treeResult.setChildren(new ArrayList<>());
treeResult.setIcon("layui-icon layui-icon-location");
treeResult.setType(location.getType());
treeResultList.add(treeResult);
}
List<TreeResult> cameraTreeResultList = new ArrayList<>();
for (Camera camera: cameraList) {
TreeResult treeResult = new TreeResult();
treeResult.setMeId(camera.getId() +"");
treeResult.setText(camera.getName());
treeResult.setChildren(new ArrayList<>());
treeResult.setParent(camera.getLocationId() +"");
treeResult.setIcon("layui-icon layui-icon-location");
treeResult.setType("camera");
cameraTreeResultList.add(treeResult);
}

//
List<TreeResult> trees = new ArrayList<>();
for (TreeResult treeResult: treeResultList) {
if (treeResult.getParent().equals("0")) {
treeResult.setParent("#");
trees.add(findChildrenWithCamera(treeResult, treeResultList, cameraTreeResultList));
}
}
return JsonResultUtils.success(trees);
}

// Query child Node
private TreeResult findChildrenWithCamera(TreeResult tree, List<TreeResult> treeList, List<TreeResult> cameraList) {
if (tree.getChildren() == null) {
tree.setChildren(new ArrayList<>());
}
for (TreeResult node: treeList) {
if (tree.getMeId().equals(node.getParent())) {

tree.getChildren().add(findChildrenWithCamera(node, treeList, cameraList));
if (tree.getChildren()!= null && tree.getChildren().size() > 0) {
tree.setIcon("layui-icon layui-icon-home");
}
}
}
if (tree.getChildren().isEmpty()) {
for (TreeResult cNote: cameraList) {
if (tree.getMeId().equals(cNote.getParent())) {
tree.getChildren().add(cNote);
}
}
}
return tree;
}

@ApiOperation("Page Query Box List, not Belt Face Box")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size"),
@ApiImplicitParam(name ="status", value ="Online Status (0- Offline 1- Online)"),
@ApiImplicitParam(name ="name", value ="Box Name"),
@ApiImplicitParam(name ="departIds", value ="belong belong Department ids")
})
@SaCheckPermission(value = {"algorithm-version","edgePlatform-casketManagement"}, mode = SaMode.OR)
@PostMapping("listPage")
@ResponseBody
public PageResult<List<Location>> listPage(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="10") Integer limit, Integer status, String name, String departIds, String platform) {
//
List<Long> filterDepartIds = new ArrayList<>();
if(StrUtil.isNotBlank(departIds)) {
try {
String[] sps = departIds.split(",");
for(String sp: sps) {
filterDepartIds.add(Long.valueOf(sp));
}
} catch (Exception e) {
return PageResultUtils.fail("belong belong Department Param Error");
}
}
//
List<Long> queryDepartIds = new ArrayList<>();
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account.getIsSuper() == null || account.getIsSuper()!= 1) {
List<Long> departIdsByAccount = apDepartService.getCurrentAndChildIds(account.getDepartId()); // Non super Level Management member, Need By User The In belong belong Department in Line Query
if(departIdsByAccount == null || departIdsByAccount.isEmpty()) {
return PageResultUtils.success(0l, new ArrayList<>());
}
// Filter Frontend Request Department
if(!filterDepartIds.isEmpty()) {
for(Long filterDepartId: filterDepartIds) {
if(departIdsByAccount.contains(filterDepartId)) {
queryDepartIds.add(filterDepartId);
}
}
//
if(queryDepartIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>()); // User Request Query Department not In Current User Permission Range inner
}
} else {
queryDepartIds.addAll(departIdsByAccount); // Query Current User belong belong Department and child Department All Box
}
} else {
if(!filterDepartIds.isEmpty()) {
queryDepartIds.addAll(filterDepartIds); // super Level Management member Query User Request Department
}
}
//
IPage<Location> pageResult = locationService.listPage(page, limit, name, queryDepartIds, platform);
List<Location> records = pageResult.getRecords();
if (records == null) {
records = new ArrayList<>();
}

//
List<Depart> departs = apDepartService.listData();

//
List<Location> datas = new ArrayList<>();
for (Location record: records) {
record.setOnline("0");
record.setDepartName(apDepartService.getLinkName(record.getDepartId(), departs));
//
if (record.getBoxHeartTime()!= null && (System.currentTimeMillis() - record.getBoxHeartTime()) < 8 * 60 * 1000) {
record.setOnline("1");
}
// Filter Status
if(status == null) {
datas.add(record);
} else if(status == 0 &&"0".equals(record.getOnline())) {
datas.add(record);
} else if(status == 1 &&"1".equals(record.getOnline())) {
datas.add(record);
}
}
return PageResultUtils.success(pageResult.getTotal(), datas);
}

@ApiOperation("Page Query Box List _ Contain Face Box")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Page Size"),
@ApiImplicitParam(name ="status", value ="Online Status (0- Offline 1- Online)"),
@ApiImplicitParam(name ="name", value ="Box Name"),
@ApiImplicitParam(name ="departIds", value ="belong belong Department ids")
})
@SaCheckPermission(value = {"algorithm-version","edgePlatform-casketManagement"}, mode = SaMode.OR)
@PostMapping("listAllPage")
@ResponseBody
public PageResult<List<Location>> listAllPage(@RequestParam(defaultValue ="1") Integer page, @RequestParam(defaultValue ="10") Integer limit, Integer status, String name, String departIds, String platform) {
//
List<Long> filterDepartIds = new ArrayList<>();
if(StrUtil.isNotBlank(departIds)) {
try {
String[] sps = departIds.split(",");
for(String sp: sps) {
filterDepartIds.add(Long.valueOf(sp));
}
} catch (Exception e) {
return PageResultUtils.fail("belong belong Department Param Error");
}
}
//
List<Long> queryDepartIds = new ArrayList<>();
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account.getIsSuper() == null || account.getIsSuper()!= 1) {
List<Long> departIdsByAccount = apDepartService.getCurrentAndChildIds(account.getDepartId()); // Non super Level Management member, Need By User The In belong belong Department in Line Query
if(departIdsByAccount == null || departIdsByAccount.isEmpty()) {
return PageResultUtils.success(0l, new ArrayList<>());
}
// Filter Frontend Request Department
if(!filterDepartIds.isEmpty()) {
for(Long filterDepartId: filterDepartIds) {
if(departIdsByAccount.contains(filterDepartId)) {
queryDepartIds.add(filterDepartId);
}
}
//
if(queryDepartIds.isEmpty()) {
return PageResultUtils.success(0L, new ArrayList<>()); // User Request Query Department not In Current User Permission Range inner
}
} else {
queryDepartIds.addAll(departIdsByAccount); // Query Current User belong belong Department and child Department All Box
}
} else {
if(!filterDepartIds.isEmpty()) {
queryDepartIds.addAll(filterDepartIds); // super Level Management member Query User Request Department
}
}
//
IPage<Location> pageResult = locationService.listAllPage(page, limit, name, queryDepartIds, platform);
List<Location> records = pageResult.getRecords();
if (records == null) {
records = new ArrayList<>();
}

//
List<Depart> departs = apDepartService.listData();

//
List<Location> datas = new ArrayList<>();
for (Location record: records) {
record.setOnline("0");
record.setDepartName(apDepartService.getLinkName(record.getDepartId(), departs));
//
if (record.getBoxHeartTime()!= null && (System.currentTimeMillis() - record.getBoxHeartTime()) < 8 * 60 * 1000) {
record.setOnline("1");
}
// Filter Status
if(status == null) {
datas.add(record);
} else if(status == 0 &&"0".equals(record.getOnline())) {
datas.add(record);
} else if(status == 1 &&"1".equals(record.getOnline())) {
datas.add(record);
}
}
return PageResultUtils.success(pageResult.getTotal(), datas);
}

@ApiOperation("Restart Edge Box")
@ApiImplicitParam(name ="id", value ="Box ID")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping("/restart")
@ResponseBody
public JsonResult<?> restartAibox(Long id) {
Location location = locationService.getById(id);
if(location == null) {
return JsonResultUtils.fail("find not to Box Device");
}
//
if(StrUtil.isBlank(location.getBoxNo())) {
return JsonResultUtils.fail("Box Device not has Index Info");
}

return JsonResultUtils.fail("Function can not real current");

// // Restart
// RestartRequest request = new RestartRequest();
// request.setType(MessageType.RESTART_INFER.getText());
// request.setRequestId(IdUtil.randomUUID());
// request.setSn(location.getBoxNo());
// Response response = messageSenderAndWaiter.sendRequest(request);
// if(response == null) {
// return JsonResultUtils.fail("Restart Inference Service Failed, Unknown Error");
//}
//
// RestartResponse restartResponse = (RestartResponse) response;
// if(restartResponse.isStatus()) {
// return JsonResultUtils.success("Restart Inference Service Success");
//} else {
// return JsonResultUtils.fail(restartResponse.getMsg());
//}
}

@ApiOperation("Restart Edge Box Process")
@ApiImplicitParam(name ="id", value ="Box ID")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping("/restartProcess")
@ResponseBody
public JsonResult<?> restartAiboxProcess(Long id) {
Location location = locationService.getById(id);
if (location == null) {
return JsonResultUtils.fail("find not to Box Device");
}
//
if (StrUtil.isBlank(location.getBoxNo())) {
return JsonResultUtils.fail("Box Device not has Index Info");
}

return JsonResultUtils.fail("Function can not real current");
}

/**
* Get Box List and History Version
* @author Abyss
* @date 2023/12/30 18:08
*/
@ApiOperation("Get Box List and History Version")
@SaCheckPermission(value = {"edgePlatform-updata"}, mode = SaMode.OR)
@GetMapping("/getBoxAndVersion")
@ResponseBody
public JsonResult<?> getBoxAndVersion() {

try {
Map<String, Object> resultMap = new HashMap<>();
List<Location> locationList = locationService.listDataByTypeNoDef("2");
resultMap.put("boxList", locationList);
String path = MODEL_DIR +"boxZip/";
File localFile = new File(path);
if (!localFile.exists()) {
localFile.mkdirs();
}
List<String> fileNameList = new ArrayList<>();
List<Path> fileList = Files.list(Paths.get(path))
.collect(Collectors.toList());
for (Path file: fileList) {
// System.out.println(file);
fileNameList.add(file.toString().replace(path,""));
}
resultMap.put("fileList", fileNameList);
return JsonResultUtils.success(resultMap);
} catch (Exception e) {
return JsonResultUtils.fail();
}

}

/**
* Upload Box zip File
* @author Abyss
* @date 2023/12/30 18:08
*/
@ApiOperation("Upload File")
@SaCheckPermission(value = {"edgePlatform-updata"}, mode = SaMode.OR)
@PostMapping("/boxFileUpload")
@ResponseBody
public JsonResult boxFileUpload(@RequestParam(value ="file", required = false) MultipartFile file,
@RequestParam("fileName") String fileName,
@RequestParam("locationId") Long locationId) {
//
if(file == null) {
return JsonResultUtils.fail("Please select File");
}
if (!"zip".equals(FileUtil.extName(file.getOriginalFilename()))) {
return JsonResultUtils.fail("Please Upload zip File");
}
//
String path = MODEL_DIR +"boxZip/";
File localFile = new File(path);
if (!localFile.exists()) {
localFile.mkdirs();
}
// Save File Name Name
String saveName = fileName +"."+ FileUtil.extName(file.getOriginalFilename());
try {
//
File newFile = new File(path + saveName);
file.transferTo(newFile);
// Update Box
Location location = locationService.getById(locationId);
String md5 = Md5FileUtils.getMD5(newFile);
String version = fileName.replace(".zip","");
// JsonResult result = httpAiBoxService.updateAiBox(location,"boxZip/"+ saveName, md5, version);
// if (result.getCode()!= 200) {
// return result;
//}
//
location.setBoxVersion(version);
location.setBoxFile(fileName);
locationService.saveOrUpdate(location);
return JsonResultUtils.success(saveName);
} catch (Exception e) {
return JsonResultUtils.fail("Storage File Exception");
}
}

/**
* Update Box Version
* @author Abyss
* @date 2023/12/30 19:33
*/
@ApiOperation("Update Box Version")
@SaCheckPermission(value = {"edgePlatform-updata"}, mode = SaMode.OR)
@PostMapping("/changeBoxVersion")
@ResponseBody
public JsonResult changeBoxVersion(Long locationId, String fileName) {
if (null == locationId) {
return JsonResultUtils.fail("Please select Box");
}
Location location = locationService.getById(locationId);
if (null == location) {
return JsonResultUtils.fail("Box does not exist");
}
if (StringUtils.isBlank(fileName)) {
return JsonResultUtils.fail("Please select Version File");
}
String path = MODEL_DIR +"boxZip/"+ fileName;
File file = new File(path);
if (!file.exists()) {
return JsonResultUtils.fail("Version File does not exist");
}
// Update Box
String md5 = Md5FileUtils.getMD5(file);
String version = fileName.replace(".zip","");
//JsonResult result = httpAiBoxService.updateAiBox(location,"boxZip/"+ fileName, md5, version);
// if (result.getCode()!= 200) {
// return result;
//}
//
location.setBoxVersion(version);
location.setBoxFile(fileName +".zip");
locationService.saveOrUpdate(location);
return JsonResultUtils.success();
}

@ApiOperation("Query Box Run Algorithm Info")
@ApiImplicitParam(name ="boxId", value ="Box ID")
@SaCheckPermission(value = {"edgePlatform-boxManagement"}, mode = SaMode.OR)
@PostMapping("/algorithm/info")
@ResponseBody
public JsonResult<List<Map<String, Object>>> listBoxInfo(Long boxId) {
// Query Box Info
Location location = locationService.getById(boxId);
if(location == null) {
return JsonResultUtils.fail("find not to Box Info");
}

//
List<Camera> cameraList = cameraService.listByBoxId(location.getId());
Map<Long, Camera> cameraMap = cameraList.stream().collect(Collectors.toMap(Camera::getId, Function.identity()));

// Query All Algorithm
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null || algorithmList.isEmpty()) {
return JsonResultUtils.success(new ArrayList<>());
}

// Query All Algorithm, and supplement Charge Phase close Back Field
List<Map<String, Object>> retDatas = new ArrayList<>();
for(Algorithm algorithm: algorithmList) {
Map<String, Object> retData = new HashMap<>();
retData.put("algorithmId", algorithm.getId());
retData.put("algorithmName", algorithm.getName());
retData.put("algorithmNameEn", algorithm.getNameEn());
retData.put("platform", location.getPlatform());

// zip Package Directory Whether Exist
String dest = FileUtils.pathTo(MODEL_DIR + File.separator + location.getPlatform() + File.separator + algorithm.getNameEn() + File.separator);
if(!FileUtil.exist(dest)) {// zip Package Directory does not exist
continue;
}

// Algorithm Status, like Result has One for In Progress Run, rule for Run
boolean run = false; // Whether In Progress Run
List<String> runCamera = new ArrayList<>(); // In Progress Run Camera Name
String runAlgorithm = null; // In Progress Run Algorithm Version


List<CameraAlgorithm> cameraAlgorithms = cameraAlgorithmService.listByAlgorithm(algorithm.getId());
for(CameraAlgorithm cameraAlgorithm: cameraAlgorithms) {
if(cameraMap.containsKey(cameraAlgorithm.getCameraId())) {
runCamera.add(cameraMap.get(cameraAlgorithm.getCameraId()).getName());

// Relate Algorithm In Progress Run
if(cameraAlgorithm.getRunStatus()!= null && cameraAlgorithm.getRunStatus() == 1) {
Date runTime = cameraAlgorithm.getRunTime();
if(runTime!= null && (System.currentTimeMillis() - runTime.getTime()) < 5 * 60 * 1000) {// 5 min inner Online
run = true;
}
}
// Relate Algorithm Version No
if(StrUtil.isBlank(runAlgorithm)) {
runAlgorithm = cameraAlgorithm.getAlgorithmVersion();
}
}
}
retData.put("isRun", run); // Algorithm Run Status
retData.put("cameraNames", String.join(",", runCamera)); // Relate In Progress Run Camera Name

// Current Run Algorithm Version
BoxVersion boxVersion = boxVersionService.getData(location.getId(), algorithm.getId());
retData.put("currentVersion", boxVersion == null?"not Enabled": boxVersion.getVersionNum()); // Current Version

// File tar = new File(tarPath);
// if(tar.exists()) {
// File[] files = tar.listFiles();
// if(files!= null) {
// for(File file: files) {
// filenames.add(file.getName());
//}
//}
//}
// // Local not has File, Direct connect Filter
// if(filenames.isEmpty()) {
// continue;
//}
// Collections.sort(filenames);
//
// // Current Run Algorithm Version
// String verName = String.format("%s-%s-%s.zip", algorithm.getPlatform(), algorithm.getNameEn(), runAlgorithm);
// retData.put("currentVersion", filenames.contains(verName)? verName: (filenames.isEmpty()?"":"not Enabled")); // Current Version


// Local File most new Version, Need from Local Remove Query File List
retData.put("highVersion",""); // most big Version
retData.put("upgrade", false); // Whether Display Algorithm Upgrade

List<String> zipfiles = FileUtils.listFiles(dest, algorithm.getNameEn(), Collections.singletonList("zip"));
Collections.sort(zipfiles);

//
int len = zipfiles.size();
if(len == 0) {
retData.put("highVersion","-");
retData.put("upgrade", false);
} else if(len == 1) {
String zipfile = zipfiles.get(0);
retData.put("highVersion", FileUtil.getName(zipfile));
retData.put("upgrade", false);
} else {
String zipfile = zipfiles.get(zipfiles.size() - 1);
if(boxVersion == null) {
retData.put("highVersion", FileUtil.getName(zipfile));
retData.put("upgrade", false);
} else {
if(zipfile.contains(boxVersion.getVersionNum())) {
retData.put("highVersion", FileUtil.getName(zipfile));
retData.put("upgrade", false);
} else {
retData.put("highVersion", FileUtil.getName(zipfile));
retData.put("upgrade", true);
}
}
}

//
// if(idx < 0 && filenames.size() > 0) {
// retData.put("highVersion", filenames.get(filenames.size() - 1));
// retData.put("upgrade", false); // Whether Display Algorithm Upgrade
//}
// if(idx >= 0 && idx < filenames.size() - 1) {
// retData.put("highVersion", filenames.get(filenames.size() - 1));
// retData.put("upgrade", true); // Whether Display Algorithm Upgrade
//}

retDatas.add(retData);
}
return JsonResultUtils.success(retDatas);
}

@ApiOperation("Get Machine code")
@ApiImplicitParam(name ="boxId", value ="Box ID")
@SaCheckPermission(value = {"edgePlatform-casketManagement"}, mode = SaMode.OR)
@PostMapping("getSn")
@ResponseBody
public JsonResult<?> getSn(Long boxId) {
Location location = locationService.getById(boxId);
if(location == null) {
return JsonResultUtils.fail("find not to Box Device");
}
if(StrUtil.isBlank(location.getBoxNo())) {
return JsonResultUtils.fail("Box Device not has Index Info");
}

DeviceSerialRequest request = new DeviceSerialRequest();
request.setType(MessageType.DEVICE_SERIAL.getType());
request.setRequestId(IdUtil.randomUUID());
request.setSn(location.getBoxNo());
log.info("DeviceSerialRequest {}", request);
Response response = messageSenderAndWaiter.sendRequest(request);
log.info("Get Machine code Reply {}", response);
if(response == null) {
return JsonResultUtils.fail("Get Machine code Failed, Unknown Error");
}
DeviceSerialResponse deviceSerialResponse = (DeviceSerialResponse) response;
if(deviceSerialResponse.isStatus()) {
locationService.updateActiveSn(boxId, deviceSerialResponse.getActiveSn());
return JsonResultUtils.success(deviceSerialResponse.getActiveSn());
}
return JsonResultUtils.fail(deviceSerialResponse.getMsg());
}

@ApiOperation("Get Activate Status")
@ApiImplicitParam(name ="boxId", value ="Box ID")
@SaCheckPermission(value = {"edgePlatform-casketManagement"}, mode = SaMode.OR)
@PostMapping("getActived")
@ResponseBody
public JsonResult<?> getActived(Long boxId) {
Location location = locationService.getById(boxId);
if(location == null) {
return JsonResultUtils.fail("find not to Box Device");
}
if(StrUtil.isBlank(location.getBoxNo())) {
return JsonResultUtils.fail("Box Device not has Index Info");
}

Integer activeStatus = location.getActiveStatus();
if(activeStatus == null || activeStatus == 0) {
return JsonResultUtils.success(0);
} else {
return JsonResultUtils.success(1);
}

// ActivationStatusRequest request = new ActivationStatusRequest();
// request.setType(MessageType.ACTIVATION_STATUS.getType());
// request.setRequestId(IdUtil.randomUUID());
// request.setSn(location.getBoxNo());
// Response response = messageSenderAndWaiter.sendRequest(request);
// log.info("Get Activate Status Reply {}", response);
// if(response == null) {
// return JsonResultUtils.fail("Get Machine Activate Status Failed, Unknown Error");
//}
// ActivationStatusResponse activationStatusResponse = (ActivationStatusResponse) response;
// if(activationStatusResponse.isStatus()) {
// if(activationStatusResponse.isActived()) {
// locationService.updateActiveStatus(boxId, 1);
// return JsonResultUtils.success("Calculate Device Activate");
//} else {
// return JsonResultUtils.fail("Calculate Device not Activate");
//}
//}
// return JsonResultUtils.fail(activationStatusResponse.getMsg());
}

@ApiOperation("Activate")
@ApiImplicitParams({
@ApiImplicitParam(name ="boxId", value ="Box ID"),
@ApiImplicitParam(name ="activeCode", value ="Activate code")
})
@SaCheckPermission(value = {"edgePlatform-casketManagement"}, mode = SaMode.OR)
@PostMapping("actived")
@ResponseBody
public JsonResult<?> actived(Long boxId, String activeCode) {
Location location = locationService.getById(boxId);
if(location == null) {
return JsonResultUtils.fail("find not to Box Device");
}
if(StrUtil.isBlank(location.getBoxNo())) {
return JsonResultUtils.fail("Box Device not has Index Info");
}

ActivateDeviceRequest request = new ActivateDeviceRequest();
request.setType(MessageType.ACTIVATE_DEVICE.getType());
request.setRequestId(IdUtil.randomUUID());
request.setSn(location.getBoxNo());
request.setActiveCode(activeCode);
Response response = messageSenderAndWaiter.sendRequest(request);
log.info("Activate Device Reply {}", response);
if(response == null) {
return JsonResultUtils.fail("Box Device Activate Failed, Unknown Error");
}
ActivateDeviceResponse activateDeviceResponse = (ActivateDeviceResponse) response;
if(activateDeviceResponse.isStatus()) {
locationService.updateActiveStatus(boxId, 1);
return JsonResultUtils.success("Box Device Activate Success");
}
return JsonResultUtils.fail(activateDeviceResponse.getMsg());
}

/**
* Department -> Box -> Camera Cascade Data result structure
* @return
*/
@ApiOperation("Department -> Box -> Camera Cascade Data")
@SaCheckPermission(value = {"flowDsetection"}, mode = SaMode.OR)
@GetMapping("treeForCass")
@ResponseBody
public JsonResult<List<DepartCassInfoDTO>> treeForCass() {
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account == null) {
return JsonResultUtils.success(new ArrayList<>());
}
//
Long parentId = 0L;
if(account.getIsSuper() == null || account.getIsSuper()!= 1) {
parentId = account.getDepartId();
}
//
if(parentId == null) {
return JsonResultUtils.success(new ArrayList<>());
}

//
List<DepartCassInfoDTO> departCassInfoDTOS = new ArrayList<>();
List<Depart> departs = apDepartService.listData();
for(Depart depart: departs) {
if((parentId == 0 && depart.getParentId().equals(0L)) || (parentId > 0 && depart.getId().equals(parentId))) {
// Query All child Node
List<Long> subIds = getSubIds(depart.getId(), departs);
// Query All Box Node
subIds.add(depart.getId());
List<Long> boxIds = locationService.getLocationIdsByDeparts(subIds);
if(boxIds == null || boxIds.isEmpty()) {
continue;
}
// Query Box down All Camera
List<BoxCassInfoDTO> boxCassInfoDTOS = new ArrayList<>();
for(Long boxId: boxIds) {
List<Camera> cameras = cameraService.listByBoxId(boxId);
if(cameras.isEmpty()) {
continue;
}
//
List<CameraCassInfoDTO> cameraCassInfoDTOS = new ArrayList<>();
for(Camera camera: cameras) {
CameraCassInfoDTO cameraCassInfoDTO = new CameraCassInfoDTO();
cameraCassInfoDTO.setId(camera.getId());
cameraCassInfoDTO.setName(camera.getName());
cameraCassInfoDTOS.add(cameraCassInfoDTO);
}
//
Location boxInfo = locationService.getById(boxId);
BoxCassInfoDTO boxCassInfoDTO = new BoxCassInfoDTO();
boxCassInfoDTO.setId(boxInfo.getId());
boxCassInfoDTO.setName(boxInfo.getName());
boxCassInfoDTO.setChildren(cameraCassInfoDTOS);
boxCassInfoDTOS.add(boxCassInfoDTO);
}
//
if(boxCassInfoDTOS.isEmpty()) {
continue;
}
//
DepartCassInfoDTO departCassInfoDTO = new DepartCassInfoDTO();
departCassInfoDTO.setId(depart.getId());
departCassInfoDTO.setName(depart.getName());
departCassInfoDTO.setChildren(boxCassInfoDTOS);
departCassInfoDTOS.add(departCassInfoDTO);
}
}
return JsonResultUtils.success(departCassInfoDTOS);
}

/**
* Get All child Node
* @param parentId
* @param departs
* @return
*/
public List<Long> getSubIds(Long parentId, List<Depart> departs) {
List<Long> subIds = new ArrayList<>();
List<Long> filterIds = new ArrayList<>();
List<Long> currIds = new ArrayList<>();
//
filterIds.add(parentId);
while(true) {
// select Get Current child Node
for(Depart depart: departs) {
if(filterIds.contains(depart.getParentId())) {
subIds.add(depart.getId());
currIds.add(depart.getId());
}
}

// not has Need Query ids, exit out
if(currIds.isEmpty()) {
break;
}

filterIds.clear();
filterIds.addAll(currIds);
currIds.clear();
}
return subIds;
}

@ApiOperation("Query All Data")
@SaCheckPermission(value = {"alarmData"}, mode = SaMode.OR)
@PostMapping("listData5")
@ResponseBody
private PageResult<?> listData5(@RequestBody LocationListData5Vo dataVo) {
List<LocationDepartDTO> locationDepartDTOS = new ArrayList<>();

// List<Long> departIds = new ArrayList<>();
// if(departId!= null) {
// List<Long> currentAndChildIds = apDepartService.getCurrentAndChildIds(departId);
// if(!currentAndChildIds.isEmpty()) {
// departIds.addAll(currentAndChildIds);
//}
//}

List<Location> locationList = locationService.listData5(dataVo.getObjectIds(), dataVo.getObjectName());
if(!locationList.isEmpty()) {
List<Depart> departList = apDepartService.list();
if(departList == null) {
departList = new ArrayList<>();
}
Map<Long, String> departMap = departList.stream().collect(Collectors.toMap(Depart::getId, Depart::getName));

List<Long> mapObjectIDList = mapObjectService.listObjectIDByType(0);

for(Location location: locationList) {
if(mapObjectIDList.contains(location.getId()) &&!dataVo.isObjectAll()) {
continue;
}

LocationDepartDTO locationDepartDTO = new LocationDepartDTO();
locationDepartDTO.setLocationId(location.getId());
locationDepartDTO.setLocationName(location.getName());
locationDepartDTO.setDepartId(location.getDepartId());
locationDepartDTO.setDepartName(departMap.get(location.getDepartId()));
locationDepartDTOS.add(locationDepartDTO);
}
}
return PageResultUtils.success(null, locationDepartDTOS);
}
}
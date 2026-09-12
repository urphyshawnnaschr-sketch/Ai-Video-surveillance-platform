package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yihecode.camera.ai.entity.face.*;
import com.yihecode.camera.ai.enums.FaceSyncEnum;
import com.yihecode.camera.ai.https.face.HttpFaceService;
import com.yihecode.camera.ai.service.face.*;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.face.dto.FaceUserAddToExistDTO;
import com.yihecode.camera.ai.web.face.dto.FaceUserLessDTO;
import com.yihecode.camera.ai.web.face.vo.FaceUserAddToExistVo;
import com.yihecode.camera.ai.web.face.vo.FaceUserFormVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
* Face Person member - Face Recognition child Module
*/
@Api(tags = "Face Recognition _ Person member Management")
@SaCheckLogin
@Slf4j
@RestController
@RequestMapping("/face/user")
public class FaceUserController {

    @Resource
    private FaceGroupService faceGroupService;

    @Resource
    private FaceUserService faceUserService;

    @Resource
    private FaceImageService faceImageService;

    @Autowired
    private FaceReportService faceReportService;

    @Autowired
    private FaceSyncObjectService faceSyncObjectService;

    @Value("${uploadDir}")
    private String uploadDir;

    //Allow Image after Concat
private final List<String> EXTS = Arrays.asList("jpg","jpeg","png");


@ApiOperation("Query All Person member")
@GetMapping({"all"})
public JsonResult<List<FaceUserLessDTO>> listAll() {
List<FaceUser> faceUserList = faceUserService.list();
List<FaceUserLessDTO> dtoList = new ArrayList<>();
for(FaceUser faceUser: faceUserList) {
FaceUserLessDTO dto = new FaceUserLessDTO();
BeanUtils.copyProperties(faceUser, dto);
dtoList.add(dto);
}
return JsonResultUtils.success(dtoList);
}

@ApiOperation("Person member Detail")
@ApiImplicitParam(name ="id", value ="Person member ID")
@SaCheckPermission(value = {"face-view"}, mode = SaMode.OR)
@GetMapping({"/detail"})
@ResponseBody
public JsonResult<FaceUser> detail(Long id) {
FaceUser faceUser = faceUserService.getById(id);
if(faceUser == null) {
return JsonResultUtils.fail("Data Not Exist");
}
//
List<FaceImage> faceImages = faceImageService.listByUser(faceUser.getId());
for(FaceImage faceImage: faceImages) {
// only Back Image Name
faceImage.setImgUrl(FileUtil.getName(faceImage.getImgUrl()));
}
faceUser.setFaceImages(faceImages);
//
FaceGroup faceGroup = faceGroupService.getById(faceUser.getGroupId());
faceUser.setFaceGroup(faceGroup);

return JsonResultUtils.success(faceUser);
}

@ApiIgnore
@ApiOperation("Person member Save")
@SaCheckPermission(value = {"face-add","face-edit"}, mode = SaMode.OR)
@PostMapping({"/savexxx"})
@ResponseBody
public JsonResult<?> savexxx(FaceUserFormVo faceUserForm) {
//
if(StrUtil.isBlank(faceUserForm.getName())) {
return JsonResultUtils.fail("Please enter Name");
}
//
if(faceUserForm.getGroupId() == null) {
return JsonResultUtils.fail("Please select Group");
}
//
FaceGroup faceGroup = faceGroupService.getById(faceUserForm.getGroupId());
if(faceGroup == null) {
return JsonResultUtils.fail("Select Group does not exist");
}

//
List<MultipartFile> files = faceUserForm.getFiles();
if(files!= null &&!files.isEmpty()) {
for(MultipartFile file: files) {
String extName = FileUtil.extName(file.getOriginalFilename());
if(extName == null ||!EXTS.contains(extName.toLowerCase())) {
return JsonResultUtils.fail(file.getOriginalFilename() +"not Support Image Type");
}
}
}

// Whether more modify Group
Long oldGroupId = null;
if (null!= faceUserForm.getId() && null!= faceUserForm.getGroupId()) {
oldGroupId = faceUserService.getById(faceUserForm.getId()).getGroupId();
}

//
FaceUser faceUser = new FaceUser();
faceUser.setId(faceUserForm.getId());
faceUser.setName(faceUserForm.getName());
faceUser.setTel(faceUserForm.getTel());
faceUser.setRemark(faceUserForm.getRemark());
faceUser.setGroupId(faceUserForm.getGroupId());
faceUser.setCreatedAt(new Date());
faceUserService.saveOrUpdate(faceUser);

// Update Record
faceReportService.updateGroupIdByUserId(faceUser.getGroupId(), faceUser.getId());

// Delete Old Face
List<FaceImage> oldFaceImages = faceImageService.listByUser(faceUser.getId());
for (FaceImage image: oldFaceImages) {
//
faceImageService.removeById(image.getId());
}

// Save Image
if(files!= null &&!files.isEmpty()) {
String tarpath = uploadDir + File.separator +"face_imgs"+ File.separator + faceUser.getId() + File.separator;
File tar = new File(tarpath);
if(!tar.exists()) {
tar.mkdirs();
}

//
for(MultipartFile file: files) {
try {
String extName = FileUtil.extName(file.getOriginalFilename());
String filepath = tarpath + IdUtil.randomUUID() +"."+ extName;
file.transferTo(new File(filepath));
//
FaceImage faceImage = new FaceImage();
faceImage.setUserId(faceUser.getId());
faceImage.setImgUrl(filepath);
faceImage.setIsAvatar(0);
faceImage.setDeleted(0);
faceImage.setCreatedAt(new Date());
faceImageService.save(faceImage);


} catch (Exception e) {
e.printStackTrace();
//return JsonResultUtils.fail("Storage File Exception");
}
}
}
List<String> faceImageStrList = faceUserForm.getFilesStr();
if (faceImageStrList!= null &&!faceImageStrList.isEmpty()) {
for (String imageStr: faceImageStrList) {
FaceImage faceImage = new FaceImage();
faceImage.setUserId(faceUser.getId());
faceImage.setImgUrl(imageStr);
faceImage.setIsAvatar(0);
faceImage.setDeleted(0);
faceImage.setCreatedAt(new Date());
faceImageService.save(faceImage);
}
}
// Register Face
try {
List<FaceImage> faceImages = faceImageService.listByUser(faceUser.getId());
for (FaceImage image: faceImages) {
}

} catch (Exception e) {
e.printStackTrace();
//return JsonResultUtils.fail("Storage File Exception");
}

return JsonResultUtils.success(faceUser.getId());
}

@ApiOperation("Person member Save")
@SaCheckPermission(value = {"face-add","face-edit"}, mode = SaMode.OR)
@PostMapping({"/save"})
@ResponseBody
public JsonResult<?> save(FaceUserFormVo faceUserForm) throws Exception {
//
if(StrUtil.isBlank(faceUserForm.getName())) {
return JsonResultUtils.fail("Please enter Name");
}
//
if(faceUserForm.getGroupId() == null) {
return JsonResultUtils.fail("Please select Group");
}
//
FaceGroup faceGroup = faceGroupService.getById(faceUserForm.getGroupId());
if(faceGroup == null) {
return JsonResultUtils.fail("Group does not exist");
}

List<MultipartFile> files = faceUserForm.getFiles();

// Validate Image Whether Valid
if(files!= null &&!files.isEmpty()) {
for(MultipartFile file: files) {
String extName = FileUtil.extName(file.getOriginalFilename());
if(extName == null ||!EXTS.contains(extName.toLowerCase())) {
return JsonResultUtils.fail(file.getOriginalFilename() +"not Support Image Type");
}

// File Size Determine, cannot exceed 2M
long filesize = file.getSize();
double mb = filesize * 1d / 1024 / 1024;
if(mb > 2) {
return JsonResultUtils.fail(file.getOriginalFilename() +"super over 2M");
}
}
}

// Create new Face, Detection Whether has Upload Face
if(faceUserForm.getId() == null) {
if(ObjectUtil.isEmpty(files)) {
return JsonResultUtils.fail("Image not Select");
}
}

// Create Temp Hour Storage Directory
String dest = FileUtils.pathTo(uploadDir +"/face_tmp/");
if(!FileUtil.exist(dest)) {
FileUtil.mkdir(dest);
}

// Temp Hour File Storage
List<String> saveFileList = new ArrayList<>();
if(ObjectUtil.isNotEmpty(files)) {
for(MultipartFile file: files) {
try {
String extName = FileUtil.extName(file.getOriginalFilename());
String newFileName = IdUtil.randomUUID() +"."+ extName;
String filepath = FileUtils.pathTo(dest +"/"+ newFileName);
file.transferTo(new File(filepath));
saveFileList.add(filepath); // Storage is Complete whole Path
} catch (Exception e) {
// Delete Storage Image
for(String saveFile: saveFileList) {
FileUtil.del(saveFile);
}

// Back Error
log.error("Face Add / Modify _ Image Storage Exception", e);
return JsonResultUtils.fail("Image Storage Error");
}
}
}

//
if(ObjectUtil.isEmpty(files) && ObjectUtil.isEmpty(faceUserForm.getFilesStr())) {
return JsonResultUtils.fail("At Least Need Add or Keep One Face Image");
}

// Limit make most big 4 sheet Image
int total = saveFileList.size() + (ObjectUtil.isEmpty(faceUserForm.getFilesStr())? 0: faceUserForm.getFilesStr().size());
if(total > 3) {
return JsonResultUtils.fail("Person member Image super over 3 sheet, Please Delete multi remainder Image");
}

//
FaceUser faceUser = new FaceUser();
faceUser.setId(faceUserForm.getId());
faceUser.setName(faceUserForm.getName());
faceUser.setTel(faceUserForm.getTel());
faceUser.setRemark(faceUserForm.getRemark());
faceUser.setGroupId(faceUserForm.getGroupId());
faceUser.setCreatedAt(new Date());

// Add Face
faceUserService.saveData(faceUser, saveFileList, faceUserForm.getFilesStr());
return JsonResultUtils.success(faceUser.getId());
}

@ApiOperation("from Stranger produce Person member Save")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/saveFromStranger"})
@ResponseBody
public JsonResult<Long> saveFromStranger(FaceUserFormVo faceUserForm) {
//
if(StrUtil.isBlank(faceUserForm.getName())) {
return JsonResultUtils.fail("Please enter Name");
}
//
if(faceUserForm.getGroupId() == null) {
return JsonResultUtils.fail("Please select Group");
}
//
FaceGroup faceGroup = faceGroupService.getById(faceUserForm.getGroupId());
if(faceGroup == null) {
return JsonResultUtils.fail("Group does not exist");
}

//
FaceReport report = faceReportService.getById(faceUserForm.getReportId());
if(report == null) {
return JsonResultUtils.fail("Recognition Record does not exist");
}

if(StrUtil.isBlank(report.getFilePath())) {
return JsonResultUtils.fail("Recognition Image does not exist");
}

if(!FileUtil.exist(report.getFilePath()) ||!FileUtil.isFile(report.getFilePath())) {
return JsonResultUtils.fail("Recognition Image does not exist");
}

String extName = FileUtil.extName(report.getFilePath());
if(extName == null) {
return JsonResultUtils.fail("Recognition Image Format Error");
}

// Create Image Storage Directory
String dest = FileUtils.pathTo(uploadDir +"/face_imgs/");

// User Info
FaceUser faceUser = new FaceUser();
faceUser.setId(faceUserForm.getId());
faceUser.setName(faceUserForm.getName());
faceUser.setTel(faceUserForm.getTel());
faceUser.setRemark(faceUserForm.getRemark());
faceUser.setGroupId(faceUserForm.getGroupId());
faceUser.setCreatedAt(new Date());

// Save Data
faceUserService.saveNewDataFromCopy(faceUser, report.getId(), report.getFilePath(), dest);
return JsonResultUtils.success(faceUser.getId());
}

@ApiOperation("from Stranger produce Person member Add in has Person member")
@SaCheckPermission(value = {"faceControl-faceHistory"}, mode = SaMode.OR)
@PostMapping({"/addImageToExist"})
@ResponseBody
public JsonResult<FaceUserAddToExistDTO> addImageToExist(FaceUserAddToExistVo existVo) {
//
if(existVo.getUserId() == null) {
return JsonResultUtils.fail("Person member not Select");
}
//
if(existVo.getReportId() == null) {
return JsonResultUtils.fail("Person member Alarm not Select");
}

FaceUser existFaceUser = faceUserService.getById(existVo.getUserId());
if(existFaceUser == null) {
return JsonResultUtils.fail("Person member does not exist");
}

FaceReport faceReport = faceReportService.getById(existVo.getReportId());
if(faceReport == null) {
return JsonResultUtils.fail("Face Alarm does not exist");
}

List<FaceImage> faceImageList = faceImageService.listByUser(existVo.getUserId());
if(faceImageList.size() >= 3) {
FaceUserAddToExistDTO existDTO = new FaceUserAddToExistDTO();
existDTO.setSuccess(false);
existDTO.setMsg("Person member Image super over 3 sheet, Please Delete Other again Add");
existDTO.setFaceImageIds(faceImageList.stream().map(FaceImage::getId).collect(Collectors.toList()));
return JsonResultUtils.success(existDTO);
}

// Save Data
faceUserService.saveToExist(existFaceUser, faceReport);

FaceUserAddToExistDTO existDTO = new FaceUserAddToExistDTO();
existDTO.setSuccess(true);
existDTO.setMsg("OK");
return JsonResultUtils.success();
}

@ApiOperation("Person member Delete")
@ApiImplicitParam(name ="id", value ="Person member ID")
@SaCheckPermission(value = {"face-delete"}, mode = SaMode.OR)
@PostMapping({"/delete"})
@ResponseBody
public JsonResult<Long> delete(Long id) {
FaceUser faceUser = faceUserService.getById(id);
if(faceUser == null) {
return JsonResultUtils.fail("Person member does not exist");
}
//
faceUserService.deleteData(id);

// Call Algorithm API
//httpFaceService.removeFaceUser(faceUser.getId(), faceUser.getGroupId());
return JsonResultUtils.success(id);
}

@ApiOperation("Person member Query")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="page", value ="Page Number"),
@ApiImplicitParam(name ="limit", value ="Pagination Number"),
@ApiImplicitParam(name ="groupId", value ="Group ID"),
@ApiImplicitParam(name ="name", value ="Person member Name"),
@ApiImplicitParam(name ="tel", value ="Contact Mode")
})
@SaCheckPermission(value = {"face-view"}, mode = SaMode.OR)
@PostMapping({"/listPage"})
@ResponseBody
public PageResult<List<FaceUser>> listPage(@RequestParam(name ="page", defaultValue ="1") Integer page,
@RequestParam(name ="limit", defaultValue ="10") Integer limit,
@RequestParam(name ="groupId", required = false) Long groupId,
@RequestParam(name ="name", required = false) String name,
@RequestParam(name ="tel", required = false) String tel) {
IPage<FaceUser> pageResult = faceUserService.listPage(page, limit, groupId, name, tel);
List<FaceUser> records = pageResult.getRecords();
if(records == null) {
records = new ArrayList<>();
}
//
List<FaceGroup> faceGroups = faceGroupService.list();
if(faceGroups == null) {
faceGroups = new ArrayList<>();
}
Map<Long, FaceGroup> faceGroupMap = faceGroups.stream().collect(Collectors.toMap(FaceGroup::getId, (s1 -> s1)));
for(FaceUser record: records) {
record.setFaceGroup(faceGroupMap.get(record.getGroupId()));
}
return PageResultUtils.success(pageResult.getTotal(), records);
}

/**
* Upload File
* @param file
* @return
*/
@ApiOperation("Batch Upload")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="file", value ="File Compress Package"),
@ApiImplicitParam(name ="faceGroupId", value ="Person member Group ID")
})
@SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
@PostMapping("/upload")
@ResponseBody
public JsonResult upload(@RequestParam(name ="file", required = false) MultipartFile file, @RequestParam(name ="faceGroupId", required = false) Long faceGroupId) {
//
if(file == null) {
return JsonResultUtils.fail("Please Upload Person member Compress Package");
}
String originalFilename = file.getOriginalFilename();
String extName = FileUtil.extName(originalFilename);
if(!"zip".equalsIgnoreCase(extName)) {
return JsonResultUtils.fail("Please Upload zip Compress Package");
}
//
if(faceGroupId == null) {
return JsonResultUtils.fail("Please select Person member Group");
}
FaceGroup faceGroup = faceGroupService.getById(faceGroupId);
if(faceGroup == null) {
return JsonResultUtils.fail("Person member Group does not exist");
}
// Create Temp Hour Directory
String dest = FileUtils.pathTo(uploadDir +"/tmp_face_imgs/");
if(!FileUtil.exist(dest)) {
FileUtil.mkdir(dest);
}
//
try {
// Storage
String tmpFileName = IdUtil.randomUUID();
String saveName = tmpFileName +"."+ extName;
File saveFile = new File(FileUtils.pathTo(dest +"/"+ saveName));
file.transferTo(saveFile);

// Decompress Zoom
String unzipDest = FileUtils.pathTo(uploadDir +"/tmp_face_imgs/"+ tmpFileName +"/");
if(!FileUtil.exist(unzipDest)) {
FileUtil.mkdir(unzipDest);
}

// Decompress Zoom
String charset ="utf-8";
boolean unzipSuccess = false;
File outDir = null;
// Try Try Use Default Code Solve code
try {
outDir = ZipUtil.unzip(saveFile.getAbsolutePath(), unzipDest);
unzipSuccess = true;
} catch (Exception e) {
//
}
// Default Code Decompress not Success, change GBK Solve code
if(!unzipSuccess) {
try {
charset ="gbk";
outDir = ZipUtil.unzip(saveFile.getAbsolutePath(), unzipDest, Charset.forName("GBK"));
unzipSuccess = true;
} catch (Exception e) {
//
}
}
//
if(!unzipSuccess) {
return JsonResultUtils.fail("Compress Package Decompress Zoom Failed, Please Check Format Whether Correct");
}

//
int resultCount = 0;

// Iterate All Directory
List<File> subDirectories = listSubDirectories(outDir);
for(File subdirectory: subDirectories) {
File[] subfiles = subdirectory.listFiles();
if(subfiles == null || subfiles.length == 0) {
continue;
}
//
List<File> imgs = new ArrayList<>();
for(File subfile: subfiles) {
if(subfile.isFile()) {
// Filter Non Refer Fixed Type Image
String ext = FileUtil.extName(subfile).toLowerCase(Locale.ROOT);
if(!("jpg".equals(ext) ||"jpeg".equals(ext) ||"png".equals(ext) ||"bmp".equals(ext))) {
continue;
}
// Filter mac up File
String subname = subfile.getName();
if (subname.startsWith("._") || subname.equals("__MACOSX") || subname.equals(".DS_Store")) {
continue;
}
//
imgs.add(subfile);
}
}
//
if(imgs.isEmpty()) {
continue;
}

//
String name = subdirectory.getName();
if("gbk".equals(charset)) {
byte[] gbkBytes = name.getBytes("GBK");
name = new String(gbkBytes, StandardCharsets.UTF_8);
}

// Add Face Info
FaceUser faceUser = new FaceUser();
faceUser.setName(name);
faceUser.setTel("");
faceUser.setRemark("");
faceUser.setGroupId(faceGroup.getId());
faceUser.setCreatedAt(new Date());
faceUserService.save(faceUser);

// Face Storage Path
String moveDest = FileUtils.pathTo(uploadDir +"/face_imgs/"+ faceUser.getId() +"/");
if(!FileUtil.exist(moveDest)) {
FileUtil.mkdir(moveDest);
}
// Copy Image to Face Storage Path
int i = 0;
for(File img: imgs) {
// only Store front Three sheet
if (i >= 3) {
break;
}
File newImg = FileUtil.rename(img, IdUtil.randomUUID() +"."+ FileUtil.extName(img), true);
FileUtil.move(newImg, FileUtil.newFile(moveDest), true);
//
FaceImage faceImage = new FaceImage();
faceImage.setUserId(faceUser.getId());
faceImage.setImgUrl(FileUtil.getName(newImg));
faceImage.setIsAvatar(0);
faceImage.setCreatedAt(new Date());
faceImageService.save(faceImage);

FaceSyncObject faceSyncObject = new FaceSyncObject();
faceSyncObject.setOpType(FaceSyncEnum.ADD_FACE.getCode());
faceSyncObject.setUserId(faceUser.getId());
faceSyncObject.setGroupId(faceUser.getGroupId());
faceSyncObject.setImageId(faceImage.getId());
faceSyncObject.setCreatedAt(new Date());
faceSyncObjectService.save(faceSyncObject);

i++;
}
resultCount++;
}

if(resultCount == 0) {
return JsonResultUtils.fail("not has Detection to Need Import Face");
}
return JsonResultUtils.success();
} catch (Exception e) {
log.error("Face Batch Upload Exception", e);
return JsonResultUtils.fail("Upload or Parse Compress Package Error");
}
}

public static List<File> listSubDirectories(File directoryPath) {
List<File> subDirectories = new ArrayList<>();
if(directoryPath.exists() && directoryPath.isDirectory()) {
File[] subdirectories = directoryPath.listFiles();
if(subdirectories!= null) {
for(File subdirectory: subdirectories) {
if(subdirectory.isDirectory()) {
subDirectories.add(subdirectory);
List<File> subdirectoryList = listSubDirectories(subdirectory);
subDirectories.addAll(subdirectoryList);
}
}
}
}
return subDirectories;
}
}

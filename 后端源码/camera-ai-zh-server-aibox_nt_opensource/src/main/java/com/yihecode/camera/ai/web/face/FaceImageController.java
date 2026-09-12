package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.face.FaceImage;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.https.face.HttpFaceService;
import com.yihecode.camera.ai.netty.MessageSendHandler;
import com.yihecode.camera.ai.netty.data.FaceCompareResponse;
import com.yihecode.camera.ai.netty.data.FaceRecognize2Response;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.face.FaceImageService;
import com.yihecode.camera.ai.service.face.FaceReportService;
import com.yihecode.camera.ai.service.face.FaceUserService;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.ImageUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.*;

import static cn.dev33.satoken.SaManager.log;

/**
* Person member Image - Face Recognition child Module
*/
@Api(tags = "Face Recognition _ Person member Image Management")
@SaCheckLogin
@RestController
@RequestMapping("/face/image")
public class FaceImageController {

    @Resource
    private FaceImageService faceImageService;

    @Resource
    private FaceUserService faceUserService;

    @Resource
    private HttpFaceService httpFaceService;

    @Autowired
    private FaceReportService faceReportService;

    @Autowired
    private MessageSendHandler messageSendHandler;

    @Autowired
    private LocationService locationService;

    @Value("${uploadDir}")
    private String uploadDir;

    private List<String> EXTS = Arrays.asList("jpg", "jpeg", "png");

    @ApiOperation("Image Delete")
    @ApiImplicitParam(name = "id", value = "Image ID")
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping({"/delete"})
    @ResponseBody
    public JsonResult<Long> delete(Long id) {
        FaceImage faceImage = faceImageService.getById(id);
        if(faceImage == null) {
            return JsonResultUtils.fail("Face Image does not exist");
        }
        //
faceImageService.removeById(id);
//
FaceUser faceUser = faceUserService.getById(faceImage.getUserId());
if(faceUser!= null) {
httpFaceService.removeFaceImage(faceImage.getId(), faceUser.getId(), faceUser.getGroupId());
}
return JsonResultUtils.success(id);
}

@ApiOperation("Image Query")
@ApiImplicitParam(name ="userId", value ="Person member ID")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/listData"})
@ResponseBody
public JsonResult<List<FaceImage>> listData(Long userId) {
List<FaceImage> faceImages = faceImageService.listByUser(userId);
if(faceImages == null) {
faceImages = new ArrayList<>();
}
return JsonResultUtils.success(faceImages);
}

@ApiOperation("Avatar Query")
@ApiImplicitParam(name ="userId", value ="Person member ID")
@SaCheckPermission(value = {"face-add","face-edit","faceControl-faceCompare"}, mode = SaMode.OR)
@GetMapping({"/avatar"})
public void avatar(Long userId, HttpServletResponse response) {
// Query Image
FaceImage faceImage = faceImageService.getAvatar(userId);
if(faceImage == null) {
return;
}

// Get Image File Path
String filepath = this.getImageFile(faceImage);
if(filepath == null) {
return;
}

// Back Image
try (FileInputStream fis = new FileInputStream(filepath)) {
response.setContentType("image/jpeg");
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
// e.printStackTrace();
}
}

@ApiOperation("Image Query")
@ApiImplicitParam(name ="imageId", value ="Image ID")
@SaCheckPermission(value = {"face-view"}, mode = SaMode.OR)
@GetMapping({"/getImage"})
public void getImage(Long imageId, HttpServletResponse response) {
FaceImage faceImage = faceImageService.getById(imageId);
if(faceImage == null) {
return;
}
// Get Image File Path
String filepath = this.getImageFile(faceImage);
if(filepath == null) {
return;
}

// Back Image
try (FileInputStream fis = new FileInputStream(filepath)) {
response.setContentType("image/jpeg");
fis.getChannel().transferTo(0, fis.available(), Channels.newChannel(response.getOutputStream()));
} catch (Exception e) {
// e.printStackTrace();
}
}

@ApiOperation("Face Recognition")
@ApiImplicitParam(name ="file", value ="Image File")
@SaCheckPermission(value = {"faceControl-faceCompare"}, mode = SaMode.OR)
@PostMapping({"/recognize"})
@ResponseBody
public JsonResult recognize(@RequestParam(name ="file", required = true) MultipartFile file) throws IOException {
String extName = FileUtil.extName(file.getOriginalFilename());
if(extName == null ||!EXTS.contains(extName.toLowerCase())) {
return JsonResultUtils.fail(file.getOriginalFilename() +"not Support Image Type");
}

// Query Face Box
List<Location> locationList = locationService.listFaceBox();
if(ObjectUtil.isEmpty(locationList)) {
return JsonResultUtils.fail("not Use can Use Face Box");
}

// Save Image
String imageName = IdUtil.randomUUID() +"."+ extName;
String filepath = FileUtils.pathTo(uploadDir +"/"+ imageName);
try {
file.transferTo(new File(filepath));
} catch (Exception e) {
return JsonResultUtils.fail("Face Image Upload Error");
}

// Send Request
String resultJson = null;
for(Location location: locationList) {
try {
Response response = messageSendHandler.sendFaceRecognize2(location, imageName);
if (response == null) {
continue;
}

FaceRecognize2Response faceRecognize2Response = (FaceRecognize2Response) response;
if (!faceRecognize2Response.isStatus()) {
continue;
}
resultJson = faceRecognize2Response.getResultJson();
break;
} catch (Exception e) {
continue;
}
}

//
if(StrUtil.isBlank(resultJson)) {
return JsonResultUtils.fail("Face Search Failed, no Service can Use");
}

//
JSONObject rootJson = JSON.parseObject(resultJson);
if(!"0".equalsIgnoreCase(rootJson.getString("status"))) {
return JsonResultUtils.fail("Face Search Failed,"+ rootJson.getString("msg"));
}

Map<String, Object> resultMap = new HashMap<>();
resultMap.put("json", resultJson);

JSONArray faces = rootJson.getJSONArray("faces");
if(faces == null || faces.isEmpty()) {
return JsonResultUtils.fail("Search no Face");
}

// No One sheet Face
JSONObject firstFace = faces.getJSONObject(0);
JSONObject firstFaceUser = firstFace.getJSONObject("user_info");
FaceImage fFaceImage = faceImageService.getById(firstFaceUser.getLong("face_id"));
resultMap.put("faceImage", fFaceImage);


//
// String resultJson ="{\"msg\":\"\",\"faces\":[{\"user_info\":{\"user_id\":1944409198319378433,\"group_id\":1718922509339394048,\"face_id\":1944409198319378434},\"bbox\":[10,10,10,10],\"liveness\":1,\"similiarity\":1.0}],\"status\":\"0\"}";
//
//
// JSONObject jsonObject = JSON.parseObject(resultJson);
// JSONArray faces = jsonObject.getJSONArray("faces");
// if (null!= faces &&!faces.isEmpty()) {
// JSONObject ff = faces.getJSONObject(0);
// JSONObject fUserInfo = ff.getJSONObject("user_info");
// Long fFaceId = fUserInfo.getLong("face_id");
// FaceImage fFaceImage = faceImageService.getById(fFaceId);
// resultMap.put("faceImage", fFaceImage);
// //save
// for (int i = 0; i < faces.size(); i++) {
// JSONObject face = faces.getJSONObject(i);
// JSONObject userInfo = face.getJSONObject("user_info");
// Long faceId = userInfo.getLong("face_id");
// Long groupId = userInfo.getLong("group_id");
// Long userId = userInfo.getLong("user_id");
// FaceImage faceImage = faceImageService.getById(faceId);
// List<Integer> points = new ArrayList<>();
// JSONArray bbox = face.getJSONArray("bbox");
// int blen = bbox.size();
// for(int j = 0; j < blen; j++) {
// points.add(Integer.parseInt(bbox.get(j).toString()));
//}
// // Frame
// String mainname = FileUtil.mainName(filepath);
// String drawoutfile = uploadDir +"/"+ mainname +"_"+ i +".jpg";
// try {
// ImageUtils.croppedImage2SafeBox(filepath, drawoutfile, points.get(0), points.get(1), points.get(2), points.get(3));
//} catch (Exception e) {
// log.error("Cut Get Avatar Exception", e);
// continue;
//}
//
// FaceReport faceReport = new FaceReport();
// faceReport.setCameraId(null);
// if (null!= faceImage) {
// faceReport.setHasStranger(0);
//} else {
// faceReport.setHasStranger(1);
//}
// faceReport.setResultJson(resultJson);
// faceReport.setFilePath(drawoutfile);
// faceReport.setCreatedAt(new Date());
// faceReport.setCreatedMills(System.currentTimeMillis());
// faceReport.setFaceId(faceId);
// faceReport.setGroupId(groupId);
// faceReport.setUserId(userId);
// faceReportService.save(faceReport);
//}
//
//}
// if(json == null) {
// return JsonResultUtils.fail("Face Search no Recognition Result");
//}

return JsonResultUtils.success(resultMap);
// return JsonResultUtils.fail("Face Search Recognition Exception");
}

@ApiOperation("Face for than")
@ApiImplicitParams({
@ApiImplicitParam(name ="file", value ="Image File 1"),
@ApiImplicitParam(name ="file2", value ="Image File 2")
})
@SaCheckPermission(value = {"faceControl-faceCompare"}, mode = SaMode.OR)
@PostMapping({"/compare"})
@ResponseBody
public JsonResult compare(@RequestParam(name ="file", required = true) MultipartFile file,
@RequestParam(name ="file2", required = true) MultipartFile file2) throws IOException {
String ext1 = FileUtil.extName(file.getOriginalFilename());
if(ext1 == null ||!EXTS.contains(ext1.toLowerCase())) {
return JsonResultUtils.fail(file.getOriginalFilename() +"not Support Image Type");
}
String ext2 = FileUtil.extName(file2.getOriginalFilename());
if(ext2 == null ||!EXTS.contains(ext2.toLowerCase())) {
return JsonResultUtils.fail(file2.getOriginalFilename() +"not Support Image Type");
}

// Query Face Box
List<Location> locationList = locationService.listFaceBox();
if(ObjectUtil.isEmpty(locationList)) {
return JsonResultUtils.fail("not Use can Use Face Box");
}

String newFileName1 = IdUtil.fastSimpleUUID() +"."+ ext1;
String newFileName2 = IdUtil.fastSimpleUUID() +"."+ ext2;
String dest1 = FileUtils.pathTo(this.uploadDir +"/"+ newFileName1);
String dest2 = FileUtils.pathTo(this.uploadDir +"/"+ newFileName2);
try {
// No One sheet image
file.transferTo(new File(dest1));

// No Two sheet image
file2.transferTo(new File(dest2));
} catch (Exception e) {
FileUtil.del(dest1);
FileUtil.del(dest2);
return JsonResultUtils.fail("Image Save Error");
}

// Send Request
for(Location location: locationList) {
try {
Response response = messageSendHandler.sendFaceCompare(location, newFileName1, newFileName2);
if (response == null) {
continue;
}

FaceCompareResponse faceCompareResponse = (FaceCompareResponse) response;
if (!faceCompareResponse.isStatus()) {
continue;
}

FileUtil.del(dest1);
FileUtil.del(dest2);
return JsonResultUtils.success(faceCompareResponse.getResultJson());
} catch (Exception e) {
// continue;
}
}
FileUtil.del(dest1);
FileUtil.del(dest2);
return JsonResultUtils.fail("Face than for Failed");


// String json = httpFaceService.compare(convertMultipartFileToBase64(file), convertMultipartFileToBase64(file2));
// if(json == null) {
// return JsonResultUtils.fail();
//}
// return JsonResultUtils.success(json);
}

private String convertMultipartFileToBase64(MultipartFile file) throws IOException {
byte[] fileContent = file.getBytes();
return Base64.getEncoder().encodeToString(fileContent);
}

/**
* Get Image Path
* @param faceImage
* @return
*/
private String getImageFile(FaceImage faceImage) {
if(faceImage == null || faceImage.getUserId() == null || StrUtil.isBlank(faceImage.getImgUrl())) {
return null;
}

String filepath = FileUtils.pathTo(uploadDir +"/face_imgs/"+ faceImage.getUserId() +"/"+ faceImage.getImgUrl());
if(!FileUtil.exist(filepath)) {
return null;
}

if(!FileUtil.isFile(filepath)) {
return null;
}
return filepath;
}

}

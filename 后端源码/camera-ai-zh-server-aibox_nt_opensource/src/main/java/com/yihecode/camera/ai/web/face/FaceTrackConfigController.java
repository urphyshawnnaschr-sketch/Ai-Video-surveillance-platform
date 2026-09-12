package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.face.FaceTrackCamera;
import com.yihecode.camera.ai.entity.face.FaceTrackConfig;
import com.yihecode.camera.ai.service.face.FaceTrackCameraService;
import com.yihecode.camera.ai.service.face.FaceTrackConfigService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.face.dto.FaceTrackConfigDTO;
import com.yihecode.camera.ai.web.face.vo.FaceTrackConfigModifyVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
* Face Tracking Base image Config
*
* @author zhou
* @since 2025.6.20
*/
@Api(tags = "Face Recognition _ Tracking Base image Config Management")
@Slf4j
@RestController
@RequestMapping("face/track/config")
public class FaceTrackConfigController {

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
    @ApiOperation("List Data")
    @SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
    @PostMapping("list")
    public PageResult<?> list() {
        List<FaceTrackConfig> faceTrackConfigList = faceTrackConfigService.list();
        return PageResultUtils.success((long) faceTrackConfigList.size(), faceTrackConfigList);
    }

    /**
* Add / Edit Data
* @return
*/
    @ApiOperation("Save Data")
    @SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
    @PostMapping("save")
    public JsonResult<Long> save(@RequestBody FaceTrackConfigModifyVo modifyVo) {
        if(StrUtil.isBlank(modifyVo.getName())) {
            return JsonResultUtils.fail("Config Name not Fill");
        }
        if(StrUtil.isBlank(modifyVo.getFilename())) {
            return JsonResultUtils.fail("Base image File not Upload");
        }
        if(modifyVo.getState() == null) {
            return JsonResultUtils.fail("Status not Select");
        }

        if(modifyVo.getId() != null) {
            FaceTrackConfig faceTrackConfig = faceTrackConfigService.getById(modifyVo.getId());
            if(faceTrackConfig != null) {
                modifyVo.setState(faceTrackConfig.getState());
            }
        }

        //like Result not has Config over, rule will Current Record Direct connect Set for Valid
List<FaceTrackConfig> faceTrackConfigList = faceTrackConfigService.list();
if(faceTrackConfigList.isEmpty()) {
modifyVo.setState(1);
} else {
// Determine Whether All for Invalid Status
boolean found = false;
for(FaceTrackConfig faceTrackConfig: faceTrackConfigList) {
if(faceTrackConfig.getState()!= null && faceTrackConfig.getState() == 1) {// Valid
found = true;
break;
}
}

// All Invalid, Current Data rule Set for Valid
if(!found) {
modifyVo.setState(1);
}
}

FaceTrackConfig faceTrackConfig = new FaceTrackConfig();
BeanUtils.copyProperties(modifyVo, faceTrackConfig);

log.info("facetrack config {}", faceTrackConfig);

faceTrackConfigService.saveData(faceTrackConfig);
return JsonResultUtils.success(faceTrackConfig.getId());
}

/**
* Delete Data
* @return
*/
@ApiOperation("Delete Data")
@SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
@PostMapping("delete")
public JsonResult<Long> delete(@RequestBody FaceTrackConfigModifyVo modifyVo) {
if(modifyVo.getId() == null) {
return JsonResultUtils.fail("Param Error");
}

faceTrackConfigService.deleteData(modifyVo.getId());
return JsonResultUtils.success(modifyVo.getId());
}

/**
* Detail Data
* @return
*/
@ApiOperation("Detail Data")
@SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
@PostMapping("info")
public JsonResult<FaceTrackConfigDTO> info(@RequestBody FaceTrackConfigModifyVo modifyVo) {
if(modifyVo.getId() == null) {
return JsonResultUtils.fail("Param Error");
}

FaceTrackConfig faceTrackConfig = faceTrackConfigService.getById(modifyVo.getId());
List<FaceTrackCamera> faceTrackCameraList = faceTrackCameraService.listByConfig(modifyVo.getId());

FaceTrackConfigDTO faceTrackConfigDTO = new FaceTrackConfigDTO();
faceTrackConfigDTO.setFaceTrackConfig(faceTrackConfig);
faceTrackConfigDTO.setFaceTrackCameraList(faceTrackCameraList);
return JsonResultUtils.success(faceTrackConfigDTO);
}

/**
* Detail Data _ Default
* @return
*/
@ApiOperation("Default Display Base image Data")
@SaCheckPermission(value = {"faceControl-faceManagent","faceControl-stranger"}, mode = SaMode.OR)
@PostMapping("primary")
public JsonResult<FaceTrackConfigDTO> primary() {
FaceTrackConfig faceTrackConfig = faceTrackConfigService.getPrimary();
if(faceTrackConfig == null) {
return JsonResultUtils.fail("lack Missing Default Config");
}

List<FaceTrackCamera> faceTrackCameraList = faceTrackCameraService.listByConfig(faceTrackConfig.getId());

FaceTrackConfigDTO faceTrackConfigDTO = new FaceTrackConfigDTO();
faceTrackConfigDTO.setFaceTrackConfig(faceTrackConfig);
faceTrackConfigDTO.setFaceTrackCameraList(faceTrackCameraList);
return JsonResultUtils.success(faceTrackConfigDTO);
}

/**
* Upload File
* @return
*/
@ApiOperation("Upload Base image Image")
@SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
@ApiImplicitParam(name ="file", value ="Image File", required = true)
@PostMapping("upload")
public JsonResult<?> upload(MultipartFile file) {
if(file == null) {
return JsonResultUtils.fail("File not Select");
}

String ext = FileUtil.extName(file.getOriginalFilename());
if(StrUtil.isBlank(ext) ||!("jpg".equalsIgnoreCase(ext) ||"jpeg".equalsIgnoreCase(ext) ||"png".equalsIgnoreCase(ext))) {
return JsonResultUtils.fail("File Format Error, Only Support jpg,jpeg,png");
}

// Create File Directory
String dest = FileUtils.pathTo(uploadDir + File.separator +"face_track"+ File.separator);
if(!FileUtil.exist(dest)) {
FileUtil.mkdir(dest);
}

try {
String filename = IdUtil.fastSimpleUUID() +"."+ ext;
String filepath = FileUtils.pathTo(dest + File.separator + filename);
file.transferTo(new File(filepath));
return JsonResultUtils.success(filename);
} catch (Exception e) {
log.info("Face Tracking, Base image File Upload Exception");
return JsonResultUtils.fail("File Upload Exception");
}
}

@ApiOperation("Upload Base image Image")
@ApiImplicitParam(name ="filename", value ="Image File Name", required = true)
@SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
@GetMapping("image")
public void image(String filename, HttpServletResponse response) {
if(StrUtil.isBlank(filename)) {
return;
}

String ext = FileUtil.extName(filename);
if(StrUtil.isBlank(ext) ||!("jpg".equalsIgnoreCase(ext) ||"jpeg".equalsIgnoreCase(ext) ||"png".equalsIgnoreCase(ext))) {
return;
}

// Create File Directory
String dest = FileUtils.pathTo(uploadDir + File.separator +"face_track"+ File.separator + filename);
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
}

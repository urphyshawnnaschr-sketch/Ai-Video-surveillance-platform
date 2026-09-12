package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.entity.CameraGroupItem;
import com.yihecode.camera.ai.entity.face.FaceTrackCamera;
import com.yihecode.camera.ai.service.CameraGroupItemService;
import com.yihecode.camera.ai.service.CameraGroupService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.face.FaceTrackCameraService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.face.dto.FaceTrackCameraDTO;
import com.yihecode.camera.ai.web.face.dto.FaceTrackCameraItemDTO;
import com.yihecode.camera.ai.web.face.vo.FaceTrackCameraModifyVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* Face Tracking Insert Point Config
*
* @author zhou
* @since 2025.6.20
*/
@Api(tags = "Face Recognition _ Tracking Camera Insert Point Management")
@Slf4j
@RestController
@RequestMapping("face/track/camera")
public class FaceTrackCameraController {

    @Autowired
    private FaceTrackCameraService faceTrackCameraService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private CameraGroupService cameraGroupService;

    @Autowired
    private CameraGroupItemService cameraGroupItemService;

    /**
* Add / Modify Insert Point Config
* @param modifyVo
* @return
*/
    @ApiOperation("Save Data")
    @SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
    @PostMapping("save")
    private JsonResult<Long> save(@RequestBody FaceTrackCameraModifyVo modifyVo) {
        if(modifyVo.getConfigId() == null) {
            return JsonResultUtils.fail("Base image Config not Select");
        }
        if(modifyVo.getCameraId() == null) {
            return JsonResultUtils.fail("Camera not Select");
        }
        if(ObjectUtil.isEmpty(modifyVo.getPosition())) {
            return JsonResultUtils.fail("Insert Point not Config");
        }

        FaceTrackCamera faceTrackCamera = new FaceTrackCamera();
        BeanUtils.copyProperties(modifyVo, faceTrackCamera);
        faceTrackCameraService.saveOrUpdate(faceTrackCamera);
        return JsonResultUtils.success(faceTrackCamera.getId());
    }

    /**
* Delete Insert Point Config
* @param modifyVo
* @return
*/
    @ApiOperation("Delete Data")
    @SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
    @PostMapping("delete")
    private JsonResult<Long> delete(@RequestBody FaceTrackCameraModifyVo modifyVo) {
        if(modifyVo.getId() == null) {
            return JsonResultUtils.fail("Param Error");
        }

        faceTrackCameraService.removeById(modifyVo.getId());
        return JsonResultUtils.success(modifyVo.getId());
    }

    /**
* Delete Insert Point Config
* @param modifyVo
* @return
*/
    @ApiOperation("List Data")
    @SaCheckPermission(value = {"faceControl-faceManagent"}, mode = SaMode.OR)
    @PostMapping("list")
    private JsonResult<FaceTrackCameraDTO> list(@RequestBody FaceTrackCameraModifyVo modifyVo) {
        if(modifyVo.getConfigId() == null) {
            return JsonResultUtils.fail("Param Error");
        }

        List<FaceTrackCameraItemDTO> markedList = new ArrayList<>();
        List<FaceTrackCameraItemDTO> unmarkList = new ArrayList<>();

        //Query All Camera
List<Camera> cameraList = cameraService.listData();
Map<Long, String> cameraMap = cameraList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Camera::getId, Camera::getName));

// Query All Group
List<CameraGroup> cameraGroupList = cameraGroupService.list();

// Query All Camera and group Relate
List<CameraGroupItem> cameraGroupItemList = cameraGroupItemService.list();
Map<Long, Long> cameraGroupItemMap = cameraGroupItemList.stream().collect(Collectors.toMap(CameraGroupItem::getCameraId, CameraGroupItem::getGroupId));

// Query Mark Camera
List<FaceTrackCamera> faceTrackCameraList = faceTrackCameraService.listByConfig(modifyVo.getConfigId());
if(ObjectUtil.isEmpty(faceTrackCameraList)) {
// not Mark Camera
for(Camera camera: cameraList) {
FaceTrackCameraItemDTO itemDTO = new FaceTrackCameraItemDTO();
itemDTO.setId(0L);
itemDTO.setConfigId(modifyVo.getConfigId());
itemDTO.setCameraId(camera.getId());
itemDTO.setCameraName(camera.getName());
itemDTO.setGroupName(cameraGroupService.getCurrentAndParentNames(cameraGroupItemMap.getOrDefault(camera.getId(), -9L), cameraGroupList));
unmarkList.add(itemDTO);
}

FaceTrackCameraDTO faceTrackCameraDTO = new FaceTrackCameraDTO();
faceTrackCameraDTO.setMarkedList(markedList);
faceTrackCameraDTO.setUnmarkList(unmarkList);
return JsonResultUtils.success(faceTrackCameraDTO);
}

//
List<Long> markedCameraIds = new ArrayList<>();
for(FaceTrackCamera faceTrackCamera: faceTrackCameraList) {
FaceTrackCameraItemDTO itemDTO = new FaceTrackCameraItemDTO();
itemDTO.setId(faceTrackCamera.getId());
itemDTO.setConfigId(modifyVo.getConfigId());
itemDTO.setCameraId(faceTrackCamera.getCameraId());
itemDTO.setCameraName(cameraMap.getOrDefault(faceTrackCamera.getCameraId(),"Unknown"));
itemDTO.setGroupName(cameraGroupService.getCurrentAndParentNames(cameraGroupItemMap.getOrDefault(faceTrackCamera.getCameraId(), -9L), cameraGroupList));
itemDTO.setPosition(faceTrackCamera.getPosition());
markedList.add(itemDTO);

// Mark Camera
markedCameraIds.add(faceTrackCamera.getCameraId());
}

// not Mark Camera
for(Camera camera: cameraList) {
if(markedCameraIds.contains(camera.getId())) {
continue;
}

//
FaceTrackCameraItemDTO itemDTO = new FaceTrackCameraItemDTO();
itemDTO.setId(0L);
itemDTO.setConfigId(modifyVo.getConfigId());
itemDTO.setCameraId(camera.getId());
itemDTO.setCameraName(camera.getName());
itemDTO.setGroupName(cameraGroupService.getCurrentAndParentNames(cameraGroupItemMap.getOrDefault(camera.getId(), -9L), cameraGroupList));
unmarkList.add(itemDTO);
}

FaceTrackCameraDTO faceTrackCameraDTO = new FaceTrackCameraDTO();
faceTrackCameraDTO.setMarkedList(markedList);
faceTrackCameraDTO.setUnmarkList(unmarkList);
return JsonResultUtils.success(faceTrackCameraDTO);
}
}

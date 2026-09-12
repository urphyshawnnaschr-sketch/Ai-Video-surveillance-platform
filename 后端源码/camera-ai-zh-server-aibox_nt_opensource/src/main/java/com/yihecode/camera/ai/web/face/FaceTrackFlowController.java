package com.yihecode.camera.ai.web.face;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.entity.face.FaceTrackCamera;
import com.yihecode.camera.ai.entity.face.FaceTrackConfig;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.face.FaceReportService;
import com.yihecode.camera.ai.service.face.FaceTrackCameraService;
import com.yihecode.camera.ai.service.face.FaceTrackConfigService;
import com.yihecode.camera.ai.service.face.FaceUserService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.face.dto.FaceTrackDTO;
import com.yihecode.camera.ai.web.face.dto.FaceTrackFlowDTO;
import com.yihecode.camera.ai.web.face.dto.FaceTrackUserDTO;
import com.yihecode.camera.ai.web.face.vo.FaceTrackFlowListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* Face Tracking Person member Point Bit Config
*
* @author zhou
* @since 2025.6.20
*/
@Api(tags = "Face Recognition _ Stranger produce Person Search Query")
@RestController
@RequestMapping("face/track/flow")
public class FaceTrackFlowController {

    @Autowired
    private FaceReportService faceReportService;

    @Autowired
    private FaceUserService faceUserService;

    @Autowired
    private FaceTrackConfigService faceTrackConfigService;

    @Autowired
    private FaceTrackCameraService faceTrackCameraService;

    @Autowired
    private CameraService cameraService;

    /**
* Query Person member via over Insert Point Bit set
* @param listVo
* @return
*/
    @ApiOperation("List Data")
    @SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
    @PostMapping("list")
    private JsonResult<FaceTrackDTO> save(@RequestBody FaceTrackFlowListVo listVo) {
        if(listVo.getStartDate() == null || listVo.getEndDate() == null) {
            return JsonResultUtils.fail("Start Time or End Time not Select");
        }

        if(listVo.getUserId() == null) {
            return JsonResultUtils.fail("Target Person member not Select");
        }

        //Query Base image Config
FaceTrackConfig faceTrackConfig = faceTrackConfigService.getPrimary();
if(faceTrackConfig == null) {
return JsonResultUtils.fail("lack Missing Base image Config");
}

// Query Insert Point
List<FaceTrackCamera> faceTrackCameraList = faceTrackCameraService.listByConfig(faceTrackConfig.getId());
if(faceTrackCameraList.isEmpty()) {
return JsonResultUtils.fail("no Camera Insert Point Record");
}
Map<Long, List<Integer>> positionMap = faceTrackCameraList.stream().collect(Collectors.toMap(FaceTrackCamera::getCameraId, FaceTrackCamera::getPosition));

// Query Alarm Record
List<FaceReport> faceReportList = faceReportService.listByUser(listVo.getUserId(), listVo.getStartDate(), listVo.getEndDate(), null, null);
if(faceReportList.isEmpty()) {
return JsonResultUtils.success(new FaceTrackDTO());
}

// Query Camera
List<Camera> cameraList = cameraService.list();
Map<Long, String> cameraMap = cameraList.stream().filter(s -> StrUtil.isNotBlank(s.getName())).collect(Collectors.toMap(Camera::getId, Camera::getName));

// Get Insert Point Info
Long prevCameraId = 0L;
List<FaceTrackFlowDTO> faceTrackFlowDTOList = new ArrayList<>();
for(FaceReport faceReport: faceReportList) {
List<Integer> position = positionMap.get(faceReport.getCameraId());
if(position == null) {
continue;
}

// like Result front One Record Camera ID and Current Camera ID Consistent, only Keep front One Record ID
if(faceReport.getCameraId().equals(prevCameraId)) {
continue;
}
prevCameraId = faceReport.getCameraId();

FaceTrackFlowDTO faceTrackFlowDTO = new FaceTrackFlowDTO();
faceTrackFlowDTO.setCameraId(faceReport.getCameraId());
faceTrackFlowDTO.setCameraName(cameraMap.getOrDefault(faceReport.getCameraId(),"-"));
faceTrackFlowDTO.setDate(DateUtil.format(faceReport.getCreatedAt(),"yyyy-MM-dd HH:mm:ss"));
faceTrackFlowDTO.setPosition(position);
faceTrackFlowDTOList.add(faceTrackFlowDTO);
}

// User Info
FaceTrackUserDTO faceTrackUserDTO = new FaceTrackUserDTO();
FaceUser faceUser = faceUserService.getById(listVo.getUserId());
if(faceUser!= null) {
faceTrackUserDTO.setId(faceUser.getId());
faceTrackUserDTO.setName(faceUser.getName());
faceTrackUserDTO.setTel(faceUser.getTel());
faceTrackUserDTO.setRemark(faceUser.getRemark());
}

FaceTrackDTO faceTrackDTO = new FaceTrackDTO();
faceTrackDTO.setFlowList(faceTrackFlowDTOList);
faceTrackDTO.setUser(faceTrackUserDTO);

return JsonResultUtils.success(faceTrackDTO);
}
}

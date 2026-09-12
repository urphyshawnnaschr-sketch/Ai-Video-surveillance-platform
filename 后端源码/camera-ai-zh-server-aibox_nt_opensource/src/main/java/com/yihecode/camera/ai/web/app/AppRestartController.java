package com.yihecode.camera.ai.web.app;

import cn.hutool.core.util.IdUtil;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.netty.data.RestartRequest;
import com.yihecode.camera.ai.netty.data.RestartResponse;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "app End _ Restart Phase close")
@RestController
@RequestMapping("app/restart")
public class AppRestartController {

    @Autowired
    private CameraService cameraService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    @ApiOperation("Restart Algorithm")
    @ApiImplicitParam(name = "cameraId", value = "Camera ID", required = true, example = "1")
    @GetMapping(value = "restart")
    public JsonResult<?> restart(Long cameraId) {
        Camera camera = cameraService.getById(cameraId);
        if(camera == null) {
            return JsonResultUtils.fail("find not to Camera");
        }

        Location location = locationService.getById(camera.getLocationId());
        if(location == null) {
            return JsonResultUtils.fail("Camera not Relate Box Device");
        }

        RestartRequest request = new RestartRequest();
        request.setType(MessageType.RESTART.getType());
        request.setRequestId(IdUtil.randomUUID());
        request.setSn(location.getBoxNo());
        Response response = messageSenderAndWaiter.sendRequest(request);
        if(response == null) {
            return JsonResultUtils.fail("Restart Inference Service Failed, Unknown Error");
        }

        RestartResponse restartResponse = (RestartResponse) response;
        if(restartResponse.isStatus()) {
            return JsonResultUtils.success("Restart Inference Service Success");
        }

        return JsonResultUtils.fail(restartResponse.getMsg());
    }
}

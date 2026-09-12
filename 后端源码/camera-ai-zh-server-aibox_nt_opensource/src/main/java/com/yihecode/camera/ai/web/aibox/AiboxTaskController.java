package com.yihecode.camera.ai.web.aibox;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.IdUtil;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.netty.data.RestartRequest;
import com.yihecode.camera.ai.netty.data.RestartResponse;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* Edge Box Operation Phase close Management
*/
@Api(tags = "Edge Box Operation Phase close Management")
@RestController
@RequestMapping("/aibox/task")
public class AiboxTaskController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    @ApiOperation(value = "Restart Box")
    @ApiImplicitParam(name = "id", value = "id")
    @SaCheckPermission(value = {"caske-restart"}, mode = SaMode.OR)
    @PostMapping("/restart")
    public JsonResult<String> restart(Long id) {
        Location location = locationService.getById(id);
        if(location == null) {
            return JsonResultUtils.fail("Box Device not find to");
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

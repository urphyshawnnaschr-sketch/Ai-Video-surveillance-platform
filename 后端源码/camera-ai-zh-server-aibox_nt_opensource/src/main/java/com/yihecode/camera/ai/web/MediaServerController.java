package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.MediaServer;
import com.yihecode.camera.ai.media.MediaRestfulService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.MediaServerService;
import com.yihecode.camera.ai.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
* Stream Media Node Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Stream Media Node Management")
@SaCheckLogin
@Controller
@RequestMapping({"/media/server"})
public class MediaServerController {

    @Autowired
    private MediaServerService mediaServerService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private MediaRestfulService mediaRestfulService;

    @ApiOperation(value = "Data List")
    @SaCheckPermission(value = {"mediaServer"}, mode = SaMode.OR)
    @PostMapping({"/listPage"})
    @ResponseBody
    public PageResult<?> listPage() {
        List<MediaServer> mediaServers = mediaServerService.list();
        if(mediaServers == null) {
            mediaServers = new ArrayList<>();
        }
        return PageResultUtils.success(null, mediaServers);
    }

    @ApiOperation(value = "Save Data")
    @SaCheckPermission(value = {"mediaServer"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult<?> save(@RequestBody MediaServer mediaServer) throws Exception {
        if(StrUtil.isBlank(mediaServer.getName())) {
            return JsonResultUtils.fail("Media Node Name cannot be empty");
        }
        if(StrUtil.isBlank(mediaServer.getIp())) {
            return JsonResultUtils.fail("ip Address cannot be empty");
        }
        if(mediaServer.getHttpPort() == null) {
            return JsonResultUtils.fail("http Port cannot be empty");
        }
        if(mediaServer.getRtspPort() == null) {
            return JsonResultUtils.fail("rtsp Port cannot be empty");
        }
        if(mediaServer.getRtcPort() == null) {
            return JsonResultUtils.fail("rtc Port cannot be empty");
        }
        if(StrUtil.isBlank(mediaServer.getRtpPortRange())) {
            return JsonResultUtils.fail("rtp Receive Port Range cannot be empty");
        }
        if(StrUtil.isBlank(mediaServer.getSendRtpPortRange())) {
            return JsonResultUtils.fail("rtp Send Port Range cannot be empty");
        }
        if(StrUtil.isBlank(mediaServer.getSecret())) {
            return JsonResultUtils.fail("Password cannot be empty");
        }

        MediaServer mediaServerDb = mediaServerService.getByIpAndPort(mediaServer.getIp(), mediaServer.getHttpPort());
        MediaServer mediaServerDb2 = mediaServerService.getByName(mediaServer.getName());
        if(mediaServer.getId() == null) {
            if(mediaServerDb != null) {
                return JsonResultUtils.fail("Media Node Exist");
            }
            if(mediaServerDb2 != null) {
                return JsonResultUtils.fail("Media Node Name Exist");
            }
        } else {
            if(mediaServerDb != null && !mediaServer.getId().equals(mediaServerDb.getId())) {
                return JsonResultUtils.fail("Media Node Exist");
            }
            if(mediaServerDb2 != null && !mediaServer.getId().equals(mediaServerDb2.getId())) {
                return JsonResultUtils.fail("Media Node Name Exist");
            }
        }

        mediaServerService.saveData(mediaServer);

        return JsonResultUtils.success();
    }

    @ApiOperation(value = "Delete Data")
    @ApiImplicitParam(name = "id", value = "id")
    @SaCheckPermission(value = {"mediaServer"}, mode = SaMode.OR)
    @GetMapping({"/delete"})
    @ResponseBody
    public JsonResult<?> delete(Long id) throws Exception {
        int count = cameraService.countByMediaServer(id);
        if(count > 0) {
            return JsonResultUtils.fail("Media Node make Use, not can Direct connect Delete");
        }
        mediaServerService.removeData(id);
        return JsonResultUtils.success();
    }

    @ApiOperation(value = "Detail Data")
    @ApiImplicitParam(name = "id", value = "id")
    @SaCheckPermission(value = {"mediaServer"}, mode = SaMode.OR)
    @GetMapping("info")
    @ResponseBody
    public JsonResult<?> info(Long id) {
        return JsonResultUtils.success(mediaServerService.getById(id));
    }

    @ApiOperation(value = "Verify")
    @ApiImplicitParam(name = "id", value = "id")
    @SaCheckPermission(value = {"mediaServer"}, mode = SaMode.OR)
    @PostMapping("check")
    @ResponseBody
    public JsonResult<?> check(Long id) {
        MediaServer mediaServer = mediaServerService.getById(id);
        if(mediaServer == null) {
            return JsonResultUtils.fail("Media Node does not exist");
        }
        return JsonResultUtils.success(mediaRestfulService.check(mediaServer));
    }
}
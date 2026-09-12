package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.util.ObjectUtil;
import com.yihecode.camera.ai.dto.ApExportDTO;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.entity.ap.ApExportDO;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.ap.ApExportService;
import com.yihecode.camera.ai.utils.FtpUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/28 23:38
* @Describe
* @Version 1.0
*/
@ApiIgnore
@Api(tags = "Export Management")
@Controller
@RequestMapping({"/ap/export"})
public class ApExportController {
    @Autowired
    private ApExportService apExportService;


    @ApiOperation(value = "Save Export")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult<Long> save(@RequestBody ApExportDTO apExportDTO) throws IOException, BizException {
        return JsonResultUtils.success(
                apExportService.save(apExportDTO));
    }

    @ApiOperation(value = "Query Export Record")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @GetMapping({"/list"})
    @ResponseBody
    public JsonResult<List<ApExportDTO>> list(@RequestParam("projectId") Long projectId) throws IOException, BizException {
        return JsonResultUtils.success(
                apExportService.findExPortList(projectId));
    }

    @ApiOperation(value = "Delete Export Record")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @DeleteMapping({"/delete"})
    @ResponseBody
    public JsonResult<List<ApExportDTO>> delete(@RequestParam("id") Long id) throws IOException, BizException {
        apExportService.delete(id);
        return JsonResultUtils.success();
    }

    @ApiOperation(value = "Download Export")
    @SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
    @GetMapping({"/dowload"})
    @ResponseBody
    public JsonResult dowload(@RequestParam("id") Long id,HttpServletResponse response) throws IOException, BizException {

        ApExportDO apExportDO = apExportService.getById(id);
        if(ObjectUtil.isNull(apExportDO)){
            return JsonResultUtils.fail("not has Export Record");
        }
        if(apExportDO.getStatus()!=1){
            return JsonResultUtils.fail("still not Type Package Complete Complete, Please Retry Later");
        }

        System.out.println("File Address {}" + apExportDO.getStoragePath());

        response.addHeader("Content-Disposition", "attachment:filename=" + StringUtils.substringAfterLast(apExportDO.getStoragePath(),"/"));
        FtpUtils.download(apExportDO.getStoragePath(),response);
        return JsonResultUtils.success();
    }
}

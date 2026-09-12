package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.SoundColumn;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.SoundColumnService;
import com.yihecode.camera.ai.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
* IP Speaker Pole Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Api(tags = "IP Speaker Pole Management")
@Controller
@RequestMapping({"/soundColumn"})
public class SoundColumnController {

    @Autowired
    private SoundColumnService soundColumnService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ConfigService configService;

    @Value("${dataModelsDir}")
    public String MODEL_DIR;

    @ApiOperation("Detail")
    @ApiImplicitParam(name = "id", value = "id")
    @SaCheckPermission(value = {"soundColumnManagement"}, mode = SaMode.OR)
    @RequestMapping({"/detail"})
    @ResponseBody
    public JsonResult<SoundColumn> detail(Long id) {
        if (id == null) {
            return JsonResultUtils.fail("find not to Data");
        }
        return JsonResultUtils.success(this.soundColumnService.getById(id));
    }

    @ApiOperation(value = "Data List")
    @SaCheckPermission(value = {"soundColumnManagement", "box-add", "box-edit"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public PageResult<List<SoundColumn>> listData() {
        List<SoundColumn> soundColumnList = this.soundColumnService.list();
        if (soundColumnList == null) {
            soundColumnList = new ArrayList<>();
        }
        return PageResultUtils.success(null, soundColumnList);
    }

    @ApiOperation(value = "Save Data")
    @SaCheckPermission(value = {"soundColumnManagement"}, mode = SaMode.OR)
    @PostMapping({"/save"})
    @ResponseBody
    public JsonResult<Void> save(SoundColumn soundColumn) {
        if (StrUtil.isBlank(soundColumn.getServer())) {
            return JsonResultUtils.fail("Please enter Speaker Pole Server");
        }
        if (StrUtil.isBlank(soundColumn.getSn())) {
            return JsonResultUtils.fail("Please enter Speaker Pole No");
        }
        if (soundColumn.getVol() == null) {
            return JsonResultUtils.fail("Please enter Select Audio Quantity");
        }

        //IPv4 Segment:0~255
String IPV4_SEGMENT ="(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

// Complete whole IPv4
String IPV4 ="(?:"+ IPV4_SEGMENT +"\\.){3}"+ IPV4_SEGMENT;

// Port Range:1 ~ 65535
String PORT ="(?:[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-3][0-5])";

// Complete whole IP:PORT Format
String IP_PORT_PATTERN ="^"+ IPV4 +":"+ PORT +"$";
boolean matcher = Pattern.compile(IP_PORT_PATTERN).matcher(soundColumn.getServer()).matches();
if(!matcher) {
return JsonResultUtils.fail("Speaker Pole Server only Support ip:port Format");
}

soundColumnService.saveOrUpdate(soundColumn);

// Adjust whole Audio Quantity
SoundColumnUtils.sendVol(soundColumn.getServer(), soundColumn.getSn(), soundColumn.getVol());
return JsonResultUtils.success();
}

/**
* Delete Data
* @param id
* @return
*/
@ApiOperation(value ="Delete Data")
@ApiImplicitParam(name ="id", value ="id")
@PostMapping({"/delete"})
@ResponseBody
public JsonResult delete(Long id) {
this.soundColumnService.removeById(id);
return JsonResultUtils.success();
}

/**
* Test
* @param sn
* @param name
* @return
*/
@SaCheckPermission("XXXXXXX")
@GetMapping("test/play")
@ResponseBody
public JsonResult testPlay(String sn, String name) {
log.info("Speaker Pole Test Play Put Request Param, sn: {}, name: {}", sn, name);
List<Algorithm> algorithmList = algorithmService.list();
if(algorithmList == null) {
algorithmList = new ArrayList<>();
}
//
SoundColumn soundColumn = soundColumnService.getBySn(sn);
if(soundColumn == null) {
return JsonResultUtils.fail("find not to Speaker Pole");
}

//
boolean ok = false;
for(Algorithm algorithm: algorithmList) {
if(algorithm.getName().equals(name)) {
ok = true;
//
String soundFile = algorithm.getSoundFile();
String path = MODEL_DIR +"/soundFile/"+ soundFile;
System.out.println("path"+ path);
log.info("path {}", path);
File tar = new File(path);
if(tar.exists() && tar.isFile()) {
File copyTar = new File("/home/yihecode/www/dist/mp3/");
if(!copyTar.exists()) {
copyTar.mkdirs();
}
File file = new File(copyTar.getAbsolutePath() + File.separator + soundFile);
if(!file.exists()) {
FileUtil.copy(tar, copyTar, true);
}

// Generate mp3 Address
String ipAddr = configService.getByValTag("ipAddr");
String[] ipAddrInfo = ipAddr.split(":");
String nginxWeb = ipAddrInfo[0] +":8090";

//String mp3file ="http://"+ nginxWeb +"/algorithm/sound/stream?id="+ algorithm.getId();
String mp3file ="http://"+ nginxWeb +"/mp3/"+ soundFile;
log.info("ip add: {} & mp3file: {}???", ipAddr, mp3file);
System.out.println("ip add: {} & mp3file: {}???"+ ipAddr +""+ mp3file);
//
SoundColumnUtils.sendPlay(soundColumn.getServer(), soundColumn.getSn(), mp3file);
} else {
System.out.println("not can Push Voice, Sound File does not exist"+ name +""+ soundFile);
log.error("not can Push Voice, Sound File does not exist name: {}, soundFile: {}", name, soundFile);
}
break;
}
}
System.out.println(ok);
return JsonResultUtils.success(ok?"find to Camera":"find not to Camera");
}
}
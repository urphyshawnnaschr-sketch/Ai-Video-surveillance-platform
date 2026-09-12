package com.yihecode.camera.ai.web.api.aibox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.yihecode.camera.ai.utils.FileUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

/**
* from Box Snapshot according, after Upload Image
*
* @author 465769438@qq.com
* @since 2025/3/7
*/
@ApiIgnore
@Slf4j
@RestController
@RequestMapping("aibox/upload")
public class AiBoxUploadController {

    @Value("${uploadDir}")
    private String uploadDir;

    /**
* Box Snapshot according after, Image Upload
* @param file
* @return
*/
    @PostMapping(value = "image")
    public JsonResult<?> uploadImage(MultipartFile file) {
        if(file == null) {
            return JsonResultUtils.fail("not has Image File");
        }

        try {
            String filename = IdUtil.randomUUID() + ".jpg";
            String saveDest = FileUtils.pathTo(uploadDir + "/" + filename);
            file.transferTo(FileUtil.newFile(saveDest));
            return JsonResultUtils.success(filename);
        } catch (Exception e) {
            log.error("Box Upload Image Exception, ex: {}", e.getMessage());
        }
        return JsonResultUtils.fail("File Upload failed");
    }

}

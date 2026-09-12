package com.yihecode.camera.ai.service.ap.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.entity.ap.ApFileDO;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApFileMapper;
import com.yihecode.camera.ai.service.ap.ApFileService;
import com.yihecode.camera.ai.service.ap.ApProjectService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @Author lichangliang
* @Date 2023/7/21 21:56
* @Describe
* @Version 1.0
*/
@Service
public class ApFileServiceImpl extends ServiceImpl<ApFileMapper, ApFileDO> implements ApFileService {

    @Autowired
    private ApProjectService apProjectService;


    @Override
    public Long saveFile(String adress, Integer type, Long projectId, Long userId) throws BizException {
        ApFileDO fileDO = ApFileDO.builder().createdAt(new Date())
                .status("0").rawData(adress).fileType(type).userId(userId).build();
        fileDO.setStatus("0");
        baseMapper.insert(fileDO);
        if (type.compareTo(1) != 0) {
            ApProjectDTO apProjectDTO = apProjectService.getpProjectById(projectId);
            if (ObjectUtil.isNull(apProjectDTO)) {
                return fileDO.getId();
            }
            apProjectDTO.setDocFileId(fileDO.getId());
            apProjectService.updatepProjectById(apProjectDTO);
        }
        return fileDO.getId();
    }
}

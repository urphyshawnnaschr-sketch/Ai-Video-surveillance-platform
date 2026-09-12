package com.yihecode.camera.ai.service.ap.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.dto.ApExportDTO;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.entity.ap.ApExportDO;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApExportMapper;
import com.yihecode.camera.ai.service.ap.ApExportService;
import com.yihecode.camera.ai.service.ap.ApProjectService;
import com.yihecode.camera.ai.utils.DatasetExportHandleUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/28 16:11
* @Describe
* @Version 1.0
*/
@Service
public class ApExportServiceImpl extends ServiceImpl<ApExportMapper, ApExportDO> implements ApExportService {

    @Autowired
    private ApProjectService apProjectService;

    @Autowired
    @Lazy
    private DatasetExportHandleUtil datasetExportHandleUtil;
    /**
* Query Project Export List
*
* @param projectId
* @return
*/
    @Override
    public List<ApExportDTO> findExPortList(Long projectId) {
        LambdaQueryWrapper<ApExportDO> exportDOLambdaQueryWrapper = new LambdaQueryWrapper<>();
        exportDOLambdaQueryWrapper.eq(ApExportDO::getProjectId, projectId);
        List<ApExportDO> apExportDOS = baseMapper.selectList(exportDOLambdaQueryWrapper);
        if (CollectionUtil.isEmpty(apExportDOS)) {
            return Collections.emptyList();
        }
        List<ApExportDTO> apExportDTOList = new ArrayList<>(apExportDOS.size());
        for (ApExportDO apExportDO : apExportDOS) {
            ApExportDTO apExportDTO = new ApExportDTO();
            BeanUtils.copyProperties(apExportDO, apExportDTO);
            if (apExportDO.getCreatedAt() != null) {
                apExportDTO.setCreatedAt(DateUtil.formatDateTime(apExportDO.getCreatedAt()));
            }
            apExportDTOList.add(apExportDTO);
        }
        return apExportDTOList;
    }

    @Override
    public Long save(ApExportDTO apExportDTO) throws BizException, IOException {
        ApProjectDTO apProjectDTO = apProjectService.getpProjectById(apExportDTO.getProjectId());
        if(ObjectUtil.isNull(apProjectDTO)){
            throw new BizException("not has this Project");
        }
        ApExportDO apExportDO = new ApExportDO();
        BeanUtils.copyProperties(apExportDTO, apExportDO);
        apExportDO.setCreatedAt(new Date());
        apExportDO.setStatus(0);
        apExportDO.setUserId(StpUtil.getLoginIdAsLong());
        apExportDTO.setFileId(apProjectDTO.getDataFileId());
        apExportDTO.setProjectName(apProjectDTO.getProjectName());
        baseMapper.insert(apExportDO);
        apExportDTO.setId(apExportDO.getId());
        datasetExportHandleUtil.exportFile(apExportDTO);
        return apExportDO.getId();
    }

    @Override
    public void delete(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public void update(ApExportDTO apExportDTO) {
        ApExportDO apExportDO = new ApExportDO();
        BeanUtils.copyProperties(apExportDTO, apExportDO);
        apExportDO.setUpdatedAt(new Date());
        baseMapper.updateById(apExportDO);
    }
}

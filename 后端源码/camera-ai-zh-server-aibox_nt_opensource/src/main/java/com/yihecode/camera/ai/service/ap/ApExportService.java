package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.dto.ApExportDTO;
import com.yihecode.camera.ai.entity.ap.ApExportDO;
import com.yihecode.camera.ai.exception.BizException;

import java.io.IOException;
import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/28 16:10
* @Describe
* @Version 1.0
*/
public interface ApExportService extends IService<ApExportDO> {
    /**
* Query Project Export List
* @param projectId
* @return
*/
    List<ApExportDTO> findExPortList(Long projectId);

    /**
* Save
* @param apExportDTO
* @return
*/
    Long save(ApExportDTO apExportDTO) throws BizException, IOException;

    /**
* Save
* @param id
* @return
*/
    void delete(Long id);

    void update(ApExportDTO apExportDTO);
}

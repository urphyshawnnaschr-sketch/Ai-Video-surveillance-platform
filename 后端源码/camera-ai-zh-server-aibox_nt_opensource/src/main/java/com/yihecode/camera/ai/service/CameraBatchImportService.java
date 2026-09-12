package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.CameraBatchImport;

import java.util.List;

/**
* Camera Batch Import Management
* @author Abyss
* @date 2024/3/5 13:29
*/
public interface CameraBatchImportService extends IService<CameraBatchImport> {

    /**
* Storage Data
* @param cameraBatchImport
*/
    void saveData(CameraBatchImport cameraBatchImport);

    /**
* Delete All
*/
    void deleteAll();

    /**
* Import
* @param cameraBatchImport
*/
    void saveImport(CameraBatchImport cameraBatchImport);

    /**
* By Status Query
* @param state
* @return
*/
    List<CameraBatchImport> listByState(Integer state);

    /**
* By Import Batch Label and Status Query
* @param tag
* @param state
* @return
*/
    List<CameraBatchImport> listByTag(String tag, Integer state);

    /**
* By Import Batch Label and Status Query Count
* @param tag
* @param state
* @return
*/
    int countByTag(String tag, Integer state);

    /**
* Query most after One Import Batch Label
* @return
*/
    String getLastImportTag();
}

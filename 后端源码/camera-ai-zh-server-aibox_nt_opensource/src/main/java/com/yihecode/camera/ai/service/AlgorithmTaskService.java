package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.AlgorithmTask;

/**
* Algorithm Download Task table
* @author Abyss
* @date 2023/12/13 15:52
*/
public interface AlgorithmTaskService extends IService<AlgorithmTask> {

    /**
* By File Name Query Download Task
* @param fileName
* @return
*/
    AlgorithmTask getByFileName(String fileName);

    /**
* By File Path Query Download Task
* @param path
* @return
*/
    int getByFilePath(String path);

    /**
* Delete All Download Task
*/
    void removeAll();

}

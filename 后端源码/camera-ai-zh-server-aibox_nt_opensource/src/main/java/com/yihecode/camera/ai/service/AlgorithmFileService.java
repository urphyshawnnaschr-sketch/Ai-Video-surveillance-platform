package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.AlgorithmFile;

import java.util.List;

/**
* Algorithm File table
* @author Abyss
* @date 2023/12/13 15:51
*/
public interface AlgorithmFileService extends IService<AlgorithmFile> {

    void removeByNameEn(String nameEn);

    List<AlgorithmFile> listByNameEn(String suanfa);
}

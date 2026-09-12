package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.AlgorithmBox;

/**
* Algorithm Uninstall or Card Delete Record table
*
* @author 465769438@qq.com
* @since 2025/4/19 15:51
*/
public interface AlgorithmBoxService extends IService<AlgorithmBox> {

    /**
* Add
*/
    void saveData(Algorithm algorithm, int type);

    /**
* By Algorithm ID, Process Type, Process Status Query
* @param algoId
* @param type
* @param state
* @return
*/
    AlgorithmBox getByAlgoAndTypeAndState(Long algoId, int type, int state);

    /**
* Get One Pending Task
* @return
*/
    AlgorithmBox getOneData();

    void deleteData(Long id, int i);
}

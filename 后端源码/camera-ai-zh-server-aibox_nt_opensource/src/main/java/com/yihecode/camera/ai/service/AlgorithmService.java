package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Algorithm;

import java.util.List;
import java.util.Map;

/**
* Algorithm Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface AlgorithmService extends IService<Algorithm> {

    /**
*
* @return
*/
    Map<Long, String> toMap();

    /**
* Update Count ID
* @param idList
*/
    void updateStaticsFlag(List<Long> idList);

    /**
* Query make Use Algorithm List
* @return
*/
    List<Algorithm> listUsed();

    /**
* Query NameEn
* @return
*/
    List<Algorithm> listNameEn(String nameEn);

    /**
* By Algorithm English Name Query
* @param nameEn
* @return
*/
    Algorithm getByNameEn(String nameEn);

    void clearLevel(Long levelId);

    List<Algorithm> getByTagAndNameLike(Long tagId, String name);

    /**
* Query id->obj Object Result
* @return
*/
    Map<Long, Algorithm> getDataMap();

    /**
* Update Algorithm Corresponding Hardware ID
* @param platform
*/
    void updatePlatform(String platform);

    /**
* Query All Algorithm, But is only Back a few Field
* @return
*/
    List<Algorithm> listLess();
}

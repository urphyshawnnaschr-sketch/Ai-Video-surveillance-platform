package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.Depart;

import java.util.List;

/**
* Organization Organization Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface ApDepartService extends IService<Depart> {

    /**
* Query All Data
* @return
*/
    List<Depart> listData();

    /**
* Delete
* @param ids
*/
    void deleteData(List<Long> ids);

    /**
* Move Node
* @param sourceId
* @param targetId
*/
    void updateParentId(Long sourceId, Long targetId);

    /**
* Query Organization Node and All child Node
* @param isSuper
* @param departId
* @return
*/
    String listChildIds(boolean isSuper, Long departId);

    /**
* Query Current Node and All child Node ids
* @param departId
* @return
*/
    List<Long> getCurrentAndChildIds(Long departId);

    /**
* Query Node Chain Name
* @param currentDepartId
* @param departList
* @return
*/
    String getLinkName(Long currentDepartId, List<Depart> departList);

    /**
* By Current Node, Get All and Back Current Node and All child Node
* @param departId
*/
    List<Depart> getCurrentAndChild(Long departId);
}


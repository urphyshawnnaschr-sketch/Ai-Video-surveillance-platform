package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.Menus;

import java.util.List;

/**
* Menu Service
* @author zhoumingxing
*/
public interface ApMenusService extends IService<Menus> {

    /**
* Add & Edit Menu
* @param menus
*/
    void saveMenus(Menus menus) throws Exception;

    /**
* Query Menu List
* @return
*/
    List<Menus> listData();

    /**
* Delete Current Menu and child Menu
* @param id
*/
    void removeCurrentAndSub(Long id);
}

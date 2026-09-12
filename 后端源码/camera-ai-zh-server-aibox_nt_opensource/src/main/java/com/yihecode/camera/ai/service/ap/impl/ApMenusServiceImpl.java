package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.Menus;
import com.yihecode.camera.ai.entity.ap.Role;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApMenusMapper;
import com.yihecode.camera.ai.mapper.ap.ApRoleMapper;
import com.yihecode.camera.ai.service.ap.ApMenusService;
import com.yihecode.camera.ai.service.ap.ApRoleService;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Menu real current class
* @author zhoumingxing
*/
@Service
public class ApMenusServiceImpl extends ServiceImpl<ApMenusMapper, Menus> implements ApMenusService {

    /**
* Add & Edit Menu
*
* @param menus
*/
    @Override
    public void saveMenus(Menus menus) throws Exception {
        //By Unique One Code Query
Menus menusOld = this.getByAuth(menus.getAuth());

//
if(menus.getId() == null) {
// Unique One Code Exist
if(menusOld!= null) {
throw new BizException("Error, Menu Unique One Code Exist");
}
// Add
menus.setCreatedAt(new Date());
menus.setUpdatedAt(new Date());
this.save(menus);
} else {
// Unique One Code Exist
if(menusOld!= null &&!menus.getId().equals(menusOld.getId())) {
throw new BizException("Error, Menu Unique One Code Exist");
}
//
menus.setUpdatedAt(new Date());
this.saveOrUpdate(menus);
}
}

/**
* Query Menu List
*
* @return
*/
@Override
public List<Menus> listData() {
LambdaQueryWrapper<Menus> queryWrapper = new LambdaQueryWrapper<>();
// queryWrapper.isNull(Menus::getDeletedAt);
queryWrapper.orderByAsc(Menus::getId);
//
List<Menus> menusList = this.list(queryWrapper);
return menusList == null? new ArrayList<>(): menusList;
}

/**
* By Menu Code Query
* @param auth
* @return
*/
private Menus getByAuth(String auth) {
LambdaQueryWrapper<Menus> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Menus::getAuth, auth);
return this.getOne(queryWrapper);
}

/**
* Delete Current Menu and child Menu
*
* @param id
*/
@Override
public void removeCurrentAndSub(Long id) {
// Delete Current Menu
removeById(id);

// Delete child Menu
LambdaQueryWrapper<Menus> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Menus::getParent, id);
this.remove(queryWrapper);
}
}
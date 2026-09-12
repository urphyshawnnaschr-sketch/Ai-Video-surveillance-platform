package com.yihecode.camera.ai.service.ap.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.mapper.ap.ApDepartMapper;
import com.yihecode.camera.ai.service.ap.ApDepartService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* Organization Organization Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class ApDepartServiceImpl extends ServiceImpl<ApDepartMapper, Depart> implements ApDepartService {

    /**
* Query All Data
*
* @return
*/
    @Override
    public List<Depart> listData() {
        LambdaQueryWrapper<Depart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Depart::getParentId);
        List<Depart> sysDeparts = this.list(queryWrapper);
        return sysDeparts == null ? new ArrayList<>() : sysDeparts;
    }

    /**
* Delete
*
* @param ids
*/
    @Override
    public void deleteData(List<Long> ids) {
        this.removeByIds(ids);
    }

    /**
* Move Node
*
* @param sourceId
* @param targetId
*/
    @Override
    public void updateParentId(Long sourceId, Long targetId) {
        LambdaUpdateWrapper<Depart> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(Depart::getParentId, targetId);
        updateWrapper.eq(Depart::getId, sourceId);
        this.update(updateWrapper);
    }

    /**
* Query Organization Node and All child Node
*
* @param isSuper
* @param departId
* @return
*/
    @Override
    public String listChildIds(boolean isSuper, Long departId) {
        if(departId == null) {
            return "";
        }
        //
List<Depart> departs = this.listData();
// super Level Management member
if(isSuper) {
List<Long> ids = departs.stream().map(Depart::getId).collect(Collectors.toList());
return ids.toString();
}
// Refer Fixed Department
List<Long> ids = new ArrayList<>();
// Pending Query Node
List<Long> queryIds = new ArrayList<>();
queryIds.add(departId);
// Query child Node
List<Long> subIds = new ArrayList<>();
while (true) {
if(queryIds.isEmpty()) {
break;
}
//
for(Long queryId: queryIds) {
for(Depart depart: departs) {
if(depart.getParentId().equals(queryId)) {
subIds.add(depart.getId());
}
}
ids.add(queryId);
}
//
queryIds.clear();
queryIds.addAll(subIds);
subIds.clear();
}
return ids.toString();
}

/**
* Query Current Node and All child Node ids
*
* @param departId
* @return
*/
@Override
public List<Long> getCurrentAndChildIds(Long departId) {
if(departId == null) {
return new ArrayList<>();
}
//
List<Depart> departs = this.listData();
// Refer Fixed Department
List<Long> ids = new ArrayList<>();
// Pending Query Node
List<Long> queryIds = new ArrayList<>();
queryIds.add(departId);
// Query child Node
List<Long> subIds = new ArrayList<>();
while (true) {
if(queryIds.isEmpty()) {
break;
}
//
for(Long queryId: queryIds) {
for(Depart depart: departs) {
if(depart.getParentId().equals(queryId)) {
subIds.add(depart.getId());
}
}
ids.add(queryId);
}
//
queryIds.clear();
queryIds.addAll(subIds);
subIds.clear();
}
return ids;
}

/**
* Query Node Chain Name
*
* @param currentDepartId
* @param departList
* @return
*/
@Override
public String getLinkName(Long currentDepartId, List<Depart> departList) {
if(currentDepartId == null) {
return"";
}
//
List<String> names = new ArrayList<>();
boolean found = false;
Long queryDepartId = currentDepartId;
while(true) {
found = false;
for(Depart depart: departList) {
if(depart.getId().equals(queryDepartId)) {
names.add(depart.getName());
found = true;
queryDepartId = depart.getParentId();
}
}
//
if(!found) {
break;
}
}
Collections.reverse(names);
return String.join("-", names);
}

/**
* By Current Node, Get All and Back Current Node and All child Node
*
* @param departId
*/
@Override
public List<Depart> getCurrentAndChild(Long departId) {
List<Depart> departList = this.list();

List<Depart> results = new ArrayList<>();
for(Depart depart: departList) {
if(depart.getId().equals(departId)) {
results.add(depart);
break;
}
}

List<Long> parentIds = new ArrayList<>();
parentIds.add(departId);
while (true) {
List<Long> subIds = new ArrayList<>();
for(Depart depart: departList) {
if(depart.getParentId()!= null && parentIds.contains(depart.getParentId())) {
results.add(depart);
subIds.add(depart.getId());
}
}

if(subIds.isEmpty()) {
break;
}

parentIds.clear();
parentIds.addAll(subIds);
}
return results;
}
}
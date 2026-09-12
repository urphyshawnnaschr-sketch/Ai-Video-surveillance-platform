package com.yihecode.camera.ai.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.mapper.AlgorithmMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Algorithm Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class AlgorithmServiceImpl extends ServiceImpl<AlgorithmMapper, Algorithm> implements AlgorithmService {

    public Map<Long, String> toMap() {
        List<Algorithm> algorithmList = list();
        if (algorithmList == null) {
            algorithmList = new ArrayList<>();
        }
        Map<Long, String> algorithmMap = new HashMap<>();
        for (Algorithm algorithm : algorithmList) {
            algorithmMap.put(algorithm.getId(), algorithm.getName());
        }
        return algorithmMap;
    }

    /**
* Update Count ID
*
* @param idList
*/
    @Override
    public void updateStaticsFlag(List<Long> idList) {
        LambdaUpdateWrapper<Algorithm> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.set(Algorithm::getStaticsFlag, 0);
        updateWrapper.eq(Algorithm::getStaticsFlag, 1);
        this.getBaseMapper().update(null, updateWrapper);

        //
for(Long id: idList) {
Algorithm algorithm = new Algorithm();
algorithm.setId(id);
algorithm.setStaticsFlag(1);
this.updateById(algorithm);
}
}

/**
* Query make Use Algorithm List
*
* @return
*/
@Override
public List<Algorithm> listUsed() {
return this.getBaseMapper().selectUsed();
}

/**
* Query NameEn
*
* @return
*/
@Override
public List<Algorithm> listNameEn(String nameEn) {
return this.getBaseMapper().selectNameEn(nameEn);
}

/**
* By Algorithm English Name Query
*
* @param nameEn
* @return
*/
@Override
public Algorithm getByNameEn(String nameEn) {
LambdaQueryWrapper<Algorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Algorithm::getNameEn, nameEn);
return this.getOne(queryWrapper, false);
}

@Override
public void clearLevel(Long levelId) {
LambdaUpdateWrapper<Algorithm> updateWrapper = new LambdaUpdateWrapper();
updateWrapper.set(Algorithm::getAlarmLevelId, null);
updateWrapper.eq(Algorithm::getAlarmLevelId, levelId);
this.getBaseMapper().update(null, updateWrapper);
}

@Override
public List<Algorithm> getByTagAndNameLike(Long tagId, String name) {
QueryWrapper<Algorithm> queryWrapper = new QueryWrapper<>();
if (tagId!= null && tagId!= 0) {
queryWrapper.eq("json_contains(tag_ids,'"+ tagId +"')", 1);
}
if (StringUtils.isNotBlank(name)) {
queryWrapper.like("name", name);
}
queryWrapper.orderByAsc("sort");
return this.list(queryWrapper);
}

/**
* Query id->obj Object Result
*
* @return
*/
@Override
public Map<Long, Algorithm> getDataMap() {
List<Algorithm> algorithms = this.list();
if(algorithms == null || algorithms.isEmpty()) {
return new HashMap<>();
}
return algorithms.stream().collect(Collectors.toMap(Algorithm::getId, Function.identity(), (s1, s2) -> s1));
}

/**
* Update Algorithm Corresponding Hardware ID
*
* @param platform
*/
@Override
public void updatePlatform(String platform) {
if(StrUtil.isBlank(platform)) {
return;
}

List<Algorithm> algorithms = this.list();
if(algorithms == null || algorithms.isEmpty()) {
return;
}

for(Algorithm algorithm: algorithms) {
if(!platform.equalsIgnoreCase(algorithm.getPlatform())) {
algorithm.setPlatform(platform);
this.updateById(algorithm);
}
}
}

/**
* Query All Algorithm, But is only Back a few Field
*
* @return
*/
@Override
public List<Algorithm> listLess() {
LambdaQueryWrapper<Algorithm> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Algorithm::getId, Algorithm::getName, Algorithm::getNameEn);
return this.list(queryWrapper);
}
}
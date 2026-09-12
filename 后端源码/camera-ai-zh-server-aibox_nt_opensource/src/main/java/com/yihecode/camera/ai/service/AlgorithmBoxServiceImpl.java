package com.yihecode.camera.ai.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.AlgorithmBox;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.mapper.AlgorithmBoxMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* Algorithm Uninstall or Card Delete Record table
*
* @author 465769438@qq.com
* @since 2025/4/19 15:51
*/
@Service
public class AlgorithmBoxServiceImpl extends ServiceImpl<AlgorithmBoxMapper, AlgorithmBox> implements AlgorithmBoxService {

    @Autowired
    private LocationService locationService;

    /**
* Add
* @param algorithm
* @param type
*/
    public void saveData(Algorithm algorithm, int type) {
        //Query All Box
List<Location> locationList = locationService.listDataByType("2");
if(locationList == null || locationList.isEmpty()) {
return;
}

// Each Box all in Line Process, Because for no Method Know that Box Config the Algorithm
for(Location location: locationList) {
if(StrUtil.isNotBlank(location.getBoxNo()) && location.getIsDef()!= null && location.getIsDef() == 0) {
AlgorithmBox algorithmBox = new AlgorithmBox();
algorithmBox.setAlgoId(algorithm.getId());
algorithmBox.setAlgoName(algorithm.getName());
algorithmBox.setAlgoCode(algorithm.getNameEn());
algorithmBox.setState(0);
algorithmBox.setCreatedAt(new Date());
algorithmBox.setBoxId(0L);
algorithmBox.setType(type);
algorithmBox.setBoxId(location.getId());
algorithmBox.setTimeMills(System.currentTimeMillis());
this.save(algorithmBox);
}
}
}

/**
* By Algorithm ID, Process Type, Process Status Query
*
* @param algoId
* @param type
* @param state
* @return
*/
@Override
public AlgorithmBox getByAlgoAndTypeAndState(Long algoId, int type, int state) {
LambdaQueryWrapper<AlgorithmBox> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(AlgorithmBox::getAlgoId, algoId);
queryWrapper.eq(AlgorithmBox::getType, 1);
queryWrapper.eq(AlgorithmBox::getState, 1);
return this.getOne(queryWrapper, false);
}

/**
* Get One Pending Task
*
* @return
*/
@Override
public AlgorithmBox getOneData() {
LambdaQueryWrapper<AlgorithmBox> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.or(wrapper -> wrapper.eq(AlgorithmBox::getState, 0).or().eq(AlgorithmBox::getState, 3));
queryWrapper.lt(AlgorithmBox::getTimeMills, System.currentTimeMillis());
queryWrapper.orderByAsc(AlgorithmBox::getTimeMills);
queryWrapper.last("limit 0, 1");
return this.getOne(queryWrapper, false);
}

@Override
public void deleteData(Long algoId, int type) {
LambdaQueryWrapper<AlgorithmBox> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(AlgorithmBox::getAlgoId, algoId);
queryWrapper.eq(AlgorithmBox::getState, 0);
queryWrapper.eq(AlgorithmBox::getType, type);
this.remove(queryWrapper);
}
}
package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.AlarmLevel;
import com.yihecode.camera.ai.mapper.AlarmLevelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* Alert Level Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class AlarmLevelServiceImpl extends ServiceImpl<AlarmLevelMapper, AlarmLevel> implements AlarmLevelService {

    /**
* Back map result structure
* @return
*/
    @Override
    public Map<Long, AlarmLevel> getDataMap() {
        List<AlarmLevel> alarmLevelList = this.list();
        if(alarmLevelList == null) {
            alarmLevelList = new ArrayList<>();
        }
        return alarmLevelList.stream().collect(Collectors.toMap(AlarmLevel::getId, obj -> obj));
    }
}
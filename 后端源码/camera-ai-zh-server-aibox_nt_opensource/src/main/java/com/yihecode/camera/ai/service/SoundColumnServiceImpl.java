package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.SoundColumn;
import com.yihecode.camera.ai.mapper.SoundColumnMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* IP Speaker Pole Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class SoundColumnServiceImpl extends ServiceImpl<SoundColumnMapper, SoundColumn> implements SoundColumnService {

    @Override
    public SoundColumn getBySn(String sn) {
        LambdaQueryWrapper<SoundColumn> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SoundColumn::getSn, sn);
        return this.getOne(queryWrapper, false);
    }

    /**
* Get Speaker Pole Data map result structure
*
* @return
*/
    @Override
    public Map<Long, SoundColumn> getDataMap() {
        List<SoundColumn> soundColumns = this.list();
        if(soundColumns == null || soundColumns.isEmpty()) {
            return new HashMap<>();
        }
        return soundColumns.stream().collect(Collectors.toMap(SoundColumn::getId, Function.identity(), (s1, s2) -> s1));
    }
}
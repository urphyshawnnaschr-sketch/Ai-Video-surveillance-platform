package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.AlgorithmFile;
import com.yihecode.camera.ai.entity.ap.Image;
import com.yihecode.camera.ai.mapper.AlgorithmFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* Algorithm File table
* @author Abyss
* @date 2023/12/13 15:53
*/
@Service
public class AlgorithmFileServiceImpl extends ServiceImpl<AlgorithmFileMapper, AlgorithmFile> implements AlgorithmFileService {

    @Autowired
    private AlgorithmFileMapper algorithmFileMapper;

    @Override
    public void removeByNameEn(String nameEn) {
        LambdaQueryWrapper<AlgorithmFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmFile::getNameEn, nameEn);
        this.remove(queryWrapper);
    }

    @Override
    public List<AlgorithmFile> listByNameEn(String suanfa) {
        LambdaQueryWrapper<AlgorithmFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlgorithmFile::getNameEn, suanfa);
        queryWrapper.orderByDesc(AlgorithmFile::getId);
        return this.list(queryWrapper);
    }
}
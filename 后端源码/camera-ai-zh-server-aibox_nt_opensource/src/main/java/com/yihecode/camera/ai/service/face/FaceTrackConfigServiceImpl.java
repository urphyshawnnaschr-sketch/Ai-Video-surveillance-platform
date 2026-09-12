package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceTrackConfig;
import com.yihecode.camera.ai.mapper.face.FaceTrackConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* Person member Tracking Base image Config
*
* @author zhou
* @since 2025.6.20
*/
@Service
public class FaceTrackConfigServiceImpl extends ServiceImpl<FaceTrackConfigMapper, FaceTrackConfig> implements FaceTrackConfigService {

    @Autowired
    private FaceTrackCameraService faceTrackCameraService;

    /**
* Add / Modify Data
*
* @param faceTrackConfig
*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveData(FaceTrackConfig faceTrackConfig) {
        //like Result Current Record Status for Valid, rule will Other Record All set for Invalid
if(faceTrackConfig.getState() == 1) {
updateUnavailable();
}
// Update Record
saveOrUpdate(faceTrackConfig);
}

/**
* Update All Data Status for Invalid
*/
private void updateUnavailable() {
LambdaUpdateWrapper<FaceTrackConfig> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(FaceTrackConfig::getState, 0);
updateWrapper.eq(FaceTrackConfig::getState, 1);
this.update(updateWrapper);
}

/**
* By ID Delete Data
*
* @param id
*/
@Transactional(rollbackFor = Exception.class)
@Override
public void deleteData(Long id) {
// Delete Record
this.removeById(id);

// Delete Camera Config
faceTrackCameraService.deleteByConfig(id);
}

/**
* Query Default Template
*
* @return
*/
@Override
public FaceTrackConfig getPrimary() {
LambdaQueryWrapper<FaceTrackConfig> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(FaceTrackConfig::getState, 1);
return this.getOne(queryWrapper, false);
}
}

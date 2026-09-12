package com.yihecode.camera.ai.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.CameraGroup;
import com.yihecode.camera.ai.entity.CameraGroupItem;
import com.yihecode.camera.ai.mapper.CameraGroupItemMapper;
import com.yihecode.camera.ai.web.vo.CameraGroupItemListVo;
import com.yihecode.camera.ai.web.vo.CameraGroupItemModifyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* Camera Group Detail
*
* @author zhou
* @since 2025.6.16
*/
@Slf4j
@Service
public class CameraGroupItemServiceImpl extends ServiceImpl<CameraGroupItemMapper, CameraGroupItem> implements CameraGroupItemService {

    /**
* By Group ID Query
*
* @param groupId
* @return
*/
    @Override
    public List<CameraGroupItem> listByGroup(Long groupId) {
        LambdaQueryWrapper<CameraGroupItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CameraGroupItem::getGroupId,groupId);
        return this.list(queryWrapper);
    }

    /**
* By Group ID Delete
*
* @param groupId
*/
    @Override
    public void deleteByGroup(Long groupId) {
        LambdaQueryWrapper<CameraGroupItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CameraGroupItem::getGroupId,groupId);
        this.remove(queryWrapper);
    }

    /**
* Page Query
*
* @param page
* @param limit
* @param cameraGroupIds
* @return
*/
    @Override
    public IPage<CameraGroupItem> listPage(Integer page, Integer limit, List<Long> cameraGroupIds, List<Long> cameraIds) {
        IPage<CameraGroupItem> pageObj = new Page<>(page, limit);
//LambdaQueryWrapper<CameraGroupItem> queryWrapper = new LambdaQueryWrapper<>();
// queryWrapper.in(CameraGroupItem::getGroupId, cameraGroupIds);
// if(ObjectUtil.isNotEmpty(cameraIds)) {
// queryWrapper.in(CameraGroupItem::getCameraId, cameraIds);
//}

QueryWrapper<CameraGroupItem> queryWrapper1 = new QueryWrapper<>();
queryWrapper1.select("*");
queryWrapper1.apply("camera_id in (select id from tbl_biz_camera where state = 0)");
queryWrapper1.in("group_id", cameraGroupIds);
if(ObjectUtil.isNotEmpty(cameraIds)) {
queryWrapper1.in("camera_id", cameraIds);
}

return this.page(pageObj, queryWrapper1);
}

/**
* Add / Modify Data
*
* @param modifyVo
*/
@Transactional(rollbackFor = Exception.class)
@Override
public void saveData(CameraGroupItemModifyVo modifyVo) {
this.deleteByGroup(modifyVo.getGroupId());

///
List<Long> cameraIds = modifyVo.getCameraIds();
if(ObjectUtil.isNotEmpty(cameraIds)) {
List<CameraGroupItem> cameraGroupItems = new ArrayList<>();
for(Long cameraId: cameraIds) {
CameraGroupItem groupItem = new CameraGroupItem();
groupItem.setGroupId(modifyVo.getGroupId());
groupItem.setCameraId(cameraId);
cameraGroupItems.add(groupItem);
}
this.saveBatch(cameraGroupItems);
}
}

/**
* By Group ID Count Count
*
* @param groupId
* @return
*/
@Override
public int countByGroup(Long groupId) {
LambdaQueryWrapper<CameraGroupItem> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraGroupItem::getGroupId, groupId);
return this.count(queryWrapper);
}

/**
* By Camera ID Query Group IDs
*
* @param cameraIds
* @return
*/
@Override
public List<Long> listIdByCamera(List<Long> cameraIds) {
LambdaQueryWrapper<CameraGroupItem> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(CameraGroupItem::getCameraId, cameraIds);
List<CameraGroupItem> cameraGroupItemList = this.list(queryWrapper);
return cameraGroupItemList.stream().map(CameraGroupItem::getGroupId).distinct().collect(Collectors.toList());
}

/**
* By Camera Query The belong Group
*
* @param cameraId
* @return
*/
@Override
public CameraGroupItem getByCamera(Long cameraId) {
LambdaQueryWrapper<CameraGroupItem> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(CameraGroupItem::getCameraId, cameraId);
return this.getOne(queryWrapper, false);
}
}

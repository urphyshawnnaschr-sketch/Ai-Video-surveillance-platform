package com.yihecode.camera.ai.service.ap.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.dto.ApGroupQueryDTO;
import com.yihecode.camera.ai.dto.ApUserGroupDTO;
import com.yihecode.camera.ai.entity.ap.UserGroup;
import com.yihecode.camera.ai.entity.ap.UserGroupRelationships;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApUserGroupMapper;
import com.yihecode.camera.ai.service.ap.ApUserGroupRelationshipsService;
import com.yihecode.camera.ai.service.ap.ApUserGroupService;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Annotation group real current class
*/
@Service
public class ApUserGroupServiceImpl extends ServiceImpl<ApUserGroupMapper, UserGroup> implements ApUserGroupService {
    @Autowired
    private ApUserGroupRelationshipsService apUserGroupRelationshipsService;

    @Override
    public List<ApUserGroupDTO> findGroupList(ApGroupQueryDTO groupQueryDTO) {
        List<UserGroup> groupDOList = baseMapper.findGroupList(groupQueryDTO);
        if (CollectionUtils.isEmpty(groupDOList)) {
            return new ArrayList();
        }
        List<ApUserGroupDTO> groupDTOList = new ArrayList(groupDOList.size());
        for (UserGroup userGroup : groupDOList) {
            ApUserGroupDTO apUserGroupDTO = new ApUserGroupDTO();
            BeanUtils.copyProperties(userGroup,apUserGroupDTO);
            groupDTOList.add(apUserGroupDTO);
        }
        return groupDTOList;
    }

    @Override
    public IPage<ApUserGroupDTO> findGroupPage(ApGroupQueryDTO groupQueryDTO) {
        Page<ApUserGroupDTO> page = new Page<>();
        page.setCurrent(groupQueryDTO.getPageNum());
        page.setSize(groupQueryDTO.getPageSize());
        IPage<ApUserGroupDTO> apGroupDOIPage = baseMapper.findGroupPage(page, groupQueryDTO);
        return apGroupDOIPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveGroup(ApUserGroupDTO groupDTO) {
        validateGroup(groupDTO);
        groupDTO.setCreatedAt(new Date());
        groupDTO.setUpdatedAt(new Date());

        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(groupDTO, userGroup);
        this.save(userGroup);
        return userGroup.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroupById(ApUserGroupDTO groupDTO) {
        validateGroup(groupDTO);
        groupDTO.setUpdatedAt(new Date());
        UserGroup userGroup = new UserGroup();
        BeanUtils.copyProperties(groupDTO, userGroup);
        this.updateById(userGroup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroupById(Long groupId) {
        //Delete group Relate
LambdaQueryWrapper<UserGroupRelationships> userRoleWrapper = new LambdaQueryWrapper<>();
userRoleWrapper.eq(UserGroupRelationships::getGroupId, groupId);
apUserGroupRelationshipsService.remove(userRoleWrapper);
// Delete group
removeById(groupId);
}

@Override
@Transactional(rollbackFor = Exception.class)
@SneakyThrows
public Integer addGroupUser(Long groupId, JSONArray userIds){
UserGroup userGroup = getById(groupId);
if(userGroup==null){
throw new BizException("Annotation group does not exist");
}
List<UserGroupRelationships> ugrList = new ArrayList<>();
for(int i=0;i<userIds.size();i++){
UserGroupRelationships ugr = new UserGroupRelationships();
ugr.setUserId(userIds.getLong(i));
ugr.setGroupId(groupId);
ugr.setCreatedAt(new Date());
ugr.setUpdatedAt(new Date());
ugrList.add(ugr);
}
if(CollectionUtils.isNotEmpty(ugrList)){
apUserGroupRelationshipsService.saveBatch(ugrList);
}
return ugrList.size();
}

@Override
@Transactional(rollbackFor = Exception.class)
@SneakyThrows
public Integer deleteGroupUser(Long groupId, JSONArray userIds) {
LambdaUpdateWrapper<UserGroupRelationships> ugrWrapper = new LambdaUpdateWrapper<>();
ugrWrapper.eq(UserGroupRelationships::getGroupId, groupId);
ugrWrapper.in(UserGroupRelationships::getUserId, userIds);
apUserGroupRelationshipsService.remove(ugrWrapper);
return userIds.size();
}

@SneakyThrows
private void validateGroup(ApUserGroupDTO apUserGroupDTO) {
if (ObjectUtil.isNull(apUserGroupDTO)) {
throw new BizException("Info cannot be empty");
}
if (StrUtil.isEmpty(apUserGroupDTO.getName())) {
throw new BizException("Annotation group Name cannot be empty");
}
LambdaUpdateWrapper<UserGroup> query = new LambdaUpdateWrapper<>();
query.eq(UserGroup::getName, apUserGroupDTO.getName())
.ne(ObjectUtil.isNotNull(apUserGroupDTO.getId()), UserGroup::getId, apUserGroupDTO.getId());
List<UserGroup> userGroups = list(query);
if (CollectionUtils.isNotEmpty(userGroups)) {
throw new BizException("Annotation group Name not can re reply");
}
}
}
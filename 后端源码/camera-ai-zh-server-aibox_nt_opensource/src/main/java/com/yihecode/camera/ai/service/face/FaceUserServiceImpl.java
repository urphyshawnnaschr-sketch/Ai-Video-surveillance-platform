package com.yihecode.camera.ai.service.face;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceImage;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.entity.face.FaceSyncObject;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.enums.FaceSyncEnum;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.face.FaceUserMapper;
import com.yihecode.camera.ai.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Face Person member - Face Recognition System
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Service
public class FaceUserServiceImpl extends ServiceImpl<FaceUserMapper, FaceUser> implements FaceUserService {

    @Autowired
    private FaceImageService faceImageService;

    @Autowired
    private FaceSyncObjectService faceSyncObjectService;

    @Autowired
    private FaceReportService faceReportService;

    @Value("${uploadDir}")
    private String uploadDir;

    /**
* By Group ID Query Person member Count
* @param groupId
* @return
*/
    @Override
    public Integer getCount(Long groupId) {
        LambdaQueryWrapper<FaceUser> queryWrapper = new LambdaQueryWrapper<>();
        if (null != groupId) {
            queryWrapper.eq(FaceUser::getGroupId, groupId);
        }
        return this.count(queryWrapper);
    }

    /**
* Page Query
*
* @param page
* @param limit
* @param groupId
* @return
*/
    @Override
    public IPage<FaceUser> listPage(Integer page, Integer limit, Long groupId, String name, String tel) {
        IPage<FaceUser> pageObj = new Page<>(page, limit);
        LambdaQueryWrapper<FaceUser> queryWrapper = new LambdaQueryWrapper<>();
        if(groupId != null) {
            queryWrapper.eq(FaceUser::getGroupId, groupId);
        }
        if(StrUtil.isNotBlank(name)) {
            queryWrapper.like(FaceUser::getName, name);
        }
        if(StrUtil.isNotBlank(tel)) {
            queryWrapper.like(FaceUser::getTel, tel);
        }
        return this.page(pageObj, queryWrapper);
    }

    /**
* Delete Data
*
* @param id
*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteData(Long id) {
        FaceUser faceUser = this.getById(id);
        List<FaceImage> faceImageList = faceImageService.listByUser(id);
        if(ObjectUtil.isNotEmpty(faceImageList)) {
            for(FaceImage faceImage : faceImageList) {
                //Face Image Sync
faceSyncObjectService.save(buildSyncObject(faceUser.getId(), faceUser.getGroupId(), faceImage.getId(), FaceSyncEnum.CLEAR_FACE.getCode()));
}
}

//
this.removeById(id);

//
faceImageService.removeByUser(id);

//
faceReportService.deleteByUser(id);
}

/**
* Add Data, Image from Alarm Record Copy
*
* @param faceUser
* @param filePath
* @param dest
*/
@Override
@Transactional(rollbackFor = Exception.class)
public void saveNewDataFromCopy(FaceUser faceUser, Long reportId, String filePath, String dest) {
// Add Data
this.save(faceUser);

// Copy Image
String destTo = FileUtils.pathTo(dest +"/"+ faceUser.getId() +"/");
if(!FileUtil.exist(destTo)) {
FileUtil.mkdir(destTo);
}

// Copy Image
FileUtil.copyFile(filePath, destTo, StandardCopyOption.REPLACE_EXISTING);

// Add Image Record
FaceImage faceImage = new FaceImage();
faceImage.setUserId(faceUser.getId());
faceImage.setImgUrl(FileUtil.getName(filePath));
faceImage.setIsAvatar(0);
faceImage.setDeleted(0);
faceImage.setCreatedAt(new Date());
faceImageService.save(faceImage);

// Update Alarm Corresponding Record
FaceReport modifyReport = new FaceReport();
modifyReport.setGroupId(faceUser.getGroupId());
modifyReport.setUserId(faceUser.getId());
modifyReport.setHasStranger(0);
modifyReport.setId(reportId);
faceReportService.updateById(modifyReport);

// Face Image Sync
faceSyncObjectService.save(buildSyncObject(faceUser.getId(), faceUser.getGroupId(), faceImage.getId(), FaceSyncEnum.ADD_FACE.getCode()));
}

/**
* By Name Blur Query
*
* @param userName
* @return
*/
@Override
public List<FaceUser> listLikeName(String userName) {
LambdaQueryWrapper<FaceUser> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.like(FaceUser::getName, userName);
return this.list(queryWrapper);
}

/**
* Save Face
*
* @param faceUser
* @param newFiles
* @param oldFiles
*/
@Transactional(rollbackFor = Exception.class)
@Override
public void saveData(FaceUser faceUser, List<String> newFiles, List<String> oldFiles) throws BizException {
boolean changeGroup = false;
if(faceUser.getId() == null) {
// Add Face
faceUser.setCreatedAt(new Date());
this.save(faceUser);
} else {
FaceUser faceUserDb = this.getById(faceUser.getId());
if(faceUserDb == null) {
throw new BizException("Face Data Not Exist");
}

// Modify Face
faceUser.setCreatedAt(null);
this.saveOrUpdate(faceUser);

// belong belong group Change, Need Delete User again Again Create
if(!ObjectUtil.equals(faceUserDb.getGroupId(), faceUser.getGroupId())) {
// Face Sync Object
faceSyncObjectService.save(buildSyncObject(faceUser.getId(), faceUserDb.getGroupId(), 0L, FaceSyncEnum.CLEAR_USER.getCode()));

// Cut change Group, Need will Face First Note Sell, after again Again Register
changeGroup = true;
}
}

// already exists Image
List<FaceImage> faceImageList = faceImageService.listByUser(faceUser.getId());

// Create USER_ID Storage Directory
String dest = FileUtils.pathTo(uploadDir +"/face_imgs/"+ faceUser.getId() +"/");
if(!FileUtil.exist(dest)) {
FileUtil.mkdir(dest);
}

// Add Image
if(ObjectUtil.isNotEmpty(newFiles)) {
for(String newFile: newFiles) {
// Move File to Current Face Directory
FileUtil.move(new File(newFile), new File(dest), true);

// Storage File Name Name
String filename = FileUtil.getName(newFile);

// Save Image
FaceImage faceImage = new FaceImage();
faceImage.setImgUrl(filename);
faceImage.setCreatedAt(new Date());
faceImage.setIsAvatar(0);
faceImage.setUserId(faceUser.getId());
faceImage.setDeleted(0);
faceImageService.save(faceImage);

// like Result not is change more group, rule Direct connect Add Sync Record, like Result is change more group, rule most after Again Insert in Sync
if(!changeGroup) {
// Face Image Sync
faceSyncObjectService.save(buildSyncObject(faceUser.getId(), faceUser.getGroupId(), faceImage.getId(), FaceSyncEnum.ADD_FACE.getCode()));
}
}
}

// Delete Old File
List<Long> deleteIds = new ArrayList<>();
for(FaceImage faceImage: faceImageList) {
if(!oldFiles.contains(faceImage.getImgUrl())) {
// Pending Delete ID
deleteIds.add(faceImage.getId());

// Delete Image
String filepath = getImageFile(faceUser.getId(), faceImage.getImgUrl());
if(filepath!= null) {
FileUtil.del(filepath);
}

// like Result not is change more group, rule Direct connect Add Sync Record, like Result is change more group, rule most after Again Insert in Sync
if(!changeGroup) {
// Face Image Sync
faceSyncObjectService.save(buildSyncObject(faceUser.getId(), faceUser.getGroupId(), faceImage.getId(), FaceSyncEnum.CLEAR_FACE.getCode()));
}
}
}

// Delete Image Data
if(ObjectUtil.isNotEmpty(deleteIds)) {
// Batch Delete Image
faceImageService.removeByIds(deleteIds);
}

// change more Group, Again Insert in all Quantity Sync
if(changeGroup) {
List<FaceImage> faceImages = faceImageService.listByUser(faceUser.getId());
// Face Image Sync
for(FaceImage faceImage: faceImages) {
faceSyncObjectService.save(buildSyncObject(faceUser.getId(), faceUser.getGroupId(), faceImage.getId(), FaceSyncEnum.ADD_FACE.getCode()));
}

// Modify Face Alarm User Corresponding Group
faceReportService.updateGroupIdByUserId(faceUser.getGroupId(), faceUser.getId());
}
}

/**
* supplement Charge Image to has Person member
*
* @param existFaceUser
* @param faceReport
*/
@Override
public void saveToExist(FaceUser existFaceUser, FaceReport faceReport) {
// Create USER_ID Storage Directory
String dest = FileUtils.pathTo(uploadDir +"/face_imgs/"+ existFaceUser.getId() +"/");
if(!FileUtil.exist(dest)) {
FileUtil.mkdir(dest);
}

// Move File to Current Face Directory
FileUtil.copyFile(new File(faceReport.getFilePath()), new File(dest));

// Storage File Name Name
String filename = FileUtil.getName(faceReport.getFilePath());

// Save Image
FaceImage faceImage = new FaceImage();
faceImage.setImgUrl(filename);
faceImage.setCreatedAt(new Date());
faceImage.setIsAvatar(0);
faceImage.setUserId(existFaceUser.getId());
faceImage.setDeleted(0);
faceImageService.save(faceImage);

// Update Alarm Corresponding Record
FaceReport modifyReport = new FaceReport();
modifyReport.setGroupId(existFaceUser.getGroupId());
modifyReport.setUserId(existFaceUser.getId());
modifyReport.setHasStranger(0);
modifyReport.setId(faceReport.getId());
faceReportService.updateById(modifyReport);

// Face Image Sync
faceSyncObjectService.save(buildSyncObject(existFaceUser.getId(), existFaceUser.getGroupId(), faceImage.getId(), FaceSyncEnum.ADD_FACE.getCode()));
}

/**
* By Group ID Delete
*
* @param groupId
*/
@Override
public void deleteByGroup(Long groupId) {
if(groupId == null) {
return;
}

// Query Condition
LambdaQueryWrapper<FaceUser> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(FaceUser::getGroupId, groupId);

// Delete User and Face
List<FaceUser> faceUserList = this.list(queryWrapper);
if(ObjectUtil.isNotEmpty(faceUserList)) {
for(FaceUser faceUser: faceUserList) {
// Delete Face Image
List<FaceImage> faceImageList = faceImageService.listByUser(faceUser.getId());
if(ObjectUtil.isNotEmpty(faceImageList)) {
for(FaceImage faceImage: faceImageList) {
String dest = FileUtils.pathTo(uploadDir +"/face_imgs/"+ faceUser.getId() +"/"+ faceImage.getImgUrl());
FileUtil.del(dest);
}
}

// Delete Face Data
faceImageService.removeByUser(faceUser.getId());
}
}

// Delete Person member
this.remove(queryWrapper);

// Delete group Sync
faceSyncObjectService.save(buildSyncObject(0L, groupId, 0L, FaceSyncEnum.CLEAR_GROUP.getCode()));
}

/**
* Get Image Path
* @param faceUserId
* @param imgName
* @return
*/
private String getImageFile(Long faceUserId, String imgName) {
if(faceUserId == null || StrUtil.isBlank(imgName)) {
return null;
}

String filepath = FileUtils.pathTo(uploadDir +"/face_imgs/"+ faceUserId +"/"+ imgName);
if(!FileUtil.exist(filepath)) {
return null;
}

if(!FileUtil.isFile(filepath)) {
return null;
}
return filepath;
}

/**
* structure build Face Sync Object
* @return
*/
private FaceSyncObject buildSyncObject(Long userId, Long groupId, Long imageId, String opType) {
FaceSyncObject faceSyncObject = new FaceSyncObject();
faceSyncObject.setOpType(opType);
faceSyncObject.setUserId(userId);
faceSyncObject.setGroupId(groupId);
faceSyncObject.setImageId(imageId);
faceSyncObject.setCreatedAt(new Date());
return faceSyncObject;
}
}
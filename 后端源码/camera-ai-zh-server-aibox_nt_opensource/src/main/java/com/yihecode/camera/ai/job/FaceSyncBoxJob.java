package com.yihecode.camera.ai.job;

import cn.hutool.core.util.ObjectUtil;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.face.FaceImage;
import com.yihecode.camera.ai.entity.face.FaceSyncBox;
import com.yihecode.camera.ai.entity.face.FaceSyncObject;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.enums.FaceSyncEnum;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.face.FaceImageService;
import com.yihecode.camera.ai.service.face.FaceSyncBoxService;
import com.yihecode.camera.ai.service.face.FaceSyncObjectService;
import com.yihecode.camera.ai.service.face.FaceUserService;
import com.zaxxer.hikari.util.FastList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* Face and Box Sync Process
*
* @author zhou
* @since 2025-07-10
*/
@Slf4j
@Component
public class FaceSyncBoxJob {

    @Autowired
    private LocationService locationService;

    @Autowired
    private FaceSyncObjectService faceSyncObjectService;

    @Autowired
    private FaceSyncBoxService faceSyncBoxService;

    @Autowired
    private FaceUserService faceUserService;

    @Autowired
    private FaceImageService faceImageService;

    public void runJob() {
        try {
            //Query Face Service Box
List<Location> locationList = locationService.listFaceBox();
if (locationList.isEmpty()) {
return;
}

// new Box ID List
List<Long> newBoxIdList = new ArrayList<>();

// First Process new Box
for (Location location: locationList) {
int count = faceSyncBoxService.countByBox(location.getId());
if (count > 0) {// Non new Box, already exists Sync Record
continue;
}

// all Quantity Data Sync
this.handleSyncAll(location);

// new Box ID
newBoxIdList.add(location.getId());
}

// Query increase Quantity Data
List<FaceSyncObject> faceSyncObjectList = faceSyncObjectService.list();
if (ObjectUtil.isEmpty(faceSyncObjectList)) {
return;
}

// Process Old Box
List<FaceSyncBox> faceSyncBoxList = new ArrayList<>();
for (Location location: locationList) {
// new Box not Process
if (newBoxIdList.contains(location.getId())) {
continue;
}

// Sync
for (FaceSyncObject faceSyncObject: faceSyncObjectList) {
FaceSyncBox faceSyncBox = new FaceSyncBox();
faceSyncBox.setBoxId(location.getId());
faceSyncBox.setSyncId(faceSyncObject.getId());
faceSyncBox.setSyncStatus(0);
faceSyncBox.setResultAt(null);
faceSyncBox.setResultMsg("");
faceSyncBox.setResultStatus(0);
faceSyncBox.setOpType(faceSyncObject.getOpType());
faceSyncBox.setUserId(faceSyncObject.getUserId());
faceSyncBox.setGroupId(faceSyncObject.getGroupId());
faceSyncBox.setImageId(faceSyncObject.getImageId());
faceSyncBox.setCreatedAt(faceSyncObject.getCreatedAt());
faceSyncBoxList.add(faceSyncBox);
}
}

// Batch Add Sync Record
if (!faceSyncBoxList.isEmpty()) {
faceSyncBoxService.saveBatch(faceSyncBoxList);
}

// By ID Delete Record
List<Long> faceSyncObjectIds = faceSyncObjectList.stream().map(FaceSyncObject::getId).collect(Collectors.toList());
faceSyncObjectService.removeByIds(faceSyncObjectIds);
} catch (Exception e) {
log.info("Face Data Sync Process Exception", e);
}
}

/**
* all Quantity Sync Face Record
* @param location
*/
private void handleSyncAll(Location location) {
// Query All Face Image
List<FaceImage> faceImageList = faceImageService.list();
if(faceImageList.isEmpty()) {
return;
}

// Query All Face User
List<FaceUser> faceUserList = faceUserService.list();
if(faceUserList.isEmpty()) {
return;
}
Map<Long, FaceUser> faceUserMap = faceUserList.stream().collect(Collectors.toMap(FaceUser::getId, Function.identity()));

// Process all Quantity Data
List<FaceSyncBox> faceSyncBoxList = new ArrayList<>();
for(FaceImage faceImage: faceImageList) {
FaceUser faceUser = faceUserMap.get(faceImage.getUserId());
if(faceUser == null || faceUser.getGroupId() == null) {
continue;
}

// Sync
FaceSyncBox faceSyncBox = new FaceSyncBox();
faceSyncBox.setBoxId(location.getId());
faceSyncBox.setSyncId(0L); // all Quantity Sync, Value for 0
faceSyncBox.setSyncStatus(0);
faceSyncBox.setResultAt(null);
faceSyncBox.setResultMsg("");
faceSyncBox.setResultStatus(0);
faceSyncBox.setOpType(FaceSyncEnum.ADD_FACE.getCode());
faceSyncBox.setUserId(faceUser.getId());
faceSyncBox.setGroupId(faceUser.getGroupId());
faceSyncBox.setImageId(faceImage.getId());
faceSyncBox.setCreatedAt(new Date());
faceSyncBoxList.add(faceSyncBox);
}

// Batch Add Sync Record
if(!faceSyncBoxList.isEmpty()) {
faceSyncBoxService.saveBatch(faceSyncBoxList);
}
}
}

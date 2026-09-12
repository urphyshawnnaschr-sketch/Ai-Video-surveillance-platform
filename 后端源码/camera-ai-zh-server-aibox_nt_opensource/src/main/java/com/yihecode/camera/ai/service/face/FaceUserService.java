package com.yihecode.camera.ai.service.face;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.exception.BizException;

import java.util.List;

/**
* Face Person member - Face Recognition System
*
* @Author 465769438@qq.com
*/
public interface FaceUserService extends IService<FaceUser> {

    /**
* By Group ID Query Person member Count
* @param groupId
* @return
*/
    Integer getCount(Long groupId);

    /**
* Page Query
* @param page
* @param limit
* @param groupId
* @return
*/
    IPage<FaceUser> listPage(Integer page, Integer limit, Long groupId, String name, String tel);

    /**
* Delete Data
* @param id
*/
    void deleteData(Long id);

    /**
* Add Data, Image from Alarm Record Copy
* @param faceUser
* @param filePath
* @param dest
*/
    void saveNewDataFromCopy(FaceUser faceUser, Long reportId, String filePath, String dest);

    /**
* By Name Blur Query
* @param userName
* @return
*/
    List<FaceUser> listLikeName(String userName);

    /**
* Save Face
* @param faceUser
* @param newFiles
* @param oldFiles
*/
    void saveData(FaceUser faceUser, List<String> newFiles, List<String> oldFiles) throws BizException;

    /**
* supplement Charge Image to has Person member
* @param existFaceUser
* @param faceReport
*/
    void saveToExist(FaceUser existFaceUser, FaceReport faceReport);

    /**
* By Group ID Delete
* @param groupId
*/
    void deleteByGroup(Long groupId);
}

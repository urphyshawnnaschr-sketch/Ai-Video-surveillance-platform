package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.CameraAlgorithm;

import java.util.List;

/**
* Camera and Algorithm Relate Management
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface CameraAlgorithmService extends IService<CameraAlgorithm> {

    /**
*
* @param algorithmId
* @return
*/
    List<CameraAlgorithm> listByAlgorithm(Long algorithmId);

    /**
*
* @param cameraId
* @return
*/
    List<CameraAlgorithm> listByCamera(Long cameraId);

    /**
*
* @param cameraId
*/
    void deleteByCamera(Long cameraId);

    /**
* By Camera ID and Algorithm ID Query
* @param cameraId
* @param algorithmId
* @return
*/
    CameraAlgorithm getByCameraAndAlgorithmId(Long cameraId, Long algorithmId);

    /**
* By Camera ID and Algorithm ID Delete
* @param cameraId
* @param algorithmId
*/
    void deleteByCameraAndAlgorithm(Long cameraId, Long algorithmId);

    void saveCameraAlgorithm(CameraAlgorithm cameraAlgorithm);

    /**
* By Algorithm id and Box id Query
* @author Abyss
* @date 2023/12/30 17:01
* @param algorithmId
* @param boxId
* @return java.util.List<com.yihecode.camera.ai.entity.CameraAlgorithm>
*/
    List<CameraAlgorithm> listByAlgorithmAndBoxId(Long algorithmId, Long boxId);

    /**
* By Camera id Reset All Algorithm Run Status
* @param cameraId
* @param runStatus
*/
    void updateAlgorithmRunStatus(Long cameraId, Integer runStatus);

    /**
* Query All Algorithm ids
* @return
*/
    List<Long> listAllAlgorithmIds();

    /**
* By Algorithm id and Run Status Query
* @param algorithmId
* @param runStatus
* @return
*/
    List<CameraAlgorithm> listRunningByAlgorithm(Long algorithmId, int runStatus);
}
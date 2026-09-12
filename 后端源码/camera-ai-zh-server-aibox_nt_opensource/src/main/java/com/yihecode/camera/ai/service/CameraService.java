package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.CameraAlgorithm;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.ReportPeriod;
import com.yihecode.camera.ai.web.vo.CameraListVo;
import com.yihecode.camera.ai.web.vo.CameraModifyVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* Camera Management
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface CameraService extends IService<Camera> {

    /**
*
* @param id
*/
    void delete(Long id);

    /**
*
* @param camera
* @param algorithms
* @param confidencevos
*/
    void saveCamera(Camera camera, String algorithms, String confidencevos, String markpointsvos, Integer updatePoint, String lineMarkPoints);

    /**
*
* @param cameraId
*/
    void updateActionById(Long cameraId, Integer action);

    /**
*
* @param id
* @param running
*/
    void updateRunning1(Long id, Integer running, String msg);

    /**
*
* @return
*/
    Map<Long, String> toMap();

    /**
*
* @return
*/
    List<Camera> listData();

    /**
* Page Query
* @param pageObj
* @return
*/
    IPage<Camera> listPage(IPage<Camera> pageObj, Camera queryCamera);

    /**
* By Region Node Delete Camera
* @param locationId
*/
    void removeByLocation(Long locationId);

    /**
* Query Work Dynamic Camera
* @return
*/
    List<Camera> listActives();

    /**
* By Camera Name Query
* @param cameraName
* @return
*/
    Camera getByName(String cameraName);

    /**
* By Run Status Count Total
* @param runState
* @return
*/
    Integer getCountByRunState(Integer runState);

    /**
* Update Camera Play Put Status
* @param playCameraIds
*/
    void updateVideoPlays(List<Long> playCameraIds);

    /**
* Page Query, By Video Play Put ID in Line Sort
* @param pageObj
* @return
*/
    IPage<Camera> listPageAndOrderVideoPlay(IPage<Camera> pageObj);

    /**
* Deleted Camera Delete Relate Algorithm
*/
    void removeDeleted();

    /**
* By Box id Query
* @param boxId
* @return
*/
    List<Camera> listByBoxId(Long boxId);

    List<Camera> listLikeName(String cameraName);

    /**
* By Stream Address Query Camera Whether Exist
* @param rtspUrl
* @return
*/
    Camera getByRtspUrl(String rtspUrl);

    /**
* Modify Camera Inference Status for Offline
* @param date
*/
    void updateOffline(Date date);

    /**
* By rtsp Address Update Code Info
* @param videoCodecName
* @param rtspUrl
*/
    void updateVideoCode(String videoCodecName, String rtspUrl);

    /**
* new Version Save Camera
* @param modifyVo
*/
    Long saveSubmit(CameraModifyVo modifyVo, Location location) throws Exception;

    /**
* By Box ids Query
* @param locationIds
* @return
*/
    List<Camera> listByLocationIds(List<Long> locationIds);

    /**
* By Box ids Query
* @param locationIds
* @return
*/
    List<Camera> listLessByLocationIds(List<Long> locationIds);

    /**
* Page Query, By Box ids Query
* @param page
* @param limit
* @param locationIds
* @return
*/
    IPage<Camera> listPageActivesV2(Integer page, Integer limit, List<Long> locationIds);

    /**
* By Box id Query Current Box Inference Path Number
* @param locationId
* @return
*/
    int getInferNum(Long locationId);

    /**
* Execute Rollback Operation
* @param cameraDb
* @param cameraAlgorithmsDb
*/
    void updateRollback(Camera cameraDb, List<CameraAlgorithm> cameraAlgorithmsDb, List<ReportPeriod> reportPeriodsDb);

    /**
* Query Camera
* @return
*/
    List<Camera> listData2();

    /**
* Query Camera and Box close System List
* @return
*/
    List<Camera> listCameraAndBox();

    /**
* By Media Node Count make Use Count
* @param mediaServerId
* @return
*/
    int countByMediaServer(Long mediaServerId);

    /**
* Query most small Load Media Node
* @return
*/
    Long getMinMediaServer();

    /**
* By Media Node Query Camera List
* @param mediaServerId
*/
    List<Camera> listByMediaServerId(Long mediaServerId);

    /**
* By Box ID Query List
* @param locationIds
* @param cameraName
* @return
*/
    List<Camera> listData5(List<Long> locationIds, String cameraName);

    /**
* By Camera Name Blur Query
* @param name
* @return
*/
    List<Long> listIdByName(String name);

    /**
* Page Query
* @param listVo
* @return
*/
    IPage<Camera> listPageV2(CameraListVo listVo);

    /**
* Query All
* @param listVo
* @return
*/
    List<Camera> listAllV2(CameraListVo listVo);

    /**
* Query not Contain Refer Fixed Camera ID Data
* @param cameraIds
* @param page
* @param limit
* @return
*/
    IPage<Camera> listPageNotContainCameraId(List<Long> cameraIds, String name, Integer page, Integer limit);

    /**
* Query not Contain Refer Fixed Camera ID Data
* @param cameraIds
* @return
*/
    List<Camera> getCountNotContainCameraId(List<Long> cameraIds, String name);

    /**
* By The belong Organization and The belong Group Query Camera List
* @param locationIds
* @param cameraIds
* @param running
* @return
*/
    List<Camera> listData7(List<Long> locationIds, List<Long> cameraIds, Integer running);

    /**
* By The belong Organization, The belong Group, Check select Camera Query Camera Page List
* @param page
* @param limit
* @param locationIds
* @param cameraIds
* @param running
* @return
*/
    IPage<Camera> listPage9(Integer page, Integer limit, List<Long> locationIds, List<Long> cameraIds, Integer running);

    /**
* By IP Address Query Camera
* @param ipAddress
* @return
*/
    Camera getByIp(String ipAddress);

    /**
* Query Status Normal and Enable, But is Execute Exception Camera
* @return
*/
    List<Camera> listException();

    /**
* Send Camera to Box Device
* @param camera
* @param location
* @return
*/
    String sendCameraToDevice(Camera camera, Location location);
}

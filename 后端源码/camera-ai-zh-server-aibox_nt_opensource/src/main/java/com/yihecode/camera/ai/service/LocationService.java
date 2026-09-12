package com.yihecode.camera.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.Camera;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.exception.BizException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* Camera Region Node < Tree result structure > table
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
public interface LocationService extends IService<Location> {

    /**
* Add Region
* @param location
*/
    void saveNode(Location location) throws BizException;

    /**
* Delete Region
* @param id
*/
    void deleteNodes(Long id);

    /**
* Query Data
* @return
*/
    List<Location> listData();

    List<Location> listDataByType(String locationType);

    Map<Long, String> getNameByIds(Collection<? extends Serializable> idList);

    /**
* Init Root Node Info
*/
    void initData();

    /**
* By Box No Query
* @param boxNo
* @return
*/
    Location getByBoxNo(String boxNo);

    /**
* Query Heartbeat Info
* @return
*/
    List<Location> listHeart();

    /**
* Get Default Root Node
* @param type
* @return
*/
    Location getDefRoot(Integer type);

    /**
* Page Query Data _ not Contain Face Box
* @param page
* @param limit
* @return
*/
    IPage<Location> listPage(Integer page, Integer limit, String name, List<Long> departIds, String platform);

    List<Location> listDataByTypeNoDef(String number);

    /**
* By Name Get Point Bit
* @author Abyss
* @date 2024/3/5 16:00
* @param name
* @return com.yihecode.camera.ai.entity.Location
*/
    Location getByName(String name);

    /**
* Save Batch Node
* @param locationNodes
* @return
*/
    Location saveNodes(String[] locationNodes);

    /**
* By ip Query
* @param ipAddr
* @return
*/
    Location getByIp(String ipAddr);

    /**
* By Department ids Query Box ids
* @param departIds
* @return
*/
    List<Long> getLocationIdsByDeparts(List<Long> departIds);

    /**
* By Type Query
* @param type
* @return
*/
    List<Location> listByType(String type);

    /**
* Query belong belong Department Is Empty Box ids
* @return
*/
    List<Long> getLocationIdsByNonDeparts();

    /**
* for Box Fixed make Query, not need Add Other Param
* @param sn
* @return
*/
    Location getBoxSnForRemote(String sn);

    /**
* By departId Get locationList
* @param departId
* @return
*/
    List<Location> listByDepartId(Long departId);

    /**
* Update Machine code
* @param boxId
* @param activeSn
*/
    void updateActiveSn(Long boxId, String activeSn);

    /**
* Update Activate Status
* @param boxId
* @param activeStatus
*/
    void updateActiveStatus(Long boxId, Integer activeStatus);

    /**
* By Index Query
* @param sn
* @return
*/
    Location getBySn(String sn);

    /**
* Query child Node
* @param parentId
* @return
*/
    List<Location> listSubLocations(Long parentId);

    /**
* By Department id Query Data
* @param departIds
* @param boxName
* @return
*/
    List<Location> listData5(List<Long> departIds, String boxName);

    /**
* Query Inference Box
* @return
*/
    List<Location> listInferBox();

    /**
* Query Face Box
* @return
*/
    List<Location> listFaceBox();

    /**
* Page Query Data _ Contain Face Box
* @param page
* @param limit
* @param name
* @param queryDepartIds
* @param platform
* @return
*/
    IPage<Location> listAllPage(Integer page, Integer limit, String name, List<Long> queryDepartIds, String platform);

}
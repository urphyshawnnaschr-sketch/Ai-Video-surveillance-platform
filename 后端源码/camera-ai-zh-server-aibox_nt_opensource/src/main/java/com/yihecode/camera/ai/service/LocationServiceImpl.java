package com.yihecode.camera.ai.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.enums.SocialHookType;
import com.yihecode.camera.ai.enums.SocialResultBusinessType;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.LocationMapper;
import com.yihecode.camera.ai.web.api.comm.AlarmFeishuPushV2Service;
import com.yihecode.camera.ai.web.api.comm.AlarmFeishuPushV3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* Camera Region Node < Tree result structure > table
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
@Slf4j
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location> implements LocationService {

    //
@Autowired
private CameraService cameraService;
@Autowired
private AccountService accountService;
@Autowired
private SocialHookService socialHookService;
@Autowired
private SocialResultService socialResultService;
@Autowired
private AlarmFeishuPushV2Service alarmFeishuPushService;
private static String PORT ="9315";
private Map<String, BigDecimal> map = new HashMap<>();
/**
* Add Region
*
* @param location
*/
@Override
public void saveNode(Location location) throws BizException {
// Determine Node Name
if(StrUtil.isBlank(location.getName())) {
throw new BizException("Region Name cannot be empty");
}
if("2".equals(location.getType())&&StrUtil.isBlank(location.getIpAddr())){
throw new BizException("Address cannot be empty");
}
//
if(location.getId() == null) {
//
if(location.getParentId() == null) {
throw new BizException("Please select up Level Region Node");
}
//
String parentNames ="";
String parentIds ="";
if(location.getParentId() > 0) {
// Query up Level Node
Location parentLocation = this.getById(location.getParentId());
if(parentLocation == null) {
throw new BizException("up Level Region Node does not exist");
}
parentNames = (StrUtil.isBlank(parentLocation.getParentNames())?"": (parentLocation.getParentNames() +"/")) + parentLocation.getName();
parentIds = parentLocation.getParentIds() +"/"+ parentLocation.getId();
}
location.setParentNames(parentNames);
location.setParentIds(parentIds);
//
this.save(location);
} else {
// Query History Data
Location locationDb = this.getById(location.getId());
if(locationDb == null) {
throw new BizException("Region Node does not exist");
}
// Modify BUG, Prevent type Override
location.setType(locationDb.getType());

// Update Node
this.updateById(location);

// Update down Level Node Name
if(!locationDb.getName().equals(location.getName())) {
updateParentNames(location.getId());
}
}
}

/**
* Update Region child Node up Level Region Name
* @param parentId
*/
private void updateParentNames(Long parentId) {
//
Location parentLocation = this.getById(parentId);
if(parentLocation == null) {
return;
}
//
List<Location> subLocationList = listByParent(parentId);
for(Location subLocation: subLocationList) {
//
subLocation.setParentNames((StrUtil.isBlank(parentLocation.getParentNames())?"": (parentLocation.getParentNames() +"/")) + parentLocation.getName());
//
this.saveOrUpdate(subLocation);
//
updateParentNames(subLocation.getId());
}
}

/**
* By up Level Node Query
* @param parentId
* @return
*/
private List<Location> listByParent(Long parentId) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getParentId, parentId);
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
return locations;
}

/**
* Delete Region
*
* @param id
*/
@Override
public void deleteNodes(Long id) {
deleteSubNodes(id);
}

/**
* Delete All child Node
* @param parentId
*/
private void deleteSubNodes(Long parentId) {
//
List<Location> subLocationList = listByParent(parentId);
for(Location subLocation: subLocationList) {
//
deleteSubNodes(subLocation.getId());
}
//
this.removeById(parentId);
//
cameraService.removeByLocation(parentId);
}

/**
* Query Data
*
* @return
*/
@Override
public List<Location> listData() {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
queryWrapper.orderByAsc(Location::getParentId);
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
return locations;
}

@Override
public List<Location> listDataByType(String locationType) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
if("2".equals(locationType)){
queryWrapper.eq(Location::getLocationType,locationType);
}else{
queryWrapper.ne(Location::getLocationType,"2").or().isNull(Location::getLocationType);
}
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
queryWrapper.orderByAsc(Location::getParentId);
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
return locations;
}

@Override
public Map<Long, String> getNameByIds(Collection<? extends Serializable> idList) {
if(idList == null || idList.isEmpty()) {
return new HashMap<>();
}
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(Location::getId, idList);
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
//
// List<Location> locations = this.listByIds(idList);
List<Location> locations = this.list(queryWrapper);
return locations.stream().collect(Collectors.toMap(
Location::getId,
Location::getName,
(key1, key2) -> key1
));
}

/**
* Init Root Node Info
*/
@Override
public void initData() {
// Camera Region Management Root Node
LambdaQueryWrapper<Location> queryWrapper1 = new LambdaQueryWrapper<>();
queryWrapper1.eq(Location::getType,"1");
int count1 = this.count(queryWrapper1);
if(count1 == 0) {
Location location = new Location();
location.setName("Region Management");
location.setSort(0);
location.setParentId(0l);
location.setType("1");
location.setOnline("0");
location.setLocationType("1");
location.setIsDef(1);
this.save(location);
}

// Box Management Root Node
LambdaQueryWrapper<Location> queryWrapper2 = new LambdaQueryWrapper<>();
queryWrapper2.eq(Location::getType,"2");
int count2 = this.count(queryWrapper2);
if(count2 == 0) {
Location location = new Location();
location.setName("Box Management");
location.setSort(0);
location.setParentId(0l);
location.setType("2");
location.setOnline("0");
location.setLocationType("2");
location.setIsDef(1);
this.save(location);
}
}

/**
* By Box No Query
*
* @param boxNo
* @return
*/
@Override
public Location getByBoxNo(String boxNo) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
queryWrapper.eq(Location::getBoxNo, boxNo);
return this.getOne(queryWrapper);
}

/**
* Query Heartbeat Info
*
* @return
*/
@Override
public List<Location> listHeart() {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getType,"2");
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
// queryWrapper.orderByAsc(Location::getName);
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
//
for(Location location: locations) {
location.setOnline("0");
//
if(location.getBoxHeartTime()!= null && (System.currentTimeMillis() - location.getBoxHeartTime()) < 8 * 60 * 1000) {
location.setOnline("1");
map.put(location.getName(), BigDecimal.ZERO);
log.info("Box Online:{}, Offline sub Number:{}", location.getName(), map.get(location.getName()));
} else if (!ObjectUtil.equals(location.getParentId(), 0L)){

BigDecimal count = map.get(location.getName());;
if(ObjectUtil.isNotNull(count)) {
map.put(location.getName(), count.add(BigDecimal.ONE));
log.info("Box Offline:{}, Offline sub Number:{}", location.getName(), map.get(location.getName()));
if(count.remainder(new BigDecimal(100)).compareTo(BigDecimal.ZERO) == 0){
this.sendFeishu(location);
}
}else{
map.put(location.getName(), BigDecimal.ONE);
log.info("Box Offline 1:{}, Offline sub Number:{}", location.getName(), map.get(location.getName()));
}
}
}
return locations;
}

private void sendFeishu(Location location) {
List<SocialHook> socialHookList = socialHookService.listData(SocialHookType.BOX.getType());
if(socialHookList.isEmpty()) {
// Record Send Log
SocialResult socialResult = new SocialResult();
socialResult.setSocialId(0L);
socialResult.setCreatedAt(new Date());
socialResult.setImgUrl("");
socialResult.setState(0);
socialResult.setErrorDetail("not has Send Group Config, Please Check Handle public Push");
socialResult.setSendText("");
socialResult.setCameraName(location.getName());
socialResult.setReportId(location.getId());
socialResult.setBusinessType(SocialResultBusinessType.BOX.getType());
socialResultService.save(socialResult);

return;
}
// String webUrl = configService.getByValTag("webUrl");
String content ="Box Offline, Please Check Network Connection Whether Normal.";
for (SocialHook socialHook: socialHookList) {
String locationName = location.getName();
String title ="Box Name:"+ locationName;
// String pageUrl = webUrl +"/report/ext/detail?id="+ report.getId() +"&key="+ key +"&t="+ timestamp;
alarmFeishuPushService.send(socialHook, null, null, title, content, null, null, locationName, null, location.getId(), null, SocialResultBusinessType.BOX.getType());
}
}
/**
* Get Default Root Node
*
* @param type
* @return
*/
@Override
public Location getDefRoot(Integer type) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getType, type);
queryWrapper.eq(Location::getIsDef, 1);
return this.getOne(queryWrapper, false);
}

/**
* Page Query Data
*
* @param page
* @param limit
* @return
*/
@Override
public IPage<Location> listPage(Integer page, Integer limit, String name, List<Long> departIds, String platform) {
IPage<Location> pageObj = new Page<>(page, limit);
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getType,"2");
queryWrapper.eq(Location::getIsDef, 0);
queryWrapper.in(Location::getUseType, Arrays.asList(0, 2));
//
// Account account = accountService.getById(StpUtil.getLoginIdAsLong());
// if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
// queryWrapper.eq(Location::getDepartId, account.getDepartId());
//}

if(departIds!= null &&!departIds.isEmpty()) {
queryWrapper.in(Location::getDepartId, departIds);
}

if(StrUtil.isNotBlank(name)) {
queryWrapper.like(Location::getName, name);
}

// Hardware Platform
if(StrUtil.isNotBlank(platform)) {
queryWrapper.eq(Location::getPlatform, platform);
}

queryWrapper.orderByAsc(Location::getName);
return this.page(pageObj, queryWrapper);
}

@Override
public List<Location> listDataByTypeNoDef(String locationType) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
if("2".equals(locationType)){
queryWrapper.eq(Location::getLocationType,locationType);
}else{
queryWrapper.ne(Location::getLocationType,"2").or().isNull(Location::getLocationType);
}
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
queryWrapper.eq(Location::getIsDef, 0);
queryWrapper.orderByAsc(Location::getParentId);
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
return locations;
}

@Override
public Location getByName(String name) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
queryWrapper.eq(Location::getName, name);
return this.getOne(queryWrapper);
}

/**
* Save Batch Node
*
* @param locationNodes
* @return
*/
@Override
public Location saveNodes(String[] locationNodes) {
if(locationNodes == null || locationNodes.length == 0) {
return null;
}
//
for(String locationNode: locationNodes) {
if(StrUtil.isBlank(locationNode)) {
return null;
}
}
// Server Version
Location root = this.getDefRoot(1);
if(root == null) {// Default Root Node does not exist
return null;
}
//
Long returnId = null;
Long parentId = root.getId();
String parentIds = root.getId() +"";
for(String locationNode: locationNodes) {
Location location = getByName(locationNode.trim());
boolean created = false; // Whether Create new Node
if(location == null) {// Node does not exist, new build
created = true;
} else {
if(!location.getParentId().equals(parentId)) {// not In up One Node, new build
created = true;
} else {
// up Level Node
parentId = location.getId();
parentIds = location.getParentIds() +","+ location.getId();
returnId = location.getId();
}
}
// Create new Node
if(created) {
location = new Location();
location.setName(locationNode);
location.setLocationType("1");
location.setType("1");
location.setParentId(parentId);
location.setIsDef(0);
location.setSort(1);
location.setParentIds(parentIds);
this.save(location);
// up Level Node
parentId = location.getId();
parentIds = parentIds +","+ location.getId();
returnId = location.getId();
}
}
// Back Object
Location returnLocation = new Location();
returnLocation.setId(returnId);
returnLocation.setParentIds(parentIds);
return returnLocation;
}

/**
* By ip Query
*
* @param ipAddr
* @return
*/
@Override
public Location getByIp(String ipAddr) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getIpAddr, ipAddr);
//
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
queryWrapper.eq(Location::getDepartId, account.getDepartId());
}
return this.getOne(queryWrapper, false);
}

/**
* By Department ids Query Box ids
*
* @param departIds
* @return
*/
@Override
public List<Long> getLocationIdsByDeparts(List<Long> departIds) {
if(departIds == null || departIds.isEmpty()) {
return new ArrayList<>();
}
//
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getLocationType,"2");
queryWrapper.in(Location::getDepartId, departIds);
List<Location> locations = this.list(queryWrapper);
if(locations == null || locations.isEmpty()) {
return null;
}
return locations.stream().map(Location::getId).collect(Collectors.toList());
}

/**
* By Type Query
*
* @param type
* @return
*/
@Override
public List<Location> listByType(String type) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getLocationType,"2");
queryWrapper.orderByAsc(Location::getParentId);
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
return locations;
}

/**
* Query belong belong Department Is Empty Box ids
*
* @return
*/
@Override
public List<Long> getLocationIdsByNonDeparts() {
//
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getLocationType,"2");
queryWrapper.isNull(Location::getDepartId);
List<Location> locations = this.list(queryWrapper);
if(locations == null || locations.isEmpty()) {
return new ArrayList<>();
}
return locations.stream().map(Location::getId).collect(Collectors.toList());
}

/**
* for Box Fixed make Query, not need Add Other Param
*
* @param sn
* @return
*/
@Override
public Location getBoxSnForRemote(String sn) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getBoxNo, sn);
return this.getOne(queryWrapper);
}

@Override
public List<Location> listByDepartId(Long departId) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getDepartId, departId);
queryWrapper.orderByAsc(Location::getParentId);
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
return locations;
}

/**
* Update Machine code
*
* @param boxId
* @param activeSn
*/
@Override
public void updateActiveSn(Long boxId, String activeSn) {
Location location = new Location();
location.setId(boxId);
location.setActiveSn(activeSn);
this.updateById(location);
}

/**
* Update Activate Status
*
* @param boxId
* @param activeStatus
*/
@Override
public void updateActiveStatus(Long boxId, Integer activeStatus) {
Location location = new Location();
location.setId(boxId);
location.setActiveStatus(activeStatus);
location.setActiveDate(new Date());
this.updateById(location);
}

/**
* By Index Query
*
* @param sn
* @return
*/
@Override
public Location getBySn(String sn) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getBoxNo, sn);
return this.getOne(queryWrapper, false);
}

/**
* Query child Node
*
* @param parentId
* @return
*/
@Override
public List<Location> listSubLocations(Long parentId) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getParentId, parentId);
return this.list(queryWrapper);
}

/**
* By Department id Query Data
*
* @param departIds
* @return
*/
@Override
public List<Location> listData5(List<Long> departIds, String boxName) {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Location::getId, Location::getName, Location::getDepartId);
if(departIds!= null &&!departIds.isEmpty()) {
queryWrapper.in(Location::getDepartId, departIds);
}
if(StrUtil.isNotBlank(boxName)) {
queryWrapper.like(Location::getName, boxName);
}
queryWrapper.eq(Location::getLocationType,"2");
queryWrapper.eq(Location::getIsDef, 0);
queryWrapper.in(Location::getUseType, Arrays.asList(0, 2));
queryWrapper.orderByAsc(Location::getParentId);
List<Location> locations = this.list(queryWrapper);
if(locations == null) {
return new ArrayList<>();
}
return locations;
}

/**
* Query Inference Box
*
* @return
*/
@Override
public List<Location> listInferBox() {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Location::getId, Location::getName, Location::getParentId, Location::getIpAddr, Location::getBoxNo, Location::getBoxHeartTime, Location::getDepartId, Location::getActiveStatus, Location::getPlatform, Location::getUseType, Location::getActiveDate, Location::getIsDef);
queryWrapper.eq(Location::getIsDef, 0);
queryWrapper.eq(Location::getLocationType,"2");
queryWrapper.in(Location::getUseType, Arrays.asList(0, 2));
return this.list(queryWrapper);
}

/**
* Query Face Box
*
* @return
*/
@Override
public List<Location> listFaceBox() {
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(Location::getId, Location::getName, Location::getParentId, Location::getIpAddr, Location::getBoxNo, Location::getBoxHeartTime, Location::getDepartId, Location::getActiveStatus, Location::getPlatform, Location::getUseType, Location::getActiveDate, Location::getIsDef, Location::getUseNum);
queryWrapper.eq(Location::getIsDef, 0);
queryWrapper.eq(Location::getLocationType,"2");
queryWrapper.in(Location::getUseType, Arrays.asList(1, 2));
return this.list(queryWrapper);
}

/**
* Page Query Data _ Contain Face Box
*
* @param page
* @param limit
* @param name
* @param departIds
* @param platform
* @return
*/
@Override
public IPage<Location> listAllPage(Integer page, Integer limit, String name, List<Long> departIds, String platform) {
IPage<Location> pageObj = new Page<>(page, limit);
LambdaQueryWrapper<Location> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Location::getType,"2");
queryWrapper.eq(Location::getIsDef, 0);
//
// Account account = accountService.getById(StpUtil.getLoginIdAsLong());
// if(account!= null && account.getIsSuper()!= null &&!account.getIsSuper().equals(1)) {
// queryWrapper.eq(Location::getDepartId, account.getDepartId());
//}

if(departIds!= null &&!departIds.isEmpty()) {
queryWrapper.in(Location::getDepartId, departIds);
}

if(StrUtil.isNotBlank(name)) {
queryWrapper.like(Location::getName, name);
}

// Hardware Platform
if(StrUtil.isNotBlank(platform)) {
queryWrapper.eq(Location::getPlatform, platform);
}

queryWrapper.orderByDesc(Location::getBoxHeartTime);
return this.page(pageObj, queryWrapper);
}
}
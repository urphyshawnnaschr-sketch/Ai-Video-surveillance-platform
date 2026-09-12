package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Location;
import com.yihecode.camera.ai.entity.ap.Depart;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.LocationService;
import com.yihecode.camera.ai.service.ap.*;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
* Role Region Management
* @author Abyss
*/
@Slf4j
@Api(tags = "Role Region Management")
@SaCheckLogin
@Controller
@RequestMapping({"/ap/role/location"})
public class ApRoleLocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private ApRoleLocationService apRoleLocationService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApDepartService apDepartService;

    /**
* Query Menu Tree result structure
* @return
*/
    @ApiOperation(value = "Query Role Region Data")
    @ApiImplicitParam(name = "roleId", value = "Role ID")
    @SaCheckPermission(value = {"systemManagement-role"}, mode = SaMode.OR)
    @PostMapping({"/listData"})
    @ResponseBody
    public JsonResult listData(Long roleId) {
        //Query Menu List
List<Depart> departList = new ArrayList<>();
Account account = accountService.getById(StpUtil.getLoginIdAsLong());
if (account.getIsSuper()!= null && account.getIsSuper().equals(1)) {
// super Level Management member
departList = apDepartService.listData();
} else if (account.getDepartId()!= null){
List<Long> currentAndChildIds = apDepartService.getCurrentAndChildIds(account.getDepartId());
if(currentAndChildIds!= null) {
for(Long departId: currentAndChildIds) {
Depart depart = apDepartService.getById(departId);
if(depart!= null) {
departList.add(depart);
}
}
}

// departList.add(apDepartService.getById(account.getDepartId()));
}
// Query Menu and Role Relate
List<Long> checkedLocationIds = apRoleLocationService.findLocationByRole(roleId);

for (Depart depart: departList) {
List<Location> locationList = locationService.listByDepartId(depart.getId());
for (Location location: locationList) {
if (checkedLocationIds.contains(location.getId())) {
location.setChecked(true);
}
}
depart.setLocationList(locationList);
}
//
// for(Menus menus: menusList) {
// if(checkedMenusIds.contains(menus.getId())) {
// menus.setChecked(true);
//}
//}
//
// // turn Complete Tree result structure
// List<Menus> trees = new ArrayList<>();
// for (Menus treeResult: menusList) {
// if (treeResult.getParent() == null || treeResult.getParent() == 0l) {
// treeResult.setParent(0l);
// trees.add(findChildren(treeResult, menusList));
//}
//}
return JsonResultUtils.success(departList);
}


}

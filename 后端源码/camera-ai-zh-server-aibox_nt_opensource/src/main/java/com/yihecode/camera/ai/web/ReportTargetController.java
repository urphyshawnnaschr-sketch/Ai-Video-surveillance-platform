package com.yihecode.camera.ai.web;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.R;
import com.yihecode.camera.ai.entity.ReportTarget;
import com.yihecode.camera.ai.service.ReportTargetService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.web.vo.ReportTargetItemModifyVo;
import com.yihecode.camera.ai.web.vo.ReportTargetModifyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
* Alarm Month Degree Target Value Config table
*
* @author zhou
* @since 2025.6.26
*/
@Slf4j
@RestController
@RequestMapping("report/target")
public class ReportTargetController {

    @Autowired
    private ReportTargetService reportTargetService;

    /**
* List Query, not Pagination
* @return
*/
    @SaCheckPermission("alarmData")
    @PostMapping("list")
    public JsonResult<List<ReportTarget>> list() {
        List<ReportTarget> reportTargetList = reportTargetService.list();
        return JsonResultUtils.success(reportTargetList);
    }

    public static void main(String[] args) {
//ReportTarget reportTarget = new ReportTarget();
// reportTarget.setId(1L);
// reportTarget.setMonth(1);
// reportTarget.setValue(10);
//
// List<ReportTarget> list = new ArrayList<>();
// list.add(reportTarget);
// System.out.println(JSON.toJSONString(JsonResultUtils.success(list)));


ReportTargetItemModifyVo m = new ReportTargetItemModifyVo();
m.setMonth(1L);
m.setValue(30);

ReportTargetItemModifyVo c = new ReportTargetItemModifyVo();
c.setMonth(2L);
c.setValue(45);

List<ReportTargetItemModifyVo> x = new ArrayList<>();
x.add(m);
x.add(c);

ReportTargetModifyVo modifyVo = new ReportTargetModifyVo();
modifyVo.setItems(x);

System.out.println(JSON.toJSONString(modifyVo));

}

/**
* Modify Data
* @param modifyVo
* @return
*/
@SaCheckPermission("alarmData")
@PostMapping("save")
public JsonResult<Boolean> save(@RequestBody ReportTargetModifyVo modifyVo) {
List<ReportTargetItemModifyVo> items = modifyVo.getItems();
if(ObjectUtil.isBasicType(items)) {
return JsonResultUtils.fail("Param lack Missing");
}

for(ReportTargetItemModifyVo item: items) {
ReportTarget reportTarget = new ReportTarget();
reportTarget.setId(item.getMonth()); // ID and month Field is One sample
reportTarget.setValue(item.getValue());
reportTargetService.updateById(reportTarget);
}
return JsonResultUtils.success(true);
}
}

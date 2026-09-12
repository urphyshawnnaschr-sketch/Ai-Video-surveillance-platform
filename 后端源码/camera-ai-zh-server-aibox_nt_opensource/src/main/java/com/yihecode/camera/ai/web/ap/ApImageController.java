package com.yihecode.camera.ai.web.ap;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.entity.HttpResponse;
import com.yihecode.camera.ai.entity.ap.ApProjectDO;
import com.yihecode.camera.ai.entity.ap.Image;
import com.yihecode.camera.ai.enums.ap.ProjectStatusEnum;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.service.ap.ApImageService;
import com.yihecode.camera.ai.service.ap.ApProjectService;
import com.yihecode.camera.ai.utils.*;
import com.yihecode.camera.ai.web.ap.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Stream Process Stream turn
//
// Pending Annotation: image.status = 0
// Belt Annotation Info (Algorithm Recognition / Upload: image.status = 1, commit_id
// Claim Annotation Task: image.status=4, user_id,assigned_at,expired_at
// Explain Put Task: image.expired_at = now()
// Submit Annotation: image.status = 5, user_id, commit_id
// Claim Quality Check Task: image.status=11, review_user_id, assigned_at,expired_at
// Quality Check Pass: image.status=15,review_user_id
// Quality Check Type return: image.status=16,review_user_id, user_id,assigned_at,expired_at
// Quality Check Modify: image.status=15,review_user_id, commit, annotation
// Annotation member Modify: image.status = 5, user_id,commit_id, commit.version

@ApiIgnore
@Api(tags ="Annotation Platform - Image Annotation")
@SaCheckLogin
@Controller
@RequestMapping({"/ap/image"})
public class ApImageController {


@Autowired
private ApImageService apImageService;

@Autowired
private ApProjectService apProjectService;

@Resource
private OkHttpUtil okHttpUtil;

@ApiOperation(value ="Image List API")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/list"})
@ResponseBody
public PageResult<List<Image>> list(ImageSearchRequestVo request) {
// Param Validation
if (request.getProjectId() == null || request.getProjectId() <= 0) {
return PageResultUtils.fail("Please select Project");
}
if (request.getPage() == null || request.getPage() <= 0) {
request.setPage(1);
}
if (request.getLimit() == null || request.getLimit() <= 0) {
request.setLimit(20);
}

// todo Menu Permission
// todo Project Permission Validation
// StpUtil.getLoginIdAsLong();

// Query
Long total = apImageService.count(request);
if (total == 0) {
return PageResultUtils.success(0L, new ArrayList<>());
}
List<Image> imageList = apImageService.list(request, false);

return PageResultUtils.success(total, imageList);
}

@ApiOperation(value ="Annotation History API")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/history"})
@ResponseBody
public PageResult<List<Image>> history(ImageHistoryRequestVo request) {
// Param Validation
if (request.getProjectId() == null || request.getProjectId() <= 0) {
return PageResultUtils.fail("Please select Project");
}
if (request.getPage() == null || request.getPage() <= 0) {
request.setPage(1);
}
if (request.getLimit() == null || request.getLimit() <= 0) {
request.setLimit(20);
}

// todo Menu Permission
// todo Project Permission Validation
Long uId = StpUtil.getLoginIdAsLong();

ImageSearchRequestVo requestVo = new ImageSearchRequestVo();
requestVo.setProjectId(request.getProjectId());
requestVo.setPage(request.getPage());
requestVo.setLimit(request.getLimit());
if (request.getType() == 1) {
requestVo.setReviewUserId(uId);
requestVo.setReviewStatus(8);
} else {
requestVo.setLabelUserId(uId);
requestVo.setLabelStatus(3);
}

// Query
Long total = apImageService.count(requestVo);
if (total == 0) {
return PageResultUtils.success(0L, new ArrayList<>());
}
List<Image> imageList = apImageService.list(requestVo, request.getType() == 1);

return PageResultUtils.success(total, imageList);
}

@ApiIgnore
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/stream"})
public void getImageAsByteArray(@RequestParam(defaultValue ="0") Long id, HttpServletResponse response) {
Image image = apImageService.getById(id);
if(image!= null && StrUtil.isNotBlank(image.getStoragePath())) {
try {
BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(image.getStoragePath())));
response.setContentType("image/jpeg");
IOUtils.copy(in, response.getOutputStream());
in.close();
} catch (Exception e) {
e.printStackTrace();
}
}
}


// Person work Annotation Lead Get & Lock Task Annotation Timeout Back Image Detail (Contain Smart can Annotation Result)+ Annotation
@ApiOperation(value ="Person work Annotation Lead Get & Lock Task")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@GetMapping({"/assign"})
@ResponseBody
public JsonResult<ImageVo> assign(AssignRequestVo request) {
// Param Validation
if (request.getProjectId() == null || request.getProjectId() <= 0) {
return JsonResultUtils.fail("Please select Project");
}
if (request.getCount() == null || request.getCount() <= 0) {
request.setCount(1);
}
Long userId = StpUtil.getLoginIdAsLong();
// todo Project Permission Validation
// Project Status Validate
ApProjectDO project = apProjectService.getById(request.getProjectId());
if (!ProjectStatusEnum.ANN.getCode().equals(project.getStatus())) {
return JsonResultUtils.fail("Project not In Annotation in");
}

// Query Type return and Lead Get Task and Lock
List<Image> images = apImageService.assignFromRejectAndAssignedImages(userId, request.getProjectId(), request.getCount(), project.getLabelTaskTimeOut());

if (images.size() < request.getCount()) {
// Query Task Pool and Lock
images.addAll(apImageService.assignFromOpenImages(userId, request.getProjectId(), request.getCount() - images.size(), project.getLabelTaskTimeOut()));
}

ImageSearchRequestVo requestVo = new ImageSearchRequestVo();
requestVo.setProjectId(request.getProjectId());
requestVo.setLabelStatus(3);
requestVo.setLabelUserId(userId);
Long total = apImageService.count(requestVo);

return JsonResultUtils.success(ImageVo.builder().images(images).total(total).build());
}

@ApiOperation(value ="Fine Fix Model Stick combine, Param Pending Fixed")
@SaCheckPermission(value = {"XXXXXX"}, mode = SaMode.OR)
@PostMapping({"/refine"})
@ResponseBody
public JsonResult<AnnotationVo> refine(@RequestBody RefineRequestVo request) {
if (request.getAnnotation() == null || request.getAnnotation().getAnnotationType()!= 2) {
return JsonResultUtils.fail("Item front Only Support Rectangle Box");
}

try {
// todo ip port
// 1.ip:port/refine
// Function can: Detection Fix Sync API
// input in: {'image_name':'test.jpg','in_xmin_cord':278,'in_xmax_cord':542,'in_ymin_cord':192,'in_ymax_cord':476}
// input out: Normal Back {"status":1,"mask":maskvalue,"refine_xmin":re_xmin_cord,"refine_xmax":re_xmax_cord,"refine_ymin":re_ymin_cord,"refine_ymax":re_ymax_cord}
// Run has wrong Hour Back {"status":0,"message":message}

List<Float> box = request.getAnnotation().getAnnotation().getBox(); // left up x, left up y, right down x, right down y
Map<String, Object> param = new HashMap<>();
param.put("image_name", request.getStoragePath());
param.put("in_xmin_cord", box.get(0));
param.put("in_xmax_cord", box.get(2));
param.put("in_ymin_cord", box.get(1));
param.put("in_ymax_cord", box.get(3));
HttpResponse httpResponse = okHttpUtil.postJson("http://"+"ip:port"+"/refine", null, JSON.toJSONString(param));

String responseStringData = httpResponse.getStringData();
if (responseStringData == null) {
return JsonResultUtils.fail("System Exception");
}
JSONObject resultJson = JSON.parseObject(responseStringData);
if (resultJson.getInteger("status")!= 1) {
return JsonResultUtils.fail("System Exception:"+ resultJson.getString("message"));
}

box.set(0, resultJson.getFloatValue("refine_xmin"));
box.set(2, resultJson.getFloatValue("refine_xmax"));
box.set(1, resultJson.getFloatValue("refine_ymin"));
box.set(3, resultJson.getFloatValue("refine_ymax"));

return JsonResultUtils.success(request.getAnnotation());
} catch (Exception e) {
return JsonResultUtils.fail("System Exception:"+ e.getMessage());
}
}

@ApiOperation(value ="Person work Annotation Submit Annotation Result, Support Batch")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@PostMapping({"/commit"})
@ResponseBody
public JsonResult<OpResponseVo> commit(@RequestBody CommitRequestVo request) {

// Param Validation
if (request.getProjectId() == null || request.getProjectId() <= 0) {
return JsonResultUtils.fail("Please select Project");
}
if (CollectionUtils.isEmpty(request.getCommits())) {
return JsonResultUtils.fail("Please Type Mark");
}

// Project Status Validate
ApProjectDO project = apProjectService.getById(request.getProjectId());
if (!ProjectStatusEnum.ANN.getCode().equals(project.getStatus())) {
return JsonResultUtils.fail("Project not In Annotation in");
}
// todo Project Permission Validation

Long userId = StpUtil.getLoginIdAsLong();

List<Long> successIds = new ArrayList<>();
Map<Long, String> errMsg = new HashMap<>();

for (CommitVo commitVo: request.getCommits()) {
try {
Image image = apImageService.checkCommit(request.getProjectId(), commitVo.getImageId(), userId);
apImageService.commit(commitVo, image, userId);
successIds.add(commitVo.getImageId());
} catch (BizException e) {
errMsg.put(commitVo.getImageId(), e.getMessage());
}
}

return JsonResultUtils.success(OpResponseVo.builder().successIds(successIds).errMsg(errMsg).build());
}
@ApiOperation(value ="Batch Explain Put")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@PostMapping({"/release"})
@ResponseBody
public JsonResult<OpResponseVo> release(@RequestBody ReleaseRequestVo request) throws BizException {
// Param Validation
if (request.getProjectId() == null || request.getProjectId() <= 0) {
return JsonResultUtils.fail("Please select Project");
}
if (CollectionUtils.isEmpty(request.getImageIds())) {
return JsonResultUtils.fail("Please select Image");
}
// Project Status Validate
ApProjectDO project = apProjectService.getById(request.getProjectId());
if (!ProjectStatusEnum.ANN.getCode().equals(project.getStatus())) {
return JsonResultUtils.fail("Project not In Annotation in");
}
// todo Management member Permission Validation

Long userId = StpUtil.getLoginIdAsLong();

List<Long> successIds = new ArrayList<>();
Map<Long, String> errMsg = new HashMap<>();
// Explain Put
for (Long imageId: request.getImageIds()) {
try {
apImageService.release(request.getProjectId(), imageId);
successIds.add(imageId);
} catch (BizException e) {
errMsg.put(imageId, e.getMessage());
}
}

return JsonResultUtils.success(OpResponseVo.builder().successIds(successIds).errMsg(errMsg).build());
}

@ApiOperation(value ="Quality Check Lead Get & Lock Task")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@PostMapping({"/assign_review"})
@ResponseBody
public JsonResult<ImageVo> assignReview(AssignRequestVo request) {
// Param Validation
if (request.getProjectId() == null || request.getProjectId() <= 0) {
return JsonResultUtils.fail("Please select Project");
}
if (request.getCount() == null || request.getCount() <= 0) {
request.setCount(1);
}
Long userId = StpUtil.getLoginIdAsLong();
// Project Status Validate
ApProjectDO project = apProjectService.getById(request.getProjectId());
if (!ProjectStatusEnum.ANN.getCode().equals(project.getStatus())) {
return JsonResultUtils.fail("Project not In Annotation in");
}
// todo Project Permission Validation


// Query Lead Get Task and Lock
List<Image> images = apImageService.assignForReviewFromAssignedImages(userId, request.getProjectId(), request.getCount(), project.getLabelTaskTimeOut());

if (images.size() < request.getCount()) {
// Query Task Pool and Lock
images.addAll(apImageService.assignForReviewFromOpenImages(userId, request.getProjectId(), request.getCount() - images.size(), project.getLabelTaskTimeOut()));
}

ImageSearchRequestVo requestVo = new ImageSearchRequestVo();
requestVo.setProjectId(request.getProjectId());
requestVo.setReviewUserId(userId);
requestVo.setReviewStatus(8);
Long total = apImageService.count(requestVo);


return JsonResultUtils.success(ImageVo.builder().images(images).total(total).build());
}
@ApiOperation(value ="Submit Quality Check Result, Support Batch")
@SaCheckPermission(value = {"apmgr-project"}, mode = SaMode.OR)
@PostMapping({"/review"})
@ResponseBody
public JsonResult<OpResponseVo> review(@RequestBody ReviewRequestVo request) {

// Param Validation
if (request.getProjectId() == null || request.getProjectId() <= 0) {
return JsonResultUtils.fail("Please select Project");
}
if (CollectionUtils.isEmpty(request.getReviewList())) {
return JsonResultUtils.fail("Please select Quality Check Result");
}

// Project Status Validate
ApProjectDO project = apProjectService.getById(request.getProjectId());
if (!ProjectStatusEnum.ANN.getCode().equals(project.getStatus())) {
return JsonResultUtils.fail("Project not In Annotation in");
}
// todo Project Permission Validation

Long userId = StpUtil.getLoginIdAsLong();

List<Long> successIds = new ArrayList<>();
Map<Long, String> errMsg = new HashMap<>();

for (ReviewVo reviewVo: request.getReviewList()) {
try {
Image image = apImageService.checkReview(request.getProjectId(), reviewVo.getImageId(), userId, reviewVo.getCommitId());
apImageService.review(reviewVo, image, userId, project.getLabelTaskTimeOut());
successIds.add(reviewVo.getImageId());
} catch (BizException e) {
errMsg.put(reviewVo.getImageId(), e.getMessage());
}
}

return JsonResultUtils.success(OpResponseVo.builder().successIds(successIds).errMsg(errMsg).build());
}

}

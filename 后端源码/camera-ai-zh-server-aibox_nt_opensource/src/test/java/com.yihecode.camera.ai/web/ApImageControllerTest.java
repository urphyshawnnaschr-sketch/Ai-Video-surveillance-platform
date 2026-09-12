//package com.yihecode.camera.ai.web;
//
//import cn.dev33.satoken.stp.StpUtil;
//import com.alibaba.fastjson.JSON;
//import com.yihecode.camera.ai.BaseTest;
//import com.yihecode.camera.ai.entity.ap.AnnotationData;
//import com.yihecode.camera.ai.entity.ap.Image;
//import com.yihecode.camera.ai.entity.ap.Point;
//import com.yihecode.camera.ai.enums.ap.ReviewAction;
//import com.yihecode.camera.ai.exception.BizException;
//import com.yihecode.camera.ai.utils.JsonResult;
//import com.yihecode.camera.ai.web.ap.ApImageController;
//import com.yihecode.camera.ai.web.ap.vo.*;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//import static org.mockito.Mockito.mockStatic;
//
//public class ApImageControllerTest extends BaseTest {
//
//
// @Autowired
// private ApImageController apImageController;
//
//
// @Test
// public void testList() {
// ImageSearchRequestVo requestVo = new ImageSearchRequestVo();
// requestVo.setProjectId(1688514633783250946L);
// print(apImageController.list(requestVo));
// requestVo.setLabel("Person");
// requestVo.setLabelUserId(88L);
// requestVo.setLabelStatus(3);
// print(apImageController.list(requestVo));
// requestVo.setReviewStatus(2);
// print(apImageController.list(requestVo));
//
//}
//
// @Test
// public void testAssign() {
// print(apImageController.assign(AssignRequestVo.builder().projectId(222L).count(10).build()));
//}
//
// @Test
// public void testCommit() {
//
// CommitRequestVo requestVo = new CommitRequestVo();
// requestVo.setProjectId(222L);
//
// List<AnnotationVo> annotations = new ArrayList<>();
// annotations.add(AnnotationVo.builder()
//.annotation(AnnotationData.builder().box(Arrays.asList(750.5172413793102f, 65.17241379310344f, 869.6896551724137f, 189.9310344827586f)).build())
//.annotationType(2).tagName("Rectangle Box")
//.build());
// annotations.add(AnnotationVo.builder()
//.annotation(AnnotationData.builder().point(Point.builder().x(111.1f).y(222.2f).build()).build())
//.annotationType(5).tagName("Point Point")
//.build());
//
// requestVo.setCommits(Arrays.asList(
// CommitVo.builder().imageId(224L).isValid((byte) 1).annotations(annotations).build(),
// CommitVo.builder().imageId(226L).isValid((byte) 1).annotations(annotations).build()));
//
// print(apImageController.commit(requestVo));
//
//}
//
//
// @Test
// public void testRelease() throws BizException {
// print(apImageController.release(ReleaseRequestVo.builder().projectId(222L).imageIds(Arrays.asList(222L, 224L)).build()));
//
// print(apImageController.assign(AssignRequestVo.builder().projectId(222L).count(3).build()));
//
// print(apImageController.release(ReleaseRequestVo.builder().projectId(222L).imageIds(Arrays.asList(222L, 224L)).build()));
//}
//
//
// @Test
// public void testReview() throws BizException {
//
// JsonResult<ImageVo> re = apImageController.assignReview(AssignRequestVo.builder().projectId(222L).count(3).build());
// print(re);
//
// ImageVo imageVo = re.getData();
//
// List<AnnotationVo> annotations = new ArrayList<>();
// annotations.add(AnnotationVo.builder()
//.annotation(AnnotationData.builder().box(Arrays.asList(750.5172413793102f, 65.17241379310344f, 869.6896551724137f, 189.9310344827586f)).build())
//.annotationType(2).tagName("Rectangle Box")
//.build());
// annotations.add(AnnotationVo.builder()
//.annotation(AnnotationData.builder().point(Point.builder().x(111.1f).y(222.2f).build()).build())
//.annotationType(5).tagName("Point Point")
//.build());
//
// List<ReviewVo> reviewVoList = new ArrayList<>();
// reviewVoList.add(ReviewVo.builder().reviewAction(ReviewAction.PASS.getType())
//.imageId(imageVo.getImages().get(0).getId())
//.commitId(imageVo.getImages().get(0).getCommitId())
//.comment("Pass")
//.build());
// reviewVoList.add(ReviewVo.builder().reviewAction(ReviewAction.REJECT.getType())
//.imageId(imageVo.getImages().get(1).getId())
//.commitId(imageVo.getImages().get(1).getCommitId())
//.comment("again modify modify Bar")
//.build());
// reviewVoList.add(ReviewVo.builder().reviewAction(ReviewAction.EDIT.getType())
//.imageId(imageVo.getImages().get(2).getId())
//.commitId(imageVo.getImages().get(2).getCommitId())
//.isValid((byte) 1)
//.annotations(annotations)
//.comment("just this sample Bar")
//.build());
//
// print(apImageController.review(ReviewRequestVo.builder().projectId(222L).reviewList(reviewVoList).build()));
//}
//
//
// @Test
// public void testConcurrency() throws BizException, InterruptedException {
// // Concurrency 10 Person Remove Lead Get
// long n = 100;
// while (n-- > 0) {
// long userId = 80L + n;
// new Thread(()->{
// try {
// stpUtil = mockStatic(StpUtil.class);
// stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(userId);
// JsonResult<ImageVo> re = apImageController.assign(AssignRequestVo.builder().projectId(222L).count(3).build());
// if (re.getData() == null) {
// print(userId +""+ re.getMsg());
//} else {
// print(userId +""+ re.getData().getTotal() + JSON.toJSONString(re.getData().getImages().stream().map(Image::getId).collect(Collectors.toList())));
//}
//// stpUtil.close();
//} catch (Exception e) {
// e.printStackTrace();
//}
//}).start();
//}
//
// TimeUnit.SECONDS.sleep(30);
//}
//
//}
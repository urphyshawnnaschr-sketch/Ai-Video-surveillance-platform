//package com.yihecode.camera.ai.web;
//
//import com.yihecode.camera.ai.BaseTest;
//import com.yihecode.camera.ai.dto.ApProjectDTO;
//import com.yihecode.camera.ai.exception.BizException;
//import com.yihecode.camera.ai.web.ap.ApProjectController;
//import com.yihecode.camera.ai.web.ap.ApTrainingController;
//import com.yihecode.camera.ai.web.ap.vo.TrainingBaseVo;
//import com.yihecode.camera.ai.web.ap.vo.TrainingCreateProjectRequestVo;
//import com.yihecode.camera.ai.web.ap.vo.TrainingListRequestVo;
//import com.yihecode.camera.ai.web.ap.vo.TrainingPreviewRequestVo;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class TrainingControllerTest extends BaseTest {
//
//
// @Autowired
// private ApTrainingController trainingController;
//
// @Autowired
// private ApProjectController projectController;
//
//
// @Test
// public void testList() {
//
// print(trainingController.list(TrainingListRequestVo.builder().build()));
//
//
//}
//
// @Test
// public void testPreview() throws BizException {
// TrainingPreviewRequestVo requestVo = new TrainingPreviewRequestVo();
// TrainingBaseVo base = new TrainingBaseVo();
// base.setAlgorithmId(1665656992924512257L);
// base.setCameraId(1641425040524783617L);
// base.setBegin(1);
// base.setEnd(400);
// base.setInterval(500);
// requestVo.setTrainingBaseVo(base);
// print(trainingController.preview(requestVo));
//
//
//}
//
// @Test
// public void testCreate() throws Exception {
// TrainingCreateProjectRequestVo requestVo = new TrainingCreateProjectRequestVo();
// TrainingBaseVo base = new TrainingBaseVo();
// base.setAlgorithmId(1667046234236723202L);
// base.setCameraId(1641425040524783617L);
// base.setBegin(1);
// base.setEnd(4);
// base.setInterval(500);
// requestVo.setTrainingBaseVo(base);
// ApProjectDTO projectDTO = new ApProjectDTO();
// projectDTO.setProjectName("aaaaaaaaà");
// projectDTO.setNeedReview(1);
// projectDTO.setReviewRatio(100);
// requestVo.setProjectDTO(projectDTO);
//
// print(trainingController.createProject(requestVo));
//
//}
//
// @Test
// public void testCreate2() throws Exception {
// print(projectController.getDeatilById(1735306529217941506L));
//}
//}
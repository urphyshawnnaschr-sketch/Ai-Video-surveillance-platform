//package com.yihecode.camera.ai.web;
//
//import com.yihecode.camera.ai.BaseTest;
//import com.yihecode.camera.ai.dto.ApProjectDTO;
//import com.yihecode.camera.ai.exception.BizException;
//import com.yihecode.camera.ai.utils.DatasetImportHandleUtil;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Author lichangliang
// * @Date 2023/7/24 14:08
// * @Describe
// * @Version 1.0
// */
//
//public class ImportImageTest extends BaseTest{
// @Autowired
// @Lazy
// private DatasetImportHandleUtil datasetImportHandleUtil;
// @Test
// public void test() throws BizException {
// String path ="E:\\data\\dataset\\1682283940146008074\\1\\upload\\images";
// List<Long> ids = new ArrayList<>();
// ids.add(1681122239544659970L);
// datasetImportHandleUtil.autoAnnOperationMethod(path,"IMG_20230720_143328.jpg",null,null,ids);
// //datasetImportHandleUtil.importFile(apProjectDTO,0L);
//}
//}

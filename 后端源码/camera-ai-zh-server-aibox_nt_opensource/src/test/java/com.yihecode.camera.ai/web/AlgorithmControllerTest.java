//package com.yihecode.camera.ai.web;
//
//import com.alibaba.fastjson.JSON;
//import com.yihecode.camera.ai.BaseTest;
//import com.yihecode.camera.ai.entity.Algorithm;
//import com.yihecode.camera.ai.service.AlgorithmService;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class AlgorithmControllerTest extends BaseTest {
//
//
// @Autowired
// private AlgorithmController algorithmController;
//
// @Autowired
// private AlgorithmService algorithmService;
// @Autowired
// private TagController tagController;
//
// @Test
// public void testTag() {
//
// System.out.print(JSON.toJSONString(algorithmController.listData(0L)));
//
// Algorithm al = algorithmService.getById(0L);
// System.out.print(JSON.toJSONString(al));
// al.setNameEn("aha");
// al.setName("You Name");
// Set<Long> tagIds = new HashSet<>();
// tagIds.add(787L);
// tagIds.add(788L);
// al.setTagIds(tagIds);
// algorithmController.save(al);
//
// System.out.print("\n-------787L---\n");
// System.out.print(JSON.toJSONString(algorithmController.listData(787L)));
// System.out.print("\n-------0L---\n");
// System.out.print(JSON.toJSONString(algorithmController.listData(0L)));
// System.out.print("\n-------2L---\n");
// System.out.print(JSON.toJSONString(algorithmController.listData(2L)));
//
//}
//}
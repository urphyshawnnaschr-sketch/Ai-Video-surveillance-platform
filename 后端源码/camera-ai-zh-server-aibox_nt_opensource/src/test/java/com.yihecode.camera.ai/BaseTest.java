//package com.yihecode.camera.ai;
//
//import cn.dev33.satoken.stp.StpUtil;
//import com.alibaba.fastjson.JSON;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.runner.RunWith;
//import org.mockito.MockedStatic;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import static org.mockito.Mockito.mockStatic;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("dev")
//@MapperScan({"com.yihecode.camera.ai.mapper"})
//@Ignore
//public class BaseTest {
//
// protected MockedStatic<StpUtil> stpUtil;
//
// // Each form Yuan Test Start front, First Execute the Method (high Version in @Before by Replace Complete @BeforeEach)
// @Before
// public void setUp() {
// this.stpUtil = mockStatic(StpUtil.class);
// stpUtil.when(StpUtil::getLoginIdAsLong).thenReturn(88L);
//}
//
// // Each form Yuan Test Execute Complete Complete after, Execute the Method (high Version in @After by Replace Complete @AfterEach)
// @After
// public void teardown() {
// this.stpUtil.close();
//}
//
// protected void print(Object object) {
// System.out.print(JSON.toJSONString(object));
// System.out.print("\n");
//}
//}

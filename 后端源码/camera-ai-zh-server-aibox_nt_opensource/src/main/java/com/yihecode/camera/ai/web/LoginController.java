package com.yihecode.camera.ai.web;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.yihecode.camera.ai.config.ProjectConfig;
import com.yihecode.camera.ai.dto.ApAccountModifyDTO;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.LoginLog;
import com.yihecode.camera.ai.notify.sms.SendSmsUtil;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.LoginLogService;
import com.yihecode.camera.ai.service.SmsPhoneService;
import com.yihecode.camera.ai.utils.IPUtils;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.utils.PassUtils;
import com.yihecode.camera.ai.web.vo.TokenInfoVo;
import com.yihecode.camera.ai.websocket.MessageWebsocket;
import com.yihecode.camera.ai.websocket.ReportWebsocket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
* Login Control
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Api(tags = "Login Control")
@Controller
public class LoginController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageWebsocket messageWebsocket;

    @Autowired
    private ReportWebsocket reportWebsocket;

    @Autowired
    private SmsPhoneService smsPhoneService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    private LoginLogService loginLogService;

    //IP Address Lock
private Map<String, Long> accountLockMap = new ConcurrentHashMap<>();

/**
* Login Control
* @return
*/
@ApiOperation("Login")
@ApiImplicitParams(value = {
@ApiImplicitParam(name ="account", value ="Login Account"),
@ApiImplicitParam(name ="password", value ="Login Password"),
@ApiImplicitParam(name ="t", value ="Timestamp")
})
@PostMapping({"/login"})
@ResponseBody
public JsonResult<?> doLogin(String account, String password, String t, HttpServletRequest request) {
String ipAddr = IPUtils.getIpAddr(request);
boolean loginDisabled = checkLoginDisabled(ipAddr);
if(loginDisabled) {
// remaining remainder Lock Timing between
String lockRemainTime = getLockRemainTime(ipAddr);
return JsonResultUtils.fail("Account Lock,"+ lockRemainTime +"after again Again Try Try");
}

Account account1 = accountService.getByAccount(account);
if(account1 == null) {
loginLogService.saveData(ipAddr, account, 0L,"Account or Password Error", 1);
return JsonResultUtils.fail("Account or Password Error");
}
//
String ePass = PassUtils.decrypt(account1.getPassword(), t);
if(!ePass.equals(password)) {
loginLogService.saveData(ipAddr, account, account1.getId(),"Account or Password Error", 1);
return JsonResultUtils.fail("Account or Password Error");
}
//
if(!(account1.getState()!= null && account1.getState() == 0)) {
loginLogService.saveData(ipAddr, account, account1.getId(),"Account Invalid", 1);
return JsonResultUtils.fail("Account Invalid");
}
//
StpUtil.login(account1.getId());
//
SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
//
TokenInfoVo tokenInfoVo = new TokenInfoVo();
tokenInfoVo.setTokenName(tokenInfo.getTokenName());
tokenInfoVo.setTokenValue(tokenInfo.getTokenValue());
tokenInfoVo.setSoundColumnEnable(projectConfig.isSoundColumnEnable());
tokenInfoVo.setRecordEnable(projectConfig.isRecordEnable());

loginLogService.saveData(ipAddr, account, account1.getId(),"Login success", 0);
return JsonResultUtils.success(tokenInfoVo);
}

/**
* Logout Control
* @return
*/
@ApiIgnore
@GetMapping(value ="/logout")
@ResponseBody
public JsonResult<Boolean> logout() {
// Break open socket
//messageWebsocket.removeUser(StpUtil.getTokenValue());
//reportWebsocket.removeUser(StpUtil.getLoginId().toString());

//
StpUtil.logout();
return JsonResultUtils.success(true);
}

/**
* Get Register Verify code
* @author Abyss
* @date 2023/11/27 21:31
*/
@PostMapping({"/getRegisterSms"})
@ResponseBody
public JsonResult getRegisterSms(String phone) {
if (StrUtil.isBlank(phone)) {
return JsonResultUtils.fail("Please enter Phone code");
}
if(!Validator.isMobile(phone)) {
return JsonResultUtils.fail("Phone code not Correct");
}
if (null!= accountService.getByAccount(phone)) {
return JsonResultUtils.fail("Phone Register");
}
// if (null!= smsPhoneService.getRegisterSmsCache(phone)) {
// return JsonResultUtils.fail("Please Do Not Frequent Request Register Verify code");
//}
Random rand = new Random();
int num = rand.nextInt(900000) + 100000;
// SMS Platform Send SMS
SendSmsUtil.singleSend(configService.getByValTag("smsAppKey"), phone, String.format("[Person work Smart can Video Management System] You Register Verify code is %s. like Non This Person Operation, Please Ignore This SMS", num));
smsPhoneService.saveRegisterSmsCache(phone, num);
return JsonResultUtils.success(num);
}

/**
* Register
* @author Abyss
* @date 2023/11/27 21:32
*/
@PostMapping({"/register"})
@ResponseBody
public JsonResult register(String phone, String name, String password, String code) {
if (StrUtil.isBlank(phone)) {
return JsonResultUtils.fail("Please enter Phone code");
}
if(!Validator.isMobile(phone)) {
return JsonResultUtils.fail("Phone code not Correct");
}
if (StrUtil.isBlank(password)) {
return JsonResultUtils.fail("Password cannot be empty");
}
if (StrUtil.isBlank(name)) {
return JsonResultUtils.fail("User Name cannot be empty");
}
if (!code.equals(smsPhoneService.getRegisterSmsCache(phone))) {
return JsonResultUtils.fail("Verify code not Correct");
}
ApAccountModifyDTO account = new ApAccountModifyDTO();
account.setCreatedAt(new Date());
account.setUpdatedAt(new Date());
account.setAccount(phone);
account.setName(name);
account.setPassword(password);
// TODO Temp Hour not Make Review, Default All Permission
account.setState(0);
List<Long> roleIds = new ArrayList<>();
roleIds.add(10000L);
roleIds.add(10001L);
roleIds.add(10003L);
roleIds.add(10004L);
account.setRoleIds(roleIds);
accountService.saveAccount(account);
return JsonResultUtils.success();
}

@PostMapping({"/getResetPswSms"})
@ResponseBody
public JsonResult getResetPswSms(String phone) {
if (StrUtil.isBlank(phone)) {
return JsonResultUtils.fail("Please enter Phone code");
}
if(!Validator.isMobile(phone)) {
return JsonResultUtils.fail("Phone code not Correct");
}
Account account = accountService.getByAccount(phone);
if (null == account) {
return JsonResultUtils.fail("Phone code Still not Register");
}
// if (null!= smsPhoneService.getResetPswSmsCache(phone)) {
// return JsonResultUtils.fail("Please Do Not Frequent Request Register Verify code");
//}
Random rand = new Random();
int num = rand.nextInt(900000) + 100000;
// SMS Platform Send SMS
SendSmsUtil.singleSend(configService.getByValTag("smsAppKey"), phone, String.format("[Person work Smart can Video Management System] You Password Reset Verify code is %s", num));
smsPhoneService.saveResetPswSmsCache(phone, num);
return JsonResultUtils.success(num);
}

@PostMapping({"/resetPsw"})
@ResponseBody
public JsonResult resetPsw(String phone, String password, String code) {
if (StrUtil.isBlank(phone)) {
return JsonResultUtils.fail("Please enter Phone code");
}
if(!Validator.isMobile(phone)) {
return JsonResultUtils.fail("Phone code not Correct");
}
if (StrUtil.isBlank(password)) {
return JsonResultUtils.fail("Password cannot be empty");
}
if (!code.equals(smsPhoneService.getResetPswSmsCache(phone))) {
return JsonResultUtils.fail("Verify code not Correct");
}
Account account = accountService.getByAccount(phone);
account.setPassword(PassUtils.encrypt(password));
account.setUpdatedAt(new Date());
accountService.updateById(account);
return JsonResultUtils.success();
}

/**
* Generate app Login 2D code
* @author Abyss
* @date 2024/2/21 00:29
*/
@ApiOperation("Generate app Login 2D code")
@GetMapping("/appLoginQRCode")
public void uploadStream(String phone, String password, HttpServletResponse response) {
try {
String ipHost = configService.getByValTag("ipAddr");
QrCodeUtil.generate(String.format("{\"phone\":\"%s\",\"password\":\"%s\",\"ipHost\":\"%s\"}", phone, password, ipHost), QrConfig.create(), ImgUtil.IMAGE_TYPE_PNG, response.getOutputStream());
response.setContentType("image/png");
response.setHeader("Cache-Control","no-cache, no-store, must-revalidate");
response.setHeader("Pragma","no-cache");
response.setDateHeader("Expires", 0);
} catch (Exception e) {
e.printStackTrace();
}
}

/**
* Detection Login Whether Disable
* @param ipAddr
* @return
*/
private boolean checkLoginDisabled(String ipAddr) {
// Determine Account Whether Lock
if(accountLockMap.containsKey(ipAddr)) {
long lockTime = accountLockMap.get(ipAddr);
if(lockTime - System.currentTimeMillis() > 0) {
return true;
}
}

long offsetEndTimeMills = System.currentTimeMillis();
long offsetStartTimeMills = offsetEndTimeMills - projectConfig.getLoginErrorTime() * 1000;

// like Result In this Segment Time inner, Current IP connect Continue Error, rule Disable Login
List<LoginLog> loginLogList = loginLogService.listData(ipAddr, offsetStartTimeMills, offsetEndTimeMills);
int size = Math.min(projectConfig.getLoginErrorCount(), loginLogList.size());

// Record Number not Enough Login Error Limit make Number
if(size < projectConfig.getLoginErrorCount()) {
return false;
}

// Determine Whether connect Continue Login Error
boolean disabled = true;
for(int i = 0; i < size; i++) {
if(loginLogList.get(i).getLoginState() == 0) {
disabled = false;
break;
}
}

// Lock, X min
if(disabled) {
accountLockMap.put(ipAddr, System.currentTimeMillis() + projectConfig.getLoginErrorLockTime() * 60 * 1000);
return true;
}
return false;
}

/**
* Get remaining remainder Lock Timing between
* @param ipAddr
* @return
*/
private String getLockRemainTime(String ipAddr) {
long currTime = System.currentTimeMillis();
long lockTime = accountLockMap.get(ipAddr);
long sec = (lockTime- currTime) / 1000;
if(sec < 60) {
return sec +"s";
}

long min = Double.valueOf(sec * 1.0 / 60).longValue();
if(min < 60) {
return min +"min";
}

long hour = Double.valueOf(sec * 1.0 / 60 / 60).longValue();
return hour +"hour";
}

}

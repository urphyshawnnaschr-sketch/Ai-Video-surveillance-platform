package com.yihecode.camera.ai.web.app.push;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ReportService;
import com.yihecode.camera.ai.utils.JsonResult;
import com.yihecode.camera.ai.utils.JsonResultUtils;
import com.yihecode.camera.ai.vo.ReportMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Api(tags = "app End _ Push Phase close")
@SaCheckLogin
@Controller
@RequestMapping({"/app/push"})
public class AppPushController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ReportService reportService;

    @ApiIgnore
    @PostMapping("/test")
    @ResponseBody
    public JsonResult test(String cid, String title, String content) {
        AppPushUtils.push(cid, title, content, null);
        return JsonResultUtils.success();
    }

    /**
* Save cid and app Version
* @author Abyss
* @date 2024/3/19 22:27
* @param cid
* @param appVersion
* @return com.yihecode.camera.ai.utils.JsonResult
*/
    @ApiOperation("Save customer account cid and app Version")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cid", value = "customer account id"),
            @ApiImplicitParam(name = "appVersion", value = "app Version No")
    })
    @PostMapping("/saveCidAndAppVersion")
    @ResponseBody
    public JsonResult saveCid(String cid, String appVersion) {
        Long userId = StpUtil.getLoginIdAsLong();
        Account account = accountService.getById(userId);
        List<String> cidList = new ArrayList<>();
        if (StringUtils.isNotBlank(account.getPushCid())) {
            cidList = Stream
                    .of(account.getPushCid().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
        if (!cidList.contains(cid)) {
            cidList.add(cid);
        }
        account.setPushCid(String.join(",", cidList));
        account.setAppVersion(appVersion);
        accountService.saveOrUpdate(account);
        return JsonResultUtils.success();
    }

    @ApiOperation("Delete customer account id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "Account"),
            @ApiImplicitParam(name = "cid", value = "customer account id")
    })
    @PostMapping("/removeCid")
    @ResponseBody
    public JsonResult removeCid(String account, String cid) {
//Long userId = StpUtil.getLoginIdAsLong();
Account account1 = accountService.getByAccount(account);
List<String> cidList = new ArrayList<>();
if (StringUtils.isNotBlank(account1.getPushCid())) {
cidList = Stream
.of(account1.getPushCid().split(","))
.map(String::trim)
.collect(Collectors.toList());
cidList.remove(cid);
}
account1.setPushCid(String.join(",", cidList));
accountService.saveOrUpdate(account1);
return JsonResultUtils.success();
}

/**
* Push app Update Info
* @author Abyss
* @date 2024/3/19 22:27
* @return com.yihecode.camera.ai.utils.JsonResult
*/
@ApiOperation("Push app Update Info")
@PostMapping("pushAppUpdate")
@ResponseBody
public JsonResult pushAppUpdate() {
List<Account> accountList = accountService.list();
List<String> cidList = new ArrayList<>();
JsonResult lastFileOrigin = getLastFileOrigin();
JSONObject object = (JSONObject) lastFileOrigin.getData();
if (null == object) {
return JsonResultUtils.fail("Get most new Version Failed");
}
String lastVersion = object.getStr("version");
for (Account account: accountList) {
if (StringUtils.isNotBlank(account.getAppVersion())) {
if (Double.valueOf(lastVersion) > Double.valueOf(account.getAppVersion())) {
List<String> ccL = new ArrayList<>();
if (StringUtils.isNotBlank(account.getPushCid())) {
ccL = Stream
.of(account.getPushCid().split(","))
.map(String::trim)
.collect(Collectors.toList());
}
cidList.addAll(ccL);
}
}
}
object.set("pushType","appUpdate");
if (!cidList.isEmpty()) {
AppPushUtils.push(cidList.toArray(new String[cidList.size()]),"Update Remind","[Update Remind] new Version app Go Live.[Point Click Jump turn Download page Surface]", object);
}
return JsonResultUtils.success();
}

/**
* Push Alert Info
* @author Abyss
* @date 2024/3/19 22:27
* @param reportMessage
*/
public void sendReportToAll(ReportMessage reportMessage) {
List<Account> accountList = accountService.list();
List<String> cidList = new ArrayList<>();
for (Account account: accountList) {
if (StringUtils.isNotBlank(account.getPushCid())) {
List<String> ccL = new ArrayList<>();
if (StringUtils.isNotBlank(account.getPushCid())) {
ccL = Stream
.of(account.getPushCid().split(","))
.map(String::trim)
.collect(Collectors.toList());
}
cidList.addAll(ccL);
}
}
Map<String, Object> object = new HashMap<>();
object.put("pushType","report");
object.put("reportId", reportMessage.getId());
if (!cidList.isEmpty()) {
AppPushUtils.push(cidList.toArray(new String[cidList.size()]),"Algorithm Alert",
String.format("[%s]:[%s]", reportMessage.getCameraName(),
reportMessage.getAlgorithmName()), object);
}
}

/**
* not Process Event big at 0, Push
* @author Abyss
* @date 2024/3/19 22:28
*/
public void sendUnAuditReportToAll() {
LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Report::getAuditState, 0);
int count = reportService.count(queryWrapper);
if (count > 0) {
List<Account> accountList = accountService.list();
List<String> cidList = new ArrayList<>();
for (Account account: accountList) {
if (StringUtils.isNotBlank(account.getPushCid())) {
List<String> ccL = new ArrayList<>();
if (StringUtils.isNotBlank(account.getPushCid())) {
ccL = Stream
.of(account.getPushCid().split(","))
.map(String::trim)
.collect(Collectors.toList());
}
cidList.addAll(ccL);
}
}
Map<String, Object> object = new HashMap<>();
object.put("pushType","auditReport");
if (!cidList.isEmpty()) {
AppPushUtils.push(cidList.toArray(new String[cidList.size()]),"not Process Alert Remind",
String.format("[not Process Alert Remind] You has %d Monitor Alert Still not Process.",
count), object);
}
}
}

private static final String accessToken ="dx4zotsYHNzRkQyyMP2v";

/**
* Get most new Version Info
* @author Abyss
* @date 2024/3/19 22:27
* @return com.yihecode.camera.ai.utils.JsonResult
*/
@ApiOperation("Get most new Version Info")
@GetMapping("/getLastFileOrigin")
@ResponseBody
public JsonResult getLastFileOrigin() {
try {
String apiUrl ="http://gitlab.yihecode.cn/api/v4/projects/114/repository/tree?path=app_notes&recursive=true";

URL url = new URL(apiUrl);
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("GET");
connection.setRequestProperty("Private-Token", accessToken); // Set Body copy Verify Token
connection.setRequestProperty("Accept","application/json"); // Set Request Header


int responseCode = connection.getResponseCode();
if (responseCode == HttpURLConnection.HTTP_OK) {
BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
String inputLine;
StringBuilder response = new StringBuilder();
while ((inputLine = in.readLine())!= null) {
response.append(inputLine);
}
in.close();
JSONArray jsonResponse = new JSONArray(response.toString());
String lastVersion ="0.0";
JSONObject result = null;
for (Object o: jsonResponse) {
JSONObject object = new JSONObject(o);
// Filter.gitkeep File
if (object.getStr("name").equals(".gitkeep")) {
continue;
}
String[] parts = object.getStr("name").split("_");
String version = parts[0];
String date = parts[1].replace(".csv","");
object.set("date", date);
object.set("version", version);
if (Double.valueOf(version) > Double.valueOf(lastVersion)) {
lastVersion = version;
result = object;
}
}
if (null!= result) {
String path = result.getStr("path");
StringBuilder builder = getFileOrigin(path);
if (null == builder) {
result.set("notes","");
} else {
result.set("notes", string2Matrix(builder.toString()));
result.set("downLoadPath", string2DownLoadPath(builder.toString()));
}
}
return JsonResultUtils.success(result);
} else {
System.out.println("GET request not worked");
return JsonResultUtils.fail("httpCode:"+ responseCode);
}
} catch (Exception e) {
e.printStackTrace();
return JsonResultUtils.fail(e.getMessage());
}
}

public StringBuilder getFileOrigin(String path) {
try {
String apiUrl ="http://gitlab.yihecode.cn/api/v4/projects/114/repository/files/"+ scapeStr2(path) +"/raw?ref=master";

URL url = new URL(apiUrl);
HttpURLConnection connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("GET");
connection.setRequestProperty("Private-Token", accessToken); // Set Body copy Verify Token
connection.setRequestProperty("Accept","application/json"); // Set Request Header

int responseCode = connection.getResponseCode();
if (responseCode == HttpURLConnection.HTTP_OK) {
BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
String inputLine;
StringBuilder response = new StringBuilder();
while ((inputLine = in.readLine())!= null) {
response.append(inputLine);
response.append("\n");
}
in.close();
return response;
} else {
System.out.println("GET request not worked");
return null;
}
} catch (Exception e) {
e.printStackTrace();
return null;
}
}
private String[][] string2Matrix(String input) {
// will input in String by change Line Symbol Split for Line
String[] lines = input.split("\n");
// Fixed Meaning Matrix Line Number and Column Number
int rowCount = lines.length;
int colCount = 3; // false set Each Line all has Three Element (Index, Function can Module, Update Description)
// Create 2D Number group
String[][] matrix = new String[rowCount][colCount];
// Iterate Each Line and Fill Charge Matrix
for (int i = 0; i < rowCount; i++) {
String[] elements = lines[i].split(",");
// Remove Divide Update Description in lead No, and Replace change Line Symbol for real International change Line Symbol
if (elements.length > 2) {
String description = elements[2].replace("\"","").replace("\\n","\n");
elements[2] = description;
}
// will Element Put in Matrix
for (int j = 0; j < Math.min(elements.length, colCount); j++) {
matrix[i][j] = elements[j];
}
}
return matrix;
}
private String string2DownLoadPath(String input) {
// will input in String by change Line Symbol Split for Line
String[] lines = input.split("\n");
// Fixed Meaning Matrix Line Number and Column Number
int rowCount = lines.length;
String[] elements = lines[0].split(",");
return elements[3];
}
private String scapeStr2 (String str) {
String newStr ="";
newStr = str.replace("/","%2F");
return newStr;
}
}

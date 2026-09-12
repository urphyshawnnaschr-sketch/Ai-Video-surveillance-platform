package com.yihecode.camera.ai.web.feishu;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yihecode.camera.ai.service.ReportService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/feishu/callback")
@SaIgnore
@Api(tags = "Feishu return Adjust")
public class FeishuCallbackController {

    @Autowired
    private ReportService reportService;



    /**
* Process Feishu return Adjust Event
*
* @param requestBody Feishu Send original start JSON Data
* @return Process Result
*/
    @PostMapping
    public Object handleCallback(@RequestBody Object requestBody) {
        try {
            log.info("handleCallback receive to Data:{}", requestBody);
            //will Request Body Convert for JSON Object
JSONObject json = convertToJson(requestBody);
log.info("handleCallback Convert Data:{}", json);
// Check Whether is Verify Request
if (json.containsKey("challenge")) {
log.info("receive to Feishu Verify Request: {}", requestBody);
return handleVerification(json);
}

// Check schema Version
if (!"2.0".equals(json.getString("schema"))) {
System.out.println("not Support schema Version:"+ json.getString("schema"));
return createErrorResponse(400,"Unsupported schema version");
}

// Parse Event Head
JSONObject header = json.getJSONObject("header");
String eventId = header.getString("event_id");
String eventType = header.getString("event_type");
String createTime = header.getString("create_time");

log.info("receive to Feishu Event: eventId={},eventType={},createTime={}", eventId, eventType, createTime);


// Process not same Type Event
if ("im.message.card_btn_click".equals(eventType)) {
return handleCardButtonClick(json.getJSONObject("event"));
}

// Other Event Type Direct connect Ignore
log.info("Ignore not Process Event Type: {}", eventType);
return createSuccessResponse();

} catch (Exception e) {
log.error("Process return Adjust Exception: {}", e.getMessage(),e);
return createErrorResponse(500,"Internal server error");
}
}

/**
* will Request Body Convert for JSONObject
*/
private JSONObject convertToJson(Object requestBody) {
// like Result via is JSONObject, Direct connect Back
if (requestBody instanceof JSONObject) {
return (JSONObject) requestBody;
}

// like Result Request Body is String, Direct connect Parse
if (requestBody instanceof String) {
return JSON.parseObject((String) requestBody);
}

// will Object order Column Change for JSON String again Parse
return JSON.parseObject(JSON.toJSONString(requestBody));
}
/**
* Process Verify Request
*/
private Map<String, Object> handleVerification(JSONObject request) {
// Raise Get challenge Value
String challenge = request.getString("challenge");
log.info("Process Feishu Verify Request, challenge:"+ challenge);

// According to need Request Back challenge Value
Map<String, Object> response = new HashMap<>();
response.put("challenge", challenge);
return response;
}

/**
* Process Card Button Point Click Event
*/
private Object handleCardButtonClick(JSONObject event) {
// Parse Event Data
String userId = event.getString("user_id");
JSONObject action = event.getJSONObject("action");
String tag = action.getString("tag");
String value = action.getString("value");

log.info("User Point Click Card Button:userId={}, tag={},value={}", userId, tag, value);


// Parse Button Value (Format: action_reportId)
String[] parts = value.split("_", 2);
if (parts.length!= 2) {
log.error("Invalid Button Value Format: {}", value);
return createErrorResponse(400,"Invalid button value format");
}

String actionType = parts[0];
String reportId = parts[1];

// Process Button Operation
try {
switch (actionType) {
case"process":
// reportService.dealReport(reportId, userId,actionType);
return createSuccessResponse("Report Report Mark for Processing");
case"receive":
// reportService.dealReport(reportId, userId,actionType);
return createSuccessResponse("Report Report Mark for Receive");
case"close":
// reportService.dealReport(reportId, userId,actionType);
return createSuccessResponse("Report Report Closed");
default:
log.error("Unknown Operation Type: {}", actionType);
return createErrorResponse(400,"Unknown action type");
}
} catch (Exception e) {
log.error("Process Report Report Operation Exception: {}", e.getMessage(),e);
return createErrorResponse(500,"Failed to process action");
}
}

/**
* Create Success Response
*/
private JSONObject createSuccessResponse() {
return createSuccessResponse(null);
}

/**
* Create Belt Message Success Response
*/
private JSONObject createSuccessResponse(String message) {
JSONObject response = new JSONObject();
response.put("code", 0);
response.put("message","success");

if (message!= null) {
// can select: Back Update Card Command
JSONObject content = new JSONObject();
JSONObject config = new JSONObject();
config.put("update_multi", true);
content.put("config", config);

JSONObject element = new JSONObject();
element.put("tag","markdown");
element.put("content", message);

content.put("elements", new JSONObject[]{element});
response.put("content", content);
}

return response;
}

/**
* Create Error Response
*/
private JSONObject createErrorResponse(int code, String message) {
JSONObject response = new JSONObject();
response.put("code", code);
response.put("message", message);
return response;
}


}

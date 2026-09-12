package com.yihecode.camera.ai.websocket;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.yihecode.camera.ai.entity.Account;
import com.yihecode.camera.ai.vo.ReportMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
* Alert Push socket
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Component
@CrossOrigin
@ServerEndpoint(value = "/report/{satoken}")
public class ReportWebsocket {

    //
private static final AtomicInteger onlineCount = new AtomicInteger(0);

//
private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

//
private final Lock lock = new ReentrantLock();

/**
* Connection build Stand Success Call Method
*/
@OnOpen
public void onOpen(Session session, @PathParam(value ="satoken") String satoken) throws IOException {
// By token Get Corresponding userId
Object loginId = StpUtil.getLoginIdByToken(satoken);
if(loginId == null) {
session.close();
throw new SaTokenException("Connection failed, Invalid Token:"+ satoken);
}

//
sessionMap.put(loginId +"_"+ session.getId(), session);

//
onlineCount.incrementAndGet();
//log.info("has new Connection Add in:{}, Current Online Headcount for:{}", session.getId(), onlineCount.get());
}

/**
* Connection Close Call Method
*/
@OnClose
public void onClose(Session session, @PathParam(value ="satoken") String satoken) {
//
//sessionMap.remove(satoken);

//
onlineCount.decrementAndGet(); // Online Number decrease 1

//
try {
session.close();
} catch (Exception e) {
// e.printStackTrace();
}
}

/**
* receive to Client Message after Call Method
*
* @param message
* Client Send over Incoming Message
*/
@OnMessage
public void onMessage(String message, Session session) {
//
if(session == null) {
return;
}

//
if(!session.isOpen()) {
this.removeSession(session);
return;
}

//
final RemoteEndpoint.Basic basic = session.getBasicRemote();
if(basic == null) {
this.removeSession(session);
return;
}

try {
ReportMessage reportMessageVo = new ReportMessage();
reportMessageVo.setType("HEART");
reportMessageVo.setCameraId("0");
reportMessageVo.setParams("");
basic.sendText(JSONUtil.toJsonStr(reportMessageVo));
} catch (Exception e) {
log.error("OnMessage Error: {}", e.getMessage());
}
}

/**
*
* @param session
* @param error
*/
@OnError
public void onError(Session session, Throwable error) {
//
try {
this.removeSession(session);

session.close();
} catch (Exception e) {
// e.printStackTrace();
}
}

//
public void sendToAll(String message) {
sessionMap.forEach((sessionId, session) -> sendToUser(session, message));
}

/**
* send message to all
* @param session
* @param message
*/
private synchronized void sendToUser(Session session, String message) {
//
if(session == null) {
return;
}

//
if(!session.isOpen()) {
this.removeSession(session);
return;
}

//
try {
final RemoteEndpoint.Basic basic = session.getBasicRemote();
if(basic == null) {
this.removeSession(session);
return;
}

basic.sendText(message);
} catch (IOException e) {
log.error("Websocket sendMessage IOException {}", e.getMessage());
}
}

/**
* Delete User
* @param accountId
*/
public void removeUser(String accountId) {
//
Session session = sessionMap.get(accountId);
if(session!= null) {
try {
session.close();
} catch (Exception e) {
//
}
}

//
sessionMap.remove(accountId);

//
onlineCount.decrementAndGet(); // Online Number decrease 1
}

public void sendToAccounts(List<Account> accounts, String message) {
lock.lock();
try {
for (Account account: accounts) {
List<Session> sessions = getSessionList(account.getId());
if(sessions.isEmpty()) {
continue;
}

for(Session session: sessions) {
sendToUser(session, message);
}
}
} finally {
lock.unlock();
}
}

/**
* By Login ID Get All session
* @param loginId
* @return
*/
private List<Session> getSessionList(Long loginId) {
List<Session> sessions = new ArrayList<>();
for(Map.Entry<String, Session> entry: sessionMap.entrySet()) {
String key = entry.getKey();
String loginIdStr = key.split("_")[0];
if(loginIdStr.equalsIgnoreCase(String.valueOf(loginId))) {
sessions.add(entry.getValue());
}
}
return sessions;
}

/**
* Delete Invalid session
* @param session
*/
private void removeSession(Session session) {
try {
if(session == null) {
return;
}

//
String sessionId = session.getId();
Enumeration<String> enumeration = sessionMap.keys();
while (enumeration.hasMoreElements()) {
String currKey = enumeration.nextElement();
String currSessionId = currKey.split("_")[1];
if (currSessionId.equalsIgnoreCase(sessionId)) {
sessionMap.remove(currKey);
break;
}
}
} catch (Exception e) {
log.info("Remove Session Error: {}", e.getMessage());
}
}
}
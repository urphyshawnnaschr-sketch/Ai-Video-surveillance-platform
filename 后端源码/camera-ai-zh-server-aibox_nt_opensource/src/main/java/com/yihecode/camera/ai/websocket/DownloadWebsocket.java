package com.yihecode.camera.ai.websocket;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.yihecode.camera.ai.enums.MessageType;
import com.yihecode.camera.ai.vo.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
* Download Message socket
*/
@Slf4j
@Component
@CrossOrigin
@ServerEndpoint(value = "/download/{satoken}")
public class DownloadWebsocket {

    //
private static final AtomicInteger onlineCount = new AtomicInteger(0);

//
private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

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

// put to Set combine, Method Convenient after Continue Operation
//long userId = SaFoxUtil.getValueByType(loginId, long.class);

//
sessionMap.put(satoken, session);

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
sessionMap.remove(satoken);

//
onlineCount.decrementAndGet(); // Online Number decrease 1

//
try {
session.close();
} catch (Exception e) {
e.printStackTrace();
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
// System.out.println("OnMessage session is null");
return;
}

//
if(!session.isOpen()) {
// System.out.println("OnMessage session is close");
return;
}

//
final RemoteEndpoint.Basic basic = session.getBasicRemote();
if(basic == null) {
// System.out.println("OnMessage basic is null");
return;
}

try {
Message messageVo = new Message();
messageVo.setType(MessageType.HEART.getType());
messageVo.setContent(System.currentTimeMillis() +"");
basic.sendText(JSONUtil.toJsonStr(messageVo));
} catch (Exception e) {
System.out.println("OnMessage Error:"+ e.getMessage());
}
}

/**
*
* @param session
* @param error
*/
@OnError
public void onError(Session session, Throwable error) {
try {
session.close();
} catch (Exception e) {
e.printStackTrace();
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
final RemoteEndpoint.Basic basic = session.getBasicRemote();
if(basic == null) {
return;
}

//
try {
basic.sendText(message);
} catch (IOException e) {
log.error("Websocket sendMessage IOException {}"+ e.getMessage());
}
}

/**
* Delete User
* @param satoken
*/
public void removeUser(String satoken) {
//
Session session = sessionMap.get(satoken);
if(session!= null) {
try {
session.close();
} catch (Exception e) {
//
}
}

//
sessionMap.remove(satoken);

//
onlineCount.decrementAndGet(); // Online Number decrease 1
}
}
package com.yihecode.camera.ai.isapi;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.io.StringWriter;

/**
* Temp Measure Control - Linkage Mode Set
*/
@Slf4j
public class NotificationMethodXmlParser {

    public static NotificationMethodXmlResult checkCenter(String xlmStr) {
//String xml ="<?xml version=\"1.0\"encoding=\"UTF-8\"?>\n"+
//"<EventTrigger version=\"2.0\"xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\n"+
//"<id>thermometry-1</id>\n"+
//"<eventType>thermometry</eventType>\n"+
//"<eventDescription>thermometry Event trigger Information</eventDescription>\n"+
//"<videoInputChannelID>1</videoInputChannelID>\n"+
//"<dynVideoInputChannelID>1</dynVideoInputChannelID>\n"+
//"<EventTriggerNotificationList>\n"+
//"<EventTriggerNotification>\n"+
//"<id>center</id>\n"+
//"<notificationMethod>center</notificationMethod>\n"+
//"<notificationRecurrence>beginning</notificationRecurrence>\n"+
//"</EventTriggerNotification>\n"+
//"<EventTriggerNotification>\n"+
//"<id>xxxxx</id>\n"+
//"<notificationMethod>xxxx</notificationMethod>\n"+
//"<notificationRecurrence>xxxxx</notificationRecurrence>\n"+
//"</EventTriggerNotification>\n"+
//"</EventTriggerNotificationList>\n"+
//"</EventTrigger>";


// Whether via Set' Upload in Core' Linkage Mode
boolean hasCenter = false;
try {
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setNamespaceAware(true);
Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xlmStr)));

// Get EventTriggerNotificationList Node
NodeList notificationLists = doc.getElementsByTagNameNS(
"http://www.isapi.org/ver20/XMLSchema","EventTriggerNotificationList");

if (notificationLists == null || notificationLists.getLength() == 0) {
// not has Config
return NotificationMethodXmlResult.builder().success(false).error("XML Format Error, not has EventTriggerNotificationList Node").xmlStr(xlmStr).build();
}

for (int i = 0; i < notificationLists.getLength(); i++) {
Node listNode = notificationLists.item(i);

NodeList notifications = listNode.getChildNodes();
if (notifications.getLength() == 0) {
continue;
}

for (int j = 0; j < notifications.getLength(); j++) {
Node notification = notifications.item(j);
if (notification.getNodeType() == Node.ELEMENT_NODE &&
notification.getNodeName().equals("EventTriggerNotification")) {

// Parse form Notification Config
NodeList fields = notification.getChildNodes();
for (int k = 0; k < fields.getLength(); k++) {
Node field = fields.item(k);
if (field.getNodeType() == Node.ELEMENT_NODE) {
String nodeName = field.getNodeName();
String nodeValue = field.getTextContent();

if("id".equalsIgnoreCase(nodeName) &&"center".equalsIgnoreCase(nodeValue)) {
hasCenter = true;
}
}
}
}
}
}

return NotificationMethodXmlResult.builder().success(true).error("OK").hasCenter(hasCenter).xmlStr(xlmStr).build();
} catch (Exception e) {
log.error("Parse Temp Measure Linkage Mode XML Exception", e);
return NotificationMethodXmlResult.builder().success(false).error("XML Parse Error").xmlStr(xlmStr).hasCenter(false).build();
}
}

/**
* Modify XML
* @param xmlStr
*/
public static NotificationMethodXmlResult modifyXml(String xmlStr) {
try {
// Create Document Builder
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setNamespaceAware(true);
Document doc = factory.newDocumentBuilder()
.parse(new InputSource(new StringReader(xmlStr)));

// Get EventTriggerNotificationList Node
NodeList notificationLists = doc.getElementsByTagNameNS(
"http://www.isapi.org/ver20/XMLSchema","EventTriggerNotificationList");

if(notificationLists.getLength() == 0) {
return NotificationMethodXmlResult.builder().success(false).error("not has EventTriggerNotificationList Node").xmlStr(xmlStr).build();
}

if (notificationLists.getLength() > 0) {
Element listElement = (Element) notificationLists.item(0);

// Create new Notification Node
Element notification = doc.createElementNS(
"http://www.isapi.org/ver20/XMLSchema","EventTriggerNotification");

// Add id child Node
Element id = doc.createElementNS(
"http://www.isapi.org/ver20/XMLSchema","id");
id.setTextContent("center");
notification.appendChild(id);

// Add notificationMethod child Node
Element method = doc.createElementNS(
"http://www.isapi.org/ver20/XMLSchema","notificationMethod");
method.setTextContent("center");
notification.appendChild(method);

// Add notificationRecurrence child Node
Element recurrence = doc.createElementNS(
"http://www.isapi.org/ver20/XMLSchema","notificationRecurrence");
recurrence.setTextContent("beginning");
notification.appendChild(recurrence);

// will new Node Add to List in
listElement.appendChild(notification);
}

// will Modify after Document Convert for String
Transformer transformer = TransformerFactory.newInstance().newTransformer();
StringWriter writer = new StringWriter();
transformer.transform(new DOMSource(doc), new StreamResult(writer));
return NotificationMethodXmlResult.builder().success(true).error("OK").xmlStr(xmlStr).modifyXml(writer.toString()).build();
} catch (Exception e) {
log.error("Modify Linkage Mode XML Error", e);
return NotificationMethodXmlResult.builder().success(false).error(e.getMessage()).xmlStr(xmlStr).build();
}
}
}

package com.yihecode.camera.ai.isapi;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
* Alarm Server Process
*/
@Slf4j
public class AlarmHostsXmlParser {

    /**
* Get Alarm Server Set List
* @param xmlStr
* @return
*/
    public static AlarmHostsXmlResult check(String xmlStr) {
        List<AlarmHosts> alarmHostsList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlStr)));

            //Get All HttpHostNotification Node
NodeList notificationNodes = doc.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","HttpHostNotification");

for (int i = 0; i < notificationNodes.getLength(); i++) {
Element notification = (Element) notificationNodes.item(i);
AlarmHosts alarmHosts = new AlarmHosts();
alarmHosts.setId(getElementText(notification,"id"));
alarmHosts.setUrl(getElementText(notification,"url"));
alarmHosts.setProtocolType(getElementText(notification,"protocolType"));
alarmHosts.setParameterFormatType(getElementText(notification,"parameterFormatType"));
alarmHosts.setAddressingFormatType(getElementText(notification,"addressingFormatType"));
alarmHosts.setIpAddress(getElementText(notification,"ipAddress"));
alarmHosts.setPortNo(getElementText(notification,"portNo"));
alarmHosts.setUserName(getElementText(notification,"userName"));
alarmHosts.setHttpAuthenticationMethod(getElementText(notification,"httpAuthenticationMethod"));
alarmHostsList.add(alarmHosts);
}

return AlarmHostsXmlResult.builder().success(true).error("OK").alarmHostsList(alarmHostsList).xmlStr(xmlStr).build();
} catch (Exception e) {
log.error("Parse Alarm Server XML Exception", e);
return AlarmHostsXmlResult.builder().success(false).error("XML Parse Error").xmlStr(xmlStr).build();
}
}

private static String getElementText(Element parent, String tagName) {
NodeList nodes = parent.getElementsByTagNameNS(
"http://www.isapi.org/ver20/XMLSchema", tagName);
if (nodes.getLength() > 0) {
return nodes.item(0).getTextContent();
}
return null;
}

/**
* Modify XML
* @param xmlStr
* @param id
* @param hostIp
* @param hostPort
* @param hostUrl
* @return
*/
public static AlarmHostsXmlResult modifyXml(String xmlStr, String id, String hostIp, String hostPort, String hostUrl) {
try {
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setNamespaceAware(true);
Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlStr)));

NodeList notifications = doc.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","HttpHostNotification");

for (int i = 0; i < notifications.getLength(); i++) {
Element notification = (Element) notifications.item(i);
String currentId = notification.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","id").item(0).getTextContent();

if (currentId.equals(id)) {
// Modify ipAddress
Node ipNode = notification.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","ipAddress").item(0);
ipNode.setTextContent(hostIp);

// Modify portNo
Node portNode = notification.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","portNo").item(0);
portNode.setTextContent(hostPort);

// Modify url
Node urlNode = notification.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","url").item(0);
urlNode.setTextContent(hostUrl);
break;
}
}

Transformer transformer = TransformerFactory.newInstance().newTransformer();
StringWriter writer = new StringWriter();
transformer.transform(new DOMSource(doc), new StreamResult(writer));
return AlarmHostsXmlResult.builder().success(true).error("OK").xmlStr(xmlStr).modifyXml(writer.toString()).build();
} catch (Exception e) {
log.error("Modify Alarm Server XML Exception", e);
return AlarmHostsXmlResult.builder().success(false).error("Modify Alarm Server XML Exception,"+ e.getMessage()).xmlStr(xmlStr).build();
}
}
}

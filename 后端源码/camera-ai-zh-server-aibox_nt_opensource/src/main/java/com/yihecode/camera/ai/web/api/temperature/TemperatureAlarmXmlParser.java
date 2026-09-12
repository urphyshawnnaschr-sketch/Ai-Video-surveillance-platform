package com.yihecode.camera.ai.web.api.temperature;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
* Parse Alarm XML
*/
@Slf4j
public class TemperatureAlarmXmlParser {

    /**
* Parse Data
* @param xmlStr
* @return
*/
    public static TemperatureAlarmXmlResult parse(String xmlStr) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlStr)));

            //Parse ipAddress
NodeList ipNodes = doc.getElementsByTagName("ipAddress");
String ipAddress = ipNodes.item(0).getTextContent();
//System.out.println("IP Address:"+ ipAddress);

// Parse DetectionRegionEntry
NodeList regionEntries = doc.getElementsByTagName("DetectionRegionEntry");
for (int i = 0; i < regionEntries.getLength(); i++) {
Element entry = (Element) regionEntries.item(i);
String regionId = entry.getElementsByTagName("regionID").item(0).getTextContent();
//System.out.println("\n Detection Region ID:"+ regionId);

// Parse TMA Node
Element tma = (Element) entry.getElementsByTagName("TMA").item(0);
String currTemp = tma.getElementsByTagName("currTemperature").item(0).getTextContent();
//System.out.println("Current Temperature:"+ currTemp +"℃");

// Parse MaximumTemperaturePoint
Element maxTempPoint = (Element) tma.getElementsByTagName("MaximumTemperaturePoint").item(0);
Element coord = (Element) maxTempPoint.getElementsByTagName("RegionCoordinates").item(0);
String posX = coord.getElementsByTagName("positionX").item(0).getTextContent();
String posY = coord.getElementsByTagName("positionY").item(0).getTextContent();
//System.out.println("most high Temperature Point Coordinate: ("+ posX +","+ posY +")");

// only Need Detection One most high Temperature
return TemperatureAlarmXmlResult.builder()
.success(true)
.error("OK")
.ipAddress(ipAddress)
.highTemperature(currTemp)
.positionX(Integer.parseInt(posX))
.positionY(Integer.parseInt(posY))
.build();
}
return TemperatureAlarmXmlResult.builder().success(false).error("not Parse to most high Temperature").build();
} catch (Exception e) {
//log.error("Temperature Alarm Data Parse Error, xml: {}", xmlStr, e);
return TemperatureAlarmXmlResult.builder().success(false).error("XML Parse Error").build();
}
}
}

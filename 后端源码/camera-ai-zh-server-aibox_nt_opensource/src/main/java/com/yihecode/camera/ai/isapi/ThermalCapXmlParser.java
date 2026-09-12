package com.yihecode.camera.ai.isapi;

import javax.xml.parsers.DocumentBuilderFactory;

import cn.hutool.core.util.StrUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.StringReader;

/**
* Thermal total can Force, main need Use at Parse Channel
*/
public class ThermalCapXmlParser {

    /**
* Parse Channel Value
* @param xmlStr
* @return
*/
    public static ThermalCapXmlResult parse(String xmlStr) {
        try {
            String xml = "<ThermalCap xmlns=\"http://www.isapi.org/ver20/XMLSchema\"version=\"2.0\">\n" +
                    "<isSupportFireDetection>true</isSupportFireDetection>\n" +
                    "<FireDetection>\n" +
                    "<supportChan opt=\"chan1\"/>\n" +
                    "</FireDetection>\n" +
                    "</ThermalCap>";

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlStr)));

            //Get FireDetection Node
NodeList fireDetectionList = doc.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","FireDetection");
if(fireDetectionList.getLength() > 0) {
Element fireDetection = (Element) fireDetectionList.item(0);

// Get supportChan Node
NodeList supportChanList = fireDetection.getElementsByTagNameNS("http://www.isapi.org/ver20/XMLSchema","supportChan");
if(supportChanList.getLength() > 0) {
Element supportChan = (Element) supportChanList.item(0);
String optValue = supportChan.getAttribute("opt");
if(StrUtil.isNotBlank(optValue)) {
return ThermalCapXmlResult.builder().success(true).error("OK").xmlStr(xmlStr).channelID(optValue.replace("chan","")).build();
}
}
}
return ThermalCapXmlResult.builder().success(true).error("not has Parse to Channel Value").xmlStr(xmlStr).build();
} catch (Exception e) {
return ThermalCapXmlResult.builder().success(false).error("Parse Thermal total can Force XML Exception").xmlStr(xmlStr).build();
}
}
}

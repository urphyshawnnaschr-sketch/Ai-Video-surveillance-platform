package com.yihecode.camera.ai.isapi;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import javax.xml.namespace.NamespaceContext;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
* Temp Control - Basic Config
*/
@Slf4j
public class BasicParamXmlParser {

    /**
* Read enabled Value
* @param xmlStr
* @return
*/
    public static BasicParamXmlResult readEnabled(String xmlStr) {
//String xml ="<?xml version=\"1.0\"encoding=\"UTF-8\"standalone=\"no\"?>\n"+
//"<ThermometryBasicParam xmlns=\"http://www.isapi.org/ver20/XMLSchema\"version=\"2.0\">\n"+
//"<id>1</id>\n"+
//"<enabled>false</enabled>\n"+
//"<streamOverlay>true</streamOverlay>\n"+
//"<AlertOutputIOPortList>\n"+
//"<OutputIOPort>\n"+
//"<portID>1</portID>\n"+
//"<enabled>true</enabled>\n"+
//"</OutputIOPort>\n"+
//"</AlertOutputIOPortList>\n"+
//"</ThermometryBasicParam>";

try {
XPath xpath = XPathFactory.newInstance().newXPath();
InputSource source = new InputSource(new StringReader(xmlStr));

// Register Life Name empty between (close Key Step Step)
xpath.setNamespaceContext(new NamespaceContext() {
public String getNamespaceURI(String prefix) {
return"http://www.isapi.org/ver20/XMLSchema";
}

public String getPrefix(String uri) {
return null;
}

public Iterator getPrefixes(String uri) {
return null;
}
});

// Exact Match allocate Root Node down Direct connect child Node enabled
String expr ="/ns:ThermometryBasicParam/ns:enabled";
String value = xpath.evaluate(expr, new InputSource(new StringReader(xmlStr)));
return BasicParamXmlResult.builder().success(true).error("OK").xmlStr(xmlStr).enabled(value).build();
} catch (Exception e) {
log.error("Parse Temp Measure Basic Config XML Exception, xml: {}", xmlStr, e);
return BasicParamXmlResult.builder().success(false).error(e.getMessage()).xmlStr(xmlStr).build();
}

}


/**
* Modify enabled Value, and Back XML
* @param xmlStr
* @return
* @throws Exception
*/
public static BasicParamXmlResult modifyXml(String xmlStr) {
// String xml ="<?xml version=\"1.0\"encoding=\"UTF-8\"standalone=\"no\"?>\n"+
//"<ThermometryBasicParam xmlns=\"http://www.isapi.org/ver20/XMLSchema\"version=\"2.0\">\n"+
//"<id>1</id>\n"+
//"<enabled>false</enabled>\n"+
//"<streamOverlay>true</streamOverlay>\n"+
//"<AlertOutputIOPortList>\n"+
//"<OutputIOPort>\n"+
//"<portID>1</portID>\n"+
//"<enabled>true</enabled>\n"+
//"</OutputIOPort>\n"+
//"</AlertOutputIOPortList>\n"+
//"</ThermometryBasicParam>";

try {
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setNamespaceAware(true);
Document doc = factory.newDocumentBuilder()
.parse(new InputSource(new StringReader(xmlStr)));

XPath xpath = XPathFactory.newInstance().newXPath();
xpath.setNamespaceContext(new NamespaceContext() {
public String getNamespaceURI(String prefix) {
return"http://www.isapi.org/ver20/XMLSchema";
}

public String getPrefix(String uri) {
return null;
}

public Iterator getPrefixes(String uri) {
return null;
}
});

// Precise Fixed Bit Root Node down enabled
Node targetNode = (Node) xpath.evaluate(
"/ns:ThermometryBasicParam/ns:enabled",
doc, XPathConstants.NODE);

if(targetNode == null) {
return BasicParamXmlResult.builder().success(false).error("not has enabled Node").xmlStr(xmlStr).build();
}

// Modify Node Value
targetNode.setTextContent("true");

// input out Result
StringWriter writer = new StringWriter();
Transformer transformer = TransformerFactory.newInstance().newTransformer();
transformer.transform(new DOMSource(doc), new StreamResult(writer));
return BasicParamXmlResult.builder().success(true).error("OK").xmlStr(xmlStr).modifyXml(writer.toString()).build();
} catch (Exception e) {
log.error("Modify Temp Measure Basic Config XML Exception, xml: {}", xmlStr, e);
return BasicParamXmlResult.builder().success(false).error("Modify XML Error").xmlStr(xmlStr).build();
}
}

}

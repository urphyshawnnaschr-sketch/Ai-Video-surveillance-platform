package com.yihecode.camera.ai.isapi;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Iterator;

/**
* Parse Camera Back Operation Result XML, Use at Determine Operation Whether Success
*/
@Slf4j
public class HttpPutResultParser {

    public static boolean parse(String xmlStr) {
//String xmlStr ="<?xml version=\"1.0\"encoding=\"UTF-8\"?>\n"+
//"<ResponseStatus version=\"2.0\"xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\n"+
//"<requestURL></requestURL>\n"+
//"<statusCode>1</statusCode>\n"+
//"<statusString>OK</statusString>\n"+
//"<subStatusCode>ok</subStatusCode>\n"+
//"</ResponseStatus>";

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
String expr ="/ns:ResponseStatus/ns:subStatusCode";
String value = xpath.evaluate(expr, new InputSource(new StringReader(xmlStr)));
return"ok".equalsIgnoreCase(value);
} catch (Exception e) {
// e.printStackTrace();
log.error("Parse Camera Return Result XML Exception, xml: {}", xmlStr, e);
}
return false;
}
}

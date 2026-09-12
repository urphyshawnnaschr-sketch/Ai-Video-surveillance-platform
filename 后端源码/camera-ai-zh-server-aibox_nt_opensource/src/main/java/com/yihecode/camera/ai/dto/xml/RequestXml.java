package com.yihecode.camera.ai.dto.xml;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.yihecode.camera.ai.utils.XmlUtil;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.codec.CharEncoding;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
* @Author lichangliang
* @Date 2023/7/26 22:51
* @Describe
* @Version 1.0
*/
@Data
@JacksonXmlRootElement(localName = "annotation")
@Log
public class RequestXml implements Serializable {
    private static final long serialVersionUID = 1L;

    //@JacksonXmlProperty
private String folder;
private String fileName;
private Source source;
private Size size;
private Integer segmented =0;
private List<AnnoObject> object;





public static void main(String[] args) throws IOException {
RequestXml requestXml = new RequestXml();
//XmlObject dept = new XmlObject();
requestXml.setFileName("Hello.jpg");
requestXml.setFolder("12345");
List<AnnoObject> annoObjectList =new ArrayList<>();
AnnoObject annoObject =new AnnoObject();
annoObject.setName("1");
annoObject.setPose("1");
annoObject.setTruncated(1);
annoObjectList.add(annoObject);
AnnoObject annoObject1 =new AnnoObject();
annoObject1.setName("2");
annoObject1.setPose("2");
annoObject1.setTruncated(2);
annoObjectList.add(annoObject1);
requestXml.setObject(annoObjectList);
String xml = XmlUtil.objectToXml(requestXml);
FileUtil.writeString(xml,"E:\\data\\text.xml", CharEncoding.UTF_8);
log.info(xml);
}
}

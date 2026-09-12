package com.yihecode.camera.ai.javacv;

import cn.hutool.core.util.IdUtil;
import com.yihecode.camera.ai.netty.MessageSenderAndWaiter;
import com.yihecode.camera.ai.netty.data.ImageCaptureRequest;
import com.yihecode.camera.ai.netty.data.ImageCaptureResponse;
import com.yihecode.camera.ai.netty.data.MessageType;
import com.yihecode.camera.ai.netty.data.Response;
import com.yihecode.camera.ai.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
* Pass rtsp/rtmp/file etc in Line Snapshot according Get image
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Slf4j
@Component
public class TakePhoto {

    @Value("${uploadDir}")
    private String uploadDir;

    @Autowired
    private MessageSenderAndWaiter messageSenderAndWaiter;

    /**
* Cross net Get image, Box In customer account current Scene
* @param rtspUrl
* @param sn
* @return
*/
    public VideoInfo takeCross(String rtspUrl, String sn) {
        ImageCaptureRequest request = new ImageCaptureRequest();
        request.setType(MessageType.IMAGE_CAPTURE.getType());
        request.setSn(sn);
        request.setRtspUrl(DecodeRtspUlr.processRtspUrl(rtspUrl));
        request.setRequestId(IdUtil.randomUUID());

        Response response = messageSenderAndWaiter.sendRequest(request);
        if(response == null) {
            return null;
        }

        ImageCaptureResponse imageCaptureResponse = (ImageCaptureResponse) response;
        if(!imageCaptureResponse.isStatus()) {
            return null;
        }

        return VideoInfo.builder()
                .videoCodec(imageCaptureResponse.getVideoCodec())
                .videoFps(imageCaptureResponse.getVideoFps())
                .videoWidth(imageCaptureResponse.getVideoWidth())
                .videoHeight(imageCaptureResponse.getVideoHeight())
                .fileName(imageCaptureResponse.getFileName()).build();
    }

    /**
* Snapshot according - Back File Name
* @return
*/
    public VideoInfo takeLocal(String rtspUrl) {
        FFmpegFrameGrabber grabber = null;
        int frameNullCount = 0;
        try {
            String decodedRtspUrl = DecodeRtspUlr.processRtspUrl(rtspUrl);
            grabber = new FFmpegFrameGrabber(decodedRtspUrl);
            grabber.setOption("stimeout", String.valueOf(5000000));
            grabber.setOption("rtsp_transport", "tcp");
            grabber.setOption("rtsp_flags", "prefer_tcp");
            grabber.startUnsafe();

            //Get Code Name
int videoCodec = grabber.getVideoCodec();
// H264 Corresponding codec Value for 27 or"avc1"
// H265 Corresponding codec Value for 86018 or"hev1"
String videCodecName ="H264";
if (videoCodec == avcodec.AV_CODEC_ID_H264) {
videCodecName ="H264";
} else if (videoCodec == avcodec.AV_CODEC_ID_HEVC) {
videCodecName ="H265";
}

// log.info("Video Code Info: {}, Video Code Name: {}", videoCodec, videCodecName);

int width = grabber.getImageWidth();
int height = grabber.getImageHeight();

// Frame Rate
int videoFps = Double.valueOf(grabber.getVideoFrameRate()).intValue();

// from No 5 Frame Start select Get, front Surface Frames can can is Gray image
int frameIndex = 5; // new Random().nextInt((50 - 20) + 1) + 20;
int i = 0;
do {
Frame frame = grabber.grabImage();
if (frame!= null && frame.image!= null) {
i++;
if (i >= frameIndex) {
Java2DFrameConverter converter = null;
try {

converter = new Java2DFrameConverter();
String fileName = IdUtil.randomUUID() +".jpg";
BufferedImage bufferedImage = converter.getBufferedImage(frame);
ImageIO.write(bufferedImage,"jpg", new File(FileUtils.pathTo(uploadDir +"/"+ fileName)));
return VideoInfo.builder()
.fileName(fileName)
.videoFps(videoFps)
.videoCodec(videCodecName)
.videoWidth(width)
.videoHeight(height)
.build();
} catch (IOException E) {
//
} finally {
if(converter!= null) {
converter.close();
}
}
}
} else {
frameNullCount++;
}
} while (frameNullCount <= 10);
} catch (Exception e3) {
//e3.printStackTrace();
} finally {
try {
if(grabber!= null) {
grabber.close();
}
} catch (Exception e) {
//
}
}
return null;
}

/**
* Snapshot according - Back Complete whole Path File Name
*
* @return
*/
public VideoInfo takeLocalFullFileName(String rtspUrl) {
FFmpegFrameGrabber grabber = null;
int frameNullCount = 0;
try {
String decodedRtspUrl = DecodeRtspUlr.processRtspUrl(rtspUrl);
grabber = new FFmpegFrameGrabber(decodedRtspUrl);
grabber.setOption("stimeout", String.valueOf(5000000));
grabber.setOption("rtsp_transport","tcp");
grabber.setOption("rtsp_flags","prefer_tcp");
grabber.startUnsafe();

// Get Code Name
int videoCodec = grabber.getVideoCodec();
// H264 Corresponding codec Value for 27 or"avc1"
// H265 Corresponding codec Value for 86018 or"hev1"
String videCodecName ="H264";
if (videoCodec == avcodec.AV_CODEC_ID_H264) {
videCodecName ="H264";
} else if (videoCodec == avcodec.AV_CODEC_ID_HEVC) {
videCodecName ="H265";
}

// log.info("Video Code Info: {}, Video Code Name: {}", videoCodec, videCodecName);

int width = grabber.getImageWidth();
int height = grabber.getImageHeight();

// Frame Rate
int videoFps = Double.valueOf(grabber.getVideoFrameRate()).intValue();

// from No 5 Frame Start select Get, front Surface Frames can can is Gray image
int frameIndex = 5; // new Random().nextInt((50 - 20) + 1) + 20;
int i = 0;
do {
Frame frame = grabber.grabImage();
if (frame!= null && frame.image!= null) {
i++;
if (i >= frameIndex) {
Java2DFrameConverter converter = null;
try {
String fileName = FileUtils.pathTo(uploadDir +"/"+ IdUtil.randomUUID() +".jpg");

converter = new Java2DFrameConverter();
BufferedImage bufferedImage = converter.getBufferedImage(frame);
ImageIO.write(bufferedImage,"jpg", new File(fileName));
return VideoInfo.builder()
.fileName(fileName)
.videoFps(videoFps)
.videoCodec(videCodecName)
.videoWidth(width)
.videoHeight(height)
.build();
} catch (IOException E) {
//
} finally {
if(converter!= null) {
converter.close();
}
}
}
} else {
frameNullCount++;
}
} while (frameNullCount <= 10);
} catch (Exception e3) {
log.error("takeLocalCameraDir Call send produce Exception:{}", e3.getMessage(), e3);
} finally {
try {
if(grabber!= null) {
grabber.close();
}
} catch (Exception e) {
log.error("takeLocalCameraDir finally Exception:{}", e.getMessage(), e);
}
}
return null;
}

/**
* Snapshot according - Back File Name
* @return
*/
public Map<String, String> take(String rtspUrl) {
FFmpegFrameGrabber grabber = null;
int frameNullCount = 0;
try {
String decodedRtspUrl = DecodeRtspUlr.processRtspUrl(rtspUrl);
grabber = new FFmpegFrameGrabber(decodedRtspUrl);
grabber.setOption("stimeout", String.valueOf(5000000));
grabber.setOption("rtsp_transport","tcp");
grabber.setOption("rtsp_flags","prefer_tcp");
grabber.startUnsafe();

// Get Code Name
int videoCodec = grabber.getVideoCodec();
// H264 Corresponding codec Value for 27 or"avc1"
// H265 Corresponding codec Value for 86018 or"hev1"
String videCodecName ="H264";
if (videoCodec == avcodec.AV_CODEC_ID_H264) {
videCodecName ="H264";
} else if (videoCodec == avcodec.AV_CODEC_ID_H265) {
videCodecName ="H265";
}

// from No 5 Frame Start select Get, front Surface Frames can can is Gray image
int frameIndex = 5; // new Random().nextInt((50 - 20) + 1) + 20;
int i = 0;
while(true) {
Frame frame = grabber.grabImage();
if(frame!= null && frame.image!= null) {
i++;
if(i >= frameIndex) {
try {
Java2DFrameConverter converter = new Java2DFrameConverter();
String fileName = IdUtil.randomUUID() +".jpg";
BufferedImage bufferedImage = converter.getBufferedImage(frame);
ImageIO.write(bufferedImage,"jpg", new File(uploadDir + fileName));
//
Map<String, String> retMap = new HashMap<>();
retMap.put("videoCodecName", videCodecName);
retMap.put("fileName", fileName);
return retMap;
} catch (IOException E) {
//
}
}
} else {
frameNullCount++;
}

// empty Frame, exit out
if(frameNullCount > 10) {
break;
}
}
} catch (FFmpegFrameGrabber.Exception e1) {
//e1.printStackTrace();
} catch (FrameGrabber.Exception e2) {
//e2.printStackTrace();
} catch (Exception e3) {
//e3.printStackTrace();
} finally {
try {
if(grabber!= null) {
grabber.close();
}
} catch (Exception e) {

}
}
return null;
}

/**
* Get Video Code Format
* @param rtspUrl
* @return
*/
public String getVideoCodec(String rtspUrl) {
FFmpegFrameGrabber grabber = null;
try {
String decodedRtspUrl = DecodeRtspUlr.processRtspUrl(rtspUrl);
grabber = new FFmpegFrameGrabber(decodedRtspUrl);
grabber.setOption("stimeout", String.valueOf(5000000));
grabber.setOption("rtsp_transport","tcp");
grabber.setOption("rtsp_flags","prefer_tcp");
grabber.startUnsafe();
int videoCodec = grabber.getVideoCodec();
// H264 Corresponding codec Value for 27 or"avc1"
// H265 Corresponding codec Value for 86018 or"hev1"
if (videoCodec == avcodec.AV_CODEC_ID_H264) {
return"H264";
} else if (videoCodec == avcodec.AV_CODEC_ID_H265) {
return"H265";
}
} catch (Exception e) {
// e.printStackTrace();
} finally {
if(grabber!= null) {
try {
grabber.close();
grabber = null;
} catch (FrameGrabber.Exception e) {
//
}
}
}
// Video Address Error, or Unknown Code Format, Default all by H265 Process, after Play Put Hour Wait all with Algorithm Push Stream Incoming Play Put
return"H264";
}

/**
* Get Video Info
* @param rtspUrl
* @return
*/
public VideoInfo getVideoInfo(String rtspUrl) {
FFmpegFrameGrabber grabber = null;
try {
grabber = new FFmpegFrameGrabber(rtspUrl);
grabber.setOption("stimeout", String.valueOf(5000000));
grabber.setOption("rtsp_transport","tcp");
grabber.setOption("rtsp_flags","prefer_tcp");
grabber.startUnsafe();
double frameRate = grabber.getFrameRate();

int videoCodec = grabber.getVideoCodec();
// H264 Corresponding codec Value for 27 or"avc1"
// H265 Corresponding codec Value for 86018 or"hev1"
if (videoCodec == avcodec.AV_CODEC_ID_H264) {
return VideoInfo.builder().videoCodec("H264").videoFps(Double.valueOf(frameRate).intValue()).build();
} else if (videoCodec == avcodec.AV_CODEC_ID_H265) {
return VideoInfo.builder().videoCodec("H265").videoFps(Double.valueOf(frameRate).intValue()).build();
} else {
return VideoInfo.builder().videoCodec("H264").videoFps(Double.valueOf(frameRate).intValue()).build();
}
} catch (Exception e) {
// e.printStackTrace();
} finally {
if(grabber!= null) {
try {
grabber.close();
grabber = null;
} catch (FrameGrabber.Exception e) {
//
}
}
}
// Video Address Error, or Unknown Code Format, Default all by H265 Process, after Play Put Hour Wait all with Algorithm Push Stream Incoming Play Put
return VideoInfo.builder().videoCodec("H264").videoFps(25).build();
}
}

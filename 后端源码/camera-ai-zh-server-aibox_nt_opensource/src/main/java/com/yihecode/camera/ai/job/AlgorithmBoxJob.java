package com.yihecode.camera.ai.job;

import cn.hutool.core.date.DateUtil;
import com.yihecode.camera.ai.entity.*;
import com.yihecode.camera.ai.netty.MessageSendHandler;
import com.yihecode.camera.ai.netty.data.AlgoExtrasResponse;
import com.yihecode.camera.ai.netty.data.DelFilesResponse;
import com.yihecode.camera.ai.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
* Algorithm and Box Device Task Process
*/
@Slf4j
@Component
public class AlgorithmBoxJob {

    @Autowired
    private AlgorithmBoxService algorithmBoxService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MessageSendHandler messageSendHandler;

    /**
* Whether In Progress Update Process
*/
    private boolean isRunning = false;

    public void runJob() {
        if(isRunning) {
            return ;
        }

        isRunning = true;

        try {
            AlgorithmBox algorithmBox = algorithmBoxService.getOneData();
            if(algorithmBox == null) {
                return;
            }

            //
Location location = locationService.getById(algorithmBox.getBoxId());
if(location == null) {
// Update Status to Processing
AlgorithmBox modifyData2 = new AlgorithmBox();
modifyData2.setId(algorithmBox.getId());
modifyData2.setState(2); // Process success
modifyData2.setMsg("Box Device Deleted or not find to");
modifyData2.setExecAt(new Date());
algorithmBoxService.updateById(modifyData2);
return;
}

// Update Status to Processing
AlgorithmBox modifyData = new AlgorithmBox();
modifyData.setId(algorithmBox.getId());
modifyData.setState(1); // Processing
modifyData.setMsg("");
modifyData.setExecAt(new Date());
algorithmBoxService.updateById(modifyData);

// Execute Delete Process
if(algorithmBox.getType() == 0) {
// Notification Box in Line Expand Param Process
DelFilesResponse response = messageSendHandler.sendDelFiles(location, algorithmBox.getAlgoId(), algorithmBox.getAlgoName(), algorithmBox.getAlgoCode());
if(response.isStatus()) {
AlgorithmBox modifyData2 = new AlgorithmBox();
modifyData2.setId(algorithmBox.getId());
modifyData2.setState(2); // Process success
modifyData2.setMsg(response.getMsg());
modifyData2.setExecAt(new Date());
algorithmBoxService.updateById(modifyData2);
} else {
AlgorithmBox modifyData2 = new AlgorithmBox();
modifyData2.setId(algorithmBox.getId());
modifyData2.setState(3); // Process failed
modifyData2.setMsg(response.getMsg());
modifyData2.setExecAt(new Date());
modifyData2.setTimeMills(DateUtil.offsetMinute(new Date(), 5).getTime()); // 5 min after again Execute
algorithmBoxService.updateById(modifyData2);
}
//log.info("{} Box Device Update Expand Param Result, {}", location.getName(), response);
}

// Update Expand Param
if(algorithmBox.getType() == 1) {
Algorithm algorithm = algorithmService.getById(algorithmBox.getAlgoId());
if(algorithm == null) {
// Update Status to Processing
AlgorithmBox modifyData2 = new AlgorithmBox();
modifyData2.setId(algorithmBox.getId());
modifyData2.setState(2); // Process success
modifyData2.setMsg("Algorithm Deleted");
modifyData2.setExecAt(new Date());
algorithmBoxService.updateById(modifyData2);
return;
}
// Notification Box in Line Expand Param Process
AlgoExtrasResponse response = messageSendHandler.sendAlgoExtras(location, algorithmBox.getAlgoId(), algorithm.getExtras());
if(response.isStatus()) {
AlgorithmBox modifyData2 = new AlgorithmBox();
modifyData2.setId(algorithmBox.getId());
modifyData2.setState(2); // Process success
modifyData2.setMsg(response.getMsg());
modifyData2.setExecAt(new Date());
algorithmBoxService.updateById(modifyData2);
} else {
AlgorithmBox modifyData2 = new AlgorithmBox();
modifyData2.setId(algorithmBox.getId());
modifyData2.setState(3); // Process failed
modifyData2.setMsg(response.getMsg());
modifyData2.setExecAt(new Date());
modifyData2.setTimeMills(DateUtil.offsetMinute(new Date(), 5).getTime()); // 5 min after again Execute
algorithmBoxService.updateById(modifyData2);
}
// log.info("{} Box Device Update Expand Param Result, {}", location.getName(), response);
}
} catch (Exception e) {
log.error("Process Algorithm and Box Device Task Exception", e);
} finally {
isRunning = false;
}
}
}

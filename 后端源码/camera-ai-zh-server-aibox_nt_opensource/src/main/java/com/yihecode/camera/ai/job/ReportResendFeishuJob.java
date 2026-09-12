package com.yihecode.camera.ai.job;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.entity.SocialHook;
import com.yihecode.camera.ai.entity.SocialResult;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.service.ReportService;
import com.yihecode.camera.ai.service.SocialHookService;
import com.yihecode.camera.ai.service.SocialResultService;
import com.yihecode.camera.ai.web.api.comm.AlarmFeishuPushV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
* Alarm Again Push Feishu job
*
* @author zhoumingxing
* @since 2025-10-15
*/
@Slf4j
@Component
public class ReportResendFeishuJob {
    @Autowired
    private SocialResultService socialResultService;
    @Autowired
    private AlarmFeishuPushV2Service alarmFeishuPushService;
    @Autowired
    private SocialHookService socialHookService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ConfigService configService;
    /**
* Push Feishu Hour Report Again Push
*/
    public void resendFeishuTask() {
        log.info("--------------- Alert Data Again Push Feishu --------------------");
        Date today = DateUtil.truncate(new Date(), DateField.DAY_OF_MONTH);
        Date tomorrow = DateUtil.offsetDay(today, 1);
        List<SocialResult> socialResultList =
                socialResultService.list(new LambdaQueryWrapper<SocialResult>()
                        .eq(SocialResult::getState, 1)
                        .eq(SocialResult::getBusinessType, 1)
                        .le(SocialResult::getResendNum, 7)
                        .between(SocialResult::getCreatedAt, today, tomorrow));
        log.info("Query to Need Again Push Alert Data:{}", socialResultList.size());
        String webUrl = configService.getByValTag("webUrl");

        //page Surface Access Address
for (SocialResult socialResult: socialResultList) {
Long reportId = socialResult.getReportId();
String pageUrl = webUrl +"/report/detail?id="+ reportId;
String[] split = socialResult.getSendText().split("###");
String title = split[0];
String content = split[1];
if(ObjectUtil.isNull(socialResult.getSocialId()) || ObjectUtil.equals(socialResult.getSocialId(), 0L)){
continue;
}
SocialHook socialHook = socialHookService.getById(socialResult.getSocialId());
Report report = reportService.getById(reportId);
if(ObjectUtil.isNull(report)){
socialResultService.removeById(socialResult.getId());
continue;
}
if(ObjectUtil.equals(report.getPushed(), 2)) {
log.info("Again Push Feishu Request Param:socialHook:{}, report:{}, title:{}, content:{}", JSON.toJSONString(socialHook), JSON.toJSONString(report), title, content);
alarmFeishuPushService.resendSync(socialHook, report, title, content, pageUrl, socialResult);
}
}

}

}

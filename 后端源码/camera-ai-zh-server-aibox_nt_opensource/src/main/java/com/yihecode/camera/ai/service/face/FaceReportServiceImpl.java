package com.yihecode.camera.ai.service.face;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.face.FaceReport;
import com.yihecode.camera.ai.entity.face.FaceUser;
import com.yihecode.camera.ai.mapper.face.FaceReportMapper;
import com.yihecode.camera.ai.mapper.face.FaceUserMapper;
import com.yihecode.camera.ai.service.ConfigService;
import com.yihecode.camera.ai.utils.TimeUtils;
import com.yihecode.camera.ai.web.face.vo.FaceReportPageVo;
import com.yihecode.camera.ai.web.face.vo.FaceReportUserPageVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Face Recognition Result - Face Recognition System
*
* @author zhoumingxing
* @mail 465769438@qq.com
*/
@Service
public class FaceReportServiceImpl extends ServiceImpl<FaceReportMapper, FaceReport> implements FaceReportService {

    @Autowired
    private FaceUserMapper faceUserMapper;

    @Autowired
    private ConfigService configService;

    /**
* Page Query
*
* @param page
* @param limit
* @param hasStranger
* @param cameraId
* @return
*/
    @Override
    public IPage<FaceReport> listPage(Integer page, Integer limit, Integer hasStranger, Long cameraId, Long groupId) {
        IPage<FaceReport> pageObj = new Page<>(page, limit);
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        if(hasStranger != null) {
            queryWrapper.eq(FaceReport::getHasStranger, hasStranger);
        }
        if(cameraId != null) {
            queryWrapper.eq(FaceReport::getCameraId, cameraId);
        }
        if(groupId != null) {
            queryWrapper.eq(FaceReport::getGroupId, groupId);
        }
        queryWrapper.orderByDesc(FaceReport::getCreatedMills);
        return this.page(pageObj, queryWrapper);
    }

    @Override
    public IPage<FaceReport> listPage2(Integer page, Integer limit, Integer hasStranger, Long cameraId, Long groupId, String startTime, String endTime, String name, String phone, Integer desc) {
        IPage<FaceReport> pageObj = new Page<>(page, limit);
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        if(hasStranger != null) {
            queryWrapper.eq(FaceReport::getHasStranger, hasStranger);
        }
        if(cameraId != null) {
            queryWrapper.eq(FaceReport::getCameraId, cameraId);
        }
        if(groupId != null) {
            queryWrapper.eq(FaceReport::getGroupId, groupId);
        }
        if(StringUtils.isNotBlank(startTime)) {
            queryWrapper.ge(FaceReport::getCreatedAt, DateUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss"));
        }
        if(StringUtils.isNotBlank(endTime)) {
            queryWrapper.le(FaceReport::getCreatedAt, DateUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss"));
        }
        if(StringUtils.isNotBlank(name) || StringUtils.isNotBlank(phone)) {
            LambdaQueryWrapper<FaceUser> query = new LambdaQueryWrapper<>();
            if(StringUtils.isNotBlank(name)) {
                query.like(FaceUser::getName, name);
            }
            if(StringUtils.isNotBlank(phone)) {
                query.like(FaceUser::getTel, phone);
            }
            query.eq(FaceUser::getDeleted, 0);
            List<FaceUser> users = faceUserMapper.selectList(query);
            List<Long> userIds = new ArrayList<>();
            for (FaceUser user : users) {
                userIds.add(user.getId());
            }
            if (!userIds.isEmpty()) {
                queryWrapper.in(FaceReport::getUserId, userIds);
            } else {
                queryWrapper.eq(FaceReport::getUserId, -1);
            }
        }
        if (desc == 1) {
            queryWrapper.orderByDesc(FaceReport::getCreatedMills);
        } else {
            queryWrapper.orderByAsc(FaceReport::getCreatedMills);
        }

        return this.page(pageObj, queryWrapper);
    }

    @Override
    public void updateGroupIdByUserId(Long groupId, Long userId) {
        FaceReport report = new FaceReport();
        report.setGroupId(groupId);
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceReport::getUserId, userId);
        queryWrapper.eq(FaceReport::getHasStranger, 0);
        this.update(report, queryWrapper);
    }

    @Override
    public void removeByUser(Long userId) {
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceReport::getUserId, userId);
        this.remove(queryWrapper);
    }

    @Override
    public Integer countByGroupId(Long groupId) {
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        if (null != groupId) {
            queryWrapper.eq(FaceReport::getGroupId, groupId);
        }
        return this.count(queryWrapper);
    }

    @Override
    public void clearReport() {
        String day = configService.getByValTag("clearFaceReportDay");
        if (StringUtils.isBlank(day)) {
            configService.saveData("Scheduled Task Clear Divide Face Alert Info Keep day Number", "clearFaceReportDay", "1");
            day = "1";
        }
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        if (day.equals("0")) {
            queryWrapper.lt(FaceReport::getCreatedAt, TimeUtils.getZero(new Date()));
        } else {
            queryWrapper.lt(FaceReport::getCreatedAt, DateUtils.addDays(new Date(), -Integer.parseInt(day)));
        }
        List<FaceReport> faceReportList = this.list(queryWrapper);
        for (FaceReport faceReport : faceReportList) {
            if(StrUtil.isNotBlank(faceReport.getFilePath())) {
                FileUtil.del(faceReport.getFilePath());
            }
            if(StrUtil.isNotBlank(faceReport.getSourceFile())) {
                FileUtil.del(faceReport.getSourceFile());
            }
        }
        this.remove(queryWrapper);
    }

    /**
* Query Person member Trajectory
*
* @param userId
* @param startDate
* @param endDate
*/
    @Override
    public List<FaceReport> listByUser(Long userId, Date startDate, Date endDate, Integer hasStranger, List<Long> cameraIds) {
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FaceReport::getUserId, userId);
        queryWrapper.gt(FaceReport::getCreatedMills, startDate.getTime());
        queryWrapper.lt(FaceReport::getCreatedMills, endDate.getTime());
        if(hasStranger != null) {
            queryWrapper.eq(FaceReport::getHasStranger, hasStranger);
        }
        if(ObjectUtil.isNotEmpty(cameraIds)) {
            queryWrapper.in(FaceReport::getCameraId, cameraIds);
        }
        queryWrapper.orderByAsc(FaceReport::getCreatedMills);
        return this.list(queryWrapper);
    }

    /**
* Query Temp near Record, type=0, by Time up order up One, type=1, by Time down order down One
*
* @param pageVo
* @return
*/
    @Override
    public FaceReport getNearly(FaceReportPageVo pageVo) {
        LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(FaceReport::getId);

        if(ObjectUtil.isNotEmpty(pageVo.getCameraIds())) {
            queryWrapper.in(FaceReport::getCameraId, pageVo.getCameraIds());
        }
        if(ObjectUtil.isNotEmpty(pageVo.getUserIds())) {
            queryWrapper.in(FaceReport::getUserId, pageVo.getUserIds());
        }
        if(ObjectUtil.isNotNull(pageVo.getStartDate())) {
            queryWrapper.gt(FaceReport::getCreatedMills, pageVo.getStartDate().getTime());
        }
        if(ObjectUtil.isNotNull(pageVo.getEndDate())) {
            queryWrapper.lt(FaceReport::getCreatedMills, pageVo.getEndDate().getTime());
        }
        if(pageVo.getGroupId() != null) {
            queryWrapper.eq(FaceReport::getGroupId, pageVo.getGroupId());
        }
        if(pageVo.getIsStranger() != null && pageVo.getIsStranger() == 1) {
            queryWrapper.eq(FaceReport::getHasStranger, 1);
        } else {
            if(pageVo.getGroupId() != null) {
                queryWrapper.eq(FaceReport::getHasStranger, 0);
            }
        }

        //type=0, by Time up order up One
if(pageVo.getType() == 0) {
queryWrapper.gt(FaceReport::getId, pageVo.getReportId());
queryWrapper.orderByAsc(FaceReport::getCreatedMills);
}

// type=1, by Time down order down One
if(pageVo.getType() == 1) {
queryWrapper.lt(FaceReport::getId, pageVo.getReportId());
queryWrapper.orderByDesc(FaceReport::getCreatedMills);
}

// Only Only Query One set
queryWrapper.last("limit 0, 1");
return this.getOne(queryWrapper);
}

/**
* Page Query
*
* @param pageVo
* @return
*/
@Override
public IPage<FaceReport> listPageV5(FaceReportPageVo pageVo) {
IPage<FaceReport> iPage = new Page<>(pageVo.getPage(), pageVo.getLimit());

LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
if(ObjectUtil.isNotEmpty(pageVo.getCameraIds())) {
queryWrapper.in(FaceReport::getCameraId, pageVo.getCameraIds());
}
if(ObjectUtil.isNotEmpty(pageVo.getUserIds())) {
queryWrapper.in(FaceReport::getUserId, pageVo.getUserIds());
}
if(ObjectUtil.isNotNull(pageVo.getStartDate())) {
queryWrapper.gt(FaceReport::getCreatedMills, pageVo.getStartDate().getTime());
}
if(ObjectUtil.isNotNull(pageVo.getEndDate())) {
queryWrapper.lt(FaceReport::getCreatedMills, pageVo.getEndDate().getTime());
}
if(pageVo.getGroupId()!= null) {
queryWrapper.eq(FaceReport::getGroupId, pageVo.getGroupId());
}
if(pageVo.getIsStranger()!= null) {
queryWrapper.eq(FaceReport::getHasStranger, pageVo.getIsStranger());
}
if(pageVo.getIsStranger() == null && pageVo.getGroupId()!= null) {
queryWrapper.eq(FaceReport::getHasStranger, 0);
}
if(ObjectUtil.isNotEmpty(pageVo.getUserIds())) {// 20260306 like Result Query Contain User, rule only Query Non Stranger produce Person
queryWrapper.eq(FaceReport::getHasStranger, 0);
}

queryWrapper.orderByDesc(FaceReport::getCreatedMills);
return this.page(iPage, queryWrapper);
}

/**
* By User ID Query
*
* @param pageVo
* @return
*/
@Override
public List<FaceReport> listUserAllV2(FaceReportUserPageVo pageVo) {
LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(FaceReport::getId, FaceReport::getCameraId, FaceReport::getUserId, FaceReport::getCreatedAt);
queryWrapper.eq(FaceReport::getUserId, pageVo.getUserId());
queryWrapper.eq(FaceReport::getHasStranger, 0);
if(pageVo.getStartDate()!= null) {
queryWrapper.gt(FaceReport::getCreatedAt, pageVo.getStartDate());
}
if(pageVo.getEndDate()!= null) {
queryWrapper.lt(FaceReport::getCreatedAt, pageVo.getEndDate());
}
if(ObjectUtil.isNotEmpty(pageVo.getCameraIds())) {
queryWrapper.in(FaceReport::getCameraId, pageVo.getCameraIds());
}
queryWrapper.orderByAsc(FaceReport::getCreatedAt);
return this.list(queryWrapper);
}

/**
* By User ID Query
*
* @param pageVo
* @return
*/
@Override
public IPage<FaceReport> listUserPageV2(FaceReportUserPageVo pageVo) {
IPage<FaceReport> iPage = new Page<>(pageVo.getPage(), pageVo.getLimit());
LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.select(FaceReport::getId, FaceReport::getCameraId, FaceReport::getUserId, FaceReport::getCreatedAt);
queryWrapper.eq(FaceReport::getUserId, pageVo.getUserId());
queryWrapper.eq(FaceReport::getHasStranger, 0);
if(pageVo.getStartDate()!= null) {
queryWrapper.gt(FaceReport::getCreatedAt, pageVo.getStartDate());
}
if(pageVo.getEndDate()!= null) {
queryWrapper.lt(FaceReport::getCreatedAt, pageVo.getEndDate());
}
if(ObjectUtil.isNotEmpty(pageVo.getCameraIds())) {
queryWrapper.in(FaceReport::getCameraId, pageVo.getCameraIds());
}
queryWrapper.orderByDesc(FaceReport::getCreatedAt);
return this.page(iPage, queryWrapper);
}

/**
* By User ID Delete
*
* @param userId
*/
@Override
public void deleteByUser(Long userId) {
LambdaQueryWrapper<FaceReport> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(FaceReport::getUserId, userId);
this.remove(queryWrapper);
}
}
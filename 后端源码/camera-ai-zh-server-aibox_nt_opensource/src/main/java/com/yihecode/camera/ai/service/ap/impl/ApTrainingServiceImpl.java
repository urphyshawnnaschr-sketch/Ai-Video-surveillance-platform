package com.yihecode.camera.ai.service.ap.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yihecode.camera.ai.dto.ApProjectDTO;
import com.yihecode.camera.ai.entity.Algorithm;
import com.yihecode.camera.ai.entity.Report;
import com.yihecode.camera.ai.entity.ReportParam;
import com.yihecode.camera.ai.entity.ap.*;
import com.yihecode.camera.ai.enums.ap.ImageStatus;
import com.yihecode.camera.ai.enums.ap.ProjectStatusEnum;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ReportMapper;
import com.yihecode.camera.ai.mapper.ap.ApCommitMapper;
import com.yihecode.camera.ai.mapper.ap.ApProjectMapper;
import com.yihecode.camera.ai.mapper.ap.UserGroupProjectMapper;
import com.yihecode.camera.ai.service.AlgorithmService;
import com.yihecode.camera.ai.service.CameraService;
import com.yihecode.camera.ai.service.ap.ApAnnotationService;
import com.yihecode.camera.ai.service.ap.ApCommitService;
import com.yihecode.camera.ai.service.ap.ApImageService;
import com.yihecode.camera.ai.service.ap.ApTrainingService;
import com.yihecode.camera.ai.utils.Md5FileUtils;
import com.yihecode.camera.ai.web.ap.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ApTrainingServiceImpl implements ApTrainingService {

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private ApProjectMapper projectMapper;

    @Autowired
    private UserGroupProjectMapper userGroupProjectMapper;

    @Autowired
    private ApImageService imageService;

    @Autowired
    private ApAnnotationService annotationService;

    @Autowired
    private ApCommitService commitService;

    @Autowired
    private ApCommitMapper commitMapper;

    @Override
    public int count(TrainingListRequestVo request) {
        QueryWrapper<Report> queryWrapper = new QueryWrapper<Report>()
                .select("distinct camera_id, algorithm_id")
                .eq("display", 0)
                .eq("audit_state", 0);
        if (request.getCameraId() != null && request.getCameraId() > 0) {
            queryWrapper = queryWrapper.eq("camera_id", request.getCameraId());
        }
        if (request.getAlgorithmId() != null && request.getAlgorithmId() > 0) {
            queryWrapper = queryWrapper.eq("algorithm_id", request.getAlgorithmId());
        }

        return reportMapper.selectCount(queryWrapper);
    }

    @Override
    public List<ReportAggVo> list(TrainingListRequestVo request) {

        QueryWrapper<Report> queryWrapper = new QueryWrapper<Report>()
                .select("camera_id, algorithm_id, count(1) as image_num, max(created_mills) as updated_at")
                .eq("display", 0)
                .eq("audit_state", 0);

        if (request.getCameraId() != null && request.getCameraId() > 0) {
            queryWrapper = queryWrapper.eq("camera_id", request.getCameraId());
        }
        if (request.getAlgorithmId() != null && request.getAlgorithmId() > 0) {
            queryWrapper = queryWrapper.eq("algorithm_id", request.getAlgorithmId());
        }

        queryWrapper = queryWrapper.groupBy("camera_id, algorithm_id")
                .orderByDesc("updated_at")
                .last("limit" + (request.getPage() - 1) * request.getLimit() + "," + request.getLimit());


        List<Map<String, Object>> resultMap = reportMapper.selectMaps(queryWrapper);
        List<ReportAggVo> reportList = new ArrayList<>();
        if (CollectionUtils.isEmpty(resultMap)) {
            return reportList;
        }

        Map<Long, String> algorithmIdNameMap = algorithmService.toMap();
        Map<Long, String> cameraIdNameMap = cameraService.toMap();
        for (Map<String, Object> m : resultMap) {
            reportList.add(ReportAggVo.builder()
                    .cameraId(toLong( m.get("camera_id")))
                    .algorithmId(toLong( m.get("algorithm_id")))
                    .imageNum(toLong( m.get("image_num")))
                    .updatedAt(new Date(toLong( m.get("updated_at"))))
                    .cameraName(cameraIdNameMap.getOrDefault(toLong( m.get("camera_id")), ""))
                    .algorithmName(algorithmIdNameMap.getOrDefault(toLong( m.get("algorithm_id")), ""))
                    .build());
        }

        return reportList;
    }

    @Override
    public Long[] range(TrainingBaseVo request) throws BizException {

        QueryWrapper<Report> queryWrapper = commonQuery(request);
        queryWrapper.orderByDesc("created_mills");
        int offset  = request.getBegin() > 0 ? request.getBegin() - 1 : 0;
        int count = 1;
        queryWrapper.last("limit" + offset + "," + count);
        Report begin = reportMapper.selectOne(queryWrapper);
        if (begin == null) {
            throw new BizException("super out Range");
        }
        offset  = request.getEnd() > 0 ? request.getEnd() - 1 : 0;
        queryWrapper.last("limit" + offset + "," + count);
        Report end = reportMapper.selectOne(queryWrapper);
        if (end == null) {
            queryWrapper = commonQuery(request);
            queryWrapper.orderByAsc("created_mills");
            queryWrapper.last("limit 1");
            end = reportMapper.selectOne(queryWrapper);
            if (end == null) {
                throw new BizException("super out Range");
            }
        }

        return new Long[]{begin.getId(), end.getId()};
    }

    private QueryWrapper<Report> commonQuery(TrainingBaseVo request) {
        QueryWrapper<Report> queryWrapper = new QueryWrapper<Report>()
                .eq("display", 0)
                .eq("audit_state", 0);
        if (request.getCameraId() != null && request.getCameraId() > 0) {
            queryWrapper = queryWrapper.eq("camera_id", request.getCameraId());
        }
        if (request.getAlgorithmId() != null && request.getAlgorithmId() > 0) {
            queryWrapper = queryWrapper.eq("algorithm_id", request.getAlgorithmId());
        }
        return queryWrapper;
    }


    @Override
    public int count(TrainingBaseVo request, Long[] range) throws BizException {

        //select count(distinct ROUND(created_mills/500000)) FROM tbl_biz_report WHERE camera_id = 1641425040524783617 and algorithm_id = 1667046234236723202
int intervalMs = request.getInterval() * 1000;
QueryWrapper<Report> queryWrapper = commonQuery(request)
.select("distinct ROUND(created_mills/"+ intervalMs +")")
.le("id", range[0]).ge("id", range[1]);

return reportMapper.selectCount(queryWrapper);
}

@Override
public List<Report> list(TrainingBaseVo request, Long[] range, Integer page, Integer limit) {
// select max(id), ROUND(created_mills/500000) as u FROM tbl_biz_report
// WHERE camera_id = 1641425040524783617 and algorithm_id = 1667046234236723202
// group by u order by u desc limit 5,10
int intervalMs = request.getInterval() * 1000;
QueryWrapper<Report> queryWrapper = commonQuery(request)
.select("max(id) as id, ROUND(created_mills/"+ intervalMs +") as u")
.le("id", range[0]).ge("id", range[1])
.orderByDesc("u")
.groupBy("u");
if (page!= null && limit!= null) {
queryWrapper = queryWrapper.last("limit"+ (page - 1) * limit +","+ limit);
}
List<Object> idList = reportMapper.selectObjs(queryWrapper);
return reportMapper.selectBatchIds((List<Long>)(List)idList);
}

@Override
@Transactional(rollbackFor = Exception.class)
public Long createProject(TrainingCreateProjectRequestVo requestVo, List<Report> reportList) throws Exception {
Algorithm algorithm = algorithmService.getById(requestVo.getTrainingBaseVo().getAlgorithmId());
if (algorithm == null) {
throw new BizException("Algorithm does not exist");
}

ApProjectDTO projectDTO = requestVo.getProjectDTO();
// project
ApProjectDO apProjectDO = new ApProjectDO();
BeanUtils.copyProperties(projectDTO, apProjectDO);
apProjectDO.setId(null);
apProjectDO.setLabeled(0);
apProjectDO.setItemCount(reportList.size());
apProjectDO.setProjectType(4); // increase o Quantity Training
apProjectDO.setStatus(ProjectStatusEnum.ANN.getCode()); // can can still Need over Stick combine Model
projectMapper.insert(apProjectDO);

// user group project
List<Long> userGroupIdList = projectDTO.getUserGroupId();
if (CollectionUtil.isNotEmpty(userGroupIdList)) {
for (Long groupId: userGroupIdList) {
UserGroupProjectDO userGroupProjectDO = new UserGroupProjectDO();
userGroupProjectDO.setGroupId(groupId);
userGroupProjectDO.setProjectId(apProjectDO.getId());
userGroupProjectMapper.insert(userGroupProjectDO);
}
}

// images + annotation + commit
List<Image> imageList = new ArrayList<>();
Map<String, String> imageFileParamMap = new HashMap<>();
List<Long> reportIds = new ArrayList<>();
int index = 0;
for (Report r: reportList) {
if (r.getParams() == null ||"NULL".equalsIgnoreCase(r.getParams())) {
continue; // not has Annotation Info Image
}
reportIds.add(r.getId());
Image i = new Image();
i.setProjectId(apProjectDO.getId());
i.setStoragePath(r.getFileName());
i.setStatus(ImageStatus.IMPORT_PROGRESS_FINSH.getType());
i.setVersion(0);
try {
File file = new File(r.getFileName());
BufferedImage image = ImageIO.read(file);
i.setWidth(image.getWidth());
i.setHeight(image.getHeight());
i.setMd5(Md5FileUtils.getMD5(file));
} catch (IOException e) {
System.out.print("read file IOException:"+ r.getFileName());
System.out.print(e);
}
// Quality Check Get sample
index ++;
i.setNeedReview(1);
if (projectDTO.getNeedReview() == 1 && projectDTO.getReviewRatio() > 0) {
if (index % 100 < projectDTO.getReviewRatio()) {
i.setNeedReview(1);
}
}
imageList.add(i);
imageFileParamMap.put(r.getFileName(), r.getParams());
}

if (imageList.size() == 0) {
return apProjectDO.getId();
}

// todo Actually is One sql One Start Execute And
imageService.saveBatch(imageList);

List<Annotation> annotationList = new ArrayList<>();
Map<Long, Commit> imageCommitMap = new HashMap<>();
for (Image i: imageList) {
String params = imageFileParamMap.get(i.getStoragePath());
List<ReportParam> paramList = JSON.parseArray(params, ReportParam.class);
for (ReportParam p: paramList) {
Annotation annotation = new Annotation();
annotation.setProjectId(apProjectDO.getId());
annotation.setImageId(i.getId());
annotation.setAnnotationType(2); // Rectangle Box
annotation.setAnnotation(AnnotationData.builder().box(p.getPosition())
.label(Arrays.asList(Label.builder().name(algorithm.getName()).build())).build());
annotation.setTagName(algorithm.getName());
annotationList.add(annotation);
}

Commit commit = new Commit();
commit.setProjectId(apProjectDO.getId());
commit.setImageId(i.getId());
commit.setIsValid((byte) 1);
commit.setAnnotationCount(paramList.size());
imageCommitMap.put(i.getId(), commit);
}
commitService.saveBatch(imageCommitMap.values());
for (Annotation annotation: annotationList) {
annotation.setCommitId(imageCommitMap.get(annotation.getImageId()).getId());
}
annotationService.saveBatch(annotationList);

for (Image i: imageList) {
i.setCommitId(imageCommitMap.get(i.getId()).getId());
}
imageService.updateBatchById(imageList);

// report
LambdaUpdateWrapper<Report> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Report::getAuditState, 5)
.set(Report::getProjectId, apProjectDO.getId())
.in(Report::getId, reportIds);
reportMapper.update(null, updateWrapper);
return apProjectDO.getId();
}

// Delete not Extract in Image
@Override
public int delete(TrainingBaseVo request, Long[] range) {
QueryWrapper<Report> queryWrapper = commonQuery(request)
.le("id", range[0]).ge("id", range[1]);
List<Report> reportList = reportMapper.selectList(queryWrapper);
return delete(reportList);
}

private int delete(List<Report> reportList) {
List<Long> reportIds = new ArrayList<>();
for (Report r: reportList) {
reportIds.add(r.getId());
File file = new File(r.getFileName());
file.delete();
}
if (reportIds.size() == 0) {
return 0;
}
LambdaUpdateWrapper<Report> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(Report::getAuditState, 11)
.set(Report::getDisplay, 1)
.in(Report::getId, reportIds);
return reportMapper.update(null, updateWrapper);
}

@Override
public Integer delete(TrainingDeleteRequestVo request) {
QueryWrapper<Report> queryWrapper = commonQuery(request.getTrainingBaseVo());
int offset = request.getTrainingBaseVo().getBegin() > 0? request.getTrainingBaseVo().getBegin() - 1: 0;
int count = request.getTrainingBaseVo().getEnd() - request.getTrainingBaseVo().getBegin();
queryWrapper.last("limit"+ offset +","+ count);
List<Report> reportList = reportMapper.selectList(queryWrapper);

return delete(reportList);
}

private long toLong(Object o) {
if (o == null) {
return 0;
}
return (long) o;
}

}

package com.yihecode.camera.ai.service.ap.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.constant.GlobConstant;
import com.yihecode.camera.ai.dto.*;
import com.yihecode.camera.ai.entity.ap.*;
import com.yihecode.camera.ai.enums.ap.ImageStatus;
import com.yihecode.camera.ai.enums.ap.ProjectStatusEnum;
import com.yihecode.camera.ai.enums.ap.ReviewAction;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.*;
import com.yihecode.camera.ai.service.AccountService;
import com.yihecode.camera.ai.service.ap.*;
import com.yihecode.camera.ai.utils.DatasetImportHandleUtil;
import com.yihecode.camera.ai.utils.RelativeNumberFormatToolUtil;
import com.yihecode.camera.ai.vo.ap.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
* pProjectService real current class
* 1. Please supplement Charge Generate Method Throw Exception, Exception make Use Corresponding Exception Code
* 2. All Generate Method Disable Add Any Business Logic
*/
@Service
@Slf4j
public class ApProjectServiceImpl extends ServiceImpl<ApProjectMapper, ApProjectDO> implements ApProjectService {

    @Autowired
    private ApProjectMapper projectMapper;

    @Autowired
    private UserGroupProjectMapper userGroupProjectMapper;

    public static volatile Long MAXID = 1L;

    @Autowired
    @Lazy
    private DatasetImportHandleUtil datasetImportHandleUtil;

    @Autowired
    private ApImageService apImageService;

    @Autowired
    private ApAnnotationService apAnnotationService;

    @Autowired
    private ApReviewMapper apReviewMapper;

    @Autowired
    private ApCommitMapper apCommitMapper;

    @Autowired
    @Lazy
    private ApFileService apFileService;

    @Autowired
    private ApUserTeamRelationshipsMapper apUserTeamRelationshipsMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ApUserGroupService apUserGroupService;

    @Autowired
    private ApUserTeamService apUserTeamService;

    @Resource
    private ApReviewMapper reviewMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long savepProject(ApProjectDTO projectDTO) throws BizException {
        validateProject(projectDTO);
        projectDTO.setCreatedAt(new Date());
        projectDTO.setUpdatedAt(new Date());
        projectDTO.setStatus(ProjectStatusEnum.DATA_LOADING.getCode());
        ApProjectDO apProjectDO = new ApProjectDO();

        BeanUtils.copyProperties(projectDTO, apProjectDO);
        apProjectDO.setItemCount(0);
        apProjectDO.setLabeled(0);
        projectMapper.insert(apProjectDO);
        projectDTO.setId(apProjectDO.getId());
        List<Long> userGroupIdList = projectDTO.getUserGroupId();
        if (CollectionUtil.isNotEmpty(userGroupIdList)) {
            for (Long groupId : userGroupIdList) {
                UserGroupProjectDO userGroupProjectDO = new UserGroupProjectDO();
                userGroupProjectDO.setGroupId(groupId);
                userGroupProjectDO.setProjectId(apProjectDO.getId());
                userGroupProjectMapper.insert(userGroupProjectDO);
            }
        }
        datasetImportHandleUtil.importFile(projectDTO, StpUtil.getLoginIdAsLong());
        return apProjectDO.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletepProjectById(Long projectId) {


        projectMapper.deleteById(projectId);
        LambdaUpdateWrapper<Image> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Image::getDeletedAt,new Date());
        updateWrapper.eq(Image::getProjectId,projectId);


        apImageService.update(updateWrapper);
        LambdaQueryWrapper<Commit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Commit::getProjectId,projectId);
        apCommitMapper.delete(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatepProjectById(ApProjectDTO project) throws BizException {
        validateProject(project);
        project.setUpdatedAt(new Date());
        ApProjectDO apProjectDO = new ApProjectDO();
        BeanUtils.copyProperties(project, apProjectDO);

        projectMapper.updateById(apProjectDO);
    }


    @Override
    public ApProjectDTO getpProjectById(Long projectId) {

        ApProjectDO project = projectMapper.selectById(projectId);
        if (ObjectUtil.isNull(project)) {
            return null;
        }
        ApProjectDTO apProjectDTO = new ApProjectDTO();
        ApFileDO apFileDO = apFileService.getById(project.getDocFileId());
        BeanUtils.copyProperties(project, apProjectDTO);
        if (ObjectUtil.isNotNull(apFileDO)) {
            apProjectDTO.setDocPath(apFileDO.getRawData());
        }
        return apProjectDTO;
    }


    @Override
    public List<ApProjectDTO> findpProjectList(ApProjectQueryDTO projectQueryDTO) {

        List<ApProjectDO> pProjectDOList = projectMapper.findpProjectList(projectQueryDTO);
        if (CollectionUtils.isEmpty(pProjectDOList)) {
            return new ArrayList();
        }
        List<ApProjectDTO> ProjectList = new ArrayList(pProjectDOList.size());
        for (ApProjectDO pProjectDO : pProjectDOList) {
            ApProjectDTO apProjectDTO = new ApProjectDTO();
            BeanUtils.copyProperties(pProjectDO, apProjectDTO);

            if (pProjectDO.getItemCount() > 0) {
                BigDecimal finshRatio = new BigDecimal(ObjectUtil.isNull(pProjectDO.getLabeled()) ? 0 : pProjectDO.getLabeled())
                        .divide(new BigDecimal(pProjectDO.getItemCount()), 2, RoundingMode.DOWN);
                apProjectDTO.setAnnoRatio(finshRatio.multiply(new BigDecimal(100)));
            }

            ProjectList.add(apProjectDTO);
        }
        return ProjectList;
    }

    @Override
    public IPage<ApProjectDTO> findpProjectPage(ApProjectQueryDTO pProjectQuery) {
        Page<ApProjectDO> page = new Page<>();
        page.setCurrent(pProjectQuery.getPageNum());
        page.setSize(pProjectQuery.getPageSize());
        IPage<ApProjectDO> apProjectDOIPage = projectMapper.findpProjectPage(page, pProjectQuery);
        List<ApProjectDO> records = apProjectDOIPage.getRecords();
        List<ApProjectDTO> ProjectList = new ArrayList(records.size());
        for (ApProjectDO pProjectDO : records) {
            ApProjectDTO apProjectDTO = new ApProjectDTO();
            BeanUtils.copyProperties(pProjectDO, apProjectDTO);
            apProjectDTO.setNoPassImageNum(getNoPassImages(apProjectDTO.getId()));
            apProjectDTO.setReviewNum(getReviewCount(apProjectDTO.getId()));
//if (pProjectDO.getItemCount() > 0) {
// BigDecimal finshRatio = new BigDecimal(ObjectUtil.isNull(pProjectDO.getLabeled())? 0: pProjectDO.getLabeled())
//.divide(new BigDecimal(pProjectDO.getItemCount()), 2, RoundingMode.DOWN);
// apProjectDTO.setAnnoRatio(finshRatio.multiply(new BigDecimal(100)));
//}
ProjectList.add(apProjectDTO);
}
IPage<ApProjectDTO> pageRest = new Page<>();
pageRest.setRecords(ProjectList);
pageRest.setCurrent(apProjectDOIPage.getCurrent());
pageRest.setRecords(ProjectList);
pageRest.setPages(apProjectDOIPage.getPages());
pageRest.setSize(apProjectDOIPage.getSize());
pageRest.setTotal(apProjectDOIPage.getTotal());
return pageRest;
}

/**
* with down supplement Charge its It Method real current
*/

@Override
public void submitProject(Long projectId) throws BizException {
LambdaQueryWrapper<Image> imageLambdaQueryWrapper = new LambdaQueryWrapper<>();
imageLambdaQueryWrapper.eq(Image::getProjectId, projectId)
.eq(Image::getNeedReview, 1)
.ne(Image::getStatus, ImageStatus.PASS.getType());
List<Image> list = apImageService.list(imageLambdaQueryWrapper);
if (CollectionUtil.isNotEmpty(list)) {
throw new BizException("still has not Quality Check Image");
}

imageLambdaQueryWrapper.eq(Image::getNeedReview, 0)
.ne(Image::getStatus, ImageStatus.DONE.getType());
list = apImageService.list(imageLambdaQueryWrapper);
if (CollectionUtil.isNotEmpty(list)) {
throw new BizException("still has not Annotation Complete Complete Image");
}
ApProjectDO apProjectDO = new ApProjectDO();
apProjectDO.setId(projectId);
apProjectDO.setStatus(ProjectStatusEnum.FINSH.getCode());
projectMapper.updateById(apProjectDO);
}


private void validateProject(ApProjectDTO project) throws BizException {
if (ObjectUtil.isNull(project)) {
throw new BizException("Info cannot be empty");
}
if (StrUtil.isEmpty(project.getProjectName())) {
throw new BizException("Project Name cannot be empty");
}
List<Long> userGroupId = project.getUserGroupId();
// todo Verify User group
if (CollectionUtil.isNotEmpty(userGroupId)) {
for (Long groupId: userGroupId) {

}
}
LambdaUpdateWrapper<ApProjectDO> query = new LambdaUpdateWrapper<>();
query.eq(ApProjectDO::getProjectName, project.getProjectName())
.ne(ObjectUtil.isNotNull(project.getId()), ApProjectDO::getId, project.getId());
List<ApProjectDO> apProjectDOS = projectMapper.selectList(query);
if (CollectionUtils.isNotEmpty(apProjectDOS)) {
throw new BizException("Project Name not can re reply");
}
}

@Override
public Long getNextId() {
synchronized (this) {
log.info("MAXID {}",MAXID);
MAXID += 1;
}
return MAXID;
}

@Override
public ComputeAnnotationVO computeAnnotation(Long projectId, Integer type) {
// Query Project Image
LambdaQueryWrapper<Image> imageLambdaQueryWrapper = new LambdaQueryWrapper<>();
imageLambdaQueryWrapper.eq(Image::getProjectId, projectId);
List<Image> list = apImageService.list(imageLambdaQueryWrapper);
// not Image Direct connect Back
if (CollectionUtils.isEmpty(list)) {
return ComputeAnnotationVO.builder()
.count("0")
.annCount("0")
.noAnnoCount(0)
.backCount(0)
.finshRatio(BigDecimal.ZERO).build();
}
// Annotation Complete Complete
List<Image> annotations = list.stream().filter(image -> image.getStatus() == 5 || image.getStatus() == 11 || image.getStatus() == 15).collect(Collectors.toList());

// exit return
List<Image> backAnnotations = list.stream().filter(image -> image.getStatus() == 16).collect(Collectors.toList());


if (type == 2) {
Long userId = StpUtil.getLoginIdAsLong();
// Filter This Person Annotation Image
annotations = annotations.stream().filter(noAnnotation -> {
if ((ObjectUtil.isNotNull(noAnnotation.getLabelUserId()) && noAnnotation.getLabelUserId().compareTo(userId) == 0)
&& ImageStatus.labeled(noAnnotation.getStatus())) {
return true;
}
return false;
}).collect(Collectors.toList());
// Filter This Person not Annotation Image
backAnnotations = backAnnotations.stream().filter(backAnnotation -> {
if ((ObjectUtil.isNotNull(backAnnotation.getLabelUserId()) && backAnnotation.getLabelUserId().compareTo(userId) == 0)
&& ImageStatus.REJECTED.getType().compareTo(backAnnotation.getStatus()) == 0) {
return true;
}
return false;
}).collect(Collectors.toList());

}

// Get Submit id
List<Long> commitList = annotations.stream().map(image -> {
return image.getCommitId();
}).collect(Collectors.toList());

// Get Annotation Info
List<List<Long>> splitCommits = CollectionUtil.split(commitList, 1000);
BigDecimal detail = BigDecimal.ZERO;
for (List<Long> splitCommit: splitCommits) {
LambdaQueryWrapper<Annotation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
lambdaQueryWrapper.in(Annotation::getCommitId, splitCommit);
int partSum = apAnnotationService.count(lambdaQueryWrapper);
detail = detail.add(new BigDecimal(partSum));
}

String count = RelativeNumberFormatToolUtil.relativeNumberFormat(list.size(), RelativeNumberFormatToolUtil.PY);
String detailSum = RelativeNumberFormatToolUtil.relativeNumberFormat(detail, RelativeNumberFormatToolUtil.PY);
//String noAnnoCount = RelativeNumberFormatToolUtil.relativeNumberFormat(noAnnotations.size(), RelativeNumberFormatToolUtil.PY);
//String backCount = RelativeNumberFormatToolUtil.relativeNumberFormat(backAnnotations.size(), RelativeNumberFormatToolUtil.PY);

int noAnnotations = list.size() - annotations.size();
String annotationCount = RelativeNumberFormatToolUtil.relativeNumberFormat(annotations.size(), RelativeNumberFormatToolUtil.PY);

BigDecimal finshRatio = new BigDecimal(annotations.size()).divide(new BigDecimal(list.size()), 2, RoundingMode.DOWN);
return ComputeAnnotationVO.builder()
.count(count)
.annCount(annotationCount)
.noAnnoCount(noAnnotations)
.backCount(backAnnotations.size())
.annInfoCount(detailSum)
.finshRatio(finshRatio.multiply(new BigDecimal(100))).build();

}

@Override
public void initProject() {
Long maxId = projectMapper.selectMaxId();
if(ObjectUtil.isNotNull(maxId)){
MAXID = projectMapper.selectMaxId();
}else{
MAXID = 1L;
}
}

@Override
public void updataProjectStatusLableNum(Long projectId) throws BizException {
ApProjectDTO apProjectDTO = this.getpProjectById(projectId);
if (ObjectUtil.isNull(apProjectDTO)) {
return;
}
LambdaQueryWrapper<Image> lambdaQueryWrapper = new LambdaQueryWrapper<>();
lambdaQueryWrapper.eq(Image::getProjectId, projectId);
int count = apImageService.count(lambdaQueryWrapper);
apProjectDTO.setItemCount(count);
lambdaQueryWrapper.and(qr -> qr.in(Image::getStatus, ImageStatus.DONE.getType(),
ImageStatus.ALGORITHM.getType(), ImageStatus.IMPORT_PROGRESS_FINSH.getType())); // Annotation
int annCount = apImageService.count(lambdaQueryWrapper);
apProjectDTO.setLabeled(annCount);
this.updatepProjectById(apProjectDTO);
}

@Override
public List<ComputeAnnotationLableVO> computeAnnotationLableList(Long projectId) {
// Get Complete Complete Annotation Label
List<Image> finshAnnoList = apImageService.getFinshAnnoList(projectId);
if (CollectionUtils.isEmpty(finshAnnoList)) {
return new ArrayList<>();
}
// Get Submit id
List<Long> commitList = finshAnnoList.stream().map(image -> {
return image.getCommitId();
}).collect(Collectors.toList());

// Get Annotation Info
List<List<Long>> splitCommits = CollectionUtil.split(commitList, 1000);
List<Annotation> list = new ArrayList<>();
for (List<Long> splitCommit: splitCommits) {
LambdaQueryWrapper<Annotation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
lambdaQueryWrapper.in(Annotation::getCommitId, splitCommit);
list.addAll(apAnnotationService.list(lambdaQueryWrapper));
}

int sumCount = list.size();

// by Label Category
Map<String, List<Annotation>> annotationListMap = list.stream().collect(Collectors.groupingBy(Annotation::getTagName));

int[] arr = new int[annotationListMap.size()];

int i = 0;
for (Map.Entry<String, List<Annotation>> stringListEntry: annotationListMap.entrySet()) {
arr[i] = stringListEntry.getValue().size();
i++;
}
i = 0;
List<ComputeAnnotationLableVO> computeAnnotationLableVOList = new ArrayList<>();
for (Map.Entry<String, List<Annotation>> stringListEntry: annotationListMap.entrySet()) {
double percentValue = RelativeNumberFormatToolUtil.getPercentValue(arr, RelativeNumberFormatToolUtil.realTenThousand(new BigDecimal(sumCount)).intValue(), i, 0);
computeAnnotationLableVOList.add(ComputeAnnotationLableVO.builder()
.label(stringListEntry.getKey()).annCount(stringListEntry.getValue().size())
.annRatio(new BigDecimal(percentValue)).build());
i++;

}


return computeAnnotationLableVOList;
}

@Override
public ComputeAnnotationReviewVO annotationReview(Long projectId) {

List<Image> reviewAnnoList = apImageService.getReviewAnnoList(projectId);
if (CollectionUtils.isEmpty(reviewAnnoList)) {
return ComputeAnnotationReviewVO.builder().count("0")
.noReviewCount("0")
.backCount(0)
.reviewCount(0)
.finshRatio(BigDecimal.ZERO).build();
}
List<Image> finshReview = reviewAnnoList.stream().filter(review -> {
return Integer.valueOf(15).compareTo(review.getStatus()) == 0;
}).collect(Collectors.toList());

List<Image> backReview = reviewAnnoList.stream().filter(review -> {
return Integer.valueOf(16).compareTo(review.getStatus()) == 0;
}).collect(Collectors.toList());
int noReview = reviewAnnoList.size() - finshReview.size();

BigDecimal finshRatio = new BigDecimal(finshReview.size()).divide(new BigDecimal(reviewAnnoList.size()), 2, RoundingMode.DOWN);

String count = RelativeNumberFormatToolUtil.relativeNumberFormat(finshReview.size() + backReview.size(), RelativeNumberFormatToolUtil.PY);
String noReviewS = RelativeNumberFormatToolUtil.relativeNumberFormat(noReview, RelativeNumberFormatToolUtil.PY);
return ComputeAnnotationReviewVO.builder().count(count)
.noReviewCount(noReviewS)
.backCount(backReview.size())
.reviewCount(finshReview.size())
.finshRatio(finshRatio.multiply(new BigDecimal(100))).build();
}

@Override
public ProjectEfficiencyVO projectEfficiency(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {
if(StringUtils.isEmpty(apProjectStatisticsQueryDTO.getStartTime())||StringUtils.isEmpty(apProjectStatisticsQueryDTO.getEndTime())){
throw new BizException("Date cannot be empty");
}
List<String> detHashList = getDetHashList(apProjectStatisticsQueryDTO.getStartTime(), apProjectStatisticsQueryDTO.getEndTime());
Long teamId = apProjectStatisticsQueryDTO.getTeamId();
List<Long> projectIds = new ArrayList<>();
boolean group = false;
Long projectId = apProjectStatisticsQueryDTO.getProjectId();
// Annotation group not Is Empty, Need check find Annotation group Param and Project
if (ObjectUtil.isNotNull(teamId)) {
projectIds = getPiojectIds(teamId);
if (CollectionUtil.isEmpty(projectIds)) {
return new ProjectEfficiencyVO();
}
if(ObjectUtil.isNotNull(projectId)){
if(projectIds.contains(projectId)){
projectIds = new ArrayList<>();
projectIds.add(projectId);
}else{
return new ProjectEfficiencyVO();
}
}
group = true;
} else {
if (ObjectUtil.isNotNull(projectId)) {
projectIds.add(projectId);
group = true;
}
}
List<EfficiencyImageVO> efficiencyImageListVO = new ArrayList<>();
List<EfficiencyReviewVO> efficiencyReviewListVO = new ArrayList<>();
for (String date: detHashList) {
EfficiencyImageVO efficiencyImageVO = new EfficiencyImageVO();
EfficiencyReviewVO efficiencyReviewVO = new EfficiencyReviewVO();
// Query image
/*LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(group, Image::getProjectId, projectIds)
.and(qr ->qr.in(Image::getStatus, ImageStatus.DONE.getType(),ImageStatus.REVIEWING.getType()
,ImageStatus.PASS.getType()))
.apply(true,"TO_DAYS('"+ date +"')-TO_DAYS(update_at) = 0");*/

LambdaQueryWrapper<Commit> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(group, Commit::getProjectId, projectIds)
.apply(true,"TO_DAYS('"+ date +"')-TO_DAYS(created_at) = 0");
List<Commit> commits = apCommitMapper.selectList(queryWrapper);
// int imageCount = apImageService.count(queryWrapper);
// int annCount = getAnnDetailCount(projectIds, group, date);
efficiencyImageVO.setImageNum(commits.size());
int annoSum = commits.stream().mapToInt(Commit::getAnnotationCount).sum();
efficiencyImageVO.setAnnNum(annoSum);
efficiencyImageVO.setDate(date);
efficiencyImageListVO.add(efficiencyImageVO);

// Query Quality Check
LambdaQueryWrapper<Review> queryReviewWrapper = new LambdaQueryWrapper<>();
queryReviewWrapper.in(group, Review::getProjectId, projectIds)
.in(Review::getReviewAction, ReviewAction.PASS.getType(), ReviewAction.REJECT.getType())
.apply(true,"TO_DAYS('"+ date +"')-TO_DAYS(created_at) = 0");
List<Review> reviews = apReviewMapper.selectList(queryReviewWrapper);
Map<Integer, List<Review>> reviewMap = reviews.stream().collect(Collectors.groupingBy(Review::getReviewAction));
efficiencyReviewVO.setDate(date);
List<Review> backReviews = reviewMap.get(ReviewAction.REJECT.getType());
efficiencyReviewVO.setBackNum(CollectionUtil.isEmpty(backReviews)? 0: backReviews.size());
List<Review> passReviews = reviewMap.get(ReviewAction.PASS.getType());
efficiencyReviewVO.setPassNum(CollectionUtil.isEmpty(passReviews)? 0: passReviews.size());

efficiencyReviewListVO.add(efficiencyReviewVO);
}
return ProjectEfficiencyVO.builder().imageVOList(efficiencyImageListVO).reviewVOList(efficiencyReviewListVO).build();

}


private int getAnnDetailCount(List<Long> projectIds, boolean group, String date) {
// Query Annotation
LambdaQueryWrapper<Annotation> queryAnnoWrapper = new LambdaQueryWrapper<>();
queryAnnoWrapper.in(group, Annotation::getProjectId, projectIds)
.apply(true,"TO_DAYS('"+ date +"')-TO_DAYS(created_at) = 0");
return apAnnotationService.count(queryAnnoWrapper);
}

@Override
public IPage<ProjectStatisticsVO> projectStatistics(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {
if(StringUtils.isEmpty(apProjectStatisticsQueryDTO.getStartTime())||StringUtils.isEmpty(apProjectStatisticsQueryDTO.getEndTime())){
throw new BizException("Date cannot be empty");
}
Long projectId = apProjectStatisticsQueryDTO.getProjectId();
List<Long> projectIds = new ArrayList<>();
List<Long> userId = new ArrayList<>();
if (ObjectUtil.isNotNull(projectId)) {
projectIds.add(projectId);
}
Long teamId = apProjectStatisticsQueryDTO.getTeamId();

if (ObjectUtil.isNotNull(teamId)) {
projectIds = new ArrayList<>();
LambdaQueryWrapper<UserTeamRelationships> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(UserTeamRelationships::getTeamId, teamId);
List<UserTeamRelationships> userTeamRelationships = apUserTeamRelationshipsMapper.selectList(queryWrapper);

if (CollectionUtil.isEmpty(userTeamRelationships)) {
return new Page<>();
}
userId = userTeamRelationships.stream().map(teamRelationships -> teamRelationships.getUserId()).collect(Collectors.toList());
}
List<Commit> commitHistory = getCommitHistory(apProjectStatisticsQueryDTO, projectIds, userId);
if(CollectionUtil.isEmpty(commitHistory)){
return new Page<>();
}
Set<Long> userIds = commitHistory.stream().map(commit -> commit.getUserId()).collect(Collectors.toSet());
Map<Long, List<Commit>> commitUserMap = commitHistory.stream().collect(Collectors.groupingBy(Commit::getUserId));
ApAccountQueryDTO apAccountQueryDTO =new ApAccountQueryDTO();
apAccountQueryDTO.setPageNum(apProjectStatisticsQueryDTO.getPageNum());
apAccountQueryDTO.setPageSize(apProjectStatisticsQueryDTO.getPageSize());
apAccountQueryDTO.setUserIds(userIds);
IPage<ApAccountDTO> accountPage = accountService.findAccountPage(apAccountQueryDTO);
List<ApAccountDTO> records = accountPage.getRecords();
List<ProjectStatisticsVO> projecstatisticsVOS = records.stream().map(apAccountDTO -> {
ProjectStatisticsVO projecstatisticsVO = new ProjectStatisticsVO();
projecstatisticsVO.setUserName(apAccountDTO.getName());
List<Commit> commits = commitUserMap.get(apAccountDTO.getId());
projecstatisticsVO.setAnnoNum(commits.size());
int annoSum = commits.stream().mapToInt(Commit::getAnnotationCount).sum();
projecstatisticsVO.setAnnoDetailNum(annoSum);
List<Long> commitIds = commits.stream().map(commit -> commit.getId()).collect(Collectors.toList());
List<List<Long>> commitSplits = CollectionUtil.split(commitIds, 1000);


// Query Quality Check
List<Review> reviewList = new ArrayList<>();
List<Annotation> annotations = new ArrayList<>();

for (List<Long> commitSplit: commitSplits) {
LambdaQueryWrapper<Annotation> imageLambdaQueryWrapper = new LambdaQueryWrapper<>();
imageLambdaQueryWrapper.in(Annotation::getCommitId, commitSplit);
imageLambdaQueryWrapper.eq(Annotation::getTagName, GlobConstant.INVALID_TAG);
List<Annotation> list = apAnnotationService.list(imageLambdaQueryWrapper);
annotations.addAll(list);
LambdaQueryWrapper<Review> queryReviewWrapper = new LambdaQueryWrapper<>();
queryReviewWrapper.in(Review::getCommitId, commitSplit);
List<Review> reviews = apReviewMapper.selectList(queryReviewWrapper);
reviewList.addAll(reviews);
}
projecstatisticsVO.setInvalidNum(annotations.size());
Map<Integer, List<Review>> reviewMap = reviewList.stream().collect(Collectors.groupingBy(Review::getReviewAction));
List<Review> backReviews = reviewMap.get(ReviewAction.REJECT.getType());
List<Review> passReviews = reviewMap.get(ReviewAction.PASS.getType());
if(CollectionUtil.isNotEmpty(passReviews)){
projecstatisticsVO.setReviewNum(passReviews.size());
}
if(CollectionUtil.isNotEmpty(backReviews)){
projecstatisticsVO.setBackNum(backReviews.size());
}

return projecstatisticsVO;
}).collect(Collectors.toList());
IPage<ProjectStatisticsVO> page = new Page<>();
page.setTotal(accountPage.getTotal());
page.setRecords(projecstatisticsVOS);
return page;

}

private List<Annotation> getAnnotationsDetailCount(List<List<Long>> split) {
// Get Annotation Info
List<List<Long>> splitCommits = split;
List<Annotation> finshAnnoList = new ArrayList<>();
for (List<Long> splitCommit: splitCommits) {
LambdaQueryWrapper<Annotation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
lambdaQueryWrapper.in(Annotation::getCommitId, splitCommit);
finshAnnoList.addAll(apAnnotationService.list(lambdaQueryWrapper));
}
return finshAnnoList;
}

private Map<Long, List<Image>> getLastAnnoImages(List<Commit> commitList) {

if (CollectionUtil.isEmpty(commitList)) {
return null;
}
Set<Long> commitId = commitList.stream().map(commit -> commit.getId()).collect(Collectors.toSet());

LambdaQueryWrapper<Image> imageQueryWrapper = new LambdaQueryWrapper<>();
imageQueryWrapper.in(Image::getCommitId, commitId);
// imageQueryWrapper.in(CollectionUtil.isNotEmpty(projectIds),Image::getProjectId,projectIds);
// imageQueryWrapper.ne(Image::getLabelUserId,0L);
// if(ObjectUtil.isNotNull(apProjectStatisticsQueryDTO.getUserId())){
// imageQueryWrapper.eq(Image::getLabelUserId,apProjectStatisticsQueryDTO.getUserId());
//}
// imageQueryWrapper.eq(Image::getStatus, ImageStatus.DONE.getType());
// imageQueryWrapper.apply("DATE_FORMAT(updated_at,'%Y-%m-%d') BETWEEN'"+apProjectStatisticsQueryDTO.getStartTime() +
//"' AND'"+ apProjectStatisticsQueryDTO.getEndTime() +"'");

List<Image> list = apImageService.list(imageQueryWrapper);
Map<Long, List<Image>> imagesMap = list.stream().collect(Collectors.groupingBy(Image::getProjectId));
return imagesMap;
}

private List<Commit> getCommits(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO, List<Long> projectIds) {
LambdaQueryWrapper<Commit> commitLambdaQueryWrapper = new LambdaQueryWrapper<>();
commitLambdaQueryWrapper.ne(Commit::getUserId, 0L);
if (ObjectUtil.isNotNull(apProjectStatisticsQueryDTO.getUserId())) {
commitLambdaQueryWrapper.eq(Commit::getUserId, apProjectStatisticsQueryDTO.getUserId());
}
commitLambdaQueryWrapper.in(CollectionUtil.isNotEmpty(projectIds), Commit::getProjectId, projectIds);
commitLambdaQueryWrapper.apply("DATE_FORMAT(created_at,'%Y-%m-%d') BETWEEN'"+ apProjectStatisticsQueryDTO.getStartTime() +
"' AND'"+ apProjectStatisticsQueryDTO.getEndTime() +"'");

return apCommitMapper.selectList(commitLambdaQueryWrapper);
}

private List<Long> getProjectIdFromImages(Map<Long, List<Commit>> commitMap) {
List<Long> projectResult = new ArrayList<>();
for (Map.Entry<Long, List<Commit>> longListEntry: commitMap.entrySet()) {
projectResult.add(longListEntry.getKey());
}
return projectResult;
}

private List<Review> getReviewList(Long projectId, ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) {
LambdaQueryWrapper<Review> reviewLambdaQueryWrapper = new LambdaQueryWrapper<>();
reviewLambdaQueryWrapper.eq(Review::getProjectId, projectId);
reviewLambdaQueryWrapper.apply("DATE_FORMAT(created_at,'%Y-%m-%d') BETWEEN'"+ apProjectStatisticsQueryDTO.getStartTime() +
"' AND'"+ apProjectStatisticsQueryDTO.getEndTime() +"'");
return apReviewMapper.selectList(reviewLambdaQueryWrapper);
}

@Override
public ProjectEfficiencyVO teamEfficiency(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {
if(StringUtils.isEmpty(apProjectStatisticsQueryDTO.getStartTime())||StringUtils.isEmpty(apProjectStatisticsQueryDTO.getEndTime())){
throw new BizException("Date cannot be empty");
}
Long loginId = StpUtil.getLoginIdAsLong();
List<String> detHashList = getDetHashList(apProjectStatisticsQueryDTO.getStartTime(), apProjectStatisticsQueryDTO.getEndTime());
List<Long> userIds = new ArrayList<>();
if (ObjectUtil.isNotNull(apProjectStatisticsQueryDTO.getUserId())) {
userIds.add(apProjectStatisticsQueryDTO.getUserId());
}else{
List<ApAccountDTO> allTeamUser = accountService.findAllTeamUser(loginId);
userIds = allTeamUser.stream().map(user -> user.getId()).collect(Collectors.toList());
}
if (CollectionUtil.isEmpty(userIds)&&loginId.compareTo(1L)!=0) {
return new ProjectEfficiencyVO();
}
List<EfficiencyImageVO> efficiencyImageListVO = new ArrayList<>();
List<EfficiencyReviewVO> efficiencyReviewListVO = new ArrayList<>();
for (String date: detHashList) {
EfficiencyImageVO efficiencyImageVO = new EfficiencyImageVO();
EfficiencyReviewVO efficiencyReviewVO = new EfficiencyReviewVO();


LambdaQueryWrapper<Commit> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(CollectionUtil.isNotEmpty(userIds), Commit::getUserId, userIds)
.apply(true,"TO_DAYS('"+ date +"')-TO_DAYS(created_at) = 0");
List<Commit> commits = apCommitMapper.selectList(queryWrapper);
efficiencyImageVO.setImageNum(commits.size());
int annoSum = commits.stream().mapToInt(Commit::getAnnotationCount).sum();
efficiencyImageVO.setAnnNum(annoSum);
efficiencyImageVO.setDate(date);
efficiencyImageListVO.add(efficiencyImageVO);
Set<Long> commitIds = commits.stream().map(commit -> commit.getId()).collect(Collectors.toSet());
List<List<Long>> commitSplits = CollectionUtil.split(commitIds, 1000);
// Query Quality Check
List<Review> reviewList = new ArrayList<>();
for (List<Long> commitSplit: commitSplits) {
LambdaQueryWrapper<Review> queryReviewWrapper = new LambdaQueryWrapper<>();
queryReviewWrapper.in(Review::getCommitId, commitSplit)
.in(Review::getReviewAction, ReviewAction.PASS.getType(), ReviewAction.REJECT.getType())
.apply(true,"TO_DAYS('"+ date +"')-TO_DAYS(created_at) = 0");
List<Review> reviews = apReviewMapper.selectList(queryReviewWrapper);
reviewList.addAll(reviews);
}

Map<Integer, List<Review>> reviewMap = reviewList.stream().collect(Collectors.groupingBy(Review::getReviewAction));
efficiencyReviewVO.setDate(date);
List<Review> backReviews = reviewMap.get(ReviewAction.REJECT.getType());
efficiencyReviewVO.setBackNum(CollectionUtil.isEmpty(backReviews)? 0: backReviews.size());
List<Review> passReviews = reviewMap.get(ReviewAction.PASS.getType());
efficiencyReviewVO.setPassNum(CollectionUtil.isEmpty(passReviews)? 0: passReviews.size());

efficiencyReviewListVO.add(efficiencyReviewVO);
}
return ProjectEfficiencyVO.builder().imageVOList(efficiencyImageListVO).reviewVOList(efficiencyReviewListVO).build();
}

/**
* Project id
* Team id -> User id
* Get Submit Record - Filter User
* Iterate User
* User Submit Info
* By Submit Info Get Invalid Image
* By Submit Info Get Quality Check Info
*
* @param apProjectStatisticsQueryDTO
* @return
*/
@Override
public IPage<ProjectStatisticsVO> teamStatistics(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO) throws BizException {
if(StringUtils.isEmpty(apProjectStatisticsQueryDTO.getStartTime())||StringUtils.isEmpty(apProjectStatisticsQueryDTO.getEndTime())){
throw new BizException("Date cannot be empty");
}
Long loginId = StpUtil.getLoginIdAsLong();
// Get Team
LambdaQueryWrapper<UserTeam> lambdaQueryWrapper =new LambdaQueryWrapper<>();
lambdaQueryWrapper.eq(UserTeam::getUserId,loginId);
List<UserTeam> list = apUserTeamService.list(lambdaQueryWrapper);
if(CollectionUtil.isEmpty(list)){
return new Page<>();
}
List<Long> projectIds = getPiojectIds(list.get(0).getId());
if (CollectionUtil.isEmpty(projectIds)&&loginId.compareTo(1L)!=0) {
return new Page<>();
}
List<Commit> commitList = getCommits(apProjectStatisticsQueryDTO, projectIds);

//Map<Long, List<Image>> lastAnnoImages = getLastAnnoImages(commitList);
Map<Long, List<Commit>> commitMap = commitList.stream().collect(Collectors.groupingBy(Commit::getProjectId));
if (CollectionUtil.isEmpty(commitMap)) {
return new Page<>();
}
List<Long> projectResults = getProjectIdFromImages(commitMap);


ApProjectQueryDTO apProjectQueryDTO = new ApProjectQueryDTO();
apProjectQueryDTO.setProjectIds(projectResults);
apProjectQueryDTO.setPageNum(apProjectStatisticsQueryDTO.getPageNum());
apProjectQueryDTO.setPageSize(apProjectStatisticsQueryDTO.getPageSize());
//apProjectQueryDTO.setStartTime(apProjectStatisticsQueryDTO.getStartTime());
//apProjectQueryDTO.setEndTime(apProjectStatisticsQueryDTO.getEndTime());
IPage<ApProjectDTO> apProjectDTOIPage = findpProjectPage(apProjectQueryDTO);
List<ApProjectDTO> records = apProjectDTOIPage.getRecords();
List<ProjectStatisticsVO> projecstatisticsVOList = records.stream().map(apProjectDTO -> {
ProjectStatisticsVO projecstatisticsVO = new ProjectStatisticsVO();
projecstatisticsVO.setProjectName(apProjectDTO.getProjectName());
//List<Image> images = lastAnnoImages.get(apProjectDTO.getId());
List<Commit> commits = commitMap.get(apProjectDTO.getId());
Set<Long> commitIds = commits.stream().map(commit -> commit.getId()).collect(Collectors.toSet());
List<Annotation> finshAnnoList = getAnnotationsDetailCount(CollectionUtil.split(commitIds, 1000));
// Annotation Image
projecstatisticsVO.setAnnoNum(commits.size());
// Annotation Box
projecstatisticsVO.setAnnoDetailNum(finshAnnoList.size());
List<Annotation> labelfreeInvalidDataList = finshAnnoList.stream().filter(annotation ->
GlobConstant.INVALID_TAG.equals(annotation.getTagName())).collect(Collectors.toList());
Set<Long> labelfreeInvalidSet = labelfreeInvalidDataList.stream().map(lable -> lable.getImageId()).collect(Collectors.toSet());
// Invalid Image
projecstatisticsVO.setInvalidNum(labelfreeInvalidSet.size());

// Quality Check
List<Review> reviewList = getReviewList(apProjectDTO.getId(), apProjectStatisticsQueryDTO);
projecstatisticsVO.setReviewNum(reviewList.size());

// Quality Check not combine Grid
List<Review> noPassReviews = reviewList.stream().filter(review -> review.getReviewAction()!= 1).collect(Collectors.toList());
projecstatisticsVO.setBackNum(noPassReviews.size());
return projecstatisticsVO;
}).collect(Collectors.toList());
IPage<ProjectStatisticsVO> page = new Page<>();
page.setTotal(apProjectDTOIPage.getTotal());
page.setRecords(projecstatisticsVOList);
return page;
}

private List<Long> getPiojectIds(Long teamId){
List<UserGroup> userGroupList = getGroupList(teamId);
if(CollectionUtil.isEmpty(userGroupList)){
return null;
}
List<Long> groupIdList = userGroupList.stream().map(userGroup -> userGroup.getId()).collect(Collectors.toList());
List<UserGroupProjectDO> groupProjectList = getGroupProjectList(groupIdList);
return groupProjectList.stream().map(userGroupProjectDO -> userGroupProjectDO.getProjectId()).collect(Collectors.toList());
}
private List<UserGroupProjectDO> getGroupProjectList(List<Long> groupIdList){
LambdaQueryWrapper<UserGroupProjectDO> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(UserGroupProjectDO::getGroupId,groupIdList);
return userGroupProjectMapper.selectList(queryWrapper);
}
private List<UserGroup> getGroupList(Long teamId){
LambdaQueryWrapper<UserGroup> lambdaQueryWrapper = new LambdaQueryWrapper<>();
lambdaQueryWrapper.eq(UserGroup::getTeamId,teamId);
return apUserGroupService.list(lambdaQueryWrapper);
}
private List<Commit> getCommitHistory(ApProjectStatisticsQueryDTO apProjectStatisticsQueryDTO, List<Long> projectIds, List<Long> userId) {
LambdaQueryWrapper<Commit> commitLambdaQueryWrapper = new LambdaQueryWrapper<>();
commitLambdaQueryWrapper.in(CollectionUtil.isNotEmpty(userId),Commit::getUserId,userId);
commitLambdaQueryWrapper.in(CollectionUtil.isNotEmpty(projectIds),Commit::getProjectId,projectIds);
commitLambdaQueryWrapper.apply("DATE_FORMAT(created_at,'%Y-%m-%d') BETWEEN'"+ apProjectStatisticsQueryDTO.getStartTime() +
"' AND'"+ apProjectStatisticsQueryDTO.getEndTime() +"'");
return apCommitMapper.selectList(commitLambdaQueryWrapper);
}

/**
* Get Two Date in All Date, and Convert for table after Concat
*
* @param begin Format:yyyy-MM-dd
* @param end Format:yyyy-MM-dd
* @return Format:MM_dd
*/
public static List<String> getDetHashList(String begin, String end) {
List<String> hashList = new ArrayList<>();
Date bDate = DateUtil.parse(begin, DatePattern.NORM_DATE_PATTERN);//yyyy-MM-dd
Date eDate = DateUtil.parse(end, DatePattern.NORM_DATE_PATTERN);
List<DateTime> dateList = DateUtil.rangeToList(bDate, eDate, DateField.DAY_OF_YEAR);// Create Date Range Generate Device
String hash = null;
for (DateTime dt: dateList) {
hash = DateUtil.formatDate(dt);
hashList.add(hash);
}
return hashList;
}

private Integer getNoPassImages(Long projectId){
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId,projectId)
.eq(Image::getNeedReview,1)
.ne(Image::getStatus,ImageStatus.PASS.getType());
return apImageService.count(queryWrapper);
}

private Integer getReviewCount(Long projectId){
LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Review::getProjectId,projectId);
return reviewMapper.selectList(queryWrapper).size();
}
public static void main(String[] args) {
Long id = null;
id += 1;
System.out.println(id);
System.out.println(RandomUtil.randomString(8));

}

// @PostConstruct
// public void init(){
//
// System.out.println(MAXID);
//}
}
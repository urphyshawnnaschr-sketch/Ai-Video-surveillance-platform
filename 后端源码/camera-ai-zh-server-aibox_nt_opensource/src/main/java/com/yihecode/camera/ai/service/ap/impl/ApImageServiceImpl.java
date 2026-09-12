package com.yihecode.camera.ai.service.ap.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yihecode.camera.ai.entity.ap.Annotation;
import com.yihecode.camera.ai.entity.ap.Commit;
import com.yihecode.camera.ai.entity.ap.Image;
import com.yihecode.camera.ai.entity.ap.Review;
import com.yihecode.camera.ai.enums.ap.ImageStatus;
import com.yihecode.camera.ai.enums.ap.ReviewAction;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.mapper.ap.ApAnnotationMapper;
import com.yihecode.camera.ai.mapper.ap.ApCommitMapper;
import com.yihecode.camera.ai.mapper.ap.ApImageMapper;
import com.yihecode.camera.ai.mapper.ap.ApReviewMapper;
import com.yihecode.camera.ai.service.ap.ApAnnotationService;
import com.yihecode.camera.ai.service.ap.ApImageService;
import com.yihecode.camera.ai.web.ap.vo.AnnotationVo;
import com.yihecode.camera.ai.web.ap.vo.CommitVo;
import com.yihecode.camera.ai.web.ap.vo.ImageSearchRequestVo;
import com.yihecode.camera.ai.web.ap.vo.ReviewVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ApImageServiceImpl extends ServiceImpl<ApImageMapper, Image> implements ApImageService {

    @Autowired
    private ApImageMapper apImageMapper;

    @Autowired
    private ApCommitMapper apCommitMapper;

    @Autowired
    private ApAnnotationMapper apAnnotationMapper;

    @Autowired
    private ApAnnotationService apAnnotationService;

    @Autowired
    private ApReviewMapper apReviewMapper;

    @Override
    public List<Image> list(ImageSearchRequestVo request, boolean needReviewInfo) {

        List<Image> imageList = apImageMapper.selectWithLabel(request);

        fillCommit(imageList, needReviewInfo);

        return imageList;
    }

    @Override
    public Long count(ImageSearchRequestVo request) {
        return apImageMapper.countWithLabel(request);
    }

    @Override
    public List<Image> assignFromRejectAndAssignedImages(Long userId, Long projectId, Integer count, Integer timeout) {
        //Query image
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId);
queryWrapper.eq(Image::getLabelUserId, userId);
queryWrapper.in(Image::getStatus, ImageStatus.REJECTED.getType(), ImageStatus.ING.getType());
queryWrapper.ge(Image::getExpiredAt, new Date());
queryWrapper.last("limit"+ count);

List<Image> imageList = apImageMapper.selectList(queryWrapper);
if (CollectionUtils.isEmpty(imageList)) {
return imageList;
}

// Lock
List<Image> lockedImageList = new ArrayList<>();
for (Image image: imageList) {
int result = this.lockImage(image, userId, timeout);
if (result > 0) {
lockedImageList.add(image);
}
}
// supplement Charge Before Submit Info
fillCommit(lockedImageList, false);

return lockedImageList;
}

@Override
public List<Image> assignFromOpenImages(Long userId, Long projectId, Integer count, Integer timeout) {
int n = 3; // self Loop Three sub
List<Image> lockedImageList = new ArrayList<>();
while (n-->0) {
// Query image
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId);
queryWrapper.and(qr ->qr.in(Image::getStatus, ImageStatus.WAIT.getType(), ImageStatus.ALGORITHM.getType(), ImageStatus.IMPORT_PROGRESS_FINSH.getType()) // not Lead Get
.or(qqr->qqr.in(Image::getStatus, ImageStatus.REJECTED.getType(), ImageStatus.ING.getType()).lt(Image::getExpiredAt, new Date()))); // Expired Lock
queryWrapper.last("limit"+ count);

List<Image> imageList = apImageMapper.selectList(queryWrapper);
if (CollectionUtils.isEmpty(imageList)) {
break;
}

int lockedFailCount = 0;
// Lock
for (Image image: imageList) {

int result = this.lockImage(image, userId, timeout);
if (result > 0) {
lockedImageList.add(image);
} else {
lockedFailCount ++;
}
}

if (lockedFailCount == 0) {
break;
}

count = lockedFailCount;
}

// supplement Charge Before Submit Info
fillCommit(lockedImageList, false);

return lockedImageList;
}

@Override
public List<Image> assignForReviewFromAssignedImages(Long userId, Long projectId, Integer count, Integer timeout) {
// Query image
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId);
queryWrapper.eq(Image::getReviewUserId, userId);
queryWrapper.eq(Image::getNeedReview, 1);
queryWrapper.in(Image::getStatus, ImageStatus.REVIEWING.getType());
queryWrapper.ge(Image::getExpiredAt, new Date());
queryWrapper.last("limit"+ count);

List<Image> imageList = apImageMapper.selectList(queryWrapper);
if (CollectionUtils.isEmpty(imageList)) {
return imageList;
}

// Lock
List<Image> lockedImageList = new ArrayList<>();
for (Image image: imageList) {
int result = this.lockImageForReview(image, userId, timeout);
if (result > 0) {
lockedImageList.add(image);
}
}
// supplement Charge Before Submit Info
fillCommit(lockedImageList, false);

return lockedImageList;
}

@Override
public List<Image> assignForReviewFromOpenImages(Long userId, Long projectId, Integer count, Integer timeout) {
int n = 3; // self Loop Three sub
List<Image> lockedImageList = new ArrayList<>();
while (n-->0) {
// Query image
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId);
queryWrapper.eq(Image::getNeedReview, 1);
queryWrapper.and(qr ->qr.in(Image::getStatus, ImageStatus.DONE.getType()) // not Lead Get
.or(qqr->qqr.in(Image::getStatus, ImageStatus.REVIEWING.getType()).lt(Image::getExpiredAt, new Date()))); // Expired Lock
queryWrapper.last("limit"+ count);

List<Image> imageList = apImageMapper.selectList(queryWrapper);
if (CollectionUtils.isEmpty(imageList)) {
break;
}

int lockedFailCount = 0;
// Lock
for (Image image: imageList) {

int result = this.lockImageForReview(image, userId, timeout);
if (result > 0) {
lockedImageList.add(image);
} else {
lockedFailCount ++;
}
}

if (lockedFailCount == 0) {
break;
}

count = lockedFailCount;
}

// supplement Charge Before Submit Info
fillCommit(lockedImageList, false);

return lockedImageList;
}

private int lockImage(Image image, Long userId, Integer timeout) {
Date now = new Date();
image.setAssignedAt(now);
image.setExpiredAt(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(timeout)));
image.setLabelUserId(userId);
image.setStatus(ImageStatus.ING.getType());

return apImageMapper.updateById(image);
}

private int lockImageForReview(Image image, Long userId, Integer timeout) {
Date now = new Date();
image.setAssignedAt(now);
image.setExpiredAt(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(timeout)));
image.setReviewUserId(userId);
image.setStatus(ImageStatus.REVIEWING.getType());

return apImageMapper.updateById(image);
}

private Image selectByIdAndProjectId(Long projectId, Long imageId) {
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId);
queryWrapper.eq(Image::getId, imageId);
return apImageMapper.selectOne(queryWrapper);
}

@Override
public Image checkCommit(Long projectId, Long imageId, Long userId) throws BizException {
Image image = selectByIdAndProjectId(projectId, imageId);
if (image == null) {
throw new BizException("Image does not exist");
}

// Whether can with Operation
if (ImageStatus.REVIEWING.getType().equals(image.getStatus())) {
throw new BizException("Quality Check in not can Submit");
}
Date now = new Date();
if (ImageStatus.ING.getType().equals(image.getStatus()) &&!image.getLabelUserId().equals(userId) && image.getExpiredAt().after(now)) {
throw new BizException("by Other Person Lock");
}

// Lock to This Person At Least still has 10min
if (ImageStatus.ING.getType().equals(image.getStatus()) && image.getLabelUserId().equals(userId) &&
image.getExpiredAt().after(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(10)))) {
return image;
}

// can Operation, not Lock First Lock 10min
if (this.lockImage(image, userId, 10)!= 1) {
throw new BizException("Lock Failed");
}

return image;
}

@Override
@Transactional(rollbackFor = Exception.class)
public void commit(CommitVo commitVo, Image image, Long userId) throws BizException {

// Record Annotation Result, can with Modify Annotation Result
// need not need delete - can with not delete, Use most new commitId Remove Relate just Line
// commit
Commit commit = new Commit();
commit.setUserId(userId);
commit.setImageId(commitVo.getImageId());
commit.setProjectId(image.getProjectId());
commit.setIsValid(commitVo.getIsValid());
commit.setAnnotationCount(commitVo.getAnnotations().size());
apCommitMapper.insert(commit);
// annotation
List<Annotation> annotations = buildAnnotation(commit, commitVo.getAnnotations());
apAnnotationService.saveBatch(annotations);
// image
image.setCommitId(commit.getId());
image.setStatus(ImageStatus.DONE.getType());

// todo Record Whether and Algorithm pre Annotation Result Consistent
if (apImageMapper.updateById(image)!= 1) {
throw new BizException("Submit Failed");
}
}

private List<Annotation> buildAnnotation(Commit commit, List<AnnotationVo> annotationVos) {
List<Annotation> annotations = new ArrayList<>();
for (AnnotationVo annotationVo: annotationVos) {
Annotation annotation = new Annotation();
annotation.setCommitId(commit.getId());
annotation.setProjectId(commit.getProjectId());
annotation.setImageId(commit.getImageId());
annotation.setTagName(annotationVo.getTagName());
annotation.setAnnotationType(annotationVo.getAnnotationType());
annotation.setAnnotation(annotationVo.getAnnotation());
annotations.add(annotation);
}
return annotations;
}

@Override
public Image checkReview(Long projectId, Long imageId, Long userId, Long commitId) throws BizException {
Image image = selectByIdAndProjectId(projectId, imageId);
if (image == null) {
throw new BizException("Image does not exist");
}
if (!image.getCommitId().equals(commitId)) {
throw new BizException("Annotation has modify Dynamic, Please Refresh Retry");
}
// Whether can with Operation
if (ImageStatus.toLabeled(image.getStatus())) {
throw new BizException("not Annotation not can Quality Check");
}
if (ImageStatus.ING.getType().equals(image.getStatus())) {
throw new BizException("Annotation in not can Quality Check");
}
Date now = new Date();
if (ImageStatus.REVIEWING.getType().equals(image.getStatus()) &&!image.getLabelUserId().equals(userId) && image.getExpiredAt().after(now)) {
throw new BizException("by Other Person Lock");
}

// Lock to This Person At Least still has 10min
if (ImageStatus.REVIEWING.getType().equals(image.getStatus()) && image.getLabelUserId().equals(userId) &&
image.getExpiredAt().after(new Date(now.getTime() + TimeUnit.MINUTES.toMillis(10)))) {
return image;
}

// can Operation, not Lock First Lock 10min
if (this.lockImageForReview(image, userId, 10)!= 1) {
throw new BizException("Lock Failed");
}

return image;
}

@Override
@Transactional(rollbackFor = Exception.class)
public void review(ReviewVo reviewVo, Image image, Long userId, Integer timeout) throws BizException {

// review
Review review = new Review();
review.setReviewUserId(userId);
review.setImageId(reviewVo.getImageId());
review.setProjectId(image.getProjectId());
review.setCommitId(reviewVo.getCommitId());
review.setReviewAction(reviewVo.getReviewAction());
review.setComment(reviewVo.getComment());
apReviewMapper.insert(review);

if (ReviewAction.EDIT.getType().equals(reviewVo.getReviewAction())) {
// Quality Check Modify
Commit commit = new Commit();
commit.setUserId(image.getLabelUserId()); // still is Compute Annotation member
commit.setImageId(reviewVo.getImageId());
commit.setProjectId(image.getProjectId());
commit.setIsValid(reviewVo.getIsValid());
commit.setAnnotationCount(reviewVo.getAnnotations().size());
apCommitMapper.insert(commit);
// annotation
List<Annotation> annotations = buildAnnotation(commit, reviewVo.getAnnotations());
apAnnotationService.saveBatch(annotations);
// image
image.setStatus(ImageStatus.PASS.getType());
image.setCommitId(commit.getId());
} else if (ReviewAction.PASS.getType().equals(reviewVo.getReviewAction())) {
// Quality Check Pass
image.setStatus(ImageStatus.PASS.getType());
} else {
// Quality Check Type return
image.setStatus(ImageStatus.REJECTED.getType());
image.setExpiredAt(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(timeout))); // First Lock to Annotation member
}
image.setReviewUserId(userId);
if (apImageMapper.updateById(image)!= 1) {
throw new BizException("Submit Failed");
}
}

@Override
public void release(Long projectId, Long imageId) throws BizException {
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId);
queryWrapper.eq(Image::getId, imageId);
Image image = apImageMapper.selectOne(queryWrapper);

if (!ImageStatus.ING.getType().equals(image.getStatus()) &&!ImageStatus.REVIEWING.getType().equals(image.getStatus())) {
throw new BizException("not Lock");
}
Date now = new Date();
if (image.getExpiredAt().before(now)) {
throw new BizException("not Lock");
}

// modify ExpiredAt Free Get not Know Rollback to What Status
image.setExpiredAt(now);
if (apImageMapper.updateById(image)!= 1) {
throw new BizException("Submit Failed");
}
}

@Override
public Long saveImage(Image image) {
apImageMapper.insert(image);
return image.getId();
}

@Override
public List<Image> getFinshAnnoList(Long projectId) {

// Query image
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(ObjectUtil.isNotNull(projectId),Image::getProjectId, projectId);
queryWrapper.and(qr ->qr.in(Image::getStatus, ImageStatus.DONE.getType(),ImageStatus.REVIEWING.getType()
,ImageStatus.PASS.getType()));// Annotation Complete Complete Image

return apImageMapper.selectList(queryWrapper);
}


@Override
public List<Image> getReviewAnnoList(Long projectId) {
// Query image
LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(Image::getProjectId, projectId)
.eq(Image::getNeedReview,1);
return apImageMapper.selectList(queryWrapper);
}

private void fillCommit(List<Image> imageList, boolean needReviewInfo) {
Set<Long> commitIds = imageList.stream().map(Image::getCommitId).collect(Collectors.toSet());

commitIds.remove(0L);
if (CollectionUtils.isEmpty(commitIds)) {
return;
}

Map<Long, Commit> commitMap = this.getCommitByIds(commitIds);

Map<Long, List<Annotation>> annotationMap = this.getAnnotationsByCommitIds(commitIds);

Map<Long, Review> reviewMap = new HashMap<>();
if (needReviewInfo) {
reviewMap = this.getReviewByCommitIds(commitIds);
}

for (Image image: imageList) {
image.setCommit(commitMap.get(image.getCommitId()));
image.setReview(reviewMap.get(image.getCommitId()));

if (image.getCommit()!= null) {
image.getCommit().setAnnotations(annotationMap.get(image.getCommitId()));
}
}
}

private Map<Long, Review> getReviewByCommitIds(Collection<Long> commitIds) {
LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(Review::getCommitId, commitIds);

List<Review> list = apReviewMapper.selectList(queryWrapper);
if(list == null) {
return new HashMap<>();
}
return list.stream().collect(Collectors.toMap(Review::getCommitId, Function.identity(), (key1, key2) -> key1));
}

private Map<Long, Commit> getCommitByIds(Collection<Long> commitIds) {
LambdaQueryWrapper<Commit> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(Commit::getId, commitIds);

List<Commit> list = apCommitMapper.selectList(queryWrapper);
if(list == null) {
return new HashMap<>();
}
return list.stream().collect(Collectors.toMap(Commit::getId, Function.identity(), (key1, key2) -> key1));
}

private Map<Long, List<Annotation>> getAnnotationsByCommitIds(Collection<Long> commitIds) {
LambdaQueryWrapper<Annotation> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.in(Annotation::getCommitId, commitIds);

List<Annotation> list = apAnnotationMapper.selectList(queryWrapper);
if(list == null) {
return new HashMap<>();
}
return list.stream().collect(Collectors.groupingBy(Annotation::getCommitId));
}
}
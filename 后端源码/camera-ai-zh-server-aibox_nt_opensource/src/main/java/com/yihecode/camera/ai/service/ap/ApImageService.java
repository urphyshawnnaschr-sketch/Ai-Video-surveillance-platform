package com.yihecode.camera.ai.service.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.ap.Image;
import com.yihecode.camera.ai.exception.BizException;
import com.yihecode.camera.ai.web.ap.vo.CommitVo;
import com.yihecode.camera.ai.web.ap.vo.ImageSearchRequestVo;
import com.yihecode.camera.ai.web.ap.vo.ReviewVo;

import java.util.List;

public interface ApImageService extends IService<Image> {

    List<Image> list(ImageSearchRequestVo request, boolean needReviewInfo);

    Long count(ImageSearchRequestVo request);

    Long saveImage(Image image);

    //Query Type return and Lead Get Annotation Task and Lock
List<Image> assignFromRejectAndAssignedImages(Long userId, Long projectId, Integer count, Integer timeout);

// Query Annotation Task Pool and Lock
List<Image> assignFromOpenImages(Long userId, Long projectId, Integer count, Integer timeout);

// Query Lead Get Quality Check Task and Lock
List<Image> assignForReviewFromAssignedImages(Long userId, Long projectId, Integer count, Integer timeout);

// Query Quality Check Task Pool and Lock
List<Image> assignForReviewFromOpenImages(Long userId, Long projectId, Integer count, Integer timeout);

// Submit Annotation Info
Image checkCommit(Long projectId, Long imageId, Long userId) throws BizException;

// Submit Annotation Info
void commit(CommitVo commit, Image image, Long userId) throws BizException;

// Submit Annotation Info
Image checkReview(Long projectId, Long imageId, Long userId, Long commitId) throws BizException;

// Submit Annotation Info
void review(ReviewVo reviewVo, Image image, Long userId, Integer timeout) throws BizException;

// Explain Put Task
void release(Long projectId, Long imageId) throws BizException;

/**
* Get Annotation Complete Complete Image
* @param projectId
* @return
*/
List<Image> getFinshAnnoList(Long projectId);


/**
* Get Need Quality Check Total Count
* @param projectId
* @return
*/
List<Image> getReviewAnnoList(Long projectId);

}
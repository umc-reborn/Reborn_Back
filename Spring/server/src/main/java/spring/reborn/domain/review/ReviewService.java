package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.review.model.*;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;
    private final ReviewProvider reviewProvider;

    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewProvider reviewProvider){
        this.reviewDao = reviewDao;
        this.reviewProvider = reviewProvider;
    }

    public PostReviewRes createReview(PostReviewReq postReviewReq) throws BaseException {
        int reviewIdx = reviewDao.createReview(postReviewReq);
        reviewDao.calculateStoreAvgScore(postReviewReq.getRebornIdx());
        return new PostReviewRes(reviewIdx);
    }

    public PostReviewRes createReview2(PostReviewReq2 postReviewReq2) throws BaseException {
        int reviewIdx = reviewDao.createReview2(postReviewReq2);
        return new PostReviewRes(reviewIdx);
    }

    public void deleteReview(ReviewReq reviewReq) throws BaseException {
        reviewDao.deleteReview(reviewReq);
        reviewDao.calculateStoreAvgScore(reviewReq.getRebornIdx());
    }

    public ReviewImgKey findImgKey(ReviewReq reviewReq) throws BaseException {
        return reviewDao.findImgKey(reviewReq.getReviewIdx());
    }
}

package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.review.model.PostReviewReq;
import spring.reborn.domain.review.model.PostReviewRes;

import java.util.List;

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
        return new PostReviewRes(reviewIdx);
    }
}

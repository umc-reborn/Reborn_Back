package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.review.model.*;

import java.util.List;

@Service
public class ReviewProvider {

    private final ReviewDao reviewDao;
//    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    @Autowired //readme 참고
    public ReviewProvider(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
//        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    public List<GetReviewRes3> getReviewByUserIdx(Integer userIdx) throws BaseException {
        return reviewDao.getReviewByUserIdx(userIdx);
    }

    public List<GetReviewRes2> getReviewByUserIdx2(Integer userIdx) throws BaseException {
        return reviewDao.getReviewByUserIdx2(userIdx);
    }

    public List<GetReviewRes> getReviewByStoreIdx(Integer storeIdx) throws BaseException {
        return reviewDao.getReviewByStoreIdx(storeIdx);
    }

    public List<GetReviewRes2> getReviewByStoreIdx2(Integer storeIdx) throws BaseException {
        return reviewDao.getReviewByStoreIdx2(storeIdx);
    }

    public List<GetReviewRes3> getReviewByStoreIdx3(Integer storeIdx) throws BaseException {
        return reviewDao.getReviewByStoreIdx3(storeIdx);
    }

    public GetReviewRes getReviewByReviewIdx(Integer reviewIdx) throws BaseException {
        return reviewDao.getReviewByReviewIdx(reviewIdx);
    }

    public List<GetBestReviewRes> getBestReview() throws BaseException {
        return reviewDao.getBestReview();
    }

    public Integer getReviewCntByStoreIdx(Integer storeIdx) throws BaseException {
        return reviewDao.getReviewCntByStoreIdx(storeIdx);
    }

    public Integer getReviewCntByUserIdx(Integer userIdx) throws BaseException {
        return reviewDao.getReviewCntByUserIdx(userIdx);
    }
}

package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.review.model.GetReviewReq;
import spring.reborn.domain.review.model.GetReviewRes;

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

    public List<GetReviewRes> getReviewByStoreIdx(Integer storeIdx) throws BaseException {
        System.out.println("Service 시작");
        return reviewDao.getReviewByStoreIdx(storeIdx);
    }
}

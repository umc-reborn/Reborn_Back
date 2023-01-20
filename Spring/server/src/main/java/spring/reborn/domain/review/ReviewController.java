package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.awsS3.AwsS3Service;
import spring.reborn.domain.review.model.PostReviewReq;
import spring.reborn.domain.review.model.PostReviewRes;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final AwsS3Service awsS3Service;

//    @Autowired
//    private final JwtService jwtService;

    public ReviewController(ReviewProvider reviewProvider, ReviewService reviewService, AwsS3Service awsS3Service) {
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
        this.awsS3Service = awsS3Service;
//        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    @ResponseBody
    @PostMapping(value = "/review", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<PostReviewRes> createReview(@RequestPart PostReviewReq postReviewReq,
                                                    @RequestParam(name = "images") List<MultipartFile> multipartFile) {
        try {
            List<String> fileUrlList = awsS3Service.uploadImage(multipartFile);

            // 이미지 파일 객체에 추가
            if (fileUrlList.size() >= 1) {
                postReviewReq.setReviewImage1(fileUrlList.get(0));
            }
            if (fileUrlList.size() >= 2) {
                postReviewReq.setReviewImage2(fileUrlList.get(1));
            }
            if (fileUrlList.size() >= 3) {
                postReviewReq.setReviewImage3(fileUrlList.get(2));
            }
            if (fileUrlList.size() >= 4) {
                postReviewReq.setReviewImage4(fileUrlList.get(3));
            }
            if (fileUrlList.size() >= 5) {
                postReviewReq.setReviewImage5(fileUrlList.get(4));
            }

            // 리뷰 생성
            PostReviewRes postReviewRes = reviewService.createReview(postReviewReq);

            // 가게 별점 평균 업데이트


            return new BaseResponse<>(postReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}

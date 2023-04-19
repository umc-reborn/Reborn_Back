package spring.reborn.domain.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.awsS3.AwsS3Service;
import spring.reborn.domain.review.model.*;
import spring.reborn.utils.JwtService;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final AwsS3Service awsS3Service;
    @Autowired
    private final ReviewDao reviewDao;

    @Autowired
    private final JwtService jwtService;

    public ReviewController(ReviewProvider reviewProvider, ReviewService reviewService,
                            AwsS3Service awsS3Service, ReviewDao reviewDao, JwtService jwtService) {
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
        this.awsS3Service = awsS3Service;
        this.reviewDao = reviewDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

//    @ResponseBody
//    @PostMapping(value = "/review", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public BaseResponse<PostReviewRes> createReview(@RequestPart PostReviewReq3 postReviewReq3,
//                                                    @RequestParam(name = "images") List<MultipartFile> multipartFile) {
//        try {
//            List<String> fileUrlList = awsS3Service.uploadImage(multipartFile);
//
//            // 이미지 파일 객체에 추가
//            if (fileUrlList.size() >= 1) {
//                postReviewReq3.setReviewImage1(fileUrlList.get(0));
//            }
//            if (fileUrlList.size() >= 2) {
//                postReviewReq3.setReviewImage2(fileUrlList.get(1));
//            }
//            if (fileUrlList.size() >= 3) {
//                postReviewReq3.setReviewImage3(fileUrlList.get(2));
//            }
//            if (fileUrlList.size() >= 4) {
//                postReviewReq3.setReviewImage4(fileUrlList.get(3));
//            }
//            if (fileUrlList.size() >= 5) {
//                postReviewReq3.setReviewImage5(fileUrlList.get(4));
//            }
//
//            // 리뷰 생성
//            PostReviewRes postReviewRes = reviewService.createReview(postReviewReq3);
//
//            return new BaseResponse<>(postReviewRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    @ResponseBody
    @PostMapping(value = "/review/set")
    public BaseResponse<String> setReviewScore() {
        try {
            reviewDao.setReviewScore();
            return new BaseResponse<>("성공적으로 평점이 업데이트 되었습니다");
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @PostMapping(value = "/reviewboni")
    public BaseResponse<PostReviewRes> createReview2(@RequestBody PostReviewReq postReviewReq) {
        try {
            PostReviewRes postReviewRes = reviewService.createReview(postReviewReq);
            return new BaseResponse<>(postReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
//    @ResponseBody
//    @PostMapping(value = "/reviewboni2")
//    public BaseResponse<PostReviewRes> createReview3(@RequestBody PostReviewReq2 postReviewReq2) {
//        try {
//            // 리뷰 생성
//            PostReviewRes postReviewRes = reviewService.createReview2(postReviewReq2);
//
//            return new BaseResponse<>(postReviewRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }



    @ResponseBody
    @DeleteMapping("/review")
    public BaseResponse<ReviewReq> deleteReview(@RequestBody ReviewReq reviewReq) {
        try {
            ReviewImgKey reviewImgKey = reviewService.findImgKey(reviewReq);

            if (reviewImgKey.getReviewImageKey1() != null) {
                awsS3Service.deleteImage(reviewImgKey.getReviewImageKey1().split("/")[3]);
            }
            if (reviewImgKey.getReviewImageKey2() != null) {
                awsS3Service.deleteImage(reviewImgKey.getReviewImageKey2().split("/")[3]);
            }
            if (reviewImgKey.getReviewImageKey3() != null) {
                awsS3Service.deleteImage(reviewImgKey.getReviewImageKey3().split("/")[3]);
            }
            if (reviewImgKey.getReviewImageKey4() != null) {
                awsS3Service.deleteImage(reviewImgKey.getReviewImageKey4().split("/")[3]);
            }
            if (reviewImgKey.getReviewImageKey5() != null) {
                awsS3Service.deleteImage(reviewImgKey.getReviewImageKey5().split("/")[3]);
            }

            reviewService.deleteReview(reviewReq);
            return new BaseResponse<>(reviewReq);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/review")
    public BaseResponse<List<GetReviewRes3>> getReviewByUserIdx() {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetReviewRes3> getReviewRes = reviewProvider.getReviewByUserIdx(userIdxByJwt);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/reviewbuz")
    public BaseResponse<List<GetReviewRes2>> getReviewByUserIdx2() {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetReviewRes2> getReviewRes = reviewProvider.getReviewByUserIdx2(userIdxByJwt);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/review/store/{storeIdx}")
    public BaseResponse<List<GetReviewRes>> getReviewByStoreIdx(@PathVariable Integer storeIdx) {
        try {
            List<GetReviewRes> getReviewRes = reviewProvider.getReviewByStoreIdx(storeIdx);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/review/store/{storeIdx}/buz")
    public BaseResponse<List<GetReviewRes2>> getReviewByStoreIdx2(@PathVariable Integer storeIdx) {
        try {
            List<GetReviewRes2> getReviewRes = reviewProvider.getReviewByStoreIdx2(storeIdx);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/review/store/{storeIdx}/buz2")
    public BaseResponse<List<GetReviewRes3>> getReviewByStoreIdx3(@PathVariable Integer storeIdx) {
        try {
            List<GetReviewRes3> getReviewRes = reviewProvider.getReviewByStoreIdx3(storeIdx);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/review/{reviewIdx}")
    public BaseResponse<GetReviewRes> getReviewByReviewIdx(@PathVariable Integer reviewIdx) {
        try {
            GetReviewRes getReviewRes = reviewProvider.getReviewByReviewIdx(reviewIdx);
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/review/best")
    public BaseResponse<List<GetBestReviewRes>> getBestReview() {
        try {
            List<GetBestReviewRes> getReviewRes = reviewProvider.getBestReview();
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/review/cnt")
    public BaseResponse<Integer> getReviewCntByStoreIdx(@RequestParam(value = "storeIdx",required = false) Integer storeIdx,
                                                        @RequestParam(value = "userIdx",required = false) Integer userIdx) {
        try {
            Integer getReviewRes = null;
            if (storeIdx != null){
                getReviewRes = reviewProvider.getReviewCntByStoreIdx(storeIdx);
            }
            else if (userIdx != null){
                getReviewRes = reviewProvider.getReviewCntByUserIdx(userIdx);
            }
            return new BaseResponse<>(getReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}

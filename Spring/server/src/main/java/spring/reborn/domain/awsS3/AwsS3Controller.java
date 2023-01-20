package spring.reborn.domain.awsS3;

import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.review.ReviewProvider;
import spring.reborn.domain.review.ReviewService;
import spring.reborn.domain.review.model.PostReviewReq;
import spring.reborn.domain.review.model.PostReviewRes;

import java.util.List;

@Controller
public class AwsS3Controller {

    @Autowired
    private final AwsS3Service awsS3Service;

    public AwsS3Controller(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @ResponseBody
    @PostMapping("/s3")
    public BaseResponse<List<String>> uploadImage(@RequestParam(name = "file") List<MultipartFile> multipartFile) {
        List<String> fileUrlList = awsS3Service.uploadImage(multipartFile);
        return new BaseResponse<>(fileUrlList);
    }

    @ResponseBody
    @DeleteMapping("/s3")
    public BaseResponse<String> deleteImage(@RequestParam(name="url") String fileName) {
        awsS3Service.deleteImage(fileName);
        return new BaseResponse<>("성공했습니다.");
    }

}

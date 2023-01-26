package spring.reborn.domain.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewRes {
    private int reviewIdx;
    private int userIdx;
    private String userImg;
    private String userNickname;
    private String storeName;
    private String storeCategory;
    private int rebornIdx;
    private String productName;
    private int reviewScore;
    private String reviewComment;
    private String reviewImage1;
    private String reviewImage2;
    private String reviewImage3;
    private String reviewImage4;
    private String reviewImage5;
    private Timestamp reviewCreatedAt;
}

package spring.reborn.domain.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class ReviewImgRes {
    private String reviewImage1;
    private String reviewImage2;
    private String reviewImage3;
    private String reviewImage4;
    private String reviewImage5;
}

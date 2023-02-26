package spring.reborn.domain.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewReq3 {
    private int userIdx;
    private int rebornIdx;
    private int reviewScore;
    private String reviewComment;
    private String reviewImage1;
    private String reviewImage2;
    private String reviewImage3;
    private String reviewImage4;
    private String reviewImage5;
}

package spring.reborn.domain.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostReviewReq2 {
    private int userIdx;
    private int rebornIdx;
    private int reviewScore;
    private String reviewComment;
    private ReviewImg reviewImg;
}

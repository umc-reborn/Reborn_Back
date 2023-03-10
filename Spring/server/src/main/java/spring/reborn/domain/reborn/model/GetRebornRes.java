package spring.reborn.domain.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetRebornRes {
    private int rebornIdx;
    private String productName;
    private String productGuide;
    private String productComment;
    private String productImg;
    private String productLimitTime;
    private int productCnt;
    private String status;
}

package spring.reborn.src.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetRebornRes {
    private String productName;
    private String productGuide;
    private String productComment;
    private String productImg;
    private String productLimitTime;
    private int productCnt;
}

package spring.reborn.src.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRebornReq {
    private int storeIdx;
    private String productName;
    private String productGuide;
    private String productComment;
    private String productImg;
    private String productLimitTime;
    private int productCnt;
}

package spring.reborn.domain.reborn.model;

import lombok.*;

@Getter
@Setter
public class PatchRebornReq {
    private int rebornIdx;
    private String productName;
    private String productGuide;
    private String productComment;
    private String productImg;
    private String productLimitTime;
    private int productCnt;
}

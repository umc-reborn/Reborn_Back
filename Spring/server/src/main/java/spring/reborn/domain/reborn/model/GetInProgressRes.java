package spring.reborn.domain.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetInProgressRes {
    private int rebornTaskIdx;
    private int rebornIdx;
    private int storeIdx;
    private String storeName;
    private String category;
    private String productName;
    private String productImg;
    private String productLimitTime;
    private int productCnt;
}
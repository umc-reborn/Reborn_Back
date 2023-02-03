package spring.reborn.domain.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetHistroyDetailRes {
    private String productName;
    private String productGuide;
    private String productComment;
    private String storeName;
    private String storeAddress;
    private String category;
    private int productExchangeCode;
    private String createdAt;
    private String status;
}

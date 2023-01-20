package spring.reborn.domain.store.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetStoreLocationRes {
    private Long storeIdx;
    private String storeName;
    private String storeAddress;
    private Float storeScore;

}

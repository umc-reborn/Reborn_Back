package spring.reborn.domain.store.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetNewStoreRes {
    private Long storeIdx;
    private String userImage;
    private String storeName;
    private Enum<StoreCategory> category;
    private Float storeScore;
}

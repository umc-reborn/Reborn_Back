package spring.reborn.domain.store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetStoreInfoRes {
    private Long storeIdx;

    private String storeName;

    private String storeImage;
    private String userImage;

    private String storeAddress;

    private String storeDescription;

    private Float storeScore;

    private Long numOfReborn;
    private Long numOfJjim;
    private Long numOfReview;

    private Enum<StoreCategory> category;
}

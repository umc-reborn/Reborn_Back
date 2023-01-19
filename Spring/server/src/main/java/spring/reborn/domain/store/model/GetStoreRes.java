package spring.reborn.domain.store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetStoreRes {
    private Long storeIdx;

    private String storeName;

    private String storeImage;

    private String storeAddress;

    private String storeDescription;

    private Float storeScore;

    private Enum<StoreCategory> category;
}

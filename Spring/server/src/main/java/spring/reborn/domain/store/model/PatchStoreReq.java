package spring.reborn.domain.store.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchStoreReq {
    private String storeName;

    private String storeAddress;

    private String storeDescription;

    private String category;

    private String storeImage;


}

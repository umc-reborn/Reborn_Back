package spring.reborn.domain.store.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetLikeableStoreRes {

    private Long storeIdx;
    private String storeName;
    private Float storeScore;
    private String category;
    private String userImage;
    private Boolean hasJjim;
}

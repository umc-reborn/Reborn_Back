package spring.reborn.domain.store.model;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetPopularStoreRes {
    private int storeIdx;
    private String storeName;
    private String storeAddress;
    private String category;
    private float storeScore;
}

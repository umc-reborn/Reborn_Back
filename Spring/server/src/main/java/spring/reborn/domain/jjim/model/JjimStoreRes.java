package spring.reborn.domain.jjim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JjimStoreRes {
    private int jjimIdx;
    private int storeIdx;
    private String storeName;
    private String storeImage;
    private String storeCategory;
    private float storeScore;
}

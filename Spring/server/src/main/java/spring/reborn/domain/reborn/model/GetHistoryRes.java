package spring.reborn.domain.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetHistoryRes {
    private int rebornTaskIdx;
    private int rebornIdx;
    private String storeName;
    private String storeImage;
    private float storeScore;
    private String category;
    private String status;
    private String createdAt;
}

package spring.reborn.domain.rebornTask.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetRebornHistoryRes {
    // Store Table
    private String storeName;
    private String storeImage;
    private String storeAddress;
    private int storeScore;
    // rebornTask
    private String rebornTaskIdx;
    private String createdAt;
}

package spring.reborn.domain.rebornTask.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RebornTask {
    private Long rebornTaskIdx;
    private Long rebornIdx;
    private Long userIdx;
    private Long productExchangeCode;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}

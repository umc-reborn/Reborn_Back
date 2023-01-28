package spring.reborn.domain.rebornTask.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatchRebornTaskForExchange {
    private Long rebornTaskIdx;
    private Long rebornIdx;
    private String status;
    private Long productExchangeCode;
    private Long productCnt;
}

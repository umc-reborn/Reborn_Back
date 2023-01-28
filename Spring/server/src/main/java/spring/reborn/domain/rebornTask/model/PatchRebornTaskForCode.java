package spring.reborn.domain.rebornTask.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import spring.reborn.domain.reborn.model.RebornStatus;

@Getter
@Setter
@Builder
public class PatchRebornTaskForCode {
    private Long rebornTaskIdx;
    private Long rebornIdx;
    private String status;
    private Long productExchangeCode;
}

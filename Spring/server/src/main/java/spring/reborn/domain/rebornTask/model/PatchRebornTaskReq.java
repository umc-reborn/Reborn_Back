package spring.reborn.domain.rebornTask.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatchRebornTaskReq {
    private Long rebornTaskIdx;
    private Long productExchangeCode;


}

package spring.reborn.domain.rebornTask.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostRebornTaskReq {
    private Long rebornIdx;
    private Long userIdx;
}

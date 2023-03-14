package spring.reborn.domain.rebornTask.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@Builder
@NoArgsConstructor
public class PostRebornTaskReq {
    private Long rebornIdx;
    private Long userIdx;
}

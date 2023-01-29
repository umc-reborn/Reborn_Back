package spring.reborn.domain.rebornTask.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRebornTaskRes {
    private Long rebornTaskIdx;

    public PostRebornTaskRes(Long rebornTaskIdx) {
        this.rebornTaskIdx = rebornTaskIdx;
    }
}

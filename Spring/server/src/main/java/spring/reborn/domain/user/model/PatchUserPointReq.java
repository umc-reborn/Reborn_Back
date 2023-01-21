package spring.reborn.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchUserPointReq {
    private int userIdx;
    private int addPoint;

    public PatchUserPointReq() {
    }
}

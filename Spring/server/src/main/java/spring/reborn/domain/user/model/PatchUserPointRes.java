package spring.reborn.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchUserPointRes {
    private int userIdx;
    private String userEmail;
    private int addPoint;
    private int resultPoint;
}

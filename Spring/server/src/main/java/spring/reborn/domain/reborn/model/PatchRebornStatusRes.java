package spring.reborn.domain.reborn.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchRebornStatusRes {
    private int rebornIdx;
    private String productName;
}

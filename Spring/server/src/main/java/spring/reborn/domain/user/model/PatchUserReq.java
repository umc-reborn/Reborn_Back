package spring.reborn.domain.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchUserReq {
    private int userIdx;
    private String userImg;
    private String userNickname;
    private String userAddress;
    private String userLikes;
}

package spring.reborn.domain.oauth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUser {

    private int userIdx;
    private String userId;
    private String userNickname;

    public KakaoUser(int userIdx, String userId, String userNickname) {
        this.userIdx = userIdx;
        this.userId = userId;
        this.userNickname = userNickname;
    }
}

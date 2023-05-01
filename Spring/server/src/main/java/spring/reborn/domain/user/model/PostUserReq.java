package spring.reborn.domain.user.model;

import lombok.*;
import javax.persistence.*;


@Getter
@Setter
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(email, password, nickname, profileImage)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
public class PostUserReq {
    private String userEmail;
    private String userId;
    private String userPwd;
    private String userNickname;
    private String userImg;
    private String userAdAgreement;
    private String userAddress;//맞나?
    @Getter
    public enum userLikes
    {
        CAFE("카페"), FASHION("패션"), SIDEDISH("반찬"), LIFE("편의생활"), ETC("기타");

        private String userLikes;
        userLikes(String userLikes){
            this.userLikes = userLikes;
        }
    }
    @Enumerated(EnumType.STRING)
    private userLikes userLikes;
    /*public enum userLikes {
        CAFE, FASHION, SIDEDISH, LIFE, ETC
    }

    @Enumerated(EnumType.STRING)
    private userLikes userLikes;*/
}

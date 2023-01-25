package spring.reborn.domain.user.model;

import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(email, password, nickname, profileImage)를 받는 생성자를 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 해당 클래스의 파라미터가 없는 생성자를 생성, 접근제한자를 PROTECTED로 설정.
public class PostUserStoreReq {
    private String userId;
    private String userEmail;           // 이메일
    private String userPwd;             // 비밀번호
    private String userAdAgreement;     // 광고성 정보 동의
    private String storeName;           // 상호명
    private String storeRegister;       // 사업자 등록번호
    private String storeImage;          // 대표 사진
    private String storeAddress;        // 가게 주소
    private String storeDescription;    // 가게 한 줄 소개

    @Getter
    public enum category
    {
        CAFE("카페"), FASHION("패션"), SIDEDISH("반찬"), LIFE("편의생활"), ETC("기타");

        private String category;
        category(String category){
            this.category = category;
        }
    }
    @Enumerated(EnumType.STRING)
    private PostUserStoreReq.category category;
}

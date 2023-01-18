package spring.reborn.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter // 해당 클래스에 대한 접근자 생성
@Setter // 해당 클래스에 대한 설정자 생성
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(userIdx, nickname, email, password)를 받는 생성자를 생성
@NoArgsConstructor
public class UserStore {
    private int userIdx;
    private String userEmail;
    private String userPwd;
    private char userAdAgreement;
    private String storeName;
    private String storeRegister;
    private String storeImage;
    private String storeAddress;
    private String storeInfo;
    private String category;
//    private int userPoint;
}

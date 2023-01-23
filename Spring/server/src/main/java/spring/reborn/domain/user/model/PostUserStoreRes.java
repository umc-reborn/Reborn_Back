package spring.reborn.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // 해당 클래스의 모든 멤버 변수(jwt, userIdx)를 받는 생성자를 생성-
public class PostUserStoreRes {
    private int storeIdx;
    private int userIdx;
    private String storeName;
    private String jwt;
}

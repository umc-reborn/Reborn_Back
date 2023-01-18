package spring.reborn.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_NAME(false, 2011, "유저 이름을 입력해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    //폴
    INVALID_USER(false,2100,"탈퇴 혹은 블랙 유저입니다."),
    POST_USERS_EMPTY_BIRTHDATE(false, 2101, "생년월일을 입력해주세요."),
    POST_USERS_EMPTY_ADDRESS(false, 2102, "주소를 입력해주세요."),
    POST_USERS_EMPTY_LIKES(false, 2103, "관심 카테고리를 입력해주세요."),
    //민몰리
    POST_USERS_EMPTY_PASSWORD(false, 2400, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2401, "비밀번호 형식은 숫자, 특문 각 1회 이상, 영문은 2개 이상 사용하여 8자리 이상이어야 합니다."),
    POST_USERS_EMPTY_NICKNAME(false, 2402, "닉네임을 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false, 2403, "닉네임은 한글, 영문, 숫자만 가능합니다."),
    POST_USERS_EMPTY_STORENAME(false, 2404, "상호명을 입력해주세요."),
    POST_USERS_INVALID_STORENAME(false, 2405, "상호명은 한글, 영문, 숫자만 가능합니다."),
    POST_USERS_EMPTY_STOREADDRESS(false, 2406, "가게 주소를 입력해주세요."),
    POST_USERS_EMPTY_STORECATEGORY(false, 2407, "카테고리를 설정해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),
    MODIFY_FAIL_PRODUCTNAME(false,4015,"상품네임 수정 실패"),
    MODIFY_FAIL_PRICE(false,4016,"가격 수정 실패"),
    MODIFY_FAIL_CATEGORY(false,4017,"카테고리 수정 실패"),
    MODIFY_FAIL_STATUS(false,4018,"유저 비활성화 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

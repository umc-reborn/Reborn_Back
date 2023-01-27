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
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),

    //폴
    INVALID_USER(false,2100,"탈퇴 혹은 블랙 유저입니다."),
    POST_USERS_EMPTY_BIRTHDATE(false, 2101, "생년월일을 입력해주세요."),
    POST_USERS_EMPTY_ADDRESS(false, 2102, "주소를 입력해주세요."),
    POST_USERS_EMPTY_LIKES(false, 2103, "관심 카테고리를 입력해주세요."),
    POST_USERS_INVALID_BIRTHDATE(false, 2104, "생년월일을 숫자만 사용해서 8자리로 입력해주세요"),
    INVALID_USERTYPE(false,2105,"유저타입이 다른 유저입니다."),
    POST_USERS_EMPTY_ID(false, 2106, "ID를 입력해주세요."),
    POST_USERS_INVALID_ID(false, 2107, "ID 형식을 확인해주세요."),
    POST_USERS_EXISTS_ID(false,2108,"중복된 ID입니다."),
    NO_JOINED_EMAIL(false, 2109, "가입되지 않은 이메일입니다."),
    NO_JOINED_ID(false, 2110, "가입되지 않은 ID입니다."),


    //준몰리
    CAN_NOT_FOUND_STORE(false,2200,"해당하는 스토어 정보를 찾을 수 없습니다."),
    GET_STORE_EMPTY_KEYWORD(false,2201,"키워드를 입력해주세요."),
    SEARCH_STORE_ERROR(false,2202,"스토어를 검색할 수 없습니다."),
    MODIFY_FAIL_STORE(false,2203,"해당 스토어의 정보를 수정할 수 없습니다."),
    MODIFY_FAIL_STORE_EMPTY_NAME(false,2204,"해당 스토어의 이름을 입력해주세요."),
    MODIFY_FAIL_STORE_EMPTY_LOCATION(false,2205,"해당 스토어의 위치를 입력해주세요."),

    CAN_NOT_CREATE_REBORN_TASK(false, 2210, "리본을 생성할 수 없습니다. 다시 시도해주세요."),
    NOT_AVAILABLE_REBORN_TASK(false, 2211, "리본을 진행할 수 없는 상태입니다. 다시 확인해주세요."),
    UPDATE_FAIL_REBORN_TASK(false, 2212, "리본 태스크 업데이트 실패."),
    UPDATE_FAIL_REBORN_STATUS(false, 2213, "리본 상태 업데이트 실패."),
    NOT_ENOUGH_REBORN(false, 2214, "리본이 모두 소진 되었습니다."),

    AWS_UPLOAD_FAIL_ERROR(false,2297, "AWS에서 해당 이미지를 업로드에 실패하였습니다."),
    AWS_DELETE_FAIL_ERROR(false,2298, "AWS에서 해당 이미지를 제거에 실패하였습니다."),
    MODIFY_FAIL_STORE_UPDATE_TIME(false,2299,"해당 스토어 마지막 업데이트 시간을 수정할 수 없습니다."),


    // 클로이
    PATCH_REBORN_EMPTY_PRODUCTNAME(false, 2300, "상품 이름이 없습니다."),
    PATCH_REBORN_EMPTY_PRODUCTGUIDE(false, 2301, "상품 가이드가 없습니다."),
    PATCH_REBORN_EMPTY_PRODUCTCOMMENT(false, 2302, "상품 설명이 없습니다."),
    MODIFY_FAIL_REBORN(false, 2303, "리본 수정에 실패했습니다."),
    //민몰리
    POST_USERS_EMPTY_PASSWORD(false, 2400, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2401, "비밀번호 형식은 숫자, 특수문자 각 1회 이상, 영문은 대소문자 각 1개 이상 사용하여 8자리 이상 16자리 이하여야 합니다."),
    POST_USERS_EMPTY_NICKNAME(false, 2402, "닉네임을 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false, 2403, "닉네임은 한글, 영문, 숫자만 가능합니다."),
    POST_USERS_EMPTY_STORENAME(false, 2404, "상호명을 입력해주세요."),
    POST_USERS_INVALID_STORENAME(false, 2405, "상호명은 한글, 영문, 숫자만 가능합니다."),
    POST_USERS_EMPTY_STOREADDRESS(false, 2406, "가게 주소를 입력해주세요."),
    POST_USERS_EMPTY_STORECATEGORY(false, 2407, "카테고리를 설정해주세요."),
    POST_USERS_EMPTY_STOREREGISTER(false, 2408, "사업자 등록번호를 입력해주세요."),
    POST_USERS_INVALID_STOREREGISTER(false, 2409, "사업자 등록번호 형식을 확인해주세요."),


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
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),
    MODIFY_FAIL_USERSTATUS(false,4100,"유저 탈퇴 실패"),
    MODIFY_FAIL_STORESTATUS(false,4101,"가게 탈퇴 실패");


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

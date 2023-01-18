package spring.reborn.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.user.model.PostUserStoreReq;
import spring.reborn.domain.user.model.PostUserStoreRes;
import spring.reborn.utils.JwtService;

import static spring.reborn.config.BaseResponseStatus.*;
import static spring.reborn.utils.ValidationRegex.*;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());     // Log 남기기

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원가입-가게 API
     * [POST] /users
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up-store")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserStoreRes> createUserStore(@RequestBody PostUserStoreReq postUserStoreReq) {
        // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserStoreReq.getUserEmail().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(postUserStoreReq.getUserEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // password에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserStoreReq.getUserPwd().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //비밀번호 정규표현: 입력받은 비밀번호가 숫자, 특문 각 1회 이상, 영문은 대소문자 모두 사용하여 8~16자리 입력과 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexPassword(postUserStoreReq.getUserPwd())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        // 상호명 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserStoreReq.getStoreName().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_STORENAME);
        }
        // 상호명 정규표현: 입력받은 상호명이 숫자와 영문, 한글로만 이루어졌는지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexNickname(postUserStoreReq.getStoreName())) {
            return new BaseResponse<>(POST_USERS_INVALID_STORENAME);
        }
        try {
            PostUserStoreRes postUserStoreRes = userService.createUserStore(postUserStoreReq);
            return new BaseResponse<>(postUserStoreRes);
        } catch (BaseException exception) {
            System.out.println(exception);
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

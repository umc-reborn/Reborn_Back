package spring.reborn.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.*;
import spring.reborn.domain.user.model.*;
import spring.reborn.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import spring.reborn.domain.awsS3.AwsS3Service;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.*;
import static spring.reborn.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    @Autowired
    private final AwsS3Service awsS3Service;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, AwsS3Service awsS3Service) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
        this.awsS3Service = awsS3Service;
    }

    /**
     * 회원가입 API
     * [POST] /users
     */
    // Body
    @ResponseBody
    @PostMapping(value = "/sign-up", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})    // POST 방식의 요청을 매핑하기 위한 어노테이션
    @Transactional
    public BaseResponse<PostUserRes> createUser(@RequestPart PostUserReq postUserReq, @RequestParam(name = "images") List<MultipartFile> multipartFile) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
        // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getUserEmail().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(postUserReq.getUserEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // password에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getUserPwd().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //비밀번호 정규표현: 입력받은 비밀번호가 숫자, 특문 각 1회 이상, 영문은 대소문자 모두 사용하여 8~16자리 입력과 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexPassword(postUserReq.getUserPwd())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        // 닉네임 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getUserNickname().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        // 닉네임 정규표현: 입력받은 닉네임이 숫자와 영문, 한글로만 이루어졌는지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexNickname(postUserReq.getUserNickname())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }
        // 생년월일이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getUserBirthDate().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTHDATE);
        }
        // 생년월일 정규표현: 입력받은 새연월일이 숫자 8개로 이루어졌는지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexBirthDate(postUserReq.getUserBirthDate())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTHDATE);
        }
        // 주소가 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getUserAddress().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_ADDRESS);
        }
        // 관심사를 설정했는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getUserLikes() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_LIKES);
        }
        //사진 넣기
        List<String> fileUrl = awsS3Service.uploadImage(multipartFile);

        // 이미지 파일 객체에 추가
        postUserReq.setUserImg(fileUrl.get(0));
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 회원가입-가게 API
     * [POST] /users
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up-store")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserStoreRes> createUserStore(@RequestBody PostUserStoreReq postUserStoreReq) {
        // email에 값이 존재하는지 검사
        if (postUserStoreReq.getUserEmail().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사
        if (!isRegexEmail(postUserStoreReq.getUserEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // password에 값이 존재하는지 검사
        if (postUserStoreReq.getUserPwd().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //비밀번호 정규표현: 입력받은 비밀번호가 숫자, 특문, 영문 대소문자를 모두 사용하여 8~16자리 형식인지 검사
        if (!isRegexPassword(postUserStoreReq.getUserPwd())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        // 상호명 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사
        if (postUserStoreReq.getStoreName().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_STORENAME);
        }
        // 상호명 정규표현: 입력받은 상호명이 숫자와 영문, 한글로만 이루어졌는지 검사
        if (!isRegexNickname(postUserStoreReq.getStoreName())) {
            return new BaseResponse<>(POST_USERS_INVALID_STORENAME);
        }
        // 가게주소 값이 존재하는지 검사
        if (postUserStoreReq.getStoreAddress().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_STOREADDRESS);
        }
        // 카테고리 값이 존재하는지 검사
        if (postUserStoreReq.getCategory() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_STORECATEGORY);
        }
        try {
            PostUserStoreRes postUserStoreRes = userService.createUserStore(postUserStoreReq);
            return new BaseResponse<>(postUserStoreRes);
        } catch (BaseException exception) {
            System.out.println(exception);
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 회원 1명 포인트 조회 API
     * [GET] /users/:userIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/point/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserPointRes> getUser(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            GetUserPointRes getUserPointRes = userProvider.getUserPoint(userIdx);
            return new BaseResponse<>(getUserPointRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 포인트 적립, 취소 - hyerm
    @ResponseBody
    @PatchMapping("/point")
    public BaseResponse<PatchUserPointRes> editUserPoint(@RequestBody PatchUserPointReq patchUserPointReq) {
        System.out.println("controller 시작");
        PatchUserPointRes patchUserPointRes = userService.editUserPoint(patchUserPointReq);
        return new BaseResponse<>(patchUserPointRes);
    }

    /**
     * 회원 정보 조회 API
     * [GET] /users/inform/:userIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/inform/{userIdx}") // (GET) 127.0.0.1:9000/app/users/inform/:userIdx
    public BaseResponse<GetUserInformRes> getUserInform(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            GetUserInformRes getUserInformRes = userProvider.getUserInform(userIdx);
            return new BaseResponse<>(getUserInformRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }


    }
    
    /**
     * 이웃 회원탈퇴 API
     * [PATCH]
     */
    @ResponseBody
    @PatchMapping("/userDelete/{userIdx}")
    @Transactional
    public BaseResponse<String> modifyUserStatus(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {

//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
//            jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(user.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저상태 변경
//  **************************************************************************
            PatchUserStatusReq patchUserStatusReq = new PatchUserStatusReq(userIdx, user.getStatus());
            userService.modifyUserStatus(patchUserStatusReq);

            String result = "회원탈퇴가 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 스토어 회원탈퇴 API
     * [PATCH]
     */
    @ResponseBody
    @PatchMapping("/storeDelete/{userIdx}")
    @Transactional
    public BaseResponse<String> modifyStoreStatus(@PathVariable("userIdx") int userIdx, @RequestBody UserStore userStore) {
        try {

//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
//            jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userStore.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저상태 변경
//  **************************************************************************
            PatchStoreStatusReq patchStoreStatusReq = new PatchStoreStatusReq(userIdx, userStore.getStatus());
            userService.modifyStoreStatus(patchStoreStatusReq);

            String result = "회원탈퇴가 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 이웃 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    @Transactional
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getStatus() == "DELETE" || postLoginReq.getStatus() == "BLACK") {
            return new BaseResponse<>(INVALID_USER);
        }
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}

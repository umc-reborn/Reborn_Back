package spring.reborn.domain.user;

import org.json.JSONObject;
import org.json.XML;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
        // id에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getUserId().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        //id 정규표현: 입력받은 id가 영문 대소문자,숫자 4-16자리 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexId(postUserReq.getUserId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
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
    @PostMapping(value ="/sign-up-store", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserStoreRes> createUserStore(@RequestPart PostUserStoreReq postUserStoreReq, @RequestParam(name = "storeImage") List<MultipartFile> storeImageFile, @RequestParam(name = "userImage") List<MultipartFile> userImgFile) {
        // email에 값이 존재하는지 검사
        if (postUserStoreReq.getUserEmail().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사
        if (!isRegexEmail(postUserStoreReq.getUserEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // id에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserStoreReq.getUserId().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        //id 정규표현: 입력받은 id가 영문 대소문자,숫자 4-16자리 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexId(postUserStoreReq.getUserId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        // password에 값이 존재하는지 검사
        if (postUserStoreReq.getUserPwd().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        // 비밀번호 정규표현: 입력받은 비밀번호가 숫자, 특문, 영문 대소문자를 모두 사용하여 8~16자리 형식인지 검사
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
        // 사업자등록번호 값이 존재하는지 검사
        if (postUserStoreReq.getStoreRegister().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_STOREREGISTER);
        }
        // 사업자등록번호 정규표현: 000-00-00000 형식으로 이루어졌는지 검사
        if (!isRegexStoreRegister(postUserStoreReq.getStoreRegister())) {
            return new BaseResponse<>(POST_USERS_INVALID_STOREREGISTER);
        }
        // 가게주소 값이 존재하는지 검사
        if (postUserStoreReq.getStoreAddress().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_STOREADDRESS);
        }
        // 카테고리 값이 존재하는지 검사
        if (postUserStoreReq.getCategory() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_STORECATEGORY);
        }
        // 스토어 홈 배경 사진 넣기
        List<String> fileUrl = awsS3Service.uploadImage(storeImageFile);
        postUserStoreReq.setStoreImage(fileUrl.get(0));             // 이미지 파일 객체에 추가
        // 스토어 프로필 사진 넣기
        fileUrl = awsS3Service.uploadImage(userImgFile);
        postUserStoreReq.setUserImg(fileUrl.get(0));                // 이미지 파일 객체에 추가
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
    @GetMapping("/point") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserPointRes> getUser() {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            int userIdx = jwtService.getUserIdx();
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
    @GetMapping("/inform") // (GET) 127.0.0.1:9000/app/users/inform/:userIdx
    public BaseResponse<GetUserInformRes> getUserInform() {
        try {
            int userIdx = jwtService.getUserIdx();
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
    @PatchMapping("/userDelete")
    public BaseResponse<String> modifyUserStatus() {
        try {
            int userIdx = jwtService.getUserIdx();
            userService.modifyUserStatus(userIdx);

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
    @PatchMapping("/storeDelete")
    public BaseResponse<String> modifyStoreStatus() {
        try {
            int userIdx = jwtService.getUserIdx();
            userService.modifyStoreStatus(userIdx);

            String result = "회원탈퇴가 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
/////////////////////////////////////
    /**
     * 회원정보 수정 API
     * [PATCH]
     */
    @ResponseBody
    @PatchMapping(value="/userModify", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<String> modifyUserInform(@RequestPart User user,  @RequestParam(name = "userImg") List<MultipartFile> multipartFile) {
        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //같다면 유저정보 변경

            int userIdx = jwtService.getUserIdx();
            //사진 넣기
            List<String> fileUrl = awsS3Service.uploadImage(multipartFile);
            // 이미지 파일 객체에 추가
            user.setUserImg(fileUrl.get(0));

            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getUserImg(), user.getUserNickname(), user.getUserAddress(), user.getUserBirthDate(), user.getUserLikes());
            userService.modifyUserInform(patchUserReq);

            String result = "회원정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            System.out.println(exception);
            return new BaseResponse<>((exception.getStatus()));
        }
  }
  
    /**
     * 이웃 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getUserId().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        //id 정규표현: 입력받은 id가 영문 대소문자,숫자 4-16자리 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexId(postLoginReq.getUserId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        // password에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postLoginReq.getUserPwd().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //비밀번호 정규표현: 입력받은 비밀번호가 숫자, 특문 각 1회 이상, 영문은 대소문자 모두 사용하여 8~16자리 입력과 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexPassword(postLoginReq.getUserPwd())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        try {
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * 스토어 로그인 API
     * [POST]
     */
    @ResponseBody
    @PostMapping("/log-in-store")
    public BaseResponse<PostStoreLoginRes> storeLogIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getUserId().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        //id 정규표현: 입력받은 id가 영문 대소문자,숫자 4-16자리 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexId(postLoginReq.getUserId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        // password에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postLoginReq.getUserPwd().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //비밀번호 정규표현: 입력받은 비밀번호가 숫자, 특문 각 1회 이상, 영문은 대소문자 모두 사용하여 8~16자리 입력과 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexPassword(postLoginReq.getUserPwd())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        try {
            PostStoreLoginRes postStoreLoginRes = userProvider.storeLogIn(postLoginReq);
            return new BaseResponse<>(postStoreLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이메일 인증 요청 API
     * [POST] /users/logIn/mailConfirm
     */
    @PostMapping("login/mailConfirm")
    @ResponseBody
    public BaseResponse<String> mailConfirm(@RequestParam("email") String email) throws Exception {
        try {
            String code = userService.sendSimpleMessage(email);
            return new BaseResponse<>(code);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 이메일 인증 확인 API - 암호화
     * [GET] /users/logIn/mailConfirm
     */
    @GetMapping("login/mailCheck")
    @ResponseBody
    public BaseResponse<String> mailCheck(@RequestParam("code") String code) throws Exception {
        try {
            String result = userService.encryptionCode(code);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 아이디 찾기 - 부분 암호 API
     * [GET] /users/IdFindPart
     */
    @GetMapping("/IdFindPart")
    @ResponseBody
    public BaseResponse<GetUserIdRes> idFindPart(@RequestParam("email") String email) throws Exception {
        try {
            GetUserIdRes getUserIdRes = userService.idFindPart(email);
            return new BaseResponse<>(getUserIdRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 아이디 찾기 - 메일 전송 API
     * [POST] /users/IdFindMail
     */
    @PostMapping("/IdFindMail")
    @ResponseBody
    public BaseResponse<String> idMailSend(@RequestParam("email") String email) throws Exception {
        try {
            userService.sendIDMessage(email);
            String result = "가입하신 이메일로 아이디가 발송 되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 비밀번호 초기화 API
     * [PATCH] /users/pwd-reset
     */
    @PatchMapping("pwd-reset")
    @ResponseBody
    public BaseResponse<String> resetPwd(@RequestBody PatchUserPwdResetReq patchUserPwdResetReq) throws Exception {

        try {
            userService.sendTempPwd(patchUserPwdResetReq);
            String code = "이메일로 임시 비밀번호가 발급되었습니다";
            return new BaseResponse<>(code);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 비밀번호 변경 API
     * [PATCH]
     */
    @ResponseBody
    @PatchMapping("/modifyPwd")
    public BaseResponse<String> modifyPwd(@RequestBody PatchUserPwdReq patchUserPwdReq) {
        // password에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (patchUserPwdReq.getUserNewPwd().length() == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //비밀번호 정규표현: 입력받은 비밀번호가 숫자, 특문 각 1회 이상, 영문은 대소문자 모두 사용하여 8~16자리 입력과 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexPassword(patchUserPwdReq.getUserNewPwd())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        try {
            if(jwtService.compareUserIdx(patchUserPwdReq.getUserIdx()) == 0){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyUserPwd(patchUserPwdReq);

            String result = "비번 변경이 완료되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 로그아웃 API
     * [POST] /users/log-out
     */
    @ResponseBody
    @PostMapping("/log-out")
    public BaseResponse<PostLogoutRes> logOut() {
        try {
            int userIdx = jwtService.getUserIdx();
            PostLogoutRes postLogoutRes = userProvider.logOut(userIdx);
            return new BaseResponse<>(postLogoutRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

//    /**
//     * 사업자 등록 상태조회 API
//     */
//    @GetMapping("/storeConfirm")
//    public String callStoreApiWithJson() {
//        StringBuffer result = new StringBuffer();
//        String jsonPrintString = null;
//        try {
//            String apiUrl = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=eXVANVFN1Vq7ll1sSwTSkiinLph3JfWR26scJ1E1Bce7YKVBTWypJjJWFiPhWnzHrUHQerFSRlso3PnOU1LziA%3D%3D";
//            URL url = new URL(apiUrl);
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.connect();
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, "UTF-8"));
//            String returnLine;
//            while((returnLine = bufferedReader.readLine()) != null) {
//                result.append(returnLine);
//            }
//
//            JSONObject jsonObject = XML.toJSONObject(result.toString());
//            jsonPrintString = jsonObject.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return jsonPrintString;
//    }

}


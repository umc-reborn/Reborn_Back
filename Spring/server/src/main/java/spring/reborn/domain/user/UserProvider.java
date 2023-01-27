package spring.reborn.domain.user;

import spring.reborn.config.BaseException;
import spring.reborn.config.secret.Secret;
import spring.reborn.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import spring.reborn.domain.user.model.*;

import static spring.reborn.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
// [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class UserProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************
    public int checkUserEmail(String userEmail) throws BaseException {
        try {
            return userDao.checkUserEmail(userEmail);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserId(String userId) throws BaseException {
        try {
            return userDao.checkUserId(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 User의 포인트 조회
    public GetUserPointRes getUserPoint(int userIdx) throws BaseException {
        try {
            GetUserPointRes getUserPointRes = userDao.getUserPoint(userIdx);
            return getUserPointRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 유저 정보 조회
    public GetUserInformRes getUserInform(int userIdx) throws BaseException {
        try {
            GetUserInformRes getUserInformRes = userDao.getUserInform(userIdx);
            return getUserInformRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //이웃 로그인(password 검사)
    @Transactional
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getUserPwd()); // 암호화
            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if (postLoginReq.getUserPwd().equals(password)) { //비말번호가 일치한다면 userIdx를 가져온다.
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
//            return new PostLoginRes(userIdx);
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해제 및 대체해주세요!  **************** //
            String jwt = jwtService.createJwt(userIdx);
            String status = userDao.getUserStatus(userIdx);
            String userType = userDao.getUserType(userIdx);
            String userNickname = userDao.getUserNickname(userIdx);
            if(!userType.equals("CONSUMER")){
                throw new BaseException(INVALID_USERTYPE);
            }
            if(!status.equals("ACTIVE")){
                throw new BaseException(INVALID_USER);
            } else{
                return new PostLoginRes(userIdx,userNickname,jwt);
            }
//  **************************************************************************

        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }
    //스토어 로그인(password 검사)
    @Transactional
    public PostStoreLoginRes storeLogIn(PostLoginReq postLoginReq) throws BaseException {
        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getUserPwd()); // 암호화
            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if (postLoginReq.getUserPwd().equals(password)) { //비말번호가 일치한다면 userIdx를 가져온다.
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String userType = userDao.getUserType(userIdx);
            if(!userType.equals("STORE")){
                throw new BaseException(INVALID_USERTYPE);
            }
            int storeIdx = userDao.getStorePwd(userIdx).getStoreIdx();
            String storeName = userDao.getStoreName(storeIdx);
//            return new PostLoginRes(userIdx);
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해제 및 대체해주세요!  **************** //
            String jwt = jwtService.createJwt(userIdx);
            String status = userDao.getUserStatus(userIdx);
            if(!status.equals("ACTIVE")){
                throw new BaseException(INVALID_USER);
            } else{
                return new PostStoreLoginRes(userIdx,storeIdx,storeName,jwt);
            }
//  **************************************************************************

        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    //로그아웃
    @Transactional
    public PostLogoutRes logOut(int userIdx) throws BaseException {
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해제 및 대체해주세요!  **************** //
            String jwt = jwtService.createEmptyJwt(userIdx);
            String userNickname = userDao.getUserNickname(userIdx);
            return new PostLogoutRes(userIdx,userNickname,jwt);
//  **************************************************************************
    }
}

package spring.reborn.domain.user;

import spring.reborn.config.BaseException;
import spring.reborn.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    // 해당 userIdx를 갖는 User의 포인트 조회
    public GetUserPointRes getUserPoint(int userIdx) throws BaseException {
        try {
            GetUserPointRes getUserPointRes = userDao.getUserPoint(userIdx);
            return getUserPointRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}

package spring.reborn.domain.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    /**
     * DAO관련 함수코드의 전반부는 크게 String ~~~Query와 Object[] ~~~~Params, jdbcTemplate함수로 구성되어 있습니다.(보통은 동적 쿼리문이지만, 동적쿼리가 아닐 경우, Params부분은 없어도 됩니다.)
     * Query부분은 DB에 SQL요청을 할 쿼리문을 의미하는데, 대부분의 경우 동적 쿼리(실행할 때 값이 주입되어야 하는 쿼리) 형태입니다.
     * 그래서 Query의 동적 쿼리에 입력되어야 할 값들이 필요한데 그것이 Params부분입니다.
     * Params부분은 클라이언트의 요청에서 제공하는 정보(~~~~Req.java에 있는 정보)로 부터 getXXX를 통해 값을 가져옵니다. ex) getEmail -> email값을 가져옵니다.
     *      Notice! get과 get의 대상은 카멜케이스로 작성됩니다. ex) item -> getItem, password -> getPassword, email -> getEmail, userIdx -> getUserIdx
     * 그 다음 GET, POST, PATCH 메소드에 따라 jabcTemplate의 적절한 함수(queryForObject, query, update)를 실행시킵니다(DB요청이 일어납니다.).
     *      Notice!
     *      POST, PATCH의 경우 jdbcTemplate.update
     *      GET은 대상이 하나일 경우 jdbcTemplate.queryForObject, 대상이 복수일 경우, jdbcTemplate.query 함수를 사용합니다.
     * jdbcTeplate이 실행시킬 때 Query 부분과 Params 부분은 대응(값을 주입)시켜서 DB에 요청합니다.
     * <p>
     * 정리하자면 < 동적 쿼리문 설정(Query) -> 주입될 값 설정(Params) -> jdbcTemplate함수(Query, Params)를 통해 Query, Params를 대응시켜 DB에 요청 > 입니다.
     * <p>
     * <p>
     * DAO관련 함수코드의 후반부는 전반부 코드를 실행시킨 후 어떤 결과값을 반환(return)할 것인지를 결정합니다.
     * 어떠한 값을 반환할 것인지 정의한 후, return문에 전달하면 됩니다.
     * ex) return this.jdbcTemplate.query( ~~~~ ) -> ~~~~쿼리문을 통해 얻은 결과를 반환합니다.
     */

    /**
     * 참고 링크
     * https://jaehoney.tistory.com/34 -> JdbcTemplate 관련 함수에 대한 설명
     * https://velog.io/@seculoper235/RowMapper%EC%97%90-%EB%8C%80%ED%95%B4 -> RowMapper에 대한 설명
     */

    // 회원가입
    @Transactional
    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (userId, userEmail, userPwd, userNickname, userImg, userAdAgreement, userAddress, userLikes) VALUES (?,?,?,?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postUserReq.getUserId(), postUserReq.getUserEmail(), postUserReq.getUserPwd(), postUserReq.getUserNickname(), postUserReq.getUserImg(), postUserReq.getUserAdAgreement(), postUserReq.getUserAddress(), postUserReq.getUserLikes().name()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    // 해당 userIdx를 갖는 유저의 닉네임조회
    public String getUserNickname(int userIdx) {
        String getUserQuery = "select userNickname from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> (
                        rs.getString("userNickname")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 userIdx를 갖는 유저의 status조회
    public String getUserStatus(int userIdx) {
        String getUserStatusQuery = "select status from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserStatusQuery,
                (rs, rowNum) -> (
                        rs.getString("status")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 userIdx를 갖는 유저의 userType조회
    public String getUserType(int userIdx) {
        String getUserStatusQuery = "select userType from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserStatusQuery,
                (rs, rowNum) -> (
                        rs.getString("userType")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 스토어 회원가입
    @Transactional
    public int createUserStore(PostUserStoreReq postUserStoreReq) {
        // DB의 Store 테이블에 스토어 데이터 삽입.
//        String createUserStoreQuery = "START TRANSACTION;\n" +
//                "INSERT INTO store (storeName, storeRegister, storeImage, storeAddress, storeInfo, category) VALUES (?,?,?,?,?,?);\n" +
//                "INSERT INTO user (storeIdx, userEmail, userPwd, userType) VALUES (last_insert_id(), ?,?, 'STORE');\n" +
//                "COMMIT;"; // 실행될 동적 쿼리문
//        Object[] createUserParams = new Object[]{postUserStoreReq.getStoreName(), postUserStoreReq.getStoreRegister(), postUserStoreReq.getStoreImage(), postUserStoreReq.getStoreAddress(), postUserStoreReq.getStoreInfo(), postUserStoreReq.getCategory(), postUserStoreReq.getUserEmail(), postUserStoreReq.getUserPwd()}; // 동적 쿼리의 ?부분에 주입될 값
//        this.jdbcTemplate.update(createUserStoreQuery, createUserParams);

        try {
            // DB의 User 테이블에 스토어 데이터 삽입.
            String createUserStoreQuery = "INSERT INTO User (userId, userEmail, userPwd, userImg, userAdAgreement, userType) VALUES (?,?,?,?,?,'STORE');";
            Object[] createUserParams = new Object[]{postUserStoreReq.getUserId(), postUserStoreReq.getUserEmail(), postUserStoreReq.getUserPwd(), postUserStoreReq.getUserImg(), postUserStoreReq.getUserAdAgreement()};
            this.jdbcTemplate.update(createUserStoreQuery, createUserParams);
        } catch (Exception exception) {
            System.out.println(exception);
        }

        try{
            // DB의 Store 테이블에 스토어 데이터 삽입.
            String createUserStoreQuery = "INSERT INTO Store (userIdx, storeName, storeRegister, storeImage, storeAddress, storeDescription, category, storeScore) VALUES (last_insert_id(), ?,?,?,?,?,?, 0.0)";
            Object[] createUserParams = new Object[]{postUserStoreReq.getStoreName(), postUserStoreReq.getStoreRegister(), postUserStoreReq.getStoreImage(), postUserStoreReq.getStoreAddress(), postUserStoreReq.getStoreDescription(), postUserStoreReq.getCategory().name()};
            this.jdbcTemplate.update(createUserStoreQuery, createUserParams);
        } catch (Exception exception) {
            System.out.println(exception);
        }

        String getStoreIdParams = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(getStoreIdParams, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    // 해당 storeIdx를 갖는 스토어 정보 조회
    @Transactional
    public PostUserStoreRes getStoreInform(int storeIdx){
        String getStoreQuery = "select storeIdx, userIdx, storeName from Store where storeIdx = ?";

        return this.jdbcTemplate.queryForObject(getStoreQuery,
                (rs, rowNum) -> new PostUserStoreRes(
                        rs.getInt("storeIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("storeName"),
                        rs.getString("storeName")),
                storeIdx);
    }

    // 해당 storeIdx를 갖는 스토어의 이름조회
    public String getStoreName(int storeIdx) {
        String getStoreNameQuery = "select storeName from Store where storeIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        return this.jdbcTemplate.queryForObject(getStoreNameQuery,
                (rs, rowNum) -> (
                        rs.getString("storeName")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                storeIdx); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 이메일 확인
    @Transactional
    public int checkUserEmail(String userEmail) {
        String checkEmailQuery = "select exists(select userEmail from User where userEmail = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkEmailParams = userEmail; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // ID 확인
    @Transactional
    public int checkUserId(String userId) {
        String checkIdQuery = "select exists(select userId from User where userId = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkIdParams = userId; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                checkIdParams); // checkIDQuery, checkIDParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 userIdx를 갖는 유저의 포인트조회
    @Transactional
    public GetUserPointRes getUserPoint(int userIdx) {
        String getUserPointQuery = "select userPoint from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserPointParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserPointQuery,
                (rs, rowNum) -> new GetUserPointRes(
                        rs.getInt("userPoint")),// RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserPointParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 포인트 적립, 취소 - hyerm
    @Transactional
    public PatchUserPointRes editUserPoint(@RequestBody PatchUserPointReq patchUserPointReq) {
        String getUserPointQuery = "select userPoint from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        Object[] getUserPointParams = new Object[]{
                patchUserPointReq.getUserIdx(),}; // 동적 쿼리의 ?부분에 주입될 값
        Integer oldPoint = jdbcTemplate.queryForObject(
                getUserPointQuery,getUserPointParams, Integer.class);

        Integer newPoint = oldPoint + patchUserPointReq.getAddPoint();

        String editUserPointQuery = "UPDATE User SET userPoint=? WHERE User.userIdx=?";
        Object[] editUserPointParams = new Object[]{
                newPoint,
                patchUserPointReq.getUserIdx(),}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(editUserPointQuery, editUserPointParams);

        String editUserPointResQuery = "SELECT * FROM User WHERE User.userIdx=?";
        Object[] editUserPointResParams = new Object[]{
                patchUserPointReq.getUserIdx(),};

        return this.jdbcTemplate.queryForObject(editUserPointResQuery,
                (rs, rowNum) -> new PatchUserPointRes(
                        rs.getInt("userIdx"),
                        rs.getString("userEmail"),
                        patchUserPointReq.getAddPoint(),
                        rs.getInt("userPoint")),
                editUserPointResParams
        );
    }

    // 해당 userIdx를 갖는 유저 정보 조회
    @Transactional
    public GetUserInformRes getUserInform(int userIdx) {
        String getUserQuery = "select * from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserInformRes(
                        rs.getString("userImg"),
                        rs.getString("userNickname"),
                        rs.getString("userAddress"),
                        rs.getString("userLikes")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 email을 갖는 유저 아이디 조회
    @Transactional
    public GetUserIdRes getUserIdInform(String email){
        String getUserIdQuery = "select userId, userImg, createdAt from User where userEmail = ?"; // 해당 userEmail을 만족하는 유저를 조회하는 쿼리문
        String checkEmailParams = email; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(getUserIdQuery,
                (rs, rowNum) -> new GetUserIdRes(
                        rs.getString("userId"),
                        rs.getString("userImg"),
                        rs.getString("createdAt")),
                checkEmailParams);
    }

    // 이웃 회원탈퇴
    @Transactional
    public int modifyUserStatus(int userIdx) {
        String modifyUserStatusQuery = "update User set status = ?, userNickname = ? where userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 status로 변경한다.
        Object[] modifyUserStatusParams = new Object[]{"DELETE", "탈퇴 회원", userIdx}; // 주입될 값들(status, userIdx) 순

        return this.jdbcTemplate.update(modifyUserStatusQuery, modifyUserStatusParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)

    }

    // 스토어 회원탈퇴
    @Transactional
    public int modifyStoreStatus(int userIdx) {
        String modifyStoreStatusQuery = "update Store set status = ? where userIdx = ? "; // 해당 storeIdx를 만족하는 Store를 해당 status로 변경한다.
        Object[] modifyStoreStatusParams = new Object[]{"DELETE", userIdx}; // 주입될 값들(status, userIdx) 순

        this.jdbcTemplate.update(modifyStoreStatusQuery, modifyStoreStatusParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)

        modifyStoreStatusQuery = "update User set status = ? where userIdx = ? "; // 해당 storeIdx를 만족하는 User를 해당 status로 변경한다.
        modifyStoreStatusParams = new Object[]{"DELETE", userIdx}; // 주입될 값들(status, userIdx) 순

        return this.jdbcTemplate.update(modifyStoreStatusQuery, modifyStoreStatusParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 회원정보 수정
    @Transactional
    public int modifyUserInform(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set userImg = ?, userNickname = ?, userAddress = ?, userLikes = ? where userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserImg(), patchUserReq.getUserNickname(), patchUserReq.getUserAddress(), patchUserReq.getUserLikes(), patchUserReq.getUserIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }
    
    // 로그인: 해당 email에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select * from User where userId = ?"; // 해당 id를 만족하는 User의 정보들을 조회한다.
        String getPwdParams = postLoginReq.getUserId(); // 주입될 id값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("userEmail"),
                        rs.getString("userPwd"),
                        rs.getString("userNickname"),
                        rs.getInt("userPoint"),
                        rs.getString("userImg"),
                        rs.getString("userAdAgreement"),
                        rs.getString("userAddress"),
                        rs.getString("userLikes"),
                        rs.getString("status")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getPwdParams
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 스토어 로그인: 해당 email에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public Store getStorePwd(int userIdx) {
        String getStorePwdQuery = "select * from Store where userIdx = ?"; // 해당 userIdx를 만족하는 Store의 정보들을 조회한다.
        //String getPwdParams = postLoginReq.getUserEmail(); // 주입될 email값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.
        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(getStorePwdQuery,
                (rs, rowNum) -> new Store(
                        rs.getInt("storeIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("storeName"),
                        rs.getString("storeRegister"),
                        rs.getString("storeImage"),
                        rs.getString("storeAddress"),
                        rs.getString("storeDescription"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getInt("storeScore")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 유저 비번 임시 비번으로 변경
    @Transactional
    public int modifyUserPwd(PatchUserPwdResetReq patchUserPwdResetReq) {
        String modifyUserPwdQuery = "update User set userPwd = ? where userId = ? "; // 해당 userId 만족하는 User의 userPwd를 해당 userPed로 변경한다.
        Object[] modifyUserPwdParams = new Object[]{patchUserPwdResetReq.getUserPwd(), patchUserPwdResetReq.getUserId()}; // 주입될 값들(status, userIdx) 순

        return this.jdbcTemplate.update(modifyUserPwdQuery, modifyUserPwdParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)

    }

    // 비번 가져오기(비번 변경용): 해당 userIdx에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public String getPwd2(int userIdx) {
        String getPwdQuery = "select userPwd from User where userIdx = ?"; // 해당 id를 만족하는 User의 정보들을 조회한다.
        int getPwdParams = userIdx; // 주입될 id값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                String.class,
                getPwdParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 유저 비번 변경
    @Transactional
    public int modifyUserPwd2(PatchUserPwdReq patchUserPwdReq) {
        String modifyUserPwdQuery = "update User set userPwd = ? where userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 status로 변경한다.
        Object[] modifyUserPwdParams = new Object[]{patchUserPwdReq.getUserNewPwd(), patchUserPwdReq.getUserIdx()}; // 주입될 값들(status, userIdx) 순

        return this.jdbcTemplate.update(modifyUserPwdQuery, modifyUserPwdParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)

    }


    public String getUserLikes(int userIdx) throws BaseException {
        try{
            String selectUserLikesQuery = "select category from User where userIdx = ? ";
            return jdbcTemplate.queryForObject(selectUserLikesQuery, String.class ,userIdx);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 애플 회원가입
    @Transactional
    public int createAppleUser(PostAppleUserReq postAppleUserReq) {
        String createUserQuery = "insert into User (userEmail, userNickname, userImg, userAdAgreement, userAddress, userLikes, userId, userPwd) VALUES (?,?,?,?,?,?,?,?,'apple')"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postAppleUserReq.getUserEmail(), postAppleUserReq.getUserNickname(), postAppleUserReq.getUserImg(), postAppleUserReq.getUserAdAgreement(), postAppleUserReq.getUserAddress(), postAppleUserReq.getUserLikes().name(), postAppleUserReq.getUserEmail()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    // 애플 로그인: 해당 email에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public User getApplePwd(PostAppleLoginReq postAppleLoginReq) {
        String getPwdQuery = "select * from User where userEmail = ?"; // 해당 id를 만족하는 User의 정보들을 조회한다.
        String getPwdParams = postAppleLoginReq.getUserEmail(); // 주입될 id값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("userEmail"),
                        rs.getString("userPwd"),
                        rs.getString("userNickname"),
                        rs.getInt("userPoint"),
                        rs.getString("userImg"),
                        rs.getString("userAdAgreement"),
                        rs.getString("userAddress"),
                        rs.getString("userLikes"),
                        rs.getString("status")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getPwdParams
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
}

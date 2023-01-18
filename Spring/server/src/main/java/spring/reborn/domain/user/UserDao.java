package spring.reborn.domain.user;

import org.springframework.transaction.annotation.Transactional;
import spring.reborn.domain.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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
    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (userEmail, userPwd, userNickname, userImg, userAdAgreement, userBirthDate, userAddress, userLikes) VALUES (?,?,?,?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postUserReq.getUserEmail(), postUserReq.getUserPwd(), postUserReq.getUserNickname(), postUserReq.getUserImg(), postUserReq.getUserAdAgreement(), postUserReq.getUserBirthDate(), postUserReq.getUserAddress(), postUserReq.getUserLikes().name()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        // email -> postUserReq.getEmail(), password -> postUserReq.getPassword(), nickname -> postUserReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 User Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
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

        // DB의 User 테이블에 스토어 데이터 삽입.
        String createUserStoreQuery = "INSERT INTO user (userEmail, userPwd, userAdAgreement, userType) VALUES (?,?,?,'STORE');";
        Object[] createUserParams = new Object[]{postUserStoreReq.getUserEmail(), postUserStoreReq.getUserPwd(), postUserStoreReq.getUserAdAgreement()};
        this.jdbcTemplate.update(createUserStoreQuery, createUserParams);

        // DB의 Store 테이블에 스토어 데이터 삽입.
        createUserStoreQuery = "INSERT INTO store (userIdx, storeName, storeRegister, storeImage, storeAddress, storeDescription, category, storeScore) VALUES (last_insert_id(), ?,?,?,?,?,?, 0.0)";
        createUserParams = new Object[]{postUserStoreReq.getStoreName(), postUserStoreReq.getStoreRegister(), postUserStoreReq.getStoreImage(), postUserStoreReq.getStoreAddress(), postUserStoreReq.getStoreDescription(), postUserStoreReq.getCategory().name()};
        this.jdbcTemplate.update(createUserStoreQuery, createUserParams);


        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    // 이메일 확인
    public int checkUserEmail(String userEmail) {
        String checkEmailQuery = "select exists(select userEmail from User where userEmail = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkEmailParams = userEmail; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 userIdx를 갖는 유저의 포인트조회
    public GetUserPointRes getUserPoint(int userIdx) {
        String getUserPointQuery = "select userPoint from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserPointParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserPointQuery,
                (rs, rowNum) -> new GetUserPointRes(
                        rs.getInt("userPoint")),// RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserPointParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }


    // 해당 userIdx를 갖는 유저 정보 조회
    public GetUserInformRes getUserInform(int userIdx) {
        String getUserQuery = "select * from User where userIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserInformRes(
                        rs.getString("userImg"),
                        rs.getString("userNickname"),
                        rs.getString("userAddress"),
                        rs.getString("userBirthDate"),
                        rs.getString("userLikes")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환

    // 회원탈퇴
    public int modifyUserStatus(PatchUserStatusReq patchUserStatusReq) {
        String modifyUserStatusQuery = "update User set status = ? where userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 status로 변경한다.
        Object[] modifyUserStatusParams = new Object[]{patchUserStatusReq.getStatus(), patchUserStatusReq.getUserIdx()}; // 주입될 값들(status, userIdx) 순

        return this.jdbcTemplate.update(modifyUserStatusQuery, modifyUserStatusParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)

    }
}

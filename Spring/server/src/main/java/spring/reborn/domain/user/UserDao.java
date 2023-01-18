package spring.reborn.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spring.reborn.domain.user.model.PostUserStoreReq;

import javax.sql.DataSource;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 스토어 회원가입
    public int createUserStore(PostUserStoreReq postUserStoreReq) {
        // DB의 Store 테이블에 스토어 데이터 삽입.
//        String createUserStoreQuery = "START TRANSACTION;\n" +
//                "INSERT INTO store (storeName, storeRegister, storeImage, storeAddress, storeInfo, category) VALUES (?,?,?,?,?,?);\n" +
//                "INSERT INTO user (storeIdx, userEmail, userPwd, userType) VALUES (last_insert_id(), ?,?, 'STORE');\n" +
//                "COMMIT;"; // 실행될 동적 쿼리문
//        Object[] createUserParams = new Object[]{postUserStoreReq.getStoreName(), postUserStoreReq.getStoreRegister(), postUserStoreReq.getStoreImage(), postUserStoreReq.getStoreAddress(), postUserStoreReq.getStoreInfo(), postUserStoreReq.getCategory(), postUserStoreReq.getUserEmail(), postUserStoreReq.getUserPwd()}; // 동적 쿼리의 ?부분에 주입될 값
//        this.jdbcTemplate.update(createUserStoreQuery, createUserParams);

        // DB의 Store 테이블에 스토어 데이터 삽입.
        String createUserStoreQuery = "INSERT INTO store (storeName, storeRegister, storeImage, storeAddress, storeInfo, category) VALUES (?,?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserStoreReq.getStoreName(), postUserStoreReq.getStoreRegister(), postUserStoreReq.getStoreImage(), postUserStoreReq.getStoreAddress(), postUserStoreReq.getStoreInfo(), postUserStoreReq.getCategory()};
        this.jdbcTemplate.update(createUserStoreQuery, createUserParams);

        // DB의 User 테이블에 스토어 데이터 삽입.
        createUserStoreQuery = "INSERT INTO user (storeIdx, userEmail, userPwd, userType) VALUES (last_insert_id(), ?,?, 'STORE');";
        createUserParams = new Object[]{postUserStoreReq.getUserEmail(), postUserStoreReq.getUserPwd()};
        this.jdbcTemplate.update(createUserStoreQuery, createUserParams);


        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    // 이메일 확인
    public int checkUserEmail(String email) {
        String checkEmailQuery = "select exists(select userEmail from User where userEmail = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkEmailParams = email; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
}

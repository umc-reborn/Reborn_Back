package spring.reborn.domain.jjim;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.jjim.model.JjimReq;
import spring.reborn.domain.jjim.model.JjimRes;
import spring.reborn.domain.jjim.model.JjimStoreRes;
import spring.reborn.domain.review.model.GetReviewRes;

import javax.sql.DataSource;

import java.util.List;

import static javax.swing.UIManager.getInt;
import static javax.swing.UIManager.getString;
import static spring.reborn.config.BaseResponseStatus.*;

@Repository
public class JjimDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Transactional
    public JjimRes changeJjim(JjimReq jjimReq) throws BaseException {
        try {
            // 찜 주체가 스토어인지 검증
            String userValidQuery = "select userIdx from User where userType = 'CONSUMER' and userIdx=?;";
            try {
                Long userIdx = this.jdbcTemplate.queryForObject(
                        "select userIdx from User where userType = 'CONSUMER' and userIdx=?;",
                        Long.class,
                        jjimReq.getUserIdx());
            } catch (DataAccessException e) {
                throw new BaseException(CAN_NOT_JJIM_STORE_TO_STORE);
            }

            System.out.println("[검증완료] 찜 주체 스토어 아님");

            JjimRes jjimRes = new JjimRes();

            // 찜이 존재하는 경우
            try {
                Integer jjimIdx = this.jdbcTemplate.queryForObject(
                        "select jjimIdx from Jjim where storeIdx = ? and userIdx = ?",
                        int.class,
                        jjimReq.getStoreIdx(), jjimReq.getUserIdx());
                jjimRes = deleteJjim (jjimReq, jjimIdx);
                System.out.println("찜을 해제 성공");

            }
            // 찜이 존재하지 않는 경우
            catch (DataAccessException e) {
                jjimRes = createJjim(jjimReq);
                System.out.println("찜을 등록 성공");
            }

            return jjimRes;

        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException(e.getStatus());
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public JjimRes deleteJjim (JjimReq jjimReq, Integer jjimIdx) throws BaseException{
        // 반환값 생성
        JjimRes jjimRes = this.jdbcTemplate.queryForObject(
                "select Jjim.jjimIdx,User.userEmail,Store.storeName\n" +
                        "from Jjim, User, Store " +
                        "where Jjim.jjimIdx=? and Jjim.userIdx = User.userIdx and Jjim.storeIdx = Store.storeIdx;",
                new Object[]{jjimIdx},
                (rs, rowNum) -> new JjimRes(
                        "찜이 해제되었습니다.",
                        rs.getInt("jjimIdx"),
                        rs.getString("userEmail"),
                        rs.getString("storeName"))
        );

        // 찜 해제
        String deleteJjimQuery = "delete from Jjim  where jjimIdx = ?";
        if (this.jdbcTemplate.update(deleteJjimQuery, jjimIdx) == 0) {
            throw new BaseException(FAIL_DELETE_JJIM);
        }

        return jjimRes;
    }

    @Transactional
    public JjimRes createJjim (JjimReq jjimReq) throws BaseException{
        // 찜 등록
        String createJjimQuery = "insert into Jjim (storeIdx, userIdx) values (?,?)";
        if (this.jdbcTemplate.update
                (createJjimQuery, jjimReq.getStoreIdx(), jjimReq.getUserIdx()) == 0) {
            throw new BaseException(FAIL_INSERT_JJIM);
        }

        // 반환값 생성
        JjimRes jjimRes = this.jdbcTemplate.queryForObject(
                "select Jjim.jjimIdx,User.userEmail,Store.storeName\n" +
                        "from Jjim, User, Store " +
                        "where Jjim.jjimIdx=last_insert_id() and Jjim.userIdx = User.userIdx and Jjim.storeIdx = Store.storeIdx;",
                (rs, rowNum) -> new JjimRes(
                        "찜이 등록되었습니다.",
                        rs.getInt("jjimIdx"),
                        rs.getString("userEmail"),
                        rs.getString("storeName"))
        );

        return jjimRes;
    }

    @Transactional
    public Integer countJjim(Integer userIdx) throws BaseException {
        String countJjimQuery = "SELECT COUNT(Jjim.jjimIdx) FROM reborn.Jjim WHERE userIdx = ?;"; // 실행될 동적 쿼리문
        Object[] countJjimParams = new Object[]{
                userIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        Integer count = jdbcTemplate.queryForObject(
                countJjimQuery, countJjimParams, Integer.class);

        return count;
    }

    @Transactional
    public List<JjimStoreRes> getJjimStoreList(Integer userIdx) throws BaseException {
        String getJjimStoreListQuery = "SELECT j.jjimIdx, j.storeIdx, s.storeName, s.storeImage, s.category, s.storeScore " +
                "FROM Jjim j JOIN Store s\n" +
                "ON j.storeIdx = s.storeIdx\n" +
                "WHERE j.userIdx = ?;";
        Object[] getJjimStoreListParams = new Object[]{
                userIdx,}; // 동적 쿼리의 ?부분에 주입될 값

        List<JjimStoreRes> jjimStoreRes = this.jdbcTemplate.query(getJjimStoreListQuery,
                (rs, rowNum) -> new JjimStoreRes(
                        rs.getInt("jjimIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("storeImage"),
                        rs.getString("category"),
                        rs.getFloat("storeScore")),
                getJjimStoreListParams
        );

        return jjimStoreRes;
    }

    // 작성중
    @Transactional
    public List<JjimStoreRes> getSortedJjimStoreList(Integer userIdx, String sort) throws BaseException {
        String getJjimStoreListQuery =
                "SELECT j.jjimIdx, j.storeIdx, s.storeName, s.storeImage, s.category, s.storeScore, \n" +
                        "(SELECT count(a.jjimIdx) FROM Jjim a JOIN Store b ON a.storeIdx = b.storeIdx WHERE a.storeIdx=j.storeIdx) jjimCnt\n" +
                        "FROM Jjim j JOIN Store s\n" +
                        "ON j.storeIdx = s.storeIdx\n" +
                        "WHERE j.userIdx = ?\n" +
                        "ORDER BY ?;";
        Object[] getJjimStoreListParams = new Object[]{
                userIdx, sort}; // 동적 쿼리의 ?부분에 주입될 값

        List<JjimStoreRes> jjimStoreRes = this.jdbcTemplate.query(getJjimStoreListQuery,
                (rs, rowNum) -> new JjimStoreRes(
                        rs.getInt("jjimIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("storeImage"),
                        rs.getString("category"),
                        rs.getFloat("storeScore")),
                getJjimStoreListParams
        );

        return jjimStoreRes;
    }
}

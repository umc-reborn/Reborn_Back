package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
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
import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Repository
public class JjimDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public JjimRes createJjim(JjimReq jjimReq) throws BaseException {
        try {
            String createJjimQuery = "insert into Jjim (storeIdx, userIdx) values (?,?)";
            Object[] createJjimParams = new Object[]{jjimReq.getStoreIdx(), jjimReq.getUserIdx()};
            this.jdbcTemplate.update(createJjimQuery, createJjimParams);

            String postJjimResponseQuery =
                    "select Jjim.jjimIdx,User.userEmail,Store.storeName\n" +
                            "from Jjim, User, Store " +
                            "where Jjim.jjimIdx=last_insert_id() and Jjim.userIdx = User.userIdx and Jjim.storeIdx = Store.storeIdx;";

            Object[] postJjimParams = new Object[]{};

            //queryForObject : DTO 하나 값 반환
            JjimRes jjimRes = this.jdbcTemplate.queryForObject(postJjimResponseQuery,
                    postJjimParams,
                    (rs, rowNum) -> new JjimRes(
                            rs.getInt("jjimIdx"),
                            rs.getString("userEmail"),
                            rs.getString("storeName"))
            );

            return jjimRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public JjimRes deleteJjim(JjimReq jjimReq) throws BaseException {
        try {
            String postJjimResponseQuery =
                    "select Jjim.jjimIdx,User.userEmail,Store.storeName\n" +
                            "from Jjim, User, Store " +
                            "where Jjim.jjimIdx=last_insert_id() and Jjim.userIdx = User.userIdx and Jjim.storeIdx = Store.storeIdx;";
            Object[] postJjimParams = new Object[]{};

            //queryForObject : DTO 하나 값 반환
            JjimRes jjimRes = this.jdbcTemplate.queryForObject(postJjimResponseQuery,
                    postJjimParams,
                    (rs, rowNum) -> new JjimRes(
                            rs.getInt("jjimIdx"),
                            rs.getString("userEmail"),
                            rs.getString("storeName"))
            );

            String deleteJjimQuery = "delete from Jjim where storeIdx=? and userIdx=?";
            Object[] deleteJjimParams = new Object[]{jjimReq.getStoreIdx(), jjimReq.getUserIdx()};
            this.jdbcTemplate.update(deleteJjimQuery, deleteJjimParams);

            return jjimRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
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
                        rs.getInt("storeScore")),
                getJjimStoreListParams
        );

        return jjimStoreRes;
    }
}

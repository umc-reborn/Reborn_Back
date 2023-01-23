package spring.reborn.domain.reborn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.model.*;

import javax.sql.DataSource;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Repository
public class RebornDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public int createReborn(PostRebornReq postRebornReq) throws BaseException {
        try {
            System.out.println("dao 시작");
            String createRebornQuery = "insert into Reborn (storeIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt) values (?,?,?,?,?,?,?)";
            Object[] createRebornParams = new Object[]{postRebornReq.getStoreIdx(), postRebornReq.getProductName(), postRebornReq.getProductGuide(), postRebornReq.getProductComment(), postRebornReq.getProductImg(), postRebornReq.getProductLimitTime(), postRebornReq.getProductCnt()};
            this.jdbcTemplate.update(createRebornQuery, createRebornParams);
            System.out.println("createDinnerQuery 끝");

            System.out.println("lastInsertIdQuery 시작");
            String lastInsertIdQuery = "select last_insert_id()";
            int rebornIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
            System.out.println("lastInsertIdQuery 끝 : " + rebornIdx);

            return rebornIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetRebornRes> getReborns(Integer storeIdx) {
        System.out.println("dao 시작");
        String getRebornsQuery = "SELECT rebornIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt, status FROM Reborn WHERE storeIdx = ? AND status = ?";
        List<GetRebornRes> result = this.jdbcTemplate.query(
                getRebornsQuery,
                (rs, rowNum) -> new GetRebornRes(
                        rs.getInt("rebornIdx"),
                        rs.getString("productName"),
                        rs.getString("productGuide"),
                        rs.getString("productComment"),
                        rs.getString("productImg"),
                        rs.getString("productLimitTime"),
                        rs.getInt("productCnt"),
                        rs.getString("status"))
                        ,
                storeIdx,
                "ACTIVE"
        );
        return result;
    }

    public List<GetInProgressRes> getInProgressReborns(Integer userIdx) {
        System.out.println("dao 시작");
        String getRebornsQuery = "SELECT T.rebornTaskIdx, T.rebornIdx, S.storeIdx, S.storeName, S.category, R.productName, R.productImg, R.productLimitTime, R.productCnt FROM Reborn AS R LEFT OUTER JOIN RebornTask AS T ON T.rebornIdx = R.rebornIdx LEFT OUTER JOIN Store AS S ON R.storeIdx = S.storeIdx WHERE (S.userIdx = ? AND T.status = ?)";
        List<GetInProgressRes> result = this.jdbcTemplate.query(
                getRebornsQuery,
                (rs, rowNum) -> new GetInProgressRes(
                        rs.getInt("rebornTaskIdx"),
                        rs.getInt("rebornIdx"),
                        rs.getInt("storeIdx"),
                        rs.getString("storeName"),
                        rs.getString("category"),
                        rs.getString("productName"),
                        rs.getString("productImg"),
                        rs.getString("productLimitTime"),
                        rs.getInt("productCnt"))
                ,
                userIdx,
                "ACTIVE"
        );
        return result;
    }
}

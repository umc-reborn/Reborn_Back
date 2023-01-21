package spring.reborn.domain.rebornTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.model.*;
import spring.reborn.domain.rebornTask.model.*;

import javax.sql.DataSource;
import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Repository
public class RebornTaskDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

//    @Transactional
//    public int createReborn(PostRebornReq postRebornReq) throws BaseException {
//        try {
//            System.out.println("dao 시작");
//            String createRebornQuery = "insert into Reborn (storeIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt) values (?,?,?,?,?,?,?)";
//            Object[] createRebornParams = new Object[]{postRebornReq.getStoreIdx(), postRebornReq.getProductName(), postRebornReq.getProductGuide(), postRebornReq.getProductComment(), postRebornReq.getProductImg(), postRebornReq.getProductLimitTime(), postRebornReq.getProductCnt()};
//            this.jdbcTemplate.update(createRebornQuery, createRebornParams);
//            System.out.println("createDinnerQuery 끝");
//
//            System.out.println("lastInsertIdQuery 시작");
//            String lastInsertIdQuery = "select last_insert_id()";
//            int rebornIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
//            System.out.println("lastInsertIdQuery 끝 : " + rebornIdx);
//
//            return rebornIdx;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

//    public List<GetRebornHistoryRes> getRebornHistories(Integer userIdx) {
//        System.out.println("dao 시작");
//        String getRebornsQuery = "SELECT S.storeName, S.storeImg, S.storeAddress, S.storeScore, T.rebornTaskIdx, T.createdAt FROM Store AS S, RebornTask AS T WHERE userIdx = ? AND status = ?";
//        List<GetRebornHistoryRes> result = this.jdbcTemplate.query(
//                getRebornsQuery,
//                (rs, rowNum) -> new GetRebornRes(
//                        rs.getString("productName"),
//                        rs.getString("productGuide"),
//                        rs.getString("productComment"),
//                        rs.getString("productImg"),
//                        rs.getString("productLimitTime"),
//                        rs.getInt("productCnt"),
//                        rs.getString("status"))
//                ,
//                userIdx,
//                "ACTIVE"
//        );
//        return result;
//    }
}

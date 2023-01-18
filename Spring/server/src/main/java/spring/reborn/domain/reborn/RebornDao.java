package spring.reborn.domain.reborn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.model.GetRebornRes;
import spring.reborn.domain.reborn.model.PostRebornReq;
import spring.reborn.src.reborn.model.*;

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
            String createRebornQuery = "insert into reborn (storeIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt) values (?,?,?,?,?,?,?)";
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
        String getRebornsQuery = "SELECT productName, productGuide, productComment, productImg, productLimitTime, productCnt FROM reborn WHERE storeIdx = ?";
        List<GetRebornRes> result = this.jdbcTemplate.query(
                getRebornsQuery,
                (rs2, rowNum2) -> new GetRebornRes(
                        rs2.getString("productName"),
                        rs2.getString("productGuide"),
                        rs2.getString("productComment"),
                        rs2.getString("productImg"),
                        rs2.getString("productLimitTime"),
                        rs2.getInt("productCnt"))
                        ,
                storeIdx
        );
        return result;
    }
}

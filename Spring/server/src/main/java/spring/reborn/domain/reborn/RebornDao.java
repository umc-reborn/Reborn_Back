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
            String createRebornQuery = "INSERT INTO Reborn (storeIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt) values (?,?,?,?,?,?,?)";
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
        String getRebornsQuery = "SELECT rebornIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt, status FROM Reborn WHERE (storeIdx = ? AND status != 'DELETE')";
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
                storeIdx
        );
        return result;
    }

    public List<GetRebornRes> getRebornsByStatus(Integer storeIdx, String status) {
        System.out.println("dao 시작");
        String getRebornsQuery = "SELECT rebornIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt, status FROM Reborn WHERE (storeIdx = ? AND status = ?)";
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
                status
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

    public int patchReborn(PatchRebornReq patchRebornReq) {
        String patchRebornQuery = "UPDATE Reborn SET productName = ?, productGuide = ?, productComment = ?, productImg = ?, productLimitTime = ?, productCnt = ? WHERE rebornIdx = ?";
        Object[] patchRebornParams = new Object[]{patchRebornReq.getProductName(), patchRebornReq.getProductGuide(), patchRebornReq.getProductComment(), patchRebornReq.getProductImg(), patchRebornReq.getProductLimitTime(), patchRebornReq.getProductCnt(), patchRebornReq.getRebornIdx()};

        return this.jdbcTemplate.update(patchRebornQuery, patchRebornParams);
    }

    public List<GetHistoryRes> getHistory(Integer userIdx) {
        System.out.println("dao 시작");
        String getHistroiesQuery = "SELECT T.rebornTaskIdx, S.storeName, S.storeScore, S.storeAddress, T.createdAt FROM Reborn AS R LEFT OUTER JOIN RebornTask AS T ON T.rebornIdx = R.rebornIdx LEFT OUTER JOIN Store AS S ON R.storeIdx = S.storeIdx WHERE (T.userIdx = ? AND T.status != 'INACTIVE')";
        List<GetHistoryRes> result = this.jdbcTemplate.query(
                getHistroiesQuery,
                (rs, rowNum) -> new GetHistoryRes(
                        rs.getInt("rebornTaskIdx"),
                        rs.getString("storeName"),
                        rs.getFloat("storeScore"),
                        rs.getString("storeAddress"),
                        rs.getString("createdAt"))
                ,
                userIdx
        );
        return result;
    }
    
    public GetHistroyDetailRes getHistoryDetail(Integer rebornTaskIdx) {
        System.out.println("dao 시작");
        String getHistoryQeury = "SELECT R.productName, R.productGuide, R.productComment, S.storeName, S.storeScore, S.storeAddress, T.productExchangeCode, T.createdAt, T.status FROM Reborn AS R LEFT OUTER JOIN RebornTask AS T ON T.rebornIdx = R.rebornIdx LEFT OUTER JOIN Store AS S ON R.storeIdx = S.storeIdx WHERE (T.rebornTaskIdx = ? AND T.status != 'DELETE')";
        return this.jdbcTemplate.queryForObject(getHistoryQeury,
                (rs, rowNum) -> new GetHistroyDetailRes(
                        rs.getString("productName"),
                        rs.getString("productGuide"),
                        rs.getString("productComment"),
                        rs.getString("storeName"),
                        rs.getFloat("storeScore"),
                        rs.getString("storeAddress"),
                        rs.getInt("productExchangeCode"),
                        rs.getString("createdAt"),
                        rs.getString("status")
                ),
                rebornTaskIdx
        );
    }

    @Transactional
    public int postHistory(int rebornTaskIdx) throws BaseException {
        try {
            this.jdbcTemplate.update("UPDATE RebornTask SET status = 'COMPLETE' WHERE rebornTaskIdx = ?", rebornTaskIdx);
            return 1;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int deleteProduct(int rebornIdx) {
        String deleteRebornQuery = "UPDATE Reborn SET status = 'DELETE' WHERE rebornIdx = ?";
        return this.jdbcTemplate.update(deleteRebornQuery, rebornIdx);
    }

    public int inactiveRebornTask(int rebornTaskIdx) {
        String inactiveRebornTaskQuery = "UPDATE RebornTask SET status = 'INACTIVE' WHERE rebornTaskIdx = ?";
        return this.jdbcTemplate.update(inactiveRebornTaskQuery, rebornTaskIdx);
    }


}

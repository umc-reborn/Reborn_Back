package spring.reborn.domain.reborn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.model.*;

import javax.sql.DataSource;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.*;

@Slf4j
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

    public List<GetRebornPageRes> getRebornsPage(Integer storeIdx) {
        System.out.println("dao 시작");
        String getRebornsPageQuery = "SELECT T.rebornTaskIdx, U.userNickname, R.productName, R.productGuide, R.productComment, R.productImg, R.productLimitTime, R.productCnt, T.status FROM RebornTask AS T LEFT OUTER JOIN Reborn AS R ON (T.rebornIdx = R.rebornIdx) LEFT OUTER JOIN User AS U ON (U.userIdx = T.userIdx) WHERE (storeIdx = ? AND T.status != 'DELETE')";
        List<GetRebornPageRes> result = this.jdbcTemplate.query(
                getRebornsPageQuery,
                (rs, rowNum) -> new GetRebornPageRes(
                        rs.getInt("rebornTaskIdx"),
                        rs.getString("userNickname"),
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

    public List<GetRebornPageRes> getRebornsPageByStatus(Integer storeIdx, String status) {
        System.out.println("dao 시작");
        String getRebornsPageQuery = "SELECT T.rebornTaskIdx, U.userNickname, R.productName, R.productGuide, R.productComment, R.productImg, R.productLimitTime, R.productCnt, T.status FROM RebornTask AS T LEFT OUTER JOIN Reborn AS R ON (T.rebornIdx = R.rebornIdx) LEFT OUTER JOIN User AS U ON (U.userIdx = T.userIdx) WHERE (storeIdx = ? AND T.status = ?)";
        List<GetRebornPageRes> result = this.jdbcTemplate.query(
                getRebornsPageQuery,
                (rs, rowNum) -> new GetRebornPageRes(
                        rs.getInt("rebornTaskIdx"),
                        rs.getString("userNickname"),
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
        String getHistoriesQuery = "SELECT T.rebornTaskIdx, S.storeName, S.storeScore, S.category, T.status, T.createdAt FROM Reborn AS R LEFT OUTER JOIN RebornTask AS T ON T.rebornIdx = R.rebornIdx LEFT OUTER JOIN Store AS S ON R.storeIdx = S.storeIdx WHERE (T.userIdx = ? AND T.status != 'INACTIVE')";
        List<GetHistoryRes> result = this.jdbcTemplate.query(
                getHistoriesQuery,
                (rs, rowNum) -> new GetHistoryRes(
                        rs.getInt("rebornTaskIdx"),
                        rs.getString("storeName"),
                        rs.getFloat("storeScore"),
                        rs.getString("category"),
                        rs.getString("status"),
                        rs.getString("createdAt"))
                ,
                userIdx
        );
        return result;
    }

    public GetHistroyDetailRes getHistoryDetail(Integer rebornTaskIdx) {
        System.out.println("dao 시작");
        String getHistoryQeury = "SELECT R.productName, R.productGuide, R.productComment, S.storeName, S.storeAddress, S.category, T.productExchangeCode, T.createdAt, T.status FROM Reborn AS R LEFT OUTER JOIN RebornTask AS T ON T.rebornIdx = R.rebornIdx LEFT OUTER JOIN Store AS S ON R.storeIdx = S.storeIdx WHERE (T.rebornTaskIdx = ? AND T.status != 'DELETE')";
        return this.jdbcTemplate.queryForObject(getHistoryQeury,
                (rs, rowNum) -> new GetHistroyDetailRes(
                        rs.getString("productName"),
                        rs.getString("productGuide"),
                        rs.getString("productComment"),
                        rs.getString("storeName"),
                        rs.getString("storeAddress"),
                        rs.getString("category"),
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

    public GetRebornRes findByUserId(Long userId) {
        String getRebornsQuery = "SELECT rebornIdx, productName, productGuide, productComment, productImg, productLimitTime, productCnt, status FROM Reborn WHERE (userIdx = ? AND status = ?)";
        GetRebornRes result = this.jdbcTemplate.queryForObject(
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
                userId, RebornStatus.ACTIVE);
        return result;
    }

    public int deleteProduct(int rebornIdx) {
        String deleteRebornQuery = "UPDATE Reborn SET status = 'DELETE' WHERE rebornIdx = ?";
        return this.jdbcTemplate.update(deleteRebornQuery, rebornIdx);
    }

    public int inactiveRebornTask(int rebornTaskIdx) {
        String inactiveRebornTaskQuery = "UPDATE RebornTask SET status = 'INACTIVE' WHERE rebornTaskIdx = ?";
        return this.jdbcTemplate.update(inactiveRebornTaskQuery, rebornTaskIdx);
    }

    public PatchRebornStatusRes ativeReborn(int rebornIdx) throws BaseException {
        System.out.println("dao start");

        String rebornStatus = this.jdbcTemplate.queryForObject("SELECT status FROM reborn.Reborn WHERE rebornIdx = ?;",
                new Object[]{rebornIdx}, String.class
        );

        System.out.println("rebornStatus"+rebornStatus);


        String activeRebornTaskQuery = "";
        PatchRebornStatusRes result = null;

        if (rebornStatus.equals("ACTIVE")){
            System.out.println("if (rebornStatus == \"ACTIVE\")");
            activeRebornTaskQuery = "UPDATE Reborn SET status = 'INACTIVE' WHERE rebornIdx = ?";
            result = new PatchRebornStatusRes(rebornIdx, "리본을 '비활성화'했습니다.");
        }
        else if (rebornStatus.equals("INACTIVE")){
            System.out.println("if (rebornStatus == \"INACTIVE\")");
            activeRebornTaskQuery = "UPDATE Reborn SET status = 'ACTIVE' WHERE rebornIdx = ?";
            result = new PatchRebornStatusRes(rebornIdx, "리본을 '활성화'했습니다.");
        }
        else {
            System.out.println("else");
            throw new BaseException(CAN_NOT_CHANGE_STATUS_TO_ACTIVE);
        }

        System.out.println("this.jdbcTemplate.update(activeRebornTaskQuery, rebornIdx); - 1");
        this.jdbcTemplate.update(activeRebornTaskQuery, rebornIdx);
        System.out.println("this.jdbcTemplate.update(activeRebornTaskQuery, rebornIdx); - 2");


        return result;
    }

    public void decreaseRebornProductCnt(Long rebornIdx) throws BaseException {
        try {
            String updateRebornQuery = "update Reborn " +
                    "set updatedAt = now(), productCnt = productCnt - 1, status = case when productCnt = 0 then 'INACTIVE' else 'ACTIVE' end " +
                    "where rebornIdx = ? and status = 'ACTIVE' and productCnt >0";

            if (this.jdbcTemplate.update(updateRebornQuery, rebornIdx) != 1) {
                throw new BaseException(UPDATE_FAIL_REBORN_PRODUCT_COUNT);
            }
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

}

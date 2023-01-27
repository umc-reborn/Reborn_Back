package spring.reborn.domain.rebornTask;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.model.*;
import spring.reborn.domain.rebornTask.model.PostRebornTaskReq;
import spring.reborn.domain.rebornTask.model.PostRebornTaskRes;

import javax.sql.DataSource;

import java.util.UUID;

import static spring.reborn.config.BaseResponseStatus.*;

@Slf4j
@Repository
public class RebornTaskDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Transactional
    public Long createRebornTask(PostRebornTaskReq postRebornTaskReq) throws BaseException {
        try {

            // todo 최적화 필요
            String countRebornTaskQuery = "select count(rebornTaskIdx) " +
                    "from RebornTask " +
                    "where rebornIdx = ? and status != 'EXPIRED'";

            String countRebornQuery = "select productCnt " +
                    "from Reborn " +
                    "where rebornIdx = ? and status = 'ACTIVE'";

            // 리본의 상품 수보다 이미 생성된 task 수가 많다면
            if(this.jdbcTemplate.queryForObject(countRebornQuery,new Object[]{postRebornTaskReq.getRebornIdx()},Integer.class)
                    <=
                    this.jdbcTemplate.queryForObject(countRebornTaskQuery, new Object[]{postRebornTaskReq.getRebornIdx()}, Integer.class))
            {
                throw new BaseException(NOT_ENOUGH_REBORN);
            }

            // 가능하다면 리본 태스크 생성
            String createRebornTaskQuery = "insert into RebornTask (userIdx, rebornIdx, productExchangeCode) values (?,?,?)";

            // 리본 생성 6자리 랜덤 값
            Object[] createRebornTaskParams = new Object[]{
                    postRebornTaskReq.getUserIdx(),
                    postRebornTaskReq.getRebornIdx(),
                    (int)(Math.random()*89999+10000)
            };
            if(this.jdbcTemplate.update(createRebornTaskQuery, createRebornTaskParams)==0){
                throw new BaseException(UPDATE_FAIL_REBORN_TASK);
            }

            String selectRebornTaskQuery = "SELECT last_insert_id() ";

            Long rebornTaskIdx = this.jdbcTemplate.queryForObject(selectRebornTaskQuery, Long.class);
            return rebornTaskIdx;
        }
        catch (BaseException e){
            log.error(e.getStatus().getMessage());
            throw new BaseException(e.getStatus());
        }
        catch (Exception e) {
            e.getStackTrace();
            log.error(e.getMessage());
            throw new BaseException(CAN_NOT_CREATE_REBORN_TASK);
        }
    }

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

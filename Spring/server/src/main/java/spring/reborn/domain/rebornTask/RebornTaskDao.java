package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.RebornDao;
import spring.reborn.domain.rebornTask.model.*;
import spring.reborn.domain.user.UserDao;

import javax.sql.DataSource;
import java.sql.Time;

import static spring.reborn.config.BaseResponseStatus.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RebornTaskDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final RebornDao rebornDao;
    private final UserDao userDao;


    @Transactional
    public Long createRebornTask(PostRebornTaskReq postRebornTaskReq) throws BaseException {
        try {

            // 가능하다면 리본 태스크 생성
            String createRebornTaskQuery = "insert into RebornTask (userIdx, rebornIdx, productExchangeCode) select ?,?,? from dual where exists(select * from Reborn where rebornIdx= ? and productCnt > 0 and status='ACTIVE')";

            // 리본 생성 및 6자리 랜덤 값
            Object[] createRebornTaskParams = new Object[]{
                    postRebornTaskReq.getUserIdx(),
                    postRebornTaskReq.getRebornIdx(),
                    (int) (Math.random() * 89999 + 10000),
                    postRebornTaskReq.getRebornIdx()
            };

            // 생성 실패시
            if (this.jdbcTemplate.update(createRebornTaskQuery, createRebornTaskParams) == 0) {
                throw new BaseException(UPDATE_FAIL_REBORN_TASK);
            }

            // 마지막 삽입한 인덱스 받기 <= update 쿼리에 영향받으므로 먼저 받아야함
            String selectRebornTaskQuery = "SELECT last_insert_id() ";

            Long rebornTaskIdx = this.jdbcTemplate.queryForObject(selectRebornTaskQuery, Long.class);

            // 생성 성공하면 개수 1개 감소
            String updateRebornQuery = "UPDATE Reborn SET productCnt = productCnt-1 WHERE rebornIdx = ?";
            if (this.jdbcTemplate.update(updateRebornQuery, postRebornTaskReq.getRebornIdx()) == 0) {
                throw new BaseException(UPDATE_FAIL_REBORN);
            }

            return rebornTaskIdx;
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            e.getStackTrace();
            log.error(e.getMessage());
            throw new BaseException(CAN_NOT_CREATE_REBORN_TASK);
        }
    }

    @Transactional
    public PatchRebornTaskRes findByRebornTaskIdAndCode(Long rebornTaskIdx, Long Code) throws BaseException {
        try {

            // todo 시간고려는 프론트가 처리해서 만료된 api 따로처리

            // 가능한 상태인지 -> 교환 코드 확인
            String selectRebornTaskQuery = "select rebornTaskIdx, rt.rebornIdx rebornIdx, productExchangeCode, rt.status status, productCnt " +
                    "from RebornTask rt join Reborn r on rt.rebornIdx = r.rebornIdx " +
                    "where rebornTaskIdx = ?";

            PatchRebornTaskForExchange rebornTask = this.jdbcTemplate.queryForObject(selectRebornTaskQuery,
                    (rs, rowNum) -> PatchRebornTaskForExchange.builder()
                            .rebornTaskIdx(rs.getLong("rebornTaskIdx"))
                            .rebornIdx(rs.getLong("rebornIdx"))
                            .productExchangeCode(rs.getLong("productExchangeCode"))
                            .status(rs.getString("status"))
                            .productCnt(rs.getLong("productCnt"))
                            .build()
                    ,
                    rebornTaskIdx);
            if (rebornTask == null) {
                throw new BaseException(CAN_NOT_FOUND_REBORN_TASK);
            }
            if (!rebornTask.getStatus().equals(RebornTaskStatus.ACTIVE.name())) {
                throw new BaseException(NOT_AVAILABLE_REBORN_TASK);
            }
            if (!rebornTask.getProductExchangeCode().equals(Code)) {
                throw new BaseException(INVALID_EXCHANGE_CODE);
            }
            if (rebornTask.getProductCnt() == 0) {
                throw new BaseException(NOT_ENOUGH_REBORN_PRODUCT_COUNT);
            }

            // 가능한 상태인 경우
            // 1. task 상태변경
            String updateRebornTaskQuery = "update RebornTask " +
                    "set updatedAt = now(), status = 'COMPLETE' " +
                    "where rebornTaskIdx = ?";

            if (this.jdbcTemplate.update(updateRebornTaskQuery, rebornTaskIdx) == 0) {
                throw new BaseException(UPDATE_FAIL_REBORN_TASK);
            }

            // 2. reborn 개수 감소
            rebornDao.decreaseRebornProductCnt(rebornTask.getRebornIdx());


            return PatchRebornTaskRes.builder().rebornTaskIdx(rebornTaskIdx).build();
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            e.getStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetRebornExchangeCodeRes getRebornExchangeCode(Long rebornTaskIdx, Long userIdx) throws BaseException {
        try {
            String selectExchangeCodeQuery = "select productExchangeCode " +
                    "from RebornTask " +
                    "where rebornTaskIdx = ? and userIdx = ?";

            return this.jdbcTemplate.queryForObject(selectExchangeCodeQuery,
                    (rs, rowNum) ->
                            new GetRebornExchangeCodeRes(rs.getLong("productExchangeCode")),
                    rebornTaskIdx, userIdx);

        } catch (Exception e) {
            e.getStackTrace();
            throw new BaseException(NO_AUTHENTIFICATION_REBORN);
        }
    }



    @Transactional
    public void expiredRebornTask(Long rebornTaskIdx, Time productLimitTime) throws BaseException {
        log.info("check Expired");
        log.info(productLimitTime.toString());
        try {
            String selectRebornTaskQuery = "select now() >= addtime(createdAt,?) " +
                    "from RebornTask " +
                    "where rebornTaskIdx = ?";
            Boolean isExpired = this.jdbcTemplate.queryForObject(selectRebornTaskQuery,
                    Boolean.class,
                    productLimitTime.toString(), rebornTaskIdx);

            if (isExpired == null) {
                throw new BaseException(CAN_NOT_CHECK_EXPIRING_REBORN_TASK);
            }

            log.info("isExpired : " + isExpired);
            if (isExpired) {
                String updateRebornTaskQuery =
                        "update RebornTask set status = 'EXPIRED', updatedAt = now() where rebornTaskIdx = ? and status = 'ACTIVE'";

                if (this.jdbcTemplate.update(updateRebornTaskQuery, rebornTaskIdx) == 0) {
                    throw new BaseException(FAIL_EXPIRING_REBORN_TASK);
                }

            }
            // 생성 성공하면 개수 1개 감소
            String updateRebornQuery = "update Reborn set productCnt = productCnt+1 where rebornIdx = (select rebornIdx from RebornTask where rebornTaskIdx=?)";
            if (this.jdbcTemplate.update(updateRebornQuery, rebornTaskIdx) == 0) {
                throw new BaseException(UPDATE_FAIL_REBORN);
            }

        } catch (BaseException e) {
            e.printStackTrace();
            log.error(e.getStatus().getMessage());
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }


    }

    public Time getRebornProductLimitTime(Long rebornTaskIdx) throws BaseException {
        try {
            String selectProductLimitTime = "select productLimitTime " +
                    "from RebornTask rt join Reborn r on rt.rebornIdx = r.rebornIdx " +
                    "where rt.rebornTaskIdx = ?";

            Time productLimitTime = this.jdbcTemplate.queryForObject(selectProductLimitTime,
                    Time.class,
                    rebornTaskIdx);

            return productLimitTime;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(CAN_NOT_FOUND_REBORN_TASK);
        }
    }
}

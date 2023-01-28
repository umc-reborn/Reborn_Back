package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.RebornDao;
import spring.reborn.domain.rebornTask.model.PatchRebornTaskForCode;
import spring.reborn.domain.rebornTask.model.PatchRebornTaskRes;
import spring.reborn.domain.rebornTask.model.PostRebornTaskReq;
import spring.reborn.domain.rebornTask.model.RebornTaskStatus;

import javax.sql.DataSource;

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

    @Transactional
    public PatchRebornTaskRes findByRebornTaskIdAndCode(Long rebornTaskIdx, Long Code) throws BaseException{
        try {

            // todo 시간고려는 프론트가 처리해서 만료된 api 따로처리

            // 가능한 상태인지 -> 교환 코드 확인
            String selectRebornTaskQuery = "select rebornTaskIdx, rebornIdx, productExchangeCode, status " +
                    "from RebornTask " +
                    "where rebornTaskIdx = ?";

            PatchRebornTaskForCode rebornTask = this.jdbcTemplate.queryForObject(selectRebornTaskQuery,
                    (rs, rowNum) -> PatchRebornTaskForCode.builder()
                            .rebornTaskIdx(rs.getLong("rebornTaskIdx"))
                            .rebornIdx(rs.getLong("rebornIdx"))
                            .productExchangeCode(rs.getLong("productExchangeCode"))
                            .status(rs.getString("status"))
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

            // 가능한 상태인 경우
            // 1. task 상태변경
            String updateRebornTaskQuery = "update RebornTask " +
                    "set updatedAt = now(), status = 'COMPLETE' " +
                    "where rebornTaskIdx = ?";

            if(this.jdbcTemplate.update(updateRebornTaskQuery, rebornTaskIdx)==0){
                throw new BaseException(UPDATE_FAIL_REBORN_TASK);
            }

            // 2. reborn 개수 감소
            rebornDao.decreaseRebornProductCnt(rebornTask.getRebornIdx());


            return PatchRebornTaskRes.builder().rebornTaskIdx(rebornTaskIdx).build();
        }catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            throw new BaseException(e.getStatus());
        }
        catch (Exception e){
            e.getStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

}

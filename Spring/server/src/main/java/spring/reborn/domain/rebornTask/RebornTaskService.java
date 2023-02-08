package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.AsyncConfig;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.RebornDao;
import spring.reborn.domain.rebornTask.model.*;
import spring.reborn.domain.user.UserDao;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class RebornTaskService {
    private final RebornTaskDao rebornTaskDao;
    private final RebornDao rebornDao;
    private final UserDao userDao;


    public PostRebornTaskRes createRebornTask(PostRebornTaskReq postRebornTaskReq) throws BaseException {
        try {
            Long rebornTaskIdx = rebornTaskDao.createRebornTask(postRebornTaskReq);

            Time productLimitTime = rebornTaskDao.getRebornProductLimitTime(rebornTaskIdx);
            CompletableFuture.runAsync(() ->
            {
                try {
                    ExpiringRebornTask(rebornTaskIdx,productLimitTime);
                } catch (BaseException | ParseException e) {
                    throw new RuntimeException(e);
                }
            });

            return new PostRebornTaskRes(rebornTaskIdx);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
        catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public PatchRebornTaskRes updateRebornTask(PatchRebornTaskReq patchRebornTaskReq) throws BaseException{
        try {
            PatchRebornTaskRes patchRebornTaskRes = rebornTaskDao.findByRebornTaskIdAndCode(
                    patchRebornTaskReq.getRebornTaskIdx(),
                    patchRebornTaskReq.getProductExchangeCode()
            );

            return patchRebornTaskRes;
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public GetRebornExchangeCodeRes getExchangeCode(Long rebornTaskIdx, Long userIdx) throws BaseException{
        try {
            return rebornTaskDao.getRebornExchangeCode(rebornTaskIdx,userIdx);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    @Async
    @Transactional
    public void ExpiringRebornTask(Long rebornTaskIdx, Time waitingTime) throws BaseException, ParseException {
        log.info("async task start - RebornTaskIdx : "+ rebornTaskIdx);
        // 쓰레드가 productLimitTime 타임만큼 대기 후 검증 및 처리
        try {
            Thread.sleep(convertTimeToMilliSecond(waitingTime));
            rebornTaskDao.expiredRebornTask(rebornTaskIdx,waitingTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("async task end - RebornTaskIdx : "+ rebornTaskIdx);

    }

    public Long convertTimeToMilliSecond(Time time) throws ParseException {
        return Long.valueOf(((time.getHours()*60)+time.getMinutes())*60+time.getSeconds())*1000;
    }
}

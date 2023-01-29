package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.RebornDao;
import spring.reborn.domain.rebornTask.model.*;
import spring.reborn.domain.user.UserDao;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class RebornTaskService {
    private final RebornTaskDao rebornTaskDao;
    private final RebornDao rebornDao;
    private final UserDao userDao;


    @Transactional
    public PostRebornTaskRes createRebornTask(PostRebornTaskReq postRebornTaskReq) throws BaseException {
        try {
            Long rebornTaskIdx = rebornTaskDao.createRebornTask(postRebornTaskReq);
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
}

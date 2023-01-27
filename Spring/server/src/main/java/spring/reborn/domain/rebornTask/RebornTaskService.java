package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.rebornTask.model.*;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class RebornTaskService {
    private final RebornTaskDao rebornTaskDao;
    @Transactional
    public PostRebornTaskRes createRebornTask(PostRebornTaskReq postRebornTaskReq) throws BaseException {
        try {
            Long rebornIdx = rebornTaskDao.createRebornTask(postRebornTaskReq);
            return new PostRebornTaskRes(rebornIdx);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
        catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

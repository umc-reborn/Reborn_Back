package spring.reborn.domain.rebornTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.rebornTask.RebornTaskDao;
import spring.reborn.domain.rebornTask.RebornTaskProvider;
import spring.reborn.domain.rebornTask.model.*;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class RebornTaskService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RebornTaskDao rebornTaskDao;
    private final RebornTaskProvider rebornTaskProvider;

    @Autowired
    public RebornTaskService(RebornTaskDao rebornTaskDao, RebornTaskProvider rebornTaskProvider) {
        this.rebornTaskDao = rebornTaskDao;
        this.rebornTaskProvider = rebornTaskProvider;
    }

//    @Transactional
//    public PostRebornRes createReborn(PostRebornReq postRebornReq) throws BaseException {
//        try {
//            System.out.println("service 시작");
//            int rebornIdx = rebornTaskDao.createReborn(postRebornReq);
//            System.out.println("dao 끝");
//            return new PostRebornRes(rebornIdx);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}

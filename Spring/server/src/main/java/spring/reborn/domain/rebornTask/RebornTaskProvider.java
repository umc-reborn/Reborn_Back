package spring.reborn.domain.rebornTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.rebornTask.RebornTaskDao;
import spring.reborn.domain.rebornTask.model.*;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class RebornTaskProvider {
    private final RebornTaskDao rebornTaskDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public RebornTaskProvider(RebornTaskDao rebornTaskDao) {
        this.rebornTaskDao = rebornTaskDao;
    }

//    public List<GetRebornHistoryRes> getRebornHistories(Integer userIdx) throws BaseException {
//        try {
//            System.out.println("provider 시작");
//            List<GetRebornHistoryRes> GetRebornHistoriesRes = rebornTaskDao.getRebornHistories(userIdx);
//            System.out.println("provider 끝");
//            return GetRebornHistoriesRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }


}

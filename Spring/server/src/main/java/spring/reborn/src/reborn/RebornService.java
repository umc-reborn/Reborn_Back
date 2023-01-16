package spring.reborn.src.reborn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.src.reborn.model.PostRebornReq;
import spring.reborn.src.reborn.model.PostRebornRes;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class RebornService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RebornDao rebornDao;
    private final RebornProvider rebornProvider;

    @Autowired
    public RebornService(RebornDao rebornDao, RebornProvider rebornProvider) {
        this.rebornDao = rebornDao;
        this.rebornProvider = rebornProvider;
    }

    @Transactional
    public PostRebornRes createReborn(PostRebornReq postRebornReq) throws BaseException {
        try {
            System.out.println("service 시작");
            int rebornIdx = rebornDao.createReborn(postRebornReq);
            System.out.println("dao 끝");
            return new PostRebornRes(rebornIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

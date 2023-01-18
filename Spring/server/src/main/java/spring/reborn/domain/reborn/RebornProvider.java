package spring.reborn.domain.reborn;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.model.GetRebornRes;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class RebornProvider {
    private final RebornDao rebornDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public RebornProvider(RebornDao rebornDao) {
        this.rebornDao = rebornDao;
    }

    public List<GetRebornRes> getReborns(Integer rebornIdx) throws BaseException {
        try {
            System.out.println("provider 시작");
            List<GetRebornRes> getRebornsRes = rebornDao.getReborns(rebornIdx);
            System.out.println("provider 끝");
            return getRebornsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

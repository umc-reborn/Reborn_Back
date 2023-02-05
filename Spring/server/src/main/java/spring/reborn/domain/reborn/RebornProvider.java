package spring.reborn.domain.reborn;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.model.*;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;
import static spring.reborn.config.BaseResponseStatus.GET_FAIL_REBORN;

@Service
public class RebornProvider {
    private final RebornDao rebornDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public RebornProvider(RebornDao rebornDao) {
        this.rebornDao = rebornDao;
    }

    public List<GetRebornRes> getReborns(Integer rebornIdx, String status) throws BaseException {
        try {
            System.out.println("provider 시작");
            if (status.equals("")) {
                List<GetRebornRes> getRebornsRes = rebornDao.getReborns(rebornIdx);
                System.out.println("provider 끝");
                return getRebornsRes;
            } else if (status.equals("ACTIVE") || status.equals("INACTIVE") || status.equals("COMPLETE")) {
                List<GetRebornRes> getRebornsByStatusRes = rebornDao.getRebornsByStatus(rebornIdx, status);
                System.out.println("provider 끝");
                return getRebornsByStatusRes;
            } else {
                System.out.println("provider 끝");
                throw new BaseException(GET_FAIL_REBORN);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetRebornPageRes> getRebornsPage(Integer rebornIdx, String status) throws BaseException {
        try {
            System.out.println("provider 시작");
            if (status.equals("")) {
                List<GetRebornPageRes> getRebornsPageRes = rebornDao.getRebornsPage(rebornIdx);
                System.out.println("provider 끝");
                return getRebornsPageRes;
            } else if (status.equals("ACTIVE") || status.equals("INACTIVE") || status.equals("COMPLETE")) {
                List<GetRebornPageRes> getRebornsPageByStatusRes = rebornDao.getRebornsPageByStatus(rebornIdx, status);
                System.out.println("provider 끝");
                return getRebornsPageByStatusRes;
            } else {
                System.out.println("provider 끝");
                throw new BaseException(GET_FAIL_REBORN);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetInProgressRes> getInProgressReborns(Integer userIdx) throws BaseException {
        try {
            System.out.println("provider 시작");
            List<GetInProgressRes> getInProgressRebornsRes = rebornDao.getInProgressReborns(userIdx);
            System.out.println("provider 끝");
            return getInProgressRebornsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetHistoryRes> getHistory(Integer userIdx) throws BaseException {
        try {
            System.out.println("provider 시작");
            List<GetHistoryRes> getHistories = rebornDao.getHistory(userIdx);
            System.out.println("provider 끝");
            return getHistories;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetHistroyDetailRes getHistoryDetail(Integer rebornTaskIdx) throws BaseException {
        try {
            System.out.println("provider 시작");
            GetHistroyDetailRes getHistroyDetailRes = rebornDao.getHistoryDetail(rebornTaskIdx);
            System.out.println("provider 끝");
            return getHistroyDetailRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

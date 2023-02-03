package spring.reborn.domain.reborn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.reborn.model.*;

import static spring.reborn.config.BaseResponseStatus.*;

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
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public String patchReborn(PatchRebornReq patchRebornReq) throws BaseException {
        try {
            System.out.println("service 시작");
            int v = rebornDao.patchReborn(patchRebornReq);
            System.out.println("dao 끝");
//            if (v == 1)
//                throw new BaseException(MODIFY_FAIL_REBORN);
            String result = "상품 수정 성공!";
            return result;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public int postHistory(int rebornTaskIdx) throws BaseException {
        try {
            System.out.println("service 시작");
            if (rebornDao.postHistory(rebornTaskIdx) == 1) {
                System.out.println("dao 끝");
            };
            return 1;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public String deleteProduct(int rebornIdx) throws BaseException {
        try {
            int v = rebornDao.deleteProduct(rebornIdx);
            if (v == 0)
                throw new BaseException(DELETE_FAIL_REBORN);
            String result = "상품이 삭제되었습니다!";
            return result;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public String inactiveRebornTask(int rebornTaskIdx) throws BaseException {
        try {
            int v = rebornDao.inactiveRebornTask(rebornTaskIdx);
            if (v == 0)
                throw new BaseException(INACTIVE_FAIL_REBORNTASK);
            String result = "나눔이 취소되었습니다!";
            return result;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public PatchRebornStatusRes ativeReborn(int rebornIdx) throws BaseException {
        try {
            System.out.println("service start");
            return rebornDao.ativeReborn(rebornIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

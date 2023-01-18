package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.jjim.model.PostJjimReq;
import spring.reborn.domain.jjim.model.PostJjimRes;
import spring.reborn.domain.reborn.RebornDao;
import spring.reborn.domain.reborn.RebornProvider;
import spring.reborn.domain.reborn.model.PostRebornReq;
import spring.reborn.domain.reborn.model.PostRebornRes;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class JjimService {
    private final JjimDao jjimDao;
    private final JjimProvider jjimProvider;

    @Autowired
    public JjimService(JjimDao jjimDao, JjimProvider jjimProvider) {
        this.jjimDao = jjimDao;
        this.jjimProvider = jjimProvider;
    }

    @Transactional
    public PostJjimRes createJjim(PostJjimReq postJjimReq) throws BaseException {
        try {
            System.out.println("Service 시작");
            PostJjimRes postJjimRes = jjimDao.createJjim(postJjimReq);
            System.out.println("Service 끝");

            return postJjimRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

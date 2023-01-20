package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.reborn.config.BaseException;
import spring.reborn.domain.jjim.model.JjimReq;
import spring.reborn.domain.jjim.model.JjimRes;

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
    public JjimRes createJjim(JjimReq jjimReq) throws BaseException {
        try {
            JjimRes jjimRes = jjimDao.createJjim(jjimReq);

            return jjimRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public JjimRes deleteJjim(JjimReq jjimReq) throws BaseException {
        try {
            System.out.println("Service 시작");
            JjimRes jjimRes = jjimDao.deleteJjim(jjimReq);
            System.out.println("Service 끝");

            return jjimRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

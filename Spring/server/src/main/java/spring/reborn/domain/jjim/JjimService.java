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
    public JjimRes changeJjim(JjimReq jjimReq) throws BaseException {
        try {
            JjimRes jjimRes = jjimDao.changeJjim(jjimReq);
            return jjimRes;
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
        catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}

package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.review.ReviewDao;

@Service
public class JjimProvider {

    private final JjimDao jjimDao;

    @Autowired //readme 참고
    public JjimProvider(JjimDao jjimDao) {
        this.jjimDao = jjimDao;
    }

    public Integer countJjim(Integer userIdx) throws BaseException {
        return jjimDao.countJjim(userIdx);
    }

}

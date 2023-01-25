package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.jjim.model.JjimStoreRes;
import spring.reborn.domain.review.ReviewDao;

import java.util.List;

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

    public List<JjimStoreRes> getJjimStoreList(Integer userIdx) throws BaseException {
        return jjimDao.getJjimStoreList(userIdx);
    }

    public List<JjimStoreRes> getSortedJjimStoreList(Integer userIdx, String sort) throws BaseException {
        return jjimDao.getSortedJjimStoreList(userIdx, sort);
    }

}

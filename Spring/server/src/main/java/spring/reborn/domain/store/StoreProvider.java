package spring.reborn.domain.store;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.domain.reborn.RebornDao;
import spring.reborn.domain.store.model.GetPopularStoreRes;
import spring.reborn.domain.store.model.GetStoreRes;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class StoreProvider {
    private final StoreDao storeDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StoreProvider(StoreDao storeDao) {
        this.storeDao = storeDao;
    }


    public List<GetPopularStoreRes> getPopularStore() throws BaseException {
        try {
            System.out.println("provider 시작");
            List<GetPopularStoreRes> getHistories = storeDao.getPopularStore();
            System.out.println("provider 끝");
            return getHistories;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}


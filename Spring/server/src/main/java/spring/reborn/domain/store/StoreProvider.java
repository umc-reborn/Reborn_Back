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
import static spring.reborn.config.BaseResponseStatus.WRONG_CATEGORY_INPUT_ERROR;

@Service
public class StoreProvider {
    private final StoreDao storeDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StoreProvider(StoreDao storeDao) {
        this.storeDao = storeDao;
    }


    public List<GetPopularStoreRes> getPopularStore(String category) throws BaseException {
        if (!(category.equals("CAFE") || category.equals("FASHION") || category.equals("SIDEDISH") || category.equals("LIFE") || category.equals("ETC"))) {
            throw new BaseException(WRONG_CATEGORY_INPUT_ERROR);
        }
        try {
            System.out.println("provider 시작");
            List<GetPopularStoreRes> getHistories = storeDao.getPopularStore(category);
            System.out.println("provider 끝");
            return getHistories;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}


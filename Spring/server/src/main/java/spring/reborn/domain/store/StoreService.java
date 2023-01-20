package spring.reborn.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.store.model.GetStoreLocationRes;
import spring.reborn.domain.store.model.GetStoreRes;
import spring.reborn.domain.store.model.PatchStoreReq;
import spring.reborn.domain.store.model.Store;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreDao storeDao;

    public List<GetStoreRes> getStoreList() throws BaseException {
        try {
            return storeDao.getStoreList();

        }
        catch (Exception e){
            log.error(e.toString());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetStoreLocationRes getStoreLocation(Long id) throws BaseException {
        try {
            return storeDao.getStoreLocation(id);
        }
        catch (Exception e){
            log.error(e.toString());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetStoreRes getStoreInfo(Long id) throws BaseException{
        try {
            return storeDao.getStoreInfo(id);

        }
        catch (Exception e){
            log.error(e.toString());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetStoreRes> searchStoreListUsingTitle(String title) throws BaseException {
        try {
            return storeDao.searchStoreUsingTitle(title);
        }
        catch (Exception e){
            log.error(e.toString());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void updateStoreInfo(Long id, PatchStoreReq patchStoreReq) throws BaseException {
        try {
            storeDao.updateStoreInfo(id ,patchStoreReq);
        }
        catch (Exception e){
            log.error(e.toString());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}


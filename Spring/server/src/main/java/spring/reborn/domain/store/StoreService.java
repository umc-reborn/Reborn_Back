package spring.reborn.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.awsS3.AwsS3Service;
import spring.reborn.domain.store.model.*;
import spring.reborn.domain.user.UserDao;
import spring.reborn.domain.user.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreDao storeDao;
    private final AwsS3Service awsS3Service;

    private final UserDao userDao;

    public List<GetStoreRes> getStoreList() throws BaseException {
        try {
            return storeDao.getStoreList();

        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }


    public List<GetNewStoreRes> getNewStoreList() throws BaseException{
        try {
            return storeDao.getNewStoreList();
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public GetStoreLocationRes getStoreLocation(Long storeIdx) throws BaseException {
        try {
            return storeDao.getStoreLocation(storeIdx);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public GetStoreRes getStoreInfo(Long storeIdx) throws BaseException{
        try {
            return storeDao.getStoreInfo(storeIdx);

        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public List<GetStoreRes> searchStoreListUsingTitle(String keyword) throws BaseException {
        try {
            return storeDao.searchStoreUsingTitle(keyword);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public List<GetStoreRes> searchStoreListUsingTitleSortByName(String keyword) throws BaseException{
        try {
            return storeDao.searchStoreUsingTitleSortByName(keyword);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public List<GetStoreRes> searchStoreListUsingTitleSortByScore(String keyword) throws BaseException{
        try {
            return storeDao.searchStoreUsingTitleSortByScore(keyword);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }
    public List<GetStoreRes> searchStoreListUsingTitleSortByJjim(String keyword) throws BaseException{
        try {
            return storeDao.searchStoreUsingTitleSortByJjim(keyword);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    @Transactional
    public void updateStoreInfo(Long storeIdx, PatchStoreReq patchStoreReq) throws BaseException {
        try {
            storeDao.updateStoreInfo(storeIdx ,patchStoreReq);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }
    @Transactional
    public void updateStoreInfo(Long storeIdx, PatchStoreReq patchStoreReq, MultipartFile multipartFile) throws BaseException {
        try {
            if(!multipartFile.isEmpty()){
                String imageUrl = awsS3Service.uploadImage(multipartFile);
                // 이미지가 비어있지 않은 경우 삭제
                if(patchStoreReq.getStoreImage() != null) {
                    awsS3Service.deleteImage(patchStoreReq.getStoreImage());
                }
                patchStoreReq.setStoreImage(imageUrl);
            }
            storeDao.updateStoreInfo(storeIdx ,patchStoreReq);
        }
        catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }


    public List<GetLikeableStoreRes> getLikeableStores(int userIdx) throws BaseException {
        // todo 유저 도메인에서 category를 받아와야하나?? -> 유저와 스토어 구분 필요할듯한거 같기두,,?
//        String userLikes = userDao.getUserLikes(userIdx);
        return storeDao.getLikeableStore(userIdx);
    }
}


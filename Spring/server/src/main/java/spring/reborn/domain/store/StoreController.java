package spring.reborn.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.store.model.*;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
@Slf4j
public class StoreController {

    private final StoreService storeService;

    /*
    todo
        가게 리스트 조회(생성순)
         */
    @GetMapping("/list")
    public BaseResponse<List<GetStoreRes>> getStoreList() {
        try {
            List<GetStoreRes> getStoreResList = storeService.getStoreList();
            log.info(getStoreResList.toString());
            return new BaseResponse<>(getStoreResList);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    todo
    가게 위치 표시
     */
    @GetMapping("/{storeIdx}/location")
    public BaseResponse<GetStoreLocationRes> getStoreLocation(@PathVariable Long storeIdx) throws BaseException {

        try {
            GetStoreLocationRes getStoreLocationRes = storeService.getStoreLocation(storeIdx);
            return new BaseResponse<>(getStoreLocationRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    todo
    가게 정보 조회
     */
    @GetMapping("/{storeIdx}")
    public BaseResponse<GetStoreRes> getStoreInfo(@PathVariable Long storeIdx) throws BaseException {
        try {
            GetStoreRes getStoreRes = storeService.getStoreInfo(storeIdx);
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }

    }


    /*
    todo
    가게 검색
     */
    @GetMapping("/search/{keyword}")
    public BaseResponse<List<GetStoreRes>> searchStore(@PathVariable String keyword) throws BaseException {
        try {
            if(keyword.isEmpty())
                throw new BaseException(BaseResponseStatus.GET_STORE_EMPTY_KEYWORD);

            List<GetStoreRes> getStoreRes = storeService.searchStoreListUsingTitle(keyword);
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());

        }
    }

    /*
    todo
    가게 정보 수정
     */
    @PatchMapping("/{storeIdx}")
    public BaseResponse<Object> updateStoreInfo(@PathVariable Long storeIdx, @RequestBody PatchStoreReq patchStoreReq) {
        try {
            if(patchStoreReq.getStoreName().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_NAME);
            if (patchStoreReq.getStoreAddress().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_LOCATION);

            storeService.updateStoreInfo(storeIdx, patchStoreReq);
            return new BaseResponse<>(new PatchStoreRes(storeIdx));
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }

    }
}

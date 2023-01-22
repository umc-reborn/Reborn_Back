package spring.reborn.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.review.model.PostReviewRes;
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
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    todo
    가게 위치 표시
     */
    @GetMapping("/{id}/location")
    public BaseResponse<GetStoreLocationRes> getStoreLocation(@PathVariable Long id) throws BaseException {

        try {
            GetStoreLocationRes getStoreLocationRes = storeService.getStoreLocation(id);
            return new BaseResponse<>(getStoreLocationRes);
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    todo
    가게 정보 조회
     */
    @GetMapping("/{id}")
    public BaseResponse<GetStoreRes> getStoreInfo(@PathVariable Long id) throws BaseException {
        try {
            GetStoreRes getStoreRes = storeService.getStoreInfo(id);
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


    /*
    todo
    가게 검색
     */
    @GetMapping("/search/{title}")
    public BaseResponse<List<GetStoreRes>> searchStore(@PathVariable String title) throws BaseException {
        try {
            List<GetStoreRes> getStoreRes = storeService.searchStoreListUsingTitle(title);
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
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
            storeService.updateStoreInfo(storeIdx, patchStoreReq);
            return new BaseResponse<>(new PatchStoreRes(storeIdx));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


}

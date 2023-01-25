package spring.reborn.domain.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.config.BaseResponseStatus;
import spring.reborn.domain.awsS3.AwsS3Service;
import spring.reborn.domain.store.model.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /*
    가게 리스트 조회(업데이트 순)
     */
    @GetMapping("/list")
    public BaseResponse<List<GetStoreRes>> getStoreList() {
        try {
            List<GetStoreRes> getStoreResList = storeService.getStoreList();
            return new BaseResponse<>(getStoreResList);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    @GetMapping("/new")
    public BaseResponse<List<GetNewStoreRes>> getNewStoreList() {
        try {
            List<GetNewStoreRes> newStoreList = storeService.getNewStoreList();
            return new BaseResponse<>(newStoreList);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    가게 위치 표시
     */
    @GetMapping("/{storeIdx}/location")
    public BaseResponse<GetStoreLocationRes> getStoreLocation(@PathVariable Long storeIdx) {

        try {
            GetStoreLocationRes getStoreLocationRes = storeService.getStoreLocation(storeIdx);
            return new BaseResponse<>(getStoreLocationRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }

    }

    /*
    가게 정보 조회
     */
    @GetMapping("/{storeIdx}")
    public BaseResponse<GetStoreRes> getStoreInfo(@PathVariable Long storeIdx) {
        try {
            GetStoreRes getStoreRes = storeService.getStoreInfo(storeIdx);
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }

    }


    /*
    가게 검색
     */
    @GetMapping("/search")
    public BaseResponse<List<GetStoreRes>> searchStore(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String sort) {
        try {
            if (keyword == null || keyword.isEmpty())
                throw new BaseException(BaseResponseStatus.GET_STORE_EMPTY_KEYWORD);

            List<GetStoreRes> getStoreRes;

            if(sort == null || sort.equals("name")){
                getStoreRes = storeService.searchStoreListUsingTitleSortByName(keyword);

            }
            else if(sort.toUpperCase().equals("score".toUpperCase())){
                getStoreRes = storeService.searchStoreListUsingTitleSortByScore(keyword);

            }
            else if(sort.toUpperCase().equals("jjim".toUpperCase())){
                getStoreRes = storeService.searchStoreListUsingTitleSortByJjim(keyword);

            }
            // 잘못된 정렬 값도 이름순 처리
            else{
                getStoreRes = storeService.searchStoreListUsingTitleSortByName(keyword);

            }


            return new BaseResponse<>(getStoreRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());

        }
    }

    /*
    가게 정보 수정
     */
    @PatchMapping(value = "/{storeIdx}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public BaseResponse<Object> updateStoreInfo(@PathVariable Long storeIdx,
                                                @RequestPart PatchStoreReq patchStoreReq,
                                                @RequestPart(name = "image",required = false) MultipartFile multipartFile) {
        try {
            if (patchStoreReq.getStoreName().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_NAME);
            if (patchStoreReq.getStoreAddress().isEmpty())
                throw new BaseException(BaseResponseStatus.MODIFY_FAIL_STORE_EMPTY_LOCATION);

            if(multipartFile == null)
                storeService.updateStoreInfo(storeIdx, patchStoreReq);
            else
                storeService.updateStoreInfo(storeIdx, patchStoreReq,multipartFile);

            return new BaseResponse<>(new PatchStoreRes(storeIdx));
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }

    }
}

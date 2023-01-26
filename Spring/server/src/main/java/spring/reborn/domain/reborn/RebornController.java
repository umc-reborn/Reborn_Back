package spring.reborn.domain.reborn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.reborn.model.*;
import spring.reborn.domain.store.model.GetPopularStoreRes;

import java.util.List;
import static spring.reborn.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/reborns")
public class RebornController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RebornProvider rebornProvider;
    @Autowired
    private final RebornService rebornService;

    public RebornController(RebornProvider rebornProvider, RebornService rebornService) {
        this.rebornProvider = rebornProvider;
        this.rebornService = rebornService;
    }

    @ResponseBody
    @PostMapping("/create")
    @Transactional
    public BaseResponse<PostRebornRes> createReborn(@RequestBody PostRebornReq postRebornReq) {
        try {
            PostRebornRes postRebornRes = rebornService.createReborn(postRebornReq);
            return new BaseResponse<>(postRebornRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 진행 중인 리본 조회 (스토어) */
    @ResponseBody
    @GetMapping("/inprogress/store/{storeIdx}")
    @Transactional
    public BaseResponse<List<GetRebornRes>> getReborns(@PathVariable Integer storeIdx) {
        try {
            List<GetRebornRes> getRebornsRes= rebornProvider.getReborns(storeIdx);
            return new BaseResponse<>(getRebornsRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 진행 중인 리본 조회 (유저) */
    @ResponseBody
    @GetMapping("/inprogress/user/{userIdx}")
    @Transactional
    public BaseResponse<List<GetInProgressRes>> getInProgressReborns(@PathVariable Integer userIdx) {
        try {
            List<GetInProgressRes> getInProgressRebornsRes = rebornProvider.getInProgressReborns(userIdx);
            return new BaseResponse<>(getInProgressRebornsRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 상품 수정 */
    @ResponseBody
    @PatchMapping("/modify")
    @Transactional
    public BaseResponse<String> patchReborn(PatchRebornReq patchRebornReq) {
        try {
            if (patchRebornReq.getProductName() == null)
                return new BaseResponse<>(PATCH_REBORN_EMPTY_PRODUCTNAME);
            if (patchRebornReq.getProductGuide() == null)
                return new BaseResponse<>(PATCH_REBORN_EMPTY_PRODUCTGUIDE);
            if (patchRebornReq.getProductComment() == null)
                return new BaseResponse<>(PATCH_REBORN_EMPTY_PRODUCTCOMMENT);
            String result = rebornService.patchReborn(patchRebornReq);
            return new BaseResponse<>(result);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본 히스토리 조회*/
    @ResponseBody
    @GetMapping("/history/{userIdx}")
    @Transactional
    public BaseResponse<List<GetHistoryRes>> getHistory(@PathVariable Integer userIdx) {
        try {
            List<GetHistoryRes> getHistoriesRes = rebornProvider.getHistory(userIdx);
            return new BaseResponse<>(getHistoriesRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본 히스토리 상세조회*/
    @ResponseBody
    @GetMapping("/history/detail/{rebornTaskIdx}")
    @Transactional
    public BaseResponse<GetHistroyDetailRes> getHistoryDetail(@PathVariable Integer rebornTaskIdx) {
        try {
            GetHistroyDetailRes getHistroyDetailRes = rebornProvider.getHistoryDetail(rebornTaskIdx);
            return new BaseResponse<>(getHistroyDetailRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 리본 히스토리 생성 */
    @ResponseBody
    @PatchMapping("/create/history/{rebornTaskIdx}")
    @Transactional
    public BaseResponse<String> postHistory(@PathVariable int rebornTaskIdx) {
        try {
            if (rebornService.postHistory(rebornTaskIdx) == 1) {
                System.out.println("성공!");
            };
            String result = "히스토리 생성에 성공하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}

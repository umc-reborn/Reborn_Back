package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.reborn.model.PostRebornReq;
import spring.reborn.domain.reborn.model.PostRebornRes;
import spring.reborn.domain.rebornTask.*;
import spring.reborn.domain.rebornTask.model.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rebornTasks")
@RequiredArgsConstructor
public class RebornTaskController {
    private final RebornTaskService rebornTaskService;


    @PostMapping("")
    public BaseResponse<PostRebornTaskRes> createReborn(@RequestBody PostRebornTaskReq postRebornTaskReq) {
        try {
            PostRebornTaskRes postRebornTaskRes = rebornTaskService.createReborn(postRebornTaskReq);
            return new BaseResponse<>(postRebornTaskRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }
    }


//    /* 리본 조회 (스토어) */
//    @ResponseBody
//    @GetMapping("/history/{userIdx}")
//    @Transactional
//    public BaseResponse<List<GetRebornHistoryRes>> getRebornHistories(@PathVariable Integer userIdx) {
//        try {
//            List<GetRebornHistoryRes> getRebornHistroiesRes= rebornTaskProvider.getRebornHistories(userIdx);
//            return new BaseResponse<>(getRebornHistroiesRes);
//        } catch (BaseException baseException) {
//            return new BaseResponse<>(baseException.getStatus());
//        }
//    }

//    /* 진행 중인 리본 조회(스토어) */
//    @ResponseBody
//    @GetMapping("/inprogress/{storeIdx}")
//    @Transactional
//    public BaseResponse<List<GetRebornRes>> getInProgressReborns(@PathVariable Integer storeIdx) {
//        try {
//            List<GetRebornRes> getInProgressRebornsRes = rebornTaskProvider.getInProgressReborns(storeIdx);
//            return new BaseResponse<>(getInProgressRebornsRes);
//        } catch (BaseException baseException) {
//            return new BaseResponse<>(baseException.getStatus());
//        }
//    }
}

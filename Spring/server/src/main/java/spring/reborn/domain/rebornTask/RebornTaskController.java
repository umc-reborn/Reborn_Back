package spring.reborn.domain.rebornTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.rebornTask.*;
import spring.reborn.domain.rebornTask.model.*;

import java.util.List;

@RestController
@RequestMapping("/rebornTasks")
public class RebornTaskController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RebornTaskProvider rebornTaskProvider;
    @Autowired
    private final RebornTaskService rebornTaskService;

    public RebornTaskController(RebornTaskProvider rebornTaskProvider, RebornTaskService rebornTaskService) {
        this.rebornTaskProvider = rebornTaskProvider;
        this.rebornTaskService = rebornTaskService;
    }

//    @ResponseBody
//    @PostMapping("/create")
//    @Transactional
//    public BaseResponse<PostRebornRes> createReborn(@RequestBody PostRebornReq postRebornReq) {
//        try {
//            PostRebornRes postRebornRes = rebornTaskService.createReborn(postRebornReq);
//            return new BaseResponse<>(postRebornRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

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

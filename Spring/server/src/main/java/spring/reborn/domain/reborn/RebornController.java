package spring.reborn.domain.reborn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.reborn.model.*;

import java.util.List;

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

    /* 리본 조회 (스토어) */
    @ResponseBody
    @GetMapping("/{storeIdx}")
    @Transactional
    public BaseResponse<List<GetRebornRes>> getReborns(@PathVariable Integer storeIdx) {
        try {
            List<GetRebornRes> getRebornsRes= rebornProvider.getReborns(storeIdx);
            return new BaseResponse<>(getRebornsRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }

    /* 진행 중인 리본 조회(스토어) */
    @ResponseBody
    @GetMapping("/inprogress/{storeIdx}")
    @Transactional
    public BaseResponse<List<GetRebornRes>> getInProgressReborns(@PathVariable Integer storeIdx) {
        try {
            List<GetRebornRes> getInProgressRebornsRes = rebornProvider.getInProgressReborns(storeIdx);
            return new BaseResponse<>(getInProgressRebornsRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}

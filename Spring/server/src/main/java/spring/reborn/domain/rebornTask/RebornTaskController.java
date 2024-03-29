package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.rebornTask.model.*;
import spring.reborn.utils.JwtService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reborn-task")
@RequiredArgsConstructor
public class RebornTaskController {

    @Autowired
    private final RedisTemplate<Object,Object> redisTemplate;


    private final RebornTaskService rebornTaskService;
    private final RebornTaskRedisService rebornTaskRedisService;
    private final JwtService jwtService;


    @PostMapping("")
    public BaseResponse<PostRebornTaskRes> createRebornTask(@RequestBody PostRebornTaskReq postRebornTaskReq) {
        try {
            PostRebornTaskRes postRebornTaskRes = rebornTaskService.createRebornTask(postRebornTaskReq);
            return new BaseResponse<>(postRebornTaskRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }
    }

    @PostMapping("/redis")
    public BaseResponse<String> createRebornTask2(@RequestBody PostRebornTaskReq postRebornTaskReq) {
        String result = "";
        result = rebornTaskRedisService.addQueue(postRebornTaskReq.getRebornIdx(), postRebornTaskReq.getUserIdx());
        return new BaseResponse<>(result);
    }

    @PostMapping("/update")
    public BaseResponse<PatchRebornTaskRes> updateRebornTask(@RequestBody PatchRebornTaskReq patchRebornTaskReq) {
        try {
            // todo 유저는 나중에
            PatchRebornTaskRes patchRebornTaskRes = rebornTaskService.updateRebornTask(patchRebornTaskReq);

            return new BaseResponse<>(patchRebornTaskRes);


        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }
    }

    @GetMapping("/{rebornTaskIdx}/code")
    public BaseResponse<GetRebornExchangeCodeRes> updateRebornTask(@PathVariable Long rebornTaskIdx) {
        try {
            Long userIdx = (long) jwtService.getUserIdx();
            GetRebornExchangeCodeRes getRebornExchangeCodeRes = rebornTaskService.getExchangeCode(rebornTaskIdx, userIdx);

            return new BaseResponse<>(getRebornExchangeCodeRes);
        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }


}

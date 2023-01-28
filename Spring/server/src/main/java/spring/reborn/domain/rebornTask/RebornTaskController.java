package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.rebornTask.model.*;
import spring.reborn.utils.JwtService;

@Slf4j
@RestController
@RequestMapping("/reborn-task")
@RequiredArgsConstructor
public class RebornTaskController {
    private final RebornTaskService rebornTaskService;
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

    @PatchMapping("")
    public BaseResponse<PatchRebornTaskRes> updateRebornTask(@RequestBody PatchRebornTaskReq patchRebornTaskReq){
        try {
            // todo 유저는 나중에
            PatchRebornTaskRes patchRebornTaskRes= rebornTaskService.updateRebornTask(patchRebornTaskReq);

            return new BaseResponse<>(patchRebornTaskRes);


        } catch (BaseException e) {
            log.error(e.getStatus().getMessage());
            return new BaseResponse<>((e.getStatus()));
        }
    }


}

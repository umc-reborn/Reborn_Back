package spring.reborn.domain.rebornTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.rebornTask.model.*;

@Slf4j
@RestController
@RequestMapping("/rebornTasks")
@RequiredArgsConstructor
public class RebornTaskController {
    private final RebornTaskService rebornTaskService;


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



}

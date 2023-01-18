package spring.reborn.domain.reborn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.reborn.model.GetRebornRes;
import spring.reborn.domain.reborn.model.PostRebornReq;
import spring.reborn.domain.reborn.model.PostRebornRes;

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
            System.out.println("controller 시작");
            PostRebornRes postRebornRes = rebornService.createReborn(postRebornReq);

            System.out.println("orderService 끝");
            return new BaseResponse<>(postRebornRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/{storeIdx}")
    @Transactional
    public BaseResponse<List<GetRebornRes>> getReborns(@PathVariable Integer storeIdx) {
        try {
            System.out.println("controller 시작");
            List<GetRebornRes> getRebornsRes= rebornProvider.getReborns(storeIdx);
            System.out.println("controller 끝");
            return new BaseResponse<>(getRebornsRes);
        } catch (BaseException baseException) {
            return new BaseResponse<>(baseException.getStatus());
        }
    }
}

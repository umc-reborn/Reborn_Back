package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.jjim.model.JjimReq;
import spring.reborn.domain.jjim.model.JjimRes;
import spring.reborn.domain.jjim.model.JjimStoreRes;

import java.util.List;

import static spring.reborn.config.BaseResponseStatus.DATABASE_ERROR;


@RestController
public class JjimController {
    @Autowired
    private final JjimProvider jjimProvider;
    @Autowired
    private final JjimService jjimService;

    public JjimController(JjimProvider jjimProvider, JjimService jjimService) {
        this.jjimProvider = jjimProvider;
        this.jjimService = jjimService;
    }

    @ResponseBody
    @PostMapping("/jjim")
    public BaseResponse<JjimRes> changeJjim(@RequestBody JjimReq jjimReq)throws BaseException  {
        try {
            JjimRes jjimRes = jjimService.changeJjim(jjimReq);
            return new BaseResponse<>(jjimRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/jjim/cnt/{userIdx}")
    public BaseResponse<Integer> countJjim(@PathVariable Integer userIdx) throws BaseException {
        return new BaseResponse<>(jjimProvider.countJjim(userIdx));
    }

    @ResponseBody
    @GetMapping("/jjim/{userIdx}")
    public BaseResponse<List<JjimStoreRes>> getSortedJjimStoreList(@PathVariable("userIdx") Integer userIdx,
                                                                   @RequestParam(value = "sort",required = false) String sort) throws BaseException {
        if (sort == null){
            // 분류 선택 X
            return new BaseResponse<>(jjimProvider.getJjimStoreList(userIdx));
        }
        else{
            // 분류 선택 O
            // jjimCnt(인기순), storeName(스토어이름), storeScore(스토어점수)
            return new BaseResponse<>(jjimProvider.getSortedJjimStoreList(userIdx, sort));
        }
    }
}
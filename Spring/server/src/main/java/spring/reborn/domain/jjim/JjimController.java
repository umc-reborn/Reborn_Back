package spring.reborn.domain.jjim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.domain.jjim.model.PostJjimReq;
import spring.reborn.domain.jjim.model.PostJjimRes;


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
    public BaseResponse<PostJjimRes> createJjim(@RequestBody PostJjimReq postJjimReq) {
        try {

            System.out.println("controller 시작");
            PostJjimRes postJjimRes = jjimService.createJjim(postJjimReq);

            System.out.println("controller 끝");
            return new BaseResponse<>(postJjimRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
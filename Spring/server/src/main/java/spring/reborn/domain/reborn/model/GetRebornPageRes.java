package spring.reborn.domain.reborn.model;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class GetRebornPageRes {
    private int rebornTaskIdx;
    private String userNickname;
    private String productName;
    private String productImg;
    private String productLimitTime;
    private int productCnt;
    private String status;
    private String createdAt;
}
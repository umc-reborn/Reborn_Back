package spring.reborn.domain.jjim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JjimRes {
    private String jjimStatus;
    private int jjimIdx;
    private String userEmail;
    private String storeName;
}

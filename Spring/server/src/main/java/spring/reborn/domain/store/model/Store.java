package spring.reborn.domain.store.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode(of = "storeIdx")
public class Store {

    private Long storeIdx;

    private String storeName;

    private String storeRegister;

    private String storeImage;

    private String storeAddress;

    private String storeDescription;

    private Float storeScore;

    private Enum<StoreCategory> category;

    private Enum<StoreStatus> status;

    private Timestamp createdAt;

    private Timestamp updatedAt;



}

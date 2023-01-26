package spring.reborn.domain.store.model;

import lombok.Getter;

public enum StoreCategory {
    CAFE("카페·디저트"),
    FASHION("패션"),
    SIDEDISH("반찬"),
    LIFE("편의·생활"),
    ETC("기타")
    ;

    private final String label;

    StoreCategory(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

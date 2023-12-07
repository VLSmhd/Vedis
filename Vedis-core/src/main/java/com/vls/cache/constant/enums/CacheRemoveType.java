package com.vls.cache.constant.enums;

public enum CacheRemoveType {
    EXPIRE("expire", "过期"),
    EVICT("evict", "淘汰");

    private String code;
    private String desc;

    CacheRemoveType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "CacheRemoveType{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}

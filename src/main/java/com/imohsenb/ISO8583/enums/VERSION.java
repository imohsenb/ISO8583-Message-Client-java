package com.imohsenb.ISO8583.enums;

/**
 * Created by Mohsen Beiranvand on 18/03/13.
 */
public enum VERSION {

    V1987("0"),
    V1993("1"),
    V2003("2");

    private final String code;

    VERSION(String versionCode) {
        this.code = versionCode;
    }

    public String getCode() {
        return code;
    }
}

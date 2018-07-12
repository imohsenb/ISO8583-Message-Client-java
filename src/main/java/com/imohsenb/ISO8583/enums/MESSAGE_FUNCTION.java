package com.imohsenb.ISO8583.enums;

/**
 * Created by Mohsen Beiranvand on 18/03/16.
 */
public enum MESSAGE_FUNCTION {

    Request("0"),
    Advice("2");

    private final String code;

    MESSAGE_FUNCTION(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}

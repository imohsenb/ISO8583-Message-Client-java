package com.imohsenb.ISO8583.enums;

/**
 * Created by Mohsen Beiranvand on 18/03/14.
 */
public enum PC_ATC {

    Default("00"),
    SavingAccount("10"),
    CheckingAccount("20"),
    CreditCardAccount("30");

    private final String code;

    PC_ATC(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

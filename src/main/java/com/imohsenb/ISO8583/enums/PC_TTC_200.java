package com.imohsenb.ISO8583.enums;

/**
 * @author Mohsen Beiranvand
 */
public enum PC_TTC_200 {

    Purchase("00"),
    Withdrawal("01"),
    Void("02"),
    Refund_Return("20"),
    Payment_Deposit_Refresh("21"),
    AccountTransfer("40"),
    PurchaseAdvise("00"),
    Refund_Return_advise("20");

    private final String code;

    PC_TTC_200(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

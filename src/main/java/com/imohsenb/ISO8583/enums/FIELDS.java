package com.imohsenb.ISO8583.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohsen Beiranvand
 */
public enum FIELDS {
// |Field title                        |no  |type  |len  |fixed |format|
    F1_Bitmap                           (1,  "b",   64,   true,  null),
    F2_PAN                              (2,  "n",   19,   false, "LL"),
    F3_ProcessCode                      (3,  "n",   6,    true,  null),
    F4_AmountTransaction                (4,  "n",   12,   true,  null),
    F5_AmountSettlement                 (5,  "n",   12,   true,  null),
    F6_AmountCardholder                 (6,  "n",   12,   true,  null),
    F7_TransmissionDateTime             (7,  "n",   10,   true,  null),
    F8_AmountCardholder_BillingFee      (8,  "n",   8,    true,  null),
    F9_ConversionRate_Settlement        (9,  "n",   8,    true,  null),
    F10_ConversionRate_Cardholder       (10, "n",   8,    true,  null),
    F11_STAN                            (11, "n",   6,    true,  null),
    F12_LocalTime                       (12, "n",   6,    true,  null),
    F13_LocalDate                       (13, "n",   4,    true,  null),
    F14_ExpirationDate                  (14, "n",   4,    true,  null),
    F15_SettlementDate                  (15, "n",   4,    true,  null),
    F16_CurrencyConversionDate          (16, "n",   4,    true,  null),
    F17_CaptureDate                     (17, "n",   4,    true,  null),
    F18_MerchantType                    (18, "n",   4,    true,  null),
    F19_AcquiringInstitution            (19, "n",   3,    true,  null),
    F20_PANExtended                     (20, "n",   3,    true,  null),
    F21_ForwardingInstitution           (21, "n",   3,    true,  null),
    F22_EntryMode                       (22, "n",   3,    true,  null),
    F23_PANSequence                     (23, "n",   3,    true,  null),
    F24_NII_FunctionCode                (24, "n",   3,    true,  null),
    F25_POS_ConditionCode               (25, "n",   2,    true,  null),
    F26_POS_CaptureCode                 (26, "n",   2,    true,  null),
    F27_AuthIdResponseLength            (27, "n",   1,    true,  null),
    F28_Amount_TransactionFee           (28, "x+n", 8,    true,  null),
    F29_Amount_SettlementFee            (29, "x+n", 8,    true,  null),
    F30_Amount_TransactionProcessingFee (30, "x+n", 8,    true,  null),
    F31_Amount_SettlementProcessingFee  (31, "x+n", 8,    true,  null),
    F32_AcquiringInstitutionIdCode      (32, "n",   11,   false, "LL"),
    F33_ForwardingInstitutionIdCode     (33, "n",   11,   false, "LL"),
    F34_PAN_Extended                    (34, "ns",  28,   false, "LL"),
    F35_Track2                          (35, "z",   37,   false, "LL"),
    F36_Track3                          (36, "z",   104,  false, "LLL"),
    F37_RRN                             (37, "an",  12,   true,  null),
    F38_AuthIdResponse                  (38, "an",  6,    true,  null),
    F39_ResponseCode                    (39, "an",  2,    true,  null),
    F40_ServiceRestrictionCode          (40, "an",  3,    true,  null),
    F41_CA_TerminalID                   (41, "ans", 8,    true,  null),
    F42_CA_ID                           (42, "ans", 15,   true,  null),
    F43_CardAcceptorInfo                (43, "ans", 40,   true,  null),
    F44_AddResponseData                 (44, "an",  25,   false, "LL"),
    F45_Track1                          (45, "an",  76,   false, "LL"),
    F46_AddData_ISO                     (46, "an",  999,  false, "LLL"),
    F47_AddData_National                (47, "an",  999,  false, "LLL"),
    F48_AddData_Private                 (48, "an",  999,  false, "LLL"),
    F49_CurrencyCode_Transaction        (49, "a|n", 3,    true,  null),
    F50_CurrencyCode_Settlement         (50, "a|n", 3,    true,  null),
    F51_CurrencyCode_Cardholder         (51, "a|n", 3,    true,  null),
    F52_PIN                             (52, "b",   8,    true,  null),
    F53_SecurityControlInfo             (53, "n",   16,   true,  null),
    F54_AddAmount                       (54, "an",  120,  false, "LLL"),
    F55_ICC                             (55, "ans", 999,  false, "LLL"),
    F56_Reserved_ISO                    (56, "ans", 999,  false, "LLL"),
    F57_Reserved_National               (57, "ans", 999,  false, "LLL"),
    F58_Reserved_National               (58, "ans", 999,  false, "LLL"),
    F59_Reserved_National               (59, "ans", 999,  false, "LLL"),
    F60_Reserved_National               (60, "ans", 999,  false, "LLL"),
    F61_Reserved_Private                (61, "ans", 999,  false, "LLL"),
    F62_Reserved_Private                (62, "ans", 999,  false, "LLL"),
    F63_Reserved_Private                (63, "ans", 999,  false, "LLL"),
    F64_MAC                             (64, "b",   16,   true,  null);




    private final int no;
    private final String type;
    private final int length;
    private final boolean fixed;
    private final String format;

    FIELDS(int no, String type, int length, boolean fixed, String format) {
        this.no = no;
        this.type = type;
        this.length = length;
        this.fixed = fixed;
        this.format = format;
    }

    private static Map<Integer, FIELDS> map = new HashMap<Integer, FIELDS>();

    static {
        for (FIELDS field : FIELDS.values()) {
            map.put(field.getNo(), field);
        }
    }

    public int getNo() {
        return no;
    }

    public String getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public boolean isFixed() {
        return fixed;
    }

    public String getFormat() {
        return format;
    }

    public static FIELDS valueOf(int no) {
        return map.get(no);
    }
}

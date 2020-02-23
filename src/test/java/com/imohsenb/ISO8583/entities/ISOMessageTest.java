package com.imohsenb.ISO8583.entities;

import com.imohsenb.ISO8583.enums.FIELDS;
import com.imohsenb.ISO8583.utils.StringUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ISOMessageTest {
    @Test
    public void MessageWithOddBcdLengthPanCanBeParsed() throws Exception {
        ISOMessage isoMessage = new ISOMessage();
        isoMessage.setMessage(StringUtil.hexStringToByteArray("080060000000000000001901234567890123456789920000"), false);
        byte[] pan = isoMessage.getField(FIELDS.F2_PAN);

        assertThat(pan).isEqualTo(StringUtil.hexStringToByteArray("01234567890123456789"));
    }

    @Test
    public void MessageWithEvenBcdLengthPanCanBeParsed() throws Exception {
        ISOMessage isoMessage = new ISOMessage();
        isoMessage.setMessage(StringUtil.hexStringToByteArray("080060000000000000000812345678920000"), false);
        byte[] pan = isoMessage.getField(FIELDS.F2_PAN);

        assertThat(pan).isEqualTo(StringUtil.hexStringToByteArray("12345678"));
    }

    @Test
    public void MessageWithOddHexLengthPanCanBeParsed() throws Exception {
        ISOMessage isoMessage = new ISOMessage();
        isoMessage.setMessage(StringUtil.hexStringToByteArray("080060000000000000001301234567890123456789920000"), false, true);
        byte[] pan = isoMessage.getField(FIELDS.F2_PAN);

        assertThat(pan).isEqualTo(StringUtil.hexStringToByteArray("01234567890123456789"));
    }

    @Test
    public void MessageWithEvenHexLengthPanCanBeParsed() throws Exception {
        ISOMessage isoMessage = new ISOMessage();
        isoMessage.setMessage(StringUtil.hexStringToByteArray("080060000000000000000812345678920000"), false, true);
        byte[] pan = isoMessage.getField(FIELDS.F2_PAN);

        assertThat(pan).isEqualTo(StringUtil.hexStringToByteArray("12345678"));
    }}

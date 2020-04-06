package com.imohsenb.ISO8583.builders;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.enums.FIELDS;
import com.imohsenb.ISO8583.exceptions.ISOException;
import com.imohsenb.ISO8583.utils.StringUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ISOMessageParseTest {
    @Test
    public void anEvenLengthPanIsParsedCorrectly() throws Exception {
        ISOMessage isoMessage = new ISOMessage();
        isoMessage.setMessage(StringUtil.hexStringToByteArray("08006000000000000000161234567890123456920000"), false);

        assertThat(isoMessage.getField(FIELDS.F2_PAN)).isEqualTo(StringUtil.hexStringToByteArray("1234567890123456"));
    }

    @Test
    public void anOddLengthPanIsParsedCorrectly() throws Exception {
        ISOMessage isoMessage = new ISOMessage();
        isoMessage.setMessage(StringUtil.hexStringToByteArray("080060000000000000001901234567890123456789920000"), false);

        assertThat(isoMessage.getField(FIELDS.F2_PAN)).isEqualTo(StringUtil.hexStringToByteArray("01234567890123456789"));
    }

    @Test
    public void anExtendedFieldIsReadCorrectly() throws ISOException {
        ISOMessage isoMessage = new ISOMessage();
        isoMessage.setMessage(StringUtil.hexStringToByteArray("0800A00000000000000004000000000000001234560310"), false);

        assertThat(isoMessage.getField(FIELDS.F70_Network_Management_Code)).isEqualTo(StringUtil.hexStringToByteArray("0310"));
    }
}

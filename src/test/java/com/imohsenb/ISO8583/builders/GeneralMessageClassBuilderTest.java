package com.imohsenb.ISO8583.builders;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.enums.FIELDS;
import com.imohsenb.ISO8583.enums.MESSAGE_FUNCTION;
import com.imohsenb.ISO8583.enums.MESSAGE_ORIGIN;
import com.imohsenb.ISO8583.enums.VERSION;
import com.imohsenb.ISO8583.exceptions.ISOException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class GeneralMessageClassBuilderTest {

    @Test
    public void checkLeftPadding() throws Exception {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setLeftPadding((byte) 0xF)
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F11_STAN, "1")
                .setField(FIELDS.F24_NII_FunctionCode, "333")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("08002020010000000000920000000001F333");

    }

    @Test
    public void checkRightPadding() throws Exception {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setRightPadding((byte) 0xF)
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F11_STAN, "1")
                .setField(FIELDS.F24_NII_FunctionCode, "333")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("08002020010000000000920000000001333F");
    }

    @Test
    public void checkSetHeader() throws ISOException {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setRightPadding((byte) 0xF)
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setHeader("1002230000")
                .build();
        //Then
        assertThat(isoMessage.toString()).isEqualTo("100223000008002000000000000000920000");
    }

    @Test
    public void checkWithoutSetHeader() throws ISOException {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setRightPadding((byte) 0xF)
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .build();
        //Then
        assertThat(isoMessage.toString()).isEqualTo("08002000000000000000920000");
    }


    @Test
    public void evenPanShouldHaveCorrectLengthPrefix() throws Exception {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setLeftPadding((byte) 0xF)
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F2_PAN, "1234567890123456")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("08006000000000000000161234567890123456920000");
    }

    @Test
    public void OddPanShouldHaveCorrectLengthPrefixAndPaddingChar() throws Exception {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setLeftPadding((byte) 0xF)
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F2_PAN, "1234567890123456789")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("080060000000000000001901234567890123456789920000");
    }

    @Test
    public void checkTrack2Field() throws ISOException {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .financial()
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F35_Track2, "1234560100004865=2012111176000060FFFF")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("0200200000002000000092000037313233343536303130303030343836353D3230313231313131373630303030363046464646");
    }

    @Test
    public void shortPanShouldHaveCorrectHexLengthPrefix() throws Exception {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setLeftPadding((byte) 0xF)
                .useHexLengthPrefixes()
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F2_PAN, "12345678")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("080060000000000000000812345678920000");
    }

    @Test
    public void evenPanShouldHaveCorrectHexLengthPrefix() throws Exception {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setLeftPadding((byte) 0xF)
                .useHexLengthPrefixes()
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F2_PAN, "1234567890123456")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("08006000000000000000101234567890123456920000");
    }

    @Test
    public void OddPanShouldHaveCorrectHexLengthPrefixAndPaddingChar() throws Exception {
        ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .setLeftPadding((byte) 0xF)
                .useHexLengthPrefixes()
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F2_PAN, "1234567890123456789")
                .build();
        System.out.println(isoMessage.toString());
        assertThat(isoMessage.toString()).isEqualTo("080060000000000000001301234567890123456789920000");
    }
    //.useBCDLengthPrefixes()

}
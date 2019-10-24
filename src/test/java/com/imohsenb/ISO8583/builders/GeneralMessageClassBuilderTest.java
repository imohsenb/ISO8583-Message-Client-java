package com.imohsenb.ISO8583.builders;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.enums.FIELDS;
import com.imohsenb.ISO8583.enums.MESSAGE_FUNCTION;
import com.imohsenb.ISO8583.enums.MESSAGE_ORIGIN;
import com.imohsenb.ISO8583.enums.VERSION;
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
}
package com.imohsenb.ISO8583.builders;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.enums.VERSION;
import com.imohsenb.ISO8583.exceptions.ISOException;
import com.imohsenb.ISO8583.interfaces.MessageClass;
import com.imohsenb.ISO8583.interfaces.MessagePacker;
import com.imohsenb.ISO8583.interfaces.UnpackMessage;
import com.imohsenb.ISO8583.interfaces.UnpackMethods;
import com.imohsenb.ISO8583.utils.StringUtil;

/**
 * @author Mohsen Beiranvand
 */
public class ISOMessageBuilder {

    public static MessageClass Packer(VERSION version)
    {
        return new Builder(version.getCode());
    }

    private static class Builder implements MessageClass {

        private final String version;

        public Builder(String version) {
            this.version = version;
        }


        @Override
        public MessagePacker<GeneralMessageClassBuilder> authorization() {
            return new GeneralMessageClassBuilder(version,"1");
        }

        @Override
        public MessagePacker<GeneralMessageClassBuilder> financial() {
            return new GeneralMessageClassBuilder(version,"2");
        }

        @Override
        public MessagePacker<GeneralMessageClassBuilder> fileAction() {
            return new GeneralMessageClassBuilder(version,"3");
        }

        @Override
        public MessagePacker<GeneralMessageClassBuilder> reversal() {
            return new GeneralMessageClassBuilder(version,"4");
        }

        @Override
        public MessagePacker<GeneralMessageClassBuilder> reconciliation() {
            return new GeneralMessageClassBuilder(version,"5");
        }

        @Override
        public MessagePacker<GeneralMessageClassBuilder> administrative() {
            return new GeneralMessageClassBuilder(version,"6");
        }

        @Override
        public MessagePacker<GeneralMessageClassBuilder> feeCollection() {
            return new GeneralMessageClassBuilder(version,"7");
        }

        @Override
        public MessagePacker<GeneralMessageClassBuilder> networkManagement() {
            return new GeneralMessageClassBuilder(version,"8");
        }

    }


    public static UnpackMessage Unpacker()
    {
        return new UnpackBuilder();
    }

    public static class UnpackBuilder implements UnpackMessage,UnpackMethods {

        private byte[] message;

        @Override
        public UnpackMethods setMessage(byte[] message) {
            this.message = message;
            return this;
        }

        @Override
        public UnpackMethods setMessage(String message) {
            setMessage(StringUtil.hexStringToByteArray(message));
            return this;
        }

        @Override
        public ISOMessage build() throws ISOException {

            ISOMessage finalMessage = new ISOMessage();
            finalMessage.setMessage(message);
            return finalMessage;
        }



    }

}

package com.imohsenb.ISO8583.entities;

import com.imohsenb.ISO8583.enums.FIELDS;
import com.imohsenb.ISO8583.exceptions.ISOException;
import com.imohsenb.ISO8583.security.ISOMacGenerator;
import com.imohsenb.ISO8583.utils.FixedBitSet;
import com.imohsenb.ISO8583.utils.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Mohsen Beiranvand on 18/04/02.
 */
public class ISOMessage {

    private TreeMap<Integer, byte[]> dataElements = new TreeMap<>();

    private boolean isNil = true;
    private String message;
    private String mti;
    private byte[] msg;
    private byte[] header;
    private byte[] body;
    private byte[] primaryBitmap;
    private int msgClass;
    private int msgFunction;
    private int msgOrigin;
    private int len = 0;

    public static ISOMessage NullObject()
    {
        return new ISOMessage();
    }

    public boolean isNil() {
        return isNil;
    }

    public byte[] getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public int length() {
        return len;
    }

    public byte[] getField(int fieldNo) throws ISOException {
        if(!dataElements.containsKey(fieldNo))
            throw new ISOException("Field No "+fieldNo+" does not exists");
        return dataElements.get(fieldNo);
    }

    
    public byte[] getField(FIELDS field) {
        return dataElements.get(field.getNo());
    }

    public String getStringField(int fieldNo) throws ISOException {
        return getStringField(FIELDS.valueOf(fieldNo));

    }

    public String getStringField(FIELDS field) throws ISOException {

        return getStringField(field,false);
    }

    public String getStringField(int fieldNo, boolean asciiFix) throws ISOException {
        return getStringField(FIELDS.valueOf(fieldNo),asciiFix);

    }

    public String getStringField(FIELDS field, boolean asciiFix) throws ISOException {

        String temp = StringUtil.fromByteArray(getField(field.getNo()));
        if(asciiFix && !field.getType().equals("n"))
            return StringUtil.hexToAscii(temp);
        return temp;
    }

    public ISOMessage setMessage(byte[] message, boolean headerAvailable) throws ISOException {

        isNil = false;

        msg = message;
        len = msg.length / 2;

        int headerOffset = 0;

        if(headerAvailable) {
            headerOffset = 5;
        }

        try {

            this.header = Arrays.copyOfRange(msg, 0, headerOffset);
            this.body = Arrays.copyOfRange(msg, headerOffset, msg.length);
            this.primaryBitmap = Arrays.copyOfRange(body, 2, 10);

            parseHeader();
            parseBody();

        }catch (Exception e) {
            throw new ISOException(e.getMessage(),e.getCause());
        }

        return this;
    }


    public ISOMessage setMessage(byte[] message) throws ISOException {
        return this.setMessage(message,true);
    }

    private void parseHeader() {
        if(body.length > 2) {
            mti = StringUtil.fromByteArray(Arrays.copyOfRange(body,0,2));
            msgClass = Integer.parseInt(mti.substring(1,2));
            msgFunction = Integer.parseInt(mti.substring(2,3));
            msgOrigin = Integer.parseInt(mti.substring(3,4));

//            System.out.println("msgClass = " + msgClass);
//            System.out.println("msgFunction = " + msgFunction);
//            System.out.println("msgOrigin = " + msgOrigin);
        }
    }

    private void parseBody()
    {
        FixedBitSet pb = new FixedBitSet(64);
        pb.fromHexString(StringUtil.fromByteArray(primaryBitmap));
        int offset = 10;

        for (int o : pb.getIndexes()) {

            FIELDS field =  FIELDS.valueOf(o);

            if(field.isFixed())
            {
                int len = field.getLength();
                switch (field.getType())
                {
                    case "n":
                        if(len % 2 !=0)
                            len++;
                        len = len/2;
                        addElement(field,Arrays.copyOfRange(body,offset,offset + len));
                        break;
                    default:
                        addElement(field,Arrays.copyOfRange(body,offset,offset + len));
                        break;
                }
                offset += len;
            }else{

                int formatLength = 1;
                switch (field.getFormat()) {
                    case "LL":
                        formatLength = 1;
                        break;
                    case "LLL":
                        formatLength = 2;
                        break;
                }

                int flen = Integer.valueOf(
                        StringUtil.fromByteArray(Arrays.copyOfRange(body,offset,offset + formatLength)));

                switch (field.getType())
                {
                    case "z":
                        flen /=2;
                }

                offset = offset + formatLength;

                addElement(field,Arrays.copyOfRange(body,offset,offset + flen));

                offset += flen;
            }

        }
    }

    private void addElement(FIELDS field, byte[] data) {
        dataElements.put(field.getNo(), data);
    }


    public Set<Map.Entry<Integer, byte[]>> getEntrySet()
    {
        return dataElements.entrySet();
    }



    public boolean fieldExits(FIELDS field) {
        return fieldExits(field.getNo());
    }

    public boolean fieldExits(int no) {
        return dataElements.containsKey(no);
    }

    public String getMti() {
        return mti;
    }

    public int getMsgClass() {
        return msgClass;
    }

    public int getMsgFunction() {
        return msgFunction;
    }

    public int getMsgOrigin() {
        return msgOrigin;
    }

    public boolean validateMac(ISOMacGenerator isoMacGenerator) throws ISOException {

        if(!fieldExits(FIELDS.F64_MAC) || getField(FIELDS.F64_MAC).length == 0 )
        {
            System.out.println("validate mac : not exists" );
            return false;
        }
        byte[] mBody = new byte[getBody().length - 8];
        System.arraycopy(getBody(),0,mBody,0,getBody().length - 8);
        byte[] oMac = Arrays.copyOf(getField(FIELDS.F64_MAC), 8);
        byte[] vMac = isoMacGenerator.generate(mBody);

        return Arrays.equals(oMac,vMac);
    }

    public String toString() {
        if(message == null)
            message = StringUtil.fromByteArray(msg);
        return message;
    }

    public String fieldsToString() {
        StringBuilder stringBuilder= new StringBuilder();

        stringBuilder.append("\r\n");
        for (Map.Entry<Integer, byte[]> item:
             dataElements.entrySet()) {
            stringBuilder
                    .append(FIELDS.valueOf(item.getKey()).name())
                    .append(" : ")
                    .append(StringUtil.fromByteArray(item.getValue()))
                    .append("\r\n");
        }
        stringBuilder.append("\r\n");
        return stringBuilder.toString();
    }

    public void clear() {

        Arrays.fill(header, (byte) 0);
        Arrays.fill(body, (byte) 0);
        Arrays.fill(primaryBitmap, (byte) 0);

        message = null;
        header = null;
        body = null;
        primaryBitmap = null;

    }

}

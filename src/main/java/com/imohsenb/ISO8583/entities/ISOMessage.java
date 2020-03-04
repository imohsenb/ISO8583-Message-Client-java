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
 * ISO Message Entity
 *
 * @author Mohsen Beiranvand
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

    public static ISOMessage NullObject() {
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

    /**
     * Get primary bitmap
     *
     * @return returns primary byte array
     * @since 1.0.4-SNAPSHOT
     */
    public byte[] getPrimaryBitmap() {
        return primaryBitmap;
    }

    /**
     * Message length
     *
     * @return returns message length
     */
    public int length() {
        return len;
    }

    /**
     * Get field value in byte array format
     *
     * @param fieldNo field number
     * @return returns field value in byte array format
     * @throws ISOException throws exception
     */
    public byte[] getField(int fieldNo) throws ISOException {
        if (!dataElements.containsKey(fieldNo))
            throw new ISOException("Field No " + fieldNo + " does not exists");
        return dataElements.get(fieldNo);
    }

    /**
     * Get field value in byte array format
     *
     * @param field field in {@link FIELDS} format
     * @return returns field value in byte array format
     */
    public byte[] getField(FIELDS field) {
        return dataElements.get(field.getNo());
    }

    /**
     * Get field value in string format
     *
     * @param fieldNo field number
     * @return returns field value in String format
     * @throws ISOException throws exception
     */
    public String getStringField(int fieldNo) throws ISOException {
        return getStringField(FIELDS.valueOf(fieldNo));

    }

    /**
     * Get field value in string format
     *
     * @param field field in {@link FIELDS} format
     * @return returns field value in String format
     * @throws ISOException throws exception
     */
    public String getStringField(FIELDS field) throws ISOException {

        return getStringField(field, false);
    }

    /**
     * Get field value in string format
     *
     * @param fieldNo  field number
     * @param asciiFix set true if you want result in ASCII format
     * @return returns field value in String format
     * @throws ISOException throws exception
     */
    public String getStringField(int fieldNo, boolean asciiFix) throws ISOException {
        return getStringField(FIELDS.valueOf(fieldNo), asciiFix);

    }

    /**
     * Get field value in string format
     *
     * @param field    field in {@link FIELDS} format
     * @param asciiFix set true if you want result in ASCII format
     * @return returns field value in String format
     * @throws ISOException throws exception
     */
    public String getStringField(FIELDS field, boolean asciiFix) throws ISOException {

        String temp = StringUtil.fromByteArray(getField(field.getNo()));
        if (asciiFix && !field.getType().equals("n"))
            return StringUtil.hexToAscii(temp);
        return temp;
    }

    /**
     * Set and parse ISO8583 message from buffer
     *
     * @param message         ISO8583 in byte array format
     * @param headerAvailable set true if header is available in buffer
     * @return returns ISO8583 message in ISOMessage type
     * @throws ISOException throws exception
     */
    public ISOMessage setMessage(byte[] message, boolean headerAvailable) throws ISOException {

        isNil = false;

        msg = message;
        len = msg.length / 2;

        int headerOffset = 0;

        if (headerAvailable) {
            headerOffset = 5;
        }

        try {

            this.header = Arrays.copyOfRange(msg, 0, headerOffset);
            this.body = Arrays.copyOfRange(msg, headerOffset, msg.length);
            this.primaryBitmap = Arrays.copyOfRange(body, 2, 10);

            parseHeader();
            parseBody();

        } catch (Exception e) {
            throw new ISOException(e.getMessage(), e.getCause());
        }

        return this;
    }

    /**
     * Set and parse ISO8583 message from buffer
     *
     * @param message ISO8583 in byte array format
     * @return returns ISO8583 message in ISOMessage type
     * @throws ISOException throws exception
     */
    public ISOMessage setMessage(byte[] message) throws ISOException {
        return this.setMessage(message, true);
    }

    private void parseHeader() {
        if (body.length > 2) {
            mti = StringUtil.fromByteArray(Arrays.copyOfRange(body, 0, 2));
            msgClass = Integer.parseInt(mti.substring(1, 2));
            msgFunction = Integer.parseInt(mti.substring(2, 3));
            msgOrigin = Integer.parseInt(mti.substring(3, 4));
        }
    }

    private void parseBody() {
        FixedBitSet pb = new FixedBitSet(64);
        pb.fromHexString(StringUtil.fromByteArray(primaryBitmap));
        int offset = 10;

        for (int o : pb.getIndexes()) {

            FIELDS field = FIELDS.valueOf(o);

            if (field.isFixed()) {
                int len = field.getLength();
                switch (field.getType()) {
                    case "n":
                        if (len % 2 != 0)
                            len++;
                        len = len / 2;
                        addElement(field, Arrays.copyOfRange(body, offset, offset + len));
                        break;
                    default:
                        addElement(field, Arrays.copyOfRange(body, offset, offset + len));
                        break;
                }
                offset += len;
            } else {

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
                        StringUtil.fromByteArray(Arrays.copyOfRange(body, offset, offset + formatLength)));

                switch (field.getType()) {
                    case "z":
                    case "n":
                        flen /= 2;
                }

                offset = offset + formatLength;

                addElement(field, Arrays.copyOfRange(body, offset, offset + flen));

                offset += flen;
            }

        }
    }

    private void addElement(FIELDS field, byte[] data) {
        dataElements.put(field.getNo(), data);
    }


    /**
     * Get EntrySet
     *
     * @return returns data elements entry set
     */
    public Set<Map.Entry<Integer, byte[]>> getEntrySet() {
        return dataElements.entrySet();
    }

    /**
     * Check Field exists by {@link FIELDS} enum
     *
     * @param field field enum
     * @return Returns true if field has value in message
     */
    public boolean fieldExits(FIELDS field) {
        return fieldExits(field.getNo());
    }

    /**
     * Check Field exists field number
     *
     * @param no field number
     * @return Returns true if field has value in message
     */
    public boolean fieldExits(int no) {
        return dataElements.containsKey(no);
    }

    /**
     * Get Message MTI
     * @return returns MTI in String format
     */
    public String getMti() {
        return mti;
    }

    /**
     * Get message class
     * @return returns message class
     */
    public int getMsgClass() {
        return msgClass;
    }

    /**
     * Get message function
     * @return returns message function
     */
    public int getMsgFunction() {
        return msgFunction;
    }

    /**
     * Get message origin
     * @return returns message origin
     */
    public int getMsgOrigin() {
        return msgOrigin;
    }

    /**
     * Validate mac
     * it's useful method to validate response MAC
     *
     * @param isoMacGenerator implementation of {@link ISOMacGenerator}
     * @return returns true if response message MAC is valid
     * @throws ISOException throws exception
     */
    public boolean validateMac(ISOMacGenerator isoMacGenerator) throws ISOException {

        if (!fieldExits(FIELDS.F64_MAC) || getField(FIELDS.F64_MAC).length == 0) {
            System.out.println("validate mac : not exists");
            return false;
        }
        byte[] mBody = new byte[getBody().length - 8];
        System.arraycopy(getBody(), 0, mBody, 0, getBody().length - 8);
        byte[] oMac = Arrays.copyOf(getField(FIELDS.F64_MAC), 8);
        byte[] vMac = isoMacGenerator.generate(mBody);

        return Arrays.equals(oMac, vMac);
    }

    /**
     * Convert ISOMessage to String
     * @return ISOMessage in String format
     */
    public String toString() {
        if (message == null)
            message = StringUtil.fromByteArray(msg);
        return message;
    }

    /**
     * Convert all fields in String format
     * @return returns strings of fields
     */
    public String fieldsToString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\r\n");
        for (Map.Entry<Integer, byte[]> item :
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

    /**
     * Clean up message
     */
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

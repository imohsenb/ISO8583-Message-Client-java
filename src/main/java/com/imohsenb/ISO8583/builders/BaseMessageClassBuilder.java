package com.imohsenb.ISO8583.builders;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.enums.*;
import com.imohsenb.ISO8583.exceptions.ISOException;
import com.imohsenb.ISO8583.interfaces.DataElement;
import com.imohsenb.ISO8583.interfaces.MessagePacker;
import com.imohsenb.ISO8583.interfaces.ProcessCode;
import com.imohsenb.ISO8583.security.ISOMacGenerator;
import com.imohsenb.ISO8583.utils.ByteArray;
import com.imohsenb.ISO8583.utils.FixedBitSet;
import com.imohsenb.ISO8583.utils.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Mohsen Beiranvand
 */
public abstract class BaseMessageClassBuilder<T> implements
        DataElement<T>, ProcessCode<T>, MessagePacker<T>
{

    private String version;
    private String messageClass = "0";
    private String messageFunction = "0";
    private String messageOrigin = "0";
    private String processCode;
    private TreeMap<Integer,byte[]> dataElements = new TreeMap<>();
    private String header;
    private byte paddingByte = 0xF;
    private boolean leftPadding = false;

    public BaseMessageClassBuilder(String version, String messageClass)
    {
        this.version = version;
        this.messageClass = messageClass;
    }

    public ISOMessage build() throws ISOException {

        ISOMessage finalMessage = new ISOMessage();
        finalMessage.setMessage(buildBuffer(true),this.header != null);

        //clear();

        return finalMessage;
    }

    private void clear()
    {
        for(Map.Entry<Integer, byte[]> elem :  dataElements.entrySet()) {
            Arrays.fill(elem.getValue(), (byte) 0);
        }
        dataElements = new TreeMap<>();
    }

    private byte[] buildBuffer(boolean generateBitmap)
    {
        FixedBitSet primaryBitmap = new FixedBitSet(64);
        ByteArray dataBuffer = new ByteArray();

        for(Map.Entry<Integer, byte[]> elem :  dataElements.entrySet()) {
            if(generateBitmap)
                primaryBitmap.flip(elem.getKey() - 1);
            dataBuffer.append(elem.getValue());
        }

        if(generateBitmap)
            dataBuffer.prepend(StringUtil.hexStringToByteArray(primaryBitmap.toHexString()));

        dataBuffer.prepend(StringUtil.hexStringToByteArray((version + messageClass + messageFunction + messageOrigin)));

        if(header!=null && generateBitmap)
            dataBuffer.prepend(StringUtil.hexStringToByteArray(header));

        return dataBuffer.array();
    }

    public DataElement<T> setHeader(String header)
    {
        this.header = header;
        return this;
    }

    @Override
    public DataElement<T> setField(int no, byte[] value) throws ISOException {
        setField(FIELDS.valueOf(no),value);
        return this;
    }

    @Override
    public DataElement<T> setField(FIELDS field, byte[] value) throws ISOException {
        return setField(field,value,value.length);
    }

    public DataElement<T> setField(FIELDS field, byte[] value, int valueLength) throws ISOException {

        byte[] fValue = value;

        if(value == null)
            throw new ISOException(field.name()+" is Null");
        //length check and padding
        if(field.isFixed())
        {
            if(field.getLength()%2 !=0)
            {
                if(field.getType().equals("n")) {
                    fValue = padding(field, value, fValue);
                }
            }else if(field.getLength()-(fValue.length*2) > 0 && field.getType().equals("n")){

                ByteArray valueBuffer = new ByteArray();
                valueBuffer.append(fValue);
                valueBuffer.prepend(new String(new char[(field.getLength()-(fValue.length*2))/2]).getBytes());
                fValue = valueBuffer.array();
                valueBuffer.clear();
                valueBuffer = null;
            }

            if(fValue.length > field.getLength())
            {
                fValue = Arrays.copyOfRange(fValue,fValue.length-field.getLength(),fValue.length);
            }

        }else{

            int dLen = fValue.length;
            switch (field.getType())
            {
                case "z":
                    if(dLen > field.getLength())
                        fValue = Arrays.copyOfRange(fValue,fValue.length - field.getLength(),fValue.length);



                    dLen = fValue.length * 2;

                    break;
            }

            ByteArray valueBuffer = new ByteArray();
            valueBuffer.append(fValue);

            switch (field.getFormat())
            {
                case "LL":
                    if(2 - String.valueOf(valueLength).length() <= 0 )
                        valueBuffer.prepend(StringUtil.hexStringToByteArray(valueLength + ""));
                    else
                        valueBuffer.prepend(StringUtil.hexStringToByteArray(String.format("%" + (2 - String.valueOf(valueLength).length()) + "d%s", 0, valueLength)));
                    break;
                case "LLL":
                    valueBuffer.prepend(StringUtil.hexStringToByteArray(String.format("%0" + (4 - String.valueOf(dLen).length()) + "d%s", 0, dLen)));
                    break;
            }

            fValue = valueBuffer.array();
            valueBuffer.clear();
            valueBuffer = null;
        }

        dataElements.put(field.getNo(),fValue);

        return this;
    }

    private byte[] padding(FIELDS field, byte[] value, byte[] fValue) {
        byte[] fixed = new byte[(int) Math.ceil(field.getLength() / 2) * 2];

        if (leftPadding) {
            leftPad(value, fValue, fixed);
        } else {
            rightPad(value, fValue, fixed);
        }
        fValue = fixed;
        return fValue;
    }

    private void leftPad(byte[] value, byte[] fValue, byte[] fixed) {
        for (int i = 0; i < fValue.length; i++) {
            fixed[i] = fValue[i];
        }
        fixed[0] = (byte) (fixed[0] + (paddingByte << 4));
    }

    private void rightPad(byte[] value, byte[] fValue, byte[] fixed) {
        for (int i = 0; i < fValue.length; i++) {
            fixed[i] = (byte) ((fValue[i] & 0x0F) << 4);
            if (i + 1 < value.length)
                fixed[i] += (fValue[i + 1] & 0xF0) >> 4;
        }
        fixed[fValue.length - 1] = (byte) (fixed[fValue.length - 1] + paddingByte);
    }

    public DataElement<T> setField(int no, String value) throws ISOException {
        setField(FIELDS.valueOf(no), value);
        return this;
    }

    public DataElement<T> setField(FIELDS field, String value) throws ISOException {
        switch (field.getType())
        {
            case "n":
                setField(field,StringUtil.hexStringToByteArray(value),value.length());
                break;
            default:
                byte[] bytes = value.getBytes();
                setField(field,bytes,bytes.length);
        }

        return this;
    }

	@Override
	public DataElement<T> generateMac(ISOMacGenerator generator)  throws ISOException {

        if(generator != null)
        {
            byte[] mac = generator.generate(buildBuffer(true));
            if(mac != null)
                setField(FIELDS.F64_MAC,mac);
            else
                throw new ISOException("MAC is null");
        }
        
		return this;
	}

    public ProcessCode<T> mti(MESSAGE_FUNCTION mFunction, MESSAGE_ORIGIN mOrigin) {
        this.messageFunction = mFunction.getCode();
        this.messageOrigin = mOrigin.getCode();
        return this;
    }

    @Override
    public MessagePacker<T> setLeftPadding(byte character) {
        this.leftPadding = true;
        this.paddingByte = character;
        return this;
    }

    @Override
    public MessagePacker<T> setRightPadding(byte character) {
        this.leftPadding = false;
        this.paddingByte = character;
        return this;
    }

    //
    public DataElement<T> processCode(String code) throws ISOException {
        this.processCode = code;
        this.setField(FIELDS.F3_ProcessCode,this.processCode);
        return this;
    }

    public DataElement<T> processCode(PC_TTC_100 ttc) throws ISOException {
        this.processCode = ttc.getCode() + PC_ATC.Default.getCode() + PC_ATC.Default.getCode();
        this.setField(FIELDS.F3_ProcessCode,this.processCode);
        return this;
    }

    public DataElement<T> processCode(PC_TTC_100 ttc, PC_ATC atcFrom, PC_ATC atcTo) throws ISOException {
        this.processCode = ttc.getCode() + atcFrom.getCode() + atcTo.getCode();
        this.setField(FIELDS.F3_ProcessCode,this.processCode);
        return this;
    }

    public DataElement<T> processCode(PC_TTC_200 ttc) throws ISOException {
        this.processCode = ttc.getCode() + PC_ATC.Default.getCode() + PC_ATC.Default.getCode();
        this.setField(FIELDS.F3_ProcessCode,this.processCode);
        return this;
    }

    public DataElement<T> processCode(PC_TTC_200 ttc, PC_ATC atcFrom, PC_ATC atcTo) throws ISOException {
        this.processCode = ttc.getCode() + atcFrom.getCode() + atcTo.getCode();
        this.setField(FIELDS.F3_ProcessCode,this.processCode);
        return this;
    }



}

package com.imohsenb.ISO8583.utils;


import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Mohsen Beiranvand
 */
public final class TLVParser {

    public static HashMap<String,byte[]> parse(byte[] message, int tagLength, int lengthOfDataLen)
    {
        HashMap<String,byte[]> parts = new HashMap<>();

        int offset = 0;

        while (offset < message.length) {
            String tag = new String(Arrays.copyOfRange(message, offset, offset+tagLength));
            int len = Integer.parseInt(new String(Arrays.copyOfRange(message, offset + tagLength, offset + tagLength + lengthOfDataLen)));

            if(message.length >= offset + tagLength + lengthOfDataLen  + len)
                parts.put(tag, Arrays.copyOfRange(message, offset + tagLength + lengthOfDataLen , offset + tagLength + lengthOfDataLen  + len));
            offset += len + tagLength + lengthOfDataLen;
        }

        return parts;
    }

}

package com.imohsenb.ISO8583.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Mohsen Beiranvand
 */
public final class StringUtil {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String fromByteArray(byte[] data)
    {
        char[] hexChars = new char[data.length * 2];
        for ( int j = 0; j < data.length; j++ ) {
            int v = data[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String asciiFromByteArray(byte[] data)
    {
        return hexToAscii(fromByteArray(data));
    }

    //it's come from http://www.baeldung.com/java-convert-hex-to-ascii
    public static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }

    //it's come from http://www.baeldung.com/java-convert-hex-to-ascii
    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static byte[] asciiToHex(byte[] data) {

        char[] hexChars = new char[data.length * 2];
        for ( int j = 0; j < data.length; j++ ) {
            int v = data[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        byte[] res = new byte[hexChars.length];
        for (int i = 0; i <hexChars.length; i++) {
            res[i] = (byte) hexChars[i];
        }

        Arrays.fill(hexChars, '\u0000');
        hexChars = null;

        return res;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        boolean padd = false;
        if(len%2 != 0)
        {
            s = "0" + s;
            len++;
            padd = true;
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }

    public static String fromByteBuffer(ByteBuffer readBuffer) {

        return fromByteArray(Arrays.copyOfRange(readBuffer.array(),0,readBuffer.position()));
    }


    public static String intToHexString(int value)
    {
        String hs = Integer.toHexString(value);
        if(hs.length() % 2 !=0)
            hs = "0" + hs;
        hs = hs.toUpperCase();
        return hs;
    }

    public static byte[] asciiToByteArray(byte[] bytes) {
        return StringUtil.hexStringToByteArray(StringUtil.hexToAscii(StringUtil.fromByteArray(bytes)));
    }

    public static String toHexString(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            sb.append(toHexString(str.charAt(i)));
        }
        return sb.toString();
    }

    /**
     * convert into Hexadecimal notation of Unicode.<br>
     * example)a?\u0061
     * @param ch
     * @return
     */
    public static String toHexString(char ch) {
        String hex = Integer.toHexString((int) ch);
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        hex = "\\u" + hex;
        return hex;
    }

}

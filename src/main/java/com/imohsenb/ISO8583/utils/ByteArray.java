package com.imohsenb.ISO8583.utils;

import java.util.Arrays;

/**
 * @author Mohsen Beiranvand
 */
public final class ByteArray {

    private static int frameSize = 512;
    private int size;
    private byte[] data;
    private int position = 0;

    public ByteArray()
    {
        init();
    }

    private void init() {
        this.size = frameSize;
        this.data = new byte[size];
        this.position = 0;
    }

    public ByteArray append(byte[] value)
    {
        if(value.length + position > size)
            expandBuffer();

        System.arraycopy(
                value,
                0,
                data,
                position,
                value.length
        );

        position += value.length;
        return this;
    }

    public ByteArray append(byte value) {
        append(new byte[]{value});
        return this;
    }

    public ByteArray prepend(byte[] value)
    {
        int vlen = value.length;
        int newSize = (int) (Math.ceil((size + vlen) / frameSize) * frameSize);
        byte[] dest = new byte[newSize];

        System.arraycopy(value,0,dest,0,vlen);
        System.arraycopy(data,0,dest,vlen,position);

        data = dest;
        position = vlen + position;
        size = newSize;
        dest = null;

        return this;
    }

    public ByteArray prepend(byte value) {
        prepend(new byte[]{value});
        return this;
    }

    private void expandBuffer() {

        int newSize = size + frameSize;
        byte[] dest = new byte[size];
        System.arraycopy(data,0,dest,0,size);
        data = dest;
        size = newSize;
        dest = null;

    }

    public int position()
    {
        return position;
    }

    public int limit()
    {
        return size;
    }

    public byte[] array()
    {
        return Arrays.copyOfRange(data,0,position);
    }


    public ByteArray compact()
    {
        data = Arrays.copyOfRange(data,0,position);
        size = position;
        return this;
    }

    public String toString()
    {
        return new String(array());
    }

    public ByteArray clear()
    {
        Arrays.fill(data, (byte) 0);
        init();
        return this;
    }

    public ByteArray replace(byte[] value) {

        init();
        append(value);
        return this;
    }
}

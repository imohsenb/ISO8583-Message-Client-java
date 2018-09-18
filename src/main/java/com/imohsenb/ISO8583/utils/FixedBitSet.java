package com.imohsenb.ISO8583.utils;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * @author Mohsen Beiranvand
 */
public class FixedBitSet extends BitSet {

    private final int nbits;

    public FixedBitSet(final int nbits) {
        super(nbits);
        this.nbits = nbits;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(nbits);

        for (int i = 0; i < nbits; i++) {
            buffer.append(get(i) ? '1' : '0');
        }

        return buffer.toString();
    }

    public FixedBitSet fromHexString(String value)
    {
        int offset = 0;
        for (int i = 0; i < value.length(); i=i+1) {
            String item = value.substring(i,i+1);
            byte bitem = (byte) Integer.parseInt(item, 16);
            if((bitem & 0b1000) > 0)
                set(offset);
            if((bitem & 0b0100) > 0)
                set(offset + 1);
            if((bitem & 0b0010) > 0)
                set(offset + 2);
            if((bitem & 0b0001) > 0)
                set(offset + 3);
            offset+=4;
        }
        return this;
    }

    public String toHexString()
    {
        final StringBuilder buffer = new StringBuilder(nbits);
        String bStr = toString();

        for(int c=0;c< nbits; c=c+4)
        {
            int decimal = Integer.parseInt(bStr.substring(c,c+4),2);
            String hexStr = Integer.toString(decimal,16);
            buffer.append(hexStr);
        }
        return buffer.toString();
    }

    public ArrayList<Integer> getIndexes()
    {
        ArrayList<Integer> list = new ArrayList<>();
        int indx = -1;
        int size = size();
        while (indx < size)
        {
            indx = nextSetBit(indx+1);
            if(indx == -1)
                break;
            list.add(indx + 1);
        }
        return list;
    }

}

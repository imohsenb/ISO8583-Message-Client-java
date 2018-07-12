package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.exceptions.ISOException;

/**
 * Created by Mohsen Beiranvand on 18/04/01.
 */
public interface UnpackMethods {

    ISOMessage build() throws ISOException;
}

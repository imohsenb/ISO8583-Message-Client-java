package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.exceptions.ISOException;

/**
 * @author Mohsen Beiranvand
 */
public interface UnpackMethods {

    ISOMessage build() throws ISOException;
}

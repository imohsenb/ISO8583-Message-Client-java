package com.imohsenb.ISO8583.exceptions;

/**
 * Created by Mohsen Beiranvand on 18/03/17.
 */
public class ISOException extends Exception {

    public ISOException(String message) {
        super(message);
    }

    public ISOException(String message, Throwable cause) {
        super(message, cause);
    }
}

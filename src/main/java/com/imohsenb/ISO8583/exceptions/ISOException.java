package com.imohsenb.ISO8583.exceptions;

/**
 * @author Mohsen Beiranvand
 */
public class ISOException extends Exception {

    public ISOException(String message) {
        super(message);
    }

    public ISOException(String message, Throwable cause) {
        super(message, cause);
    }
}

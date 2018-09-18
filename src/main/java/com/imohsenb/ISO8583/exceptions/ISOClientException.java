package com.imohsenb.ISO8583.exceptions;

/**
 * @author Mohsen Beiranvand
 */
public class ISOClientException extends Exception {

    public ISOClientException(String message) {
        super(message);
    }

    public ISOClientException(Exception e) {
        super(e);
    }
}

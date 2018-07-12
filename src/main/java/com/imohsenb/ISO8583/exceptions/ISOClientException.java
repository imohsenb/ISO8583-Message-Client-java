package com.imohsenb.ISO8583.exceptions;

/**
 * Created by Mohsen Beiranvand on 18/03/31.
 */
public class ISOClientException extends Exception {

    public ISOClientException(String message) {
        super(message);
    }

    public ISOClientException(Exception e) {
        super(e);
    }
}

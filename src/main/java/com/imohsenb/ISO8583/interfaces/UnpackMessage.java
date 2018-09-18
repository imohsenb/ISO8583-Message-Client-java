package com.imohsenb.ISO8583.interfaces;

/**
 * @author Mohsen Beiranvand
 */
public interface UnpackMessage {

    UnpackMethods setMessage(byte[] message);
    UnpackMethods setMessage(String message);

}

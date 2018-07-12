package com.imohsenb.ISO8583.interfaces;

/**
 * Created by Mohsen Beiranvand on 18/04/01.
 */
public interface UnpackMessage {

    UnpackMethods setMessage(byte[] message);
    UnpackMethods setMessage(String message);

}

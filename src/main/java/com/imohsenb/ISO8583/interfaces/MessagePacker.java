package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.enums.MESSAGE_FUNCTION;
import com.imohsenb.ISO8583.enums.MESSAGE_ORIGIN;

/**
 * Created by Mohsen Beiranvand on 18/03/16.
 */
public interface MessagePacker<T> {

    ProcessCode<T> mti(MESSAGE_FUNCTION mFunction, MESSAGE_ORIGIN mOrigin);
    MessagePacker<T> setLeftPadding(char character);
    MessagePacker<T> setRightPadding(char character);
}

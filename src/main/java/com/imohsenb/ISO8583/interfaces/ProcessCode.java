package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.enums.PC_ATC;
import com.imohsenb.ISO8583.enums.PC_TTC_100;
import com.imohsenb.ISO8583.enums.PC_TTC_200;
import com.imohsenb.ISO8583.exceptions.ISOException;

/**
 * @author Mohsen Beiranvand
 */
public interface ProcessCode<T> {
    DataElement<T> processCode(String code) throws ISOException;
    DataElement<T> processCode(PC_TTC_100 ttc) throws ISOException;
    DataElement<T> processCode(PC_TTC_100 ttc, PC_ATC atcFrom, PC_ATC atcTo) throws ISOException;
    DataElement<T> processCode(PC_TTC_200 ttc) throws ISOException;
    DataElement<T> processCode(PC_TTC_200 ttc, PC_ATC atcFrom, PC_ATC atcTo) throws ISOException;
}

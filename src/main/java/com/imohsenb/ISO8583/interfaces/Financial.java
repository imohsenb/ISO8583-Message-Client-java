package com.imohsenb.ISO8583.interfaces;

import java.math.BigDecimal;

/**
 * @author Mohsen Beiranvand
 */
public interface Financial<T> {


    DataElement<T> setAmount(BigDecimal amount);
}

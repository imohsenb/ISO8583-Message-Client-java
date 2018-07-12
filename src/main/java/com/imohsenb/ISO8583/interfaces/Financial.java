package com.imohsenb.ISO8583.interfaces;

import java.math.BigDecimal;

/**
 * Created by Mohsen Beiranvand on 18/03/16.
 */
public interface Financial<T> {


    DataElement<T> setAmount(BigDecimal amount);
}

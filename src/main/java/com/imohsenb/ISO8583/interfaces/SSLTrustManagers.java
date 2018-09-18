package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.builders.ISOClientBuilder;

import javax.net.ssl.TrustManager;

/**
 * @author Mohsen Beiranvand
 */
public interface SSLTrustManagers
{
    ISOClientBuilder.ClientBuilder setTrustManagers(TrustManager[] trustManagers);
}
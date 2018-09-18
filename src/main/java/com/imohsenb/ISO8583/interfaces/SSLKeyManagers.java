package com.imohsenb.ISO8583.interfaces;

import javax.net.ssl.KeyManager;

/**
 * @author Mohsen Beiranvand
 */
public interface SSLKeyManagers
{
    SSLTrustManagers setKeyManagers(KeyManager[] keyManagers);
}
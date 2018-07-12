package com.imohsenb.ISO8583.interfaces;

import javax.net.ssl.KeyManager;

public interface SSLKeyManagers
{
    SSLTrustManagers setKeyManagers(KeyManager[] keyManagers);
}
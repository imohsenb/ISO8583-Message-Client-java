package com.imohsenb.ISO8583.security;

/**
 * ISOMacGenerator
 */
public abstract class ISOMacGenerator {

    public abstract byte[] generate(byte[] data);
        
}
package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.exceptions.ISOClientException;

import java.io.IOException;

/**
 * @author Mohsen Beiranvand
 */
public interface ISOClient {

    /**
     *
     * @throws ISOClientException
     * @throws IOException
     */
    void connect() throws ISOClientException, IOException;

    /**
     *
     */
    void disconnect();

    /**
     *
     * @param isoMessage
     * @return
     * @throws ISOClientException
     * @throws IOException
     */
    byte[] sendMessageSync(ISOMessage isoMessage) throws ISOClientException, IOException;

    /**
     *
     *
     * @return
     */
    boolean isConnected();

    /**
     *
     * @return
     */
    boolean isClosed();

    /**
     *
     * @param isoClientEventListener
     */
    void setEventListener(ISOClientEventListener isoClientEventListener);

}

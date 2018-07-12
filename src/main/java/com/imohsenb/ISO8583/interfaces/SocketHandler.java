package com.imohsenb.ISO8583.interfaces;

import com.imohsenb.ISO8583.exceptions.ISOClientException;
import com.imohsenb.ISO8583.handlers.SSLHandler;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * <h1>Socket Handler Interface</h1>
 * Its responsible about initializing socket and connection to ISO switch
 * @author Mohsen Beiranvand
 */
public interface SocketHandler {


    /**
     * Initialize SSL connection to switch
     * @param host IP address of switch
     * @param port Switch port number
     * @param isoClientEventListener Event listener for dispatch state of operation
     * @param sslHandler Implementation of {@link SSLHandler} for handling ssl handshakes
     * @throws ISOClientException
     */
    void init(String host, int port, ISOClientEventListener isoClientEventListener, SSLHandler sslHandler) throws ISOClientException;

    /**
     * Initialize NONE SSL connection to switch
     * @param host IP address of switch
     * @param port Switch port number
     * @param isoClientEventListener Event listener for dispatch state of operation
     * @throws IOException
     */
    void init(String host, int port, ISOClientEventListener isoClientEventListener) throws IOException;

    /**
     * Send message in sync way and return result
     * @param buffer buffer for sending
     * @param length  length of message length
     * @return response buffer from message
     * @throws IOException
     * @throws ISOClientException
     */
    byte[] sendMessageSync(ByteBuffer buffer, int length) throws IOException, ISOClientException ;

    /**
     * Close current socket
     */
    void close();

    /**
     * Set waiting time for take a response from switch
     * @param readTimeout time out in milliseconds
     * @throws SocketException
     */
    void setReadTimeout(int readTimeout) throws SocketException;

    /**
     * Check socket already connected to the host.
     * @return true if is connected
     */
    boolean isConnected();

    /**
     * Check if socket is closed.
     * @return true if socket already closed
     */
    boolean isClosed();
}

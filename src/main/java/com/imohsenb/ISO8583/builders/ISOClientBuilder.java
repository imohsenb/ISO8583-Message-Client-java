package com.imohsenb.ISO8583.builders;

import com.imohsenb.ISO8583.entities.ISOMessage;
import com.imohsenb.ISO8583.exceptions.ISOClientException;
import com.imohsenb.ISO8583.handlers.IOSocketHandler;
import com.imohsenb.ISO8583.handlers.NIOSocketHandler;
import com.imohsenb.ISO8583.handlers.SSLHandler;
import com.imohsenb.ISO8583.interfaces.ISOClient;
import com.imohsenb.ISO8583.interfaces.ISOClientEventListener;
import com.imohsenb.ISO8583.interfaces.SSLProtocol;
import com.imohsenb.ISO8583.interfaces.SocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Mohsen Beiranvand
 */
public class ISOClientBuilder {


    private static ClientBuilder clientBuilder;

    public static ClientBuilder createSocket(String host, int port) {
        clientBuilder = new ClientBuilder(host, port);
        return clientBuilder;
    }

    /**
     * ClientBuilder
     */
    public static class ClientBuilder {

        private DefaultISOClient client;
        /**
         * Create ISO Client after initializing
         * @param host socket Host
         * @param port socket ip
         */
        public ClientBuilder(String host, int port) {
            client = new DefaultISOClient();
            client.setSocketAddress(host,port);
        }

        /**
         * Sending with NIO (false) or Blocking IO (true)
         * @param blocking:true
         * @return {@link ClientBuilder}
         */
        public ClientBuilder configureBlocking(boolean blocking) {
            client.setBlocking(blocking);
            return this;
        }

        /**
         * Enable sending over SSL/TLS
         * @return {@link ClientBuilder}
         */
        public SSLProtocol enableSSL() {
            return client.enableSSL(new SSLHandler(this));
        }

        /**
         * Build ISOClient for sending label
         * @return {@link ClientBuilder}
         */
        public ISOClient build() {
            return client;
        }

        /**
         * set Timeout for read from socket
         * @param millisecond timeout in millisecond
         * @return {@link ClientBuilder}
         */
        public ClientBuilder setReadTimeout(int millisecond) {
            client.setReadTimeout(millisecond);
            return this;
        }

        /**
         * Set Message length in Byte
         * @param bytes default: 2 byte
         * @return {@link ClientBuilder}
         */
        public ClientBuilder length(int bytes) {
            client.setLength(bytes);
            return this;
        }

        /**
         * Set event listener for dispatch events
         * @param eventListener Implementation of {@link ISOClientEventListener}
         * @return {@link ClientBuilder}
         */
        public ClientBuilder setEventListener(ISOClientEventListener eventListener) {
            if(eventListener != null)
                client.setEventListener(eventListener);
            return this;
        }
    }

    private static class DefaultISOClient implements ISOClient {

        private SSLHandler sslHandler = null;
        private SocketHandler socketHandler;
        private ByteBuffer buffer;
        private boolean blocking = true;
        private volatile boolean connected = false;
        private String host;
        private int port;
        private int readTimeout = 10000;
        private int length = 2;

        private final Object lock = new Object();
        private ISOClientEventListener isoClientEventListener;

        DefaultISOClient()
        {
            if(this.blocking) {
                socketHandler = new IOSocketHandler();
            }else{
                socketHandler = new NIOSocketHandler();
            }

            isoClientEventListener = new EmptyISOClientEventListener();
        }

        public void connect() throws ISOClientException, IOException {
            isoClientEventListener.connecting();

            if(sslHandler != null)
                socketHandler.init(host, port, isoClientEventListener, sslHandler);
            else socketHandler.init(host,port, isoClientEventListener);

            socketHandler.setReadTimeout(this.readTimeout);
            this.connected = true;

            isoClientEventListener.connected();
        }

        public void disconnect() {
            if(socketHandler!=null)
                socketHandler.close();
            if(buffer!=null) {
                buffer.flip();
                buffer.put(ByteBuffer.allocate(buffer.limit()));
                buffer = null;
            }
            connected = false;

            isoClientEventListener.disconnected();

        }

        private ByteBuffer initBuffer(ISOMessage isoMessage) {
            int len = isoMessage.getBody().length + isoMessage.getHeader().length;

            buffer = ByteBuffer.allocate(len + length);

            if(length > 0)
            {
                byte[] mlen = ByteBuffer.allocate(4).putInt(len).array();
                buffer.put(Arrays.copyOfRange(mlen, 2,4));
            }

            buffer.put(isoMessage.getHeader())
                    .put(isoMessage.getBody());

            return buffer;
        }

        public byte[] sendMessageSync(ISOMessage isoMessage) throws ISOClientException, IOException {

            byte[] result = new byte[0];

            synchronized (lock) {
                if (!isConnected())
                    throw new ISOClientException("Client does not connected to a server!");

                ByteBuffer buffer = initBuffer(isoMessage);


                result = socketHandler.sendMessageSync(buffer, length);
            }

            return result;
        }

        @Override
        public boolean isConnected() {
            return socketHandler != null && socketHandler.isConnected();
        }

        @Override
        public boolean isClosed() {
            return socketHandler != null && socketHandler.isClosed();
        }

        @Override
        public void setEventListener(ISOClientEventListener isoClientEventListener) {
            this.isoClientEventListener = isoClientEventListener;
        }

        private void setSocketAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }

        private SSLHandler enableSSL(SSLHandler sslHandler) {
            this.sslHandler = sslHandler;
            return sslHandler;
        }

        private void setBlocking(boolean blocking) {
            this.blocking = blocking;
        }


        private void setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        private void setLength(int length) {
            this.length = length;
        }

        private int getLength() {
            return length;
        }

    }

    private static class EmptyISOClientEventListener implements ISOClientEventListener {
        @Override
        public void connecting() {

        }

        @Override
        public void connected() {

        }

        @Override
        public void connectionFailed() {

        }

        @Override
        public void connectionClosed() {

        }

        @Override
        public void disconnected() {

        }

        @Override
        public void beforeSendingMessage() {

        }

        @Override
        public void afterSendingMessage() {

        }

        @Override
        public void onReceiveData() {

        }

        @Override
        public void beforeReceiveResponse() {

        }

        @Override
        public void afterReceiveResponse() {

        }
    }


}

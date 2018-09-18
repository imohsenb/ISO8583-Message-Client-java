package com.imohsenb.ISO8583.handlers;

import com.imohsenb.ISO8583.exceptions.ISOClientException;
import com.imohsenb.ISO8583.interfaces.ISOClientEventListener;
import com.imohsenb.ISO8583.interfaces.SocketHandler;
import com.imohsenb.ISO8583.utils.StringUtil;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author Mohsen Beiranvand
 */
public class NIOSocketHandler implements SocketHandler {

    private SocketChannel socketChannel;
    private ByteBuffer myAppData;
    private ByteBuffer myNetData;
    private ByteBuffer peerAppData;
    private ByteBuffer peerNetData;
    private SSLEngine engine;
    private SSLHandler sslHandler;

    public void init(String host, int port, ISOClientEventListener isoClientEventListener, SSLHandler sslHandler) throws ISOClientException {

        try {
            this.sslHandler = sslHandler;
            engine = sslHandler.getContext().createSSLEngine(host, port);
            engine.setUseClientMode(true);
            engine.setNeedClientAuth(false);


            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));


            while (!socketChannel.finishConnect()) {
            }

            // Create byte buffers to use for holding application and encoded data
            SSLSession session = engine.getSession();
            myAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
            myNetData = ByteBuffer.allocate(session.getPacketBufferSize());
            peerAppData = ByteBuffer.allocate(session.getApplicationBufferSize());
            peerNetData = ByteBuffer.allocate(session.getPacketBufferSize());

            boolean connected = sslHandler.doHandshake(socketChannel, engine, myNetData, peerNetData,peerAppData,myAppData);

            if(!connected)
                throw new ISOClientException("Handshake not performed well");

            postInit();

        } catch (Exception e) {
            throw new ISOClientException(e);
        }

    }

    public void init(String host, int port, ISOClientEventListener isoClientEventListener) throws IOException {

        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(host, port));


        while (!socketChannel.finishConnect()) {
        }

        myAppData = ByteBuffer.allocate(1024);

        postInit();
    }

    private void postInit() throws SocketException {
        socketChannel.socket().setSoTimeout(10000);
    }

    public byte[] sendMessageSync(ByteBuffer buffer, int length) throws IOException {

        if(sslHandler != null)
        {
            byte[] data = sendMessageSyncOverSsl(buffer);
            return Arrays.copyOfRange(data,(length>0)?(length):(0),data.length);
        }
        else{

            myAppData.clear();
            myAppData.put(buffer.array());
            myAppData.flip();

            while(myAppData.hasRemaining())
            {
                socketChannel.write(myAppData);
            }

            myAppData.clear();
            myAppData.compact();
            myAppData.flip();



            int r;
            do{
                r = socketChannel.read(myAppData);
            }
            while (myAppData.remaining() >=0 && r == 0);


            if(myAppData.position() > length)
                return Arrays.copyOfRange(myAppData.array(),(length>0)?(length):(0),myAppData.position());

            return new byte[0];
        }
    }

    private byte[] sendMessageSyncOverSsl(ByteBuffer buffer) throws IOException {

        write(buffer);
        return read();

    }

    private void write(ByteBuffer buffer) throws IOException {

        myAppData.clear();
        myAppData.put(buffer.array());
        myAppData.flip();

        while (myAppData.hasRemaining()) {
            // The loop has a meaning for (outgoing) messages larger than 16KB.
            // Every wrap call will remove 16KB from the original label and send it to the remote peer.
            myNetData.clear();
            SSLEngineResult result = engine.wrap(myAppData, myNetData);
            switch (result.getStatus()) {
                case OK:
                    myNetData.flip();
                    while (myNetData.hasRemaining()) {
                        socketChannel.write(myNetData);
                    }
                    System.out.println("Message sent to the server: " + StringUtil.fromByteArray(myAppData.array()));
                    break;
                case BUFFER_OVERFLOW:
                    myNetData = sslHandler.enlargePacketBuffer(engine, myNetData);
                    break;
                case BUFFER_UNDERFLOW:
                    throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
                case CLOSED:
                    close();
                    return;
                default:
                    throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
            }
        }
    }

    private byte[] read() throws IOException {

        byte[] response = new byte[0];


        peerNetData.clear();
        int waitToReadMillis = 50;
        boolean exitReadLoop = false;
        while (!exitReadLoop) {
            int bytesRead = socketChannel.read(peerNetData);
            if (bytesRead > 0) {
                peerNetData.flip();
                while (peerNetData.hasRemaining()) {
                    peerAppData.clear();
                    SSLEngineResult result = engine.unwrap(peerNetData, peerAppData);
                    switch (result.getStatus()) {
                        case OK:
                            peerAppData.flip();
                            byte[] resp = new byte[peerAppData.limit()];
                            for (int i = 0; i < resp.length; i++) {
                                resp[i] = peerAppData.get(i);
                            }
                            response = resp;
                            exitReadLoop = true;
                            break;
                        case BUFFER_OVERFLOW:
                            peerAppData = sslHandler.enlargeApplicationBuffer(engine, peerAppData);
                            break;
                        case BUFFER_UNDERFLOW:
                            peerNetData = sslHandler.handleBufferUnderflow(engine, peerNetData);
                            break;
                        case CLOSED:
                            close();
                            return response;
                        default:
                            throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
                    }
                }
            } else if (bytesRead < 0) {
                engine.closeInbound();
                return response;
            }
            try {
                Thread.sleep(waitToReadMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    public void close() {
        try {
            if(socketChannel!=null)
                socketChannel.close();
            if(myAppData!=null) {
                myAppData.compact();
                myAppData.clear();
                myAppData = null;
            }
            if(myNetData!=null) {
                myNetData.compact();
                myNetData.clear();
                myNetData = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setReadTimeout(int readTimeout) throws SocketException {
        socketChannel.socket().setSoTimeout(10000);
    }

    @Override
    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

    @Override
    public boolean isClosed() {
        return socketChannel == null || socketChannel.socket().isClosed();
    }

}
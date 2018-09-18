package com.imohsenb.ISO8583.handlers;

import com.imohsenb.ISO8583.exceptions.ISOClientException;
import com.imohsenb.ISO8583.interfaces.ISOClientEventListener;
import com.imohsenb.ISO8583.interfaces.SocketHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Mohsen Beiranvand
 */
public class IOSocketHandler implements SocketHandler {
    private Socket socket;
    private BufferedOutputStream socketWriter;
    private BufferedInputStream socketReader;
    private ISOClientEventListener isoClientEventListener;

    public void init(String host, int port, ISOClientEventListener isoClientEventListener, SSLHandler sslHandler) throws ISOClientException {

        this.isoClientEventListener = isoClientEventListener;

        SSLContext context = null;
        try {

            context = sslHandler.getContext();
            SSLSocketFactory sslsocketfactory = context.getSocketFactory();
            SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(
                    host, port);
            socket.setNeedClientAuth(false);

            socket.startHandshake();

            this.socket = socket;

            postInit();

        } catch (Exception e) {
            throw new ISOClientException(e);
        }

    }

    public void init(String host, int port, ISOClientEventListener isoClientEventListener) throws IOException {

        this.isoClientEventListener = isoClientEventListener;
        this.socket = new Socket(host, port);
        postInit();

    }


    private void postInit() throws IOException {
        socketWriter = new BufferedOutputStream(socket.getOutputStream());
    }

    public byte[] sendMessageSync(ByteBuffer buffer, int length) throws IOException, ISOClientException {

        isoClientEventListener.beforeSendingMessage();

        for (byte v :
                buffer.array()) {
            socketWriter.write(v);
        }

        socketWriter.flush();

        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        socketReader = new BufferedInputStream(socket.getInputStream());

        isoClientEventListener.afterSendingMessage();


        isoClientEventListener.beforeReceiveResponse();

        try {

            if(length > 0)
            {
                byte[] bLen = new byte[length];
                socketReader.read(bLen,0,length);
                int mLen = (bLen[0] & 0xff) + (bLen[1] & 0xff);
            }

            int r;
            int fo = 512;
            do{
                r = socketReader.read();
                if (!(r == -1 && socketReader.available() == 0)) {
                    readBuffer.put((byte) r);
                } else {
                    fo--;
                }
            }while (
                    ((r > -1 && socketReader.available() > 0) ||
                            (r == -1 && readBuffer.position() <= 1)) &&
                            fo > 0

                    );


            byte[] resp = Arrays.copyOfRange(readBuffer.array(),0,readBuffer.position());

            isoClientEventListener.afterReceiveResponse();

            return resp;

        } catch (SocketTimeoutException e) {
            throw new ISOClientException("Read Timeout");
        } finally {
            readBuffer.clear();
            readBuffer.compact();
            readBuffer = null;
        }
    }

    public synchronized void close() {
        try {
            if(socketWriter!=null)
                socketWriter.close();
            if(socketReader!=null)
                socketReader.close();
            if(socket!=null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setReadTimeout(int readTimeout) throws SocketException {
        socket.setSoTimeout(readTimeout);
    }

    @Override
    public boolean isConnected() {
        if(socket != null)
            return socket.isConnected();
        return false;
    }

    @Override
    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }
}
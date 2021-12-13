package com.production.vsn;

import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Connection extends Thread {

    private final SecureService secureService;
    private final String serverAddress;
    private final int port;
    private SocketChannel socketChannel;
    private ParcelFileDescriptor tunnel;
    private FileInputStream fromAndroid;
    private FileOutputStream toAndroid;
    private boolean running;

    public Connection(SecureService secureService, String serverAddress, int port) {
        this.secureService = secureService;
        this.serverAddress = serverAddress;
        this.port = port;
        running = true;
    }

    public void protect() {
        try {
            socketChannel = SocketChannel.open();
            secureService.protect(socketChannel.socket());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void build(ParcelFileDescriptor tunnel) {
        this.tunnel = tunnel;
        fromAndroid = new FileInputStream(tunnel.getFileDescriptor());
        toAndroid = new FileOutputStream(tunnel.getFileDescriptor());
    }

    public void disconnect() {
        try {
            socketChannel.close();
            tunnel.close();
            fromAndroid.close();
            toAndroid.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            socketChannel.connect(new InetSocketAddress(InetAddress.getByName(serverAddress), port));
            socketChannel.configureBlocking(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(Short.MAX_VALUE);
        int length;

        while (running) {
            try {
                length = fromAndroid.read(byteBuffer.array());
                byteBuffer.limit(length);
                socketChannel.write(byteBuffer);
                byteBuffer.clear();

                length = socketChannel.read(byteBuffer);
                if (length > 1) {
                    toAndroid.write(byteBuffer.array(), 0, length);
                }
                byteBuffer.clear();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection() {
        running = false;
    }
}

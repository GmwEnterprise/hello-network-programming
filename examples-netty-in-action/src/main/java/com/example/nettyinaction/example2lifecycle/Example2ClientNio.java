package com.example.nettyinaction.example2lifecycle;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Example2ClientNio {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress(3000));
            socketChannel.write(ByteBuffer.wrap("Hello world !".getBytes()));

            Thread.sleep(1000 * 30);
        }
    }
}

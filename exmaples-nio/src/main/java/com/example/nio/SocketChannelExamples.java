package com.example.nio;

import com.example.nio.common.Pack;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelExamples {

    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 3000));

        while (!sc.finishConnect()) ;
        for (String arg : args) {
            Pack pack = new Pack(arg);
            for (ByteBuffer buf : pack.getPack()) {
                sc.write(buf);
            }
        }
        sc.close();
    }
}

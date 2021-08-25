package com.example.nio;

import com.example.nio.common.Pack;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelExamples {

    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(true);
        sc.connect(new InetSocketAddress("localhost", 3000));

        for (String arg : args) {
            Pack pack = new Pack(arg);
            ByteBuffer[] byteBuffers = pack.packageToByteBufferArray();
            for (ByteBuffer buf : byteBuffers) {
                sc.write(buf);
            }
        }
        sc.close();
    }
}

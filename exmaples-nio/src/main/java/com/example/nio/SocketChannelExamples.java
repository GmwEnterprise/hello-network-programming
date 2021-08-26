package com.example.nio;

import com.example.Pack;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class SocketChannelExamples {

    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(true);
        // sc.setOption(SocketOptions.SO_KEEPALIVE);
        sc.connect(new InetSocketAddress("localhost", 3000));

        for (String arg : args) {
            Pack pack = new Pack(arg);
            ByteBuffer[] byteBuffers = pack.packageToByteBufferArray();
            for (ByteBuffer buf : byteBuffers) {
                sc.write(buf);
            }
        }
        Thread.sleep(3000);

        int read;
        ByteBuffer buffer = ByteBuffer.allocate(16);
        while ((read = sc.read(buffer)) > 0) {
            byte[] bytes = new byte[read];
            buffer.flip();
            buffer.get(bytes);
            buffer.compact();
            System.out.println("RETURN: " + Arrays.toString(bytes));
        }

        sc.close();
    }
}

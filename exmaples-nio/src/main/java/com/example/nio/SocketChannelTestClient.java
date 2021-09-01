package com.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class SocketChannelTestClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel sc = SocketChannel.open();
        int a = 0;
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress(3000));
        while (!sc.finishConnect())
            a++;
        System.out.println(a);

        int i = 0;
        while (i++ < 5) {
            sc.write(ByteBuffer.wrap(String.format("%d times.", i).getBytes()));
            TimeUnit.SECONDS.sleep(1L);
        }
        sc.close();
    }
}

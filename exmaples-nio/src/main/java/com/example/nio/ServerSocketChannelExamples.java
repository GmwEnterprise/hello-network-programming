package com.example.nio;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class ServerSocketChannelExamples {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress("localhost", 3000));

        while (true) {
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ByteBuffer buff = ByteBuffer.allocate(8);
                while (sc.read(buff) != -1) {
                    buff.flip();
                    int offset = buff.position();
                    int limit = buff.limit();
                    int length = limit - offset;
                    output.write(buff.array(), offset, length);
                    buff.position(limit);
                    buff.compact();
                }
                String content = output.toString();
                if ("EXIT".equals(content)) {
                    System.out.println("EXIT !");
                    break;
                } else {
                    System.out.println("RECEIVE: " + content);
                }
            }
            TimeUnit.SECONDS.sleep(1L);
            System.out.println("waiting ...");
        }
        ssc.close();
    }
}

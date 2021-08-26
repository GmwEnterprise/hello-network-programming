package com.example.nio;

import com.example.Pack;
import com.example.PackReader;

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

        PackReader reader = new PackReader();
        int exit = 0;
        while (true) {
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                ByteBuffer buf = ByteBuffer.allocate(8);
                while (sc.read(buf) != -1) {
                    buf.flip();
                    reader.readBuffer(buf);
                    buf.compact();
                    if (reader.packPrepared() != null) {
                        int command = dealContent(reader.packPrepared());
                        reader = new PackReader();
                        if (command == 0) {
                            exit = 1;
                            break;
                        }
                    }
                }
            }
            if (exit == 1) {
                System.out.println("EXIT !");
                break;
            }
            TimeUnit.SECONDS.sleep(1L);
            System.out.println("waiting ...");
        }
        ssc.close();
    }

    private static int dealContent(Pack pack) {
        String content = pack.getContent();
        if ("EXIT".equals(content)) {
            return 0;
        }
        System.out.println("Receive content: " + content);
        return 1;
    }
}

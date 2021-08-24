package com.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class SelectorExamples {

    public static void main(String[] args) throws IOException {
        try (
                Selector selector = Selector.open();
                ServerSocketChannel ssc = ServerSocketChannel.open()
        ) {
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(3000));
            ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (selector.select() > 0) {
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isAcceptable()) {
                        System.out.println("建立连接");
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buf = ByteBuffer.allocate(256);
                        while (channel.read(buf) != -1) {
                            buf.flip();
                            int readLen = buf.limit() - buf.position();
                            byte[] readBytes = new byte[readLen];
                            System.arraycopy(buf.array(), buf.position(), readBytes, 0, readLen);
                            buf.position(buf.limit());
                            System.out.println("Receive: " + Arrays.toString(readBytes));
                            buf.compact();
                        }
                        channel.close();
                    }

                    keyIterator.remove();
                }
            }
        }
    }
}

package com.example.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketChannelTestServer {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(3000));
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buf = ByteBuffer.allocate(512);
        long i = 0L;
        while (selector.select() > 0) {
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    i++;
                    SocketChannel sc = (SocketChannel) key.channel();
                    while (sc.read(buf) > 0) {
                        buf.flip();
                        byte[] dst = new byte[buf.remaining()];
                        buf.get(dst);
                        buf.clear();
                        System.out.println("[" + i + "]" + new String(dst));
                    }
                }

                keyIterator.remove();
            }
        }
    }
}

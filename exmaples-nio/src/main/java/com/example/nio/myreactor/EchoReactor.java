package com.example.nio.myreactor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

public class EchoReactor implements ChannelHandler {

    public static void main(String[] args) throws IOException {
        new EchoReactor(3000).handle();
    }

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public EchoReactor(int port) throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(port));

        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT).attach((ChannelHandler) () -> {
            final SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            final SelectionKey key = sc.register(selector, 0);
            key.attach(new EchoHandler(sc, key));
        });
    }

    @Override
    public void handle() throws IOException {
        while (selector.select() > 0) {
            final Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                final SelectionKey key = keyIterator.next();

                // 取出对应的channelHandler执行处理
                ((ChannelHandler) key.attachment()).handle();
                keyIterator.remove();
            }
        }
    }

    private static class EchoHandler implements ChannelHandler {
        private final SocketChannel sc;
        private final SelectionKey scKey;
        private byte[] content;

        private static final int READ = 1, WRITE = 2;
        private int state = READ;

        public EchoHandler(SocketChannel sc, SelectionKey scKey) {
            this.sc = sc;
            this.scKey = scKey;
        }

        @Override
        public void handle() throws IOException {
            switch (state) {
                case READ: {
                    int len;
                    ByteBuffer buffer = ByteBuffer.allocate(64);
                    final ByteArrayOutputStream output = new ByteArrayOutputStream();
                    while ((len = sc.read(buffer)) != -1) {
                        buffer.flip();
                        output.write(buffer.array(), 0, len);
                        buffer.clear();
                    }
                    content = output.toByteArray();
                    System.out.println("Read value = " + Arrays.toString(content));

                    scKey.interestOps(SelectionKey.OP_WRITE);
                    state = WRITE;
                    break;
                }
                case WRITE: {
                    ByteBuffer buffer = ByteBuffer.wrap(content);
                    sc.write(buffer);
                    System.out.println("Write ...");

                    scKey.interestOps(SelectionKey.OP_READ);
                    state = READ;
                    break;
                }
            }
        }
    }
}

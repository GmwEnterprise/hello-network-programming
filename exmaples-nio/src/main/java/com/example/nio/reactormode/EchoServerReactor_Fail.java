package com.example.nio.reactormode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

class EchoServerReactor_Fail implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel ssc;

    EchoServerReactor_Fail() throws IOException {
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(3000));

        selector = Selector.open();
        final SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
        acceptKey.attach(new AcceptHandler());

        System.out.println("初始化EchoServerReactor");
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("选择器执行选择: select num = " + selector.select());
                final Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    System.out.println("选择器状态: acceptable = " + key.isAcceptable() + ", readable = " + key.isReadable() + ", writable = " + key.isWritable());
                    ((Runnable) key.attachment()).run();
                }
                keys.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AcceptHandler implements Runnable {

        @Override
        public void run() {
            try {
                final SocketChannel sc = ssc.accept();
                if (sc != null) {
                    System.out.println("新的接收");
                    new EchoHandler(selector, sc).run();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class EchoHandler implements Runnable {
        private final SocketChannel channel;
        private final SelectionKey channelKey;
        private final ByteBuffer buf = ByteBuffer.allocate(256);

        private static final int RECEIVE = 0, SENDING = 1;
        private int state = RECEIVE;

        public EchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
            channel = socketChannel;
            channel.configureBlocking(false);

            // 获取key，不设置任何事件
            channelKey = channel.register(selector, 0);
            channelKey.attach(this);
            channelKey.interestOps(SelectionKey.OP_READ);
            selector.wakeup();
        }

        @Override
        public void run() {
            System.out.println("EchoHandler执行run");
            try {
                if (channel.isConnected()) {
                    if (state == SENDING) {
                        channel.write(buf);
                        buf.clear();
                        channelKey.interestOps(SelectionKey.OP_WRITE);
                        state = RECEIVE;
                    } else {
                        while (channel.read(buf) != -1) {
                            buf.flip();
                            System.out.println(new String(buf.array(), buf.position(), buf.limit() - buf.position()));
                            buf.clear();
                        }
                        buf.flip();
                        channelKey.interestOps(SelectionKey.OP_READ);
                        state = SENDING;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new EchoServerReactor_Fail().run();
    }
}

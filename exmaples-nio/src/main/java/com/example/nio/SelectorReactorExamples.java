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

public class SelectorReactorExamples {

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
             ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(3000));

            // 传入了一个attachment，与下面的使用方法是一样的：
            //   SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);
            //   key.attach((SelectableChannelHandler) () -> {...});
            ssc.register(selector, SelectionKey.OP_ACCEPT, (SelectableChannelHandler) () -> {
                // accept事件处理器，接收一个新的连接，并将其注册到selector

                System.out.println("Accept!");
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                ByteBuffer buf = ByteBuffer.allocate(256); // 一个sc对应一个buffer
                sc.register(selector, SelectionKey.OP_READ, (SelectableChannelHandler) () -> {
                    // read事件处理器，打印出接收到的字节数据，然后关闭socketChannel
                    // 如果不关闭会一直触发该事件

                    System.out.println(">>> ");
                    int read;
                    while ((read = sc.read(buf)) != -1) {
                        // 在一次读取结束之前可能会出现连续两次进入循环而且read == 0的情况
                        if (read > 0) {
                            buf.flip(); // for next read
                            byte[] content = new byte[read];
                            buf.get(content);
                            buf.clear(); // for next write
                            System.out.println("Receive bytes: " + Arrays.toString(content));
                        }
                    }
                    sc.close();
                });
            });

            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectableChannelHandler handler = (SelectableChannelHandler) keyIterator.next().attachment();
                    handler.handle();
                    keyIterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 事件处理器接口
    // 本来可以用Runnable的，但是需要手动处理try catch太麻烦了
    private interface SelectableChannelHandler {
        void handle() throws IOException;
    }
}

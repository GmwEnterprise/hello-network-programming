package com.example.nio;

import com.example.nio.common.Log;

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
            ssc.register(selector, SelectionKey.OP_ACCEPT, (SelectableChannelHandler) (sscKey) -> {
                // accept事件处理器，接收一个新的连接，并将其注册到selector

                Log.info("Accept!");
                SocketChannel sc = ((ServerSocketChannel) sscKey.channel()).accept();
                sc.configureBlocking(false);
                ByteBuffer buf = ByteBuffer.allocate(256); // 一个sc对应一个buffer
                sc.register(selector, SelectionKey.OP_READ, (SelectableChannelHandler) (scKey) -> {
                    // read事件处理器，打印出接收到的字节数据，然后关闭socketChannel
                    // OP_READ触发条件三选一：1）通道准备好读取；2）已到达流的末尾；3）远程已关闭
                    // OP_WRITE触发是即时的，随时都会被触发，最好通过阻塞队列的方式、或者即时注销WRITE事件

                    Log.info(">>> ");
                    SocketChannel currentSc = (SocketChannel) scKey.channel();

                    if ((scKey.readyOps() & SelectionKey.OP_READ) != 0) {
                        // read事件
                        int read;
                        while ((read = currentSc.read(buf)) != -1) {
                            // 在一次读取结束之前可能会出现连续两次进入循环而且read == 0的情况
                            if (read > 0) {
                                buf.flip(); // for next read
                                byte[] content = new byte[read];
                                buf.get(content);
                                buf.clear(); // for next write
                                Log.info("Receive bytes: " + Arrays.toString(content));
                            }
                        }
                        Log.info("read = -1");
                        sc.close();
                    }
                });
            });

            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    SelectableChannelHandler handler = (SelectableChannelHandler) key.attachment();
                    handler.handle(key);
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
        void handle(SelectionKey currentKey) throws IOException;
    }
}

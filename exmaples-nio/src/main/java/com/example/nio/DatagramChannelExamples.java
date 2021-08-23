package com.example.nio;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class DatagramChannelExamples {

    public static void example(int port) throws Exception {
        DatagramChannel dc = DatagramChannel.open();
        dc.socket().bind(new InetSocketAddress(port));

        // 阻塞式的datagramChannel使用很简单，这里就不写了
    }
}

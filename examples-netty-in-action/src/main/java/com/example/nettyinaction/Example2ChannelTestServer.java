package com.example.nettyinaction;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class Example2ChannelTestServer {

    private static class NettyServer {
        private final int port;
        private final EventLoopGroup executors;
        private Channel channel;

        public NettyServer(int port) {
            this.port = port;
            this.executors = new NioEventLoopGroup();
        }

        public void start() {
            ServerBootstrap boot = new ServerBootstrap();
            ChannelFuture bindFuture = boot
                    .localAddress(new InetSocketAddress(port))
                    .channel(NioServerSocketChannel.class)
                    .group(executors)
                    .childHandler(new ChannelInitializer<ServerSocketChannel>() {

                        @Override
                        protected void initChannel(ServerSocketChannel ch) throws Exception {

                        }
                    })
                    .bind()
                    .syncUninterruptibly();
        }
    }
}

package com.example.nettyinaction.example2lifecycle;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.time.LocalDateTime;

public class Example2Lifecycle {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup exec = new NioEventLoopGroup();
        try {
            ServerBootstrap boot = new ServerBootstrap()
                    .group(exec)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(3000)
                    .childHandler(
                            // new ChannelInitializer<SocketChannel>() {
                            //     @Override
                            //     protected void initChannel(SocketChannel ch) throws Exception {
                            //         ch.pipeline().addLast(new Example2InboundHandler1());
                            //         ch.pipeline().addLast(new Example2InboundHandler2());
                            //     }
                            // }
                            new ChannelOutboundHandlerAdapter()
                    );
            System.out.println(LocalDateTime.now() + " >> waiting for bind...");
            ChannelFuture f = boot.bind().sync();
            System.out.println(LocalDateTime.now() + " >> bind successfully");
            f.channel().closeFuture().sync();
            System.out.println(LocalDateTime.now() + " >> close future");
        } finally {
            exec.shutdownGracefully();
            System.out.println(LocalDateTime.now() + " >> exec shutdown");
        }
    }
}

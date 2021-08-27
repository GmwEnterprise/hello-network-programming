package com.example.nettyinaction;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class Example1EchoServerDemo {

    public static void main(String[] args) throws Exception {
        new EchoServer(3000).start();
    }

    private static class EchoServer {
        private final int port;

        private EchoServer(int port) {this.port = port;}

        public void start() throws Exception {
            EchoServiceHandler serviceHandler = new EchoServiceHandler();
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                ServerBootstrap boot = new ServerBootstrap()
                        .group(group)
                        .channel(NioServerSocketChannel.class)
                        .localAddress(new InetSocketAddress(port))
                        .childHandler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(serviceHandler);
                            }
                        });
                ChannelFuture f = boot.bind().sync();
                f.channel().closeFuture().sync();
            } finally {
                group.shutdownGracefully();
            }
        }
    }

    @ChannelHandler.Sharable
    private static class EchoServiceHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            System.out.printf("Server received: %s%n", buf.toString(CharsetUtil.UTF_8));
            ctx.write(buf);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}

package com.example.netty;

import com.example.common.Log;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class NettyDiscardServer {

    public static void main(String[] args) {
        new NettyDiscardServer(3000).runServer();
    }

    private final int serverPort;
    private final ServerBootstrap boot = new ServerBootstrap();

    public NettyDiscardServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void runServer() {
        NioEventLoopGroup exec = new NioEventLoopGroup();
        try {
            boot.group(exec);
            boot.channel(NioServerSocketChannel.class);
            boot.localAddress(serverPort);
            boot.option(ChannelOption.SO_KEEPALIVE, true);
            boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            boot.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new NettyDiscardHandler());
                }
            });
            ChannelFuture channelFuture = boot.bind().sync();
            Log.info("服务器启动成功，监听端口[" + channelFuture.channel().localAddress() + "]");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            exec.shutdownGracefully();
        }
    }

    private static class NettyDiscardHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            try {
                Log.info("收到消息，丢弃如下：");
                while (buf.isReadable()) {
                    System.out.print((char) buf.readByte());
                }
                System.out.println();
            } finally {
                ReferenceCountUtil.release(buf);
            }
        }
    }
}

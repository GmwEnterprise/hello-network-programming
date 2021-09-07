package com.example.nettyinaction.example2lifecycle;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.LocalDateTime;

@ChannelHandler.Sharable
public class Example2InboundHandler2 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(LocalDateTime.now() + " >> inbound 2 channel registered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(LocalDateTime.now() + " >> inbound 2 channel active");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(LocalDateTime.now() + " >> inbound 2 channel read");
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(LocalDateTime.now() + " >> inbound 2 channel read complete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(LocalDateTime.now() + " >> inbound 2 channel inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(LocalDateTime.now() + " >> inbound 2 channel unregistered");
        super.channelUnregistered(ctx);
    }
}

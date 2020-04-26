/*
 * Copyright © 文科中的技术宅
 * blog:https://www.townwang.com
 */
package com.townwang.netty;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Town
 * @created at 2018/7/11 18:22
 * @Last Modified by: Town
 * @Last Email: android@townwang.com
 * @Last Modified time: 2018/7/11 18:22
 * @Remarks 配置一个新的Channel
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

        private NettyListener listener;

        private int WRITE_WAIT_SECONDS = 10;

        private int READ_WAIT_SECONDS = 13;

        public NettyClientInitializer(NettyListener listener) {
            this.listener = listener;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new LoggingHandler(LogLevel.ERROR));    // 开启日志，可以设置日志等级
            pipeline.addLast(new DelimiterBasedFrameDecoder(1536, true, false, Unpooled.wrappedBuffer(NeetyConfig.endCheck.getBytes())));
//        pipeline.addLast(new IdleStateHandler(30, 60, 100));
            pipeline.addLast(new NettyClientHandler(listener));
        }
}

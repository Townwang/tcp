package com.townwang.tcp.tcp

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPipeline
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler

class NettyClientInitializer(private val listener: NettyListener) : ChannelInitializer<SocketChannel>() {

    private val WRITE_WAIT_SECONDS = 10

    private val READ_WAIT_SECONDS = 13

    @Throws(Exception::class)
    override fun initChannel(ch: SocketChannel) {
        val pipeline = ch.pipeline()
        pipeline.addLast(LoggingHandler(LogLevel.DEBUG))    // 开启日志，可以设置日志等级
        pipeline.addLast(DelimiterBasedFrameDecoder(1536, true, false, Unpooled.wrappedBuffer("##".toByteArray())))
        pipeline.addLast(NettyClientHandler(listener))
    }
}

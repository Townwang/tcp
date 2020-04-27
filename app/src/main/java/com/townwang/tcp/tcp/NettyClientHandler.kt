package com.townwang.tcp.tcp
import android.util.Log
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class NettyClientHandler(

    /**
     * 回调接口
     */
    private val listener: NettyListener
) : SimpleChannelInboundHandler<ByteBuf>() {
    private val  TAG = "TCP-netty"

    override fun channelActive(ctx: ChannelHandlerContext) {
        try {
            MyNettyClitent.instance.connectStatus = true
            listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_SUCCESS.toInt())
        } catch (e: Exception) {
            e.message
        }

    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        try {
            MyNettyClitent.instance.connectStatus = false
            listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_CLOSED.toInt())
            MyNettyClitent.instance.reconnect()
        } catch (e: Exception) {
            e.message
        }

    }

    override fun channelRead0(ctx: ChannelHandlerContext, byteBuf: ByteBuf) {
        try {
            val req = ByteArray(byteBuf.readableBytes())
            byteBuf.readBytes(req)
            val body = String(req)
            if (body.contains("HEARTBEAT")) {
                Log.d(TAG,"Netty TCP 收到心跳包 ---> 【$body】 ")
            } else {
                Log.d(TAG,"Netty TCP 收到消息 ---> 【$body】 ")
                listener.onMessageResponse(body)
            }
        } catch (e: Exception) {
            e.message
        }

    }

}

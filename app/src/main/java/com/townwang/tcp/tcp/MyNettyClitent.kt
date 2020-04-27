package com.townwang.tcp.tcp
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.townwang.tcp.App
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

class MyNettyClitent {

    private val  TAG = "TCP-netty"

    private var group: EventLoopGroup? = null

    private var listener: NettyListener? = null
    private var channel: Channel? = null


    private var ip:String = ""
    private var port:Int = 0

    var context:Context? = null

    /**
     * 得到TCP连接状态
     * @return true[连接] false[未连接]
     */
    /**
     * 手动设置TCP连接状态
     * @param status
     */
    var connectStatus = false

    private var reconnectNum = Integer.MAX_VALUE

    private var reconnectIntervalTime: Long = 5000

    /**
     * 连接服务器
     */
    @Synchronized
    fun connect(ip:String,port:Int): MyNettyClitent {
        this.ip = ip
        this.port = port
        if (!connectStatus) {
            group = NioEventLoopGroup()
            val bootstrap = Bootstrap().group(group!!)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel::class.java)
                .handler(listener?.let { NettyClientInitializer(it) })
            try {
                bootstrap.connect(ip, port).addListener(ChannelFutureListener { channelFuture ->
                        if (channelFuture.isSuccess) {
                            Log.d(TAG," Netty TCP --->连接成功")
                            connectStatus = true
                            listener!!.onStatusResponse(" Netty TCP --->连接成功")
                            channel = channelFuture.channel()
                        } else {
                            connectStatus = false
                        }
                    }).sync()

            } catch (e: Exception) {
                Log.e(TAG,e.message)
                listener!!.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR.toInt())
                reconnect()
            }

        }
        return this
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        group!!.shutdownGracefully()
    }

    /**
     * 断开重连机制
     */
    fun reconnect() {
        if (reconnectNum > 0 && !connectStatus) {
            reconnectNum--
            try {
                Thread.sleep(reconnectIntervalTime)
            } catch (e: InterruptedException) {
            }
            listener!!.onStatusResponse(" Netty TCP --->重新链接")
            Log.d(TAG,"Netty TCP --->重新连接")
            disconnect()
            connect(ip,port)
        } else {
            disconnect()
        }
    }

    /**
     * 发送消息
     * @param data 数据
     * @param listener 监听接口
     * @return 发送成功与否
     */
    fun sendMsgToServer(data: String, listener: ChannelFutureListener): Boolean {
        val flag = channel != null && connectStatus
        if (flag) {
            val buf = Unpooled.copiedBuffer(data.toByteArray())
            channel!!.writeAndFlush(buf).addListener(listener)
        }
        return flag
    }

    /**
     * 发送消息
     * @param msg
     */
    fun sendMsg(msg: String) {
            sendMsgToServer(msg, ChannelFutureListener { future ->
                if (future.isSuccess) {
                    Log.d(TAG,"Netty TCP --->消息发送成功 【$msg】 ")
                    listener!!.onStatusResponse(" 消息发送成功 【$msg】 ")
                } else {
                    listener!!.onStatusResponse(" 消息发送失败 【$msg】 ")
                }
            })

        print("发送完毕")
        }

    /**
     * 设置重连机制延时时间
     * @param reconnectIntervalTime
     */
    fun setReconnectIntervalTime(reconnectIntervalTime: Long) {
        this.reconnectIntervalTime = reconnectIntervalTime
    }

    /**
     * 设置回调接口
     * @param listener
     */
    fun setListener(listener: NettyListener) {
        this.listener = listener
    }




    companion object {
        @SuppressLint("StaticFieldLeak")
        val instance = MyNettyClitent()
    }
}

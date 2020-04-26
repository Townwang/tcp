/*
 * Copyright © 文科中的技术宅
 * blog:https://www.townwang.com
 */
package com.townwang.netty;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Town
 * @created at 2018/7/11 18:13
 * @Last Modified by: Town
 * @Last Email: android@townwang.com
 * @Last Modified time: 2018/7/11 18:13
 * @Remarks netty 客户端
 */
public class NeetyTown {
    /**
     * netty客户端对象
     */
    private static NeetyTown nettyClient = new NeetyTown();
    /**
     * 事件循环组
     */
    private EventLoopGroup group;
    /**
     * 通道
     */
    private Channel channel;
    /**
     * 最大重连次数
     */
    private int reconnectNum = Integer.MAX_VALUE;
    /**
     * 重连间隔时间
     */
    private long reconnectIntervalTime = 5000;
    /**
     * 当前链接状态
     */
    private boolean isConnect = false;
    /**
     * 监听接口
     */
    private NettyListener listener;


    public static NeetyTown getInstance() {
        return nettyClient;
    }
    /**
     * 连接服务器
     */
    public synchronized NeetyTown connect() {
        if (!isConnect) {
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap().group(group)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer(listener));
            try {
                bootstrap.connect(NeetyConfig.inetHost,NeetyConfig.inetPort).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            isConnect = true;
                            channel = channelFuture.channel();
                        } else {
                            isConnect = false;
                        }
                    }
                }).sync();

            } catch (Exception e) {
                listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
                reconnect();
            }
        }
        return this;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        group.shutdownGracefully();
    }

    /**
     * 断开重连机制
     */
    public void reconnect() {
        if (reconnectNum > 0 && !isConnect) {
            reconnectNum--;
            try {
                Thread.sleep(reconnectIntervalTime);
            } catch (InterruptedException e) {
            }
//            Logger.e("Netty TCP --->重新连接");
            disconnect();
            connect();
        } else {
            disconnect();
        }
    }

    /**
     * 发送消息
     * @param data 数据
     * @param listener 监听接口
     * @return 发送成功与否
     */
    protected boolean sendMsgToServer(String data, ChannelFutureListener listener) {
        boolean flag = channel != null && isConnect;
        if (flag) {
            ByteBuf buf = Unpooled.copiedBuffer(data.getBytes());
            channel.writeAndFlush(buf).addListener(listener);
        }
        return flag;
    }

    /**
     * 发送消息
     * @param msg
     */
    public  void sendMsg(final String msg){
            sendMsgToServer(msg,  new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
//                        Logger.d("Netty TCP --->消息发送成功"+" 【" + msg + "】 ");
                    } else {
//                        Logger.d("Netty TCP --->消息发送失败"+" 【" + msg + "】 ");
                    }
                }
            });
    }

    /**
     * 设置断开重连次数
     * @param reconnectNum
     */
    public void setReconnectNum(int reconnectNum) {
        this.reconnectNum = reconnectNum;
    }

    /**
     * 设置重连机制延时时间
     * @param reconnectIntervalTime
     */
    public void setReconnectIntervalTime(long reconnectIntervalTime) {
        this.reconnectIntervalTime = reconnectIntervalTime;
    }

    /**
     * 得到TCP连接状态
     * @return true[连接] false[未连接]
     */
    public boolean getConnectStatus() {
        return isConnect;
    }

    /**
     * 手动设置TCP连接状态
     * @param status
     */
    public void setConnectStatus(boolean status) {
        this.isConnect = status;
    }

    /**
     * 设置回调接口
     * @param listener
     */
    public void setListener(NettyListener listener) {
        this.listener = listener;
    }
}

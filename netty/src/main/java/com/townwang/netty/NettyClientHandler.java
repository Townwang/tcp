/*
 * Copyright © 文科中的技术宅
 * blog:https://www.townwang.com
 */
package com.townwang.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Town
 * @created at 2018/7/11 18:28
 * @Last Modified by: Town
 * @Last Email: android@townwang.com
 * @Last Modified time: 2018/7/11 18:28
 * @Remarks
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * 回调接口
     */
    private NettyListener listener;

    public NettyClientHandler(NettyListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            NeetyTown.getInstance().setConnectStatus(true);
            listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_SUCCESS);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            NeetyTown.getInstance().setConnectStatus(false);
            listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_CLOSED);
            NeetyTown.getInstance().reconnect();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
//        Logger.d("Netty TCP ---> 执行这里了吗"+byteBuf);
        try {
            byte[] req = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(req);
            String body = new String(req, "UTF-8");
            listener.onMessageResponse(body);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}

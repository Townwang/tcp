/*
 * Copyright © 文科中的技术宅
 * blog:https://www.townwang.com
 */
package com.townwang.netty;

/**
 * @author Town
 * @created at 2018/7/11 18:25
 * @Last Modified by: Town
 * @Last Email: android@townwang.com
 * @Last Modified time: 2018/7/11 18:25
 * @Remarks
 */
interface NettyListener {

    byte STATUS_CONNECT_SUCCESS = 1;//连接状态

    byte STATUS_CONNECT_CLOSED = 0;//关闭未连接

    byte STATUS_CONNECT_ERROR = 0;//出错未连接


//    /**
//     * 当接收到系统消息(ByteBuf类型)
//     */
//    void onMessageResponse(ByteBuf byteBuf);

    /**
     *当接收到系统消息(String类型)
     */
    void onMessageResponse(String byteBuf);

    /**
     * 当服务状态发生变化时触发
     */
    void onServiceStatusConnectChanged(int statusCode);
}


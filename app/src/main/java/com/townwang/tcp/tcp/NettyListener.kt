package com.townwang.tcp.tcp

interface NettyListener {


    /**
     * 当接收到系统消息(String类型)
     */
    fun onMessageResponse(byteBuf: String)

    fun onStatusResponse(str:String)

    /**
     * 当服务状态发生变化时触发
     */
    fun onServiceStatusConnectChanged(statusCode: Int)

    companion object {

        val STATUS_CONNECT_SUCCESS: Byte = 1//连接状态

        val STATUS_CONNECT_CLOSED: Byte = 0//关闭未连接

        val STATUS_CONNECT_ERROR: Byte = 0//出错未连接
    }
}

package com.townwang.tcp.tcp

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import io.netty.channel.ChannelFutureListener
class NettyService : Service(), NettyListener {
    override fun onMessageResponse(byteBuf: String) {
            val umengIntent = Intent("TCP")
            umengIntent.putExtra("log", "收到消息:【<font color='#D81B60'>$byteBuf</font>】")
            sendBroadcast(umengIntent)
    }

    override fun onStatusResponse(str: String) {
        val umengIntent = Intent("TCP")
        umengIntent.putExtra("log", "状态消息: <font color='#D81B60'> $str </font>")
        sendBroadcast(umengIntent)
    }

    private val TAG = "TCP-netty"

    private var ip: String = ""
    private var port: String? = "0"

    /**
     * 监听网络状态
     */
    private var receiver: NetworkReceiver? = null

    private var mScheduledExecutorService: ScheduledExecutorService? = null

    private fun shutdown() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService!!.shutdown()
            mScheduledExecutorService = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        receiver = NetworkReceiver()
        @Suppress("DEPRECATION") val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver!!, filter)

        // 自定义心跳，每隔45秒向服务器发送心跳包
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        mScheduledExecutorService!!.scheduleAtFixedRate({

            val requestBody = "客户端发送心跳"

            MyNettyClitent.instance.sendMsgToServer(requestBody, ChannelFutureListener { future ->
                if (future.isSuccess) {
                    Log.d(TAG, "Netty TCP --->心跳发送成功")
                } else {
                    Log.e(TAG, "Netty TCP --->心跳发送失败")
                }
            })
        }, 3, 45, TimeUnit.SECONDS)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        MyNettyClitent.instance.setListener(this)
        ip = intent.getStringExtra("ip")
        port = intent.getStringExtra("port")
        connect()
        return START_NOT_STICKY
    }

    override fun onServiceStatusConnectChanged(statusCode: Int) { //连接状态监听
        Log.e(TAG, "Netty TCP 连接状态 --->$statusCode")
        if (statusCode == NettyListener.STATUS_CONNECT_SUCCESS.toInt()) {//在线状态
            val requestBody = "客户端发送心跳"
            Log.e(TAG, "Netty TCP  发心跳--->$requestBody")
            MyNettyClitent.instance.sendMsgToServer(requestBody, ChannelFutureListener { future ->
                if (future.isSuccess) {
                    Log.e(TAG, "Netty TCP --->心跳发送成功")

                    val umengIntent = Intent("TCP")
                    umengIntent.putExtra("log", "心跳发送成功 【$requestBody】 ")
                    sendBroadcast(umengIntent)
                } else {
                    Log.e(TAG, "Netty TCP --->心跳发送失败")

                    val umengIntent = Intent("TCP")
                    umengIntent.putExtra("log", "心跳发送失败 【$requestBody】 ")
                    sendBroadcast(umengIntent)
                }
            })
        }
    }


    /**
     * 连接服务器
     */
    private fun connect() {
        if (!MyNettyClitent.instance.connectStatus) {
            Runnable {
                MyNettyClitent.instance.connect(ip,port!!.toInt())//连接服务器
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let { LocalBroadcastManager.getInstance(this).unregisterReceiver(it) }
        shutdown()
        MyNettyClitent.instance.connectStatus = false
        MyNettyClitent.instance.disconnect()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    inner class NetworkReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) { //
                @Suppress("DEPRECATION")
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    connect()
                }
            }
        }
    }
}

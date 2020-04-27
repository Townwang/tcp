package com.townwang.tcp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.townwang.tcp.tcp.MyNettyClitent
import com.townwang.tcp.tcp.NettyService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val TAG = "TCP案例"


    private val broadcastReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(context: Context?, intent: Intent?) {
                context ?: return
                intent ?: return
                when (intent.action) {
                    "TCP"-> {
                    var log = intent.getStringExtra("log")
                        addText(log)
                    }
                }
            }
        }
    }


    @Suppress("CAST_NEVER_SUCCEEDS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connect.setOnClickListener {
            val regIntent = Intent(this, NettyService::class.java)
            regIntent.putExtra("ip", ip.text.toString())
            regIntent.putExtra("port", port.text.toString())
            startService(regIntent)
        }

        send.setOnClickListener {
            MyNettyClitent.instance.sendMsg(msg.text.toString())
        }

        registerReceiver(broadcastReceiver, IntentFilter().apply {
            addAction("TCP")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    //添加日志
    @SuppressLint("NewApi")
    private fun addText(msg: String) {

        content.append(Html.fromHtml(msg,Html.FROM_HTML_MODE_COMPACT))
        content.append("\n")
        var offset = scrollView.measuredHeight - content.measuredHeight
        if (offset < 0) {
            offset = 0
        }

        //scrollview开始滚动
        scrollView.scrollTo(0, offset)

//        var offset=textView.lineCount *textView.lineHeight
//        if (offset > scrollView.height) {
//            textView.scrollTo(0,offset - textView.height)
//        }
    }

    //清空日志
    private fun clearText(mTextView: TextView) {
        mTextView.text = ""
    }
}

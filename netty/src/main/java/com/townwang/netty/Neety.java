/*
 * Copyright © 文科中的技术宅
 * blog:https://www.townwang.com
 */
package com.townwang.netty;

import java.net.InetAddress;

/**
 * @author Town
 * @created at 2018/7/11 18:44
 * @Last Modified by: Town
 * @Last Email: android@townwang.com
 * @Last Modified time: 2018/7/11 18:44
 * @Remarks
 */
public abstract class Neety implements NettyListener {

    public Neety(){
      NeetyConfig.inetHost = getInetHost();
      NeetyConfig.inetPort = getInetPort();
      NeetyConfig.heartbeat = getHeartbeat();
      NeetyConfig.heartbeatTime = getHeartbeatTime();
      NeetyTown.getInstance().setListener(this);

    }

    protected abstract InetAddress getInetHost();

    protected abstract int getInetPort();

    protected abstract String getHeartbeat();

    protected abstract int getHeartbeatTime();

    protected  void getEndCheck(String endCheck){
        if (endCheck!=null){
            NeetyConfig.endCheck = endCheck;
        }
    }

    protected void  sendCmd(String cmd){
        NeetyTown.getInstance().sendMsg(cmd);
    }


    @Override
    public void onMessageResponse(String byteBuf) {

    }

    @Override
    public void onServiceStatusConnectChanged(int statusCode) {

    }
}

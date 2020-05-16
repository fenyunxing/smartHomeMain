package com.blesdk.impl;

/**
 *  接收接口类
 * 可得到解析后的接收数据
 *
 * Created by admin on 2016/10/21.
 */
public abstract interface AgentListener {

    public abstract void onData(String paraMsg);
}


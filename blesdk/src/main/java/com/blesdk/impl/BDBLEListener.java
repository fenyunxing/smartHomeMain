package com.blesdk.impl;

/**
 * 蓝牙监听接口类
 *
 * Created by admin on 2016/10/21. Modified by admin on 2017/04/07
 */
public abstract interface BDBLEListener {

    public abstract void onConnectBleSuccess();

    public abstract void onDisConnectBleSuccess();

    public abstract void onDataRReceived(String paraMsg);

}

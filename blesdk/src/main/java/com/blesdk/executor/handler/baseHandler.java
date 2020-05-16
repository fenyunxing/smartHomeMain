package com.blesdk.executor.handler;


import com.blesdk.impl.BDBLEListener;
import com.blesdk.tools.BDMethod;

/**
 * Handler基类
 *
 * Created by admin on 2016/10/21.
 * Modified by admin on 2017/5/13
 */
public abstract class baseHandler {

    protected BDBLEListener bdbleListener;

    /**
     * 构造函数
     *
     * @param paramBDBLEListener
     *         传入监听
     */
    protected baseHandler(BDBLEListener paramBDBLEListener) {
        this.bdbleListener = paramBDBLEListener;
    }

    /**
     * 将发送的字节内容写入发送缓存中并通过蓝牙发送
     *
     * @param paramArrayOfByte
     */
    public abstract void send(byte[] paramArrayOfByte);


    public byte[] onMessageReceivered(byte[] paramByte) {

                this.bdbleListener.onDataRReceived(BDMethod.castBytesToHexString(paramByte));

        return paramByte;
    }

    public void onConnectBleSuccess() {
        this.bdbleListener.onConnectBleSuccess();
    }

    public void onDisConnectBleSuccess() {
        this.bdbleListener.onDisConnectBleSuccess();
    }

}

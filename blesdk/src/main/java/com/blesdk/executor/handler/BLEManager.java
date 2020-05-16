package com.blesdk.executor.handler;

import android.content.Context;
import android.util.Log;

import com.blesdk.impl.BDBLEListener;
import com.blesdk.impl.AgentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 终端蓝牙操作类
 * 可进行蓝牙连接、断开、发送符合协议的命令、以及接收符合协议的数据，并解析保存入相应对象，方便后续获取使用。
 *
 * Created by admin on 2016/10/21
 * Modified by admin on 2017/05/23
 */
public class BLEManager implements BDBLEListener {
    public static BDBLEHandler bdbleHandler = null;
    private static BLEManager bleManager;
    public static List<AgentListener> agentListeners = new ArrayList();
    public static String address;
    public static String deviceName;
    public static boolean ConnectState = false;

    /**
     * 获得BLEManager单例
     *
     * @return 返回单例
     */
    public static BLEManager getInstance() {
        if (bleManager == null)
            bleManager = new BLEManager();
        return bleManager;
    }

    /**
     * 连接蓝牙
     *
     * @param context
     *         传入上下文（一般传入APP全局上下文）
     */

    public void connectBle(Context context) {

        if (bdbleHandler == null) {
            bdbleHandler = new BDBLEHandler(context, bleManager);
        }
        if (bdbleHandler.initialize()) {
            if (address != null || !address.equals(" ")) {
                bdbleHandler.connectDevice(this.address);
            } else {
                Log.e("BDError", "ERROR ==> Connect without any address!");
            }
            return;
        }
        return;
    }

    /**
     * 连接蓝牙
     *
     * @param context
     *         传入上下文（一般传入APP全局上下文）
     * @param UUID_RX
     *         自定义接收通道UUID
     * @param UUID_TX
     *         自定义发送通道UUID
     */


    /**
     * 断开当前蓝牙连接
     */
    public void disConnectBle() {
        if (bdbleHandler != null) {
            bdbleHandler.disconnectDevice();
            bdbleHandler = null;
        }
    }

    /**
     * 设置连接设备的物理地址
     *
     * @param mdeviceAddr
     *         蓝牙连接设备物理地址
     */
    public BLEManager setDeviceAddress(String mdeviceAddr) {
        address = mdeviceAddr;
        return this;
    }

    /**
     * 设置连接设备的名称
     *
     * @param mdeviceName
     *         蓝牙连接设备名称
     */
    public BLEManager setDeviceName(String mdeviceName) {
        deviceName = mdeviceName;
        return this;
    }

    /**
     * 获得当前连接设备名称
     *
     * @return 当前连接设备名称
     */
    public String getDeviceName() {
        return deviceName;
    }


    /**
     * 通过蓝牙发送任意自定义内容
     *
     * @param sendMsg
     *         发送内容
     */
    public void send(byte[] sendMsg) {
        if (bdbleHandler != null) {
            bdbleHandler.send(sendMsg);
        }
    }


    @Override
    public void onConnectBleSuccess() {

    }

    @Override
    public void onDisConnectBleSuccess() {

    }

    @Override
    public void onDataRReceived(String paraMsg) {

    }
}


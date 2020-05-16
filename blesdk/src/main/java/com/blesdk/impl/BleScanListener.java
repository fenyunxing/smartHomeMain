package com.blesdk.impl;

import android.bluetooth.BluetoothDevice;

/**
 * BLE蓝牙搜索监听类
 * 可得到返回的蓝牙搜索结果
 * Created by admin on 2017/1/18.
 */
public interface BleScanListener {

    /**
     * 返回蓝牙BLE扫描结果方法
     * @param bluetoothDevice 扫描得到的设备类
     * @param rssi 扫描范围内的设备信号强度
     */
    void onScanResult(BluetoothDevice bluetoothDevice, int rssi);

    /**
     * 返回扫描失败指示错误代码
     * @param code 失败码
     */
    void onScanFailed(int code);
}

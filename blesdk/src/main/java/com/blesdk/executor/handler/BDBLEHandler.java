package com.blesdk.executor.handler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.blesdk.BeidouBleSdk;
import com.blesdk.BeidouParams;
import com.blesdk.executor.thread.revThread;
import com.blesdk.executor.thread.rssiThread;
import com.blesdk.executor.thread.sendThread;
import com.blesdk.impl.BDBLEListener;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * BLE蓝牙管理类 该类可直接对蓝牙操作，连接断开，发送与接收，不涉及协议，但不建议直接用此类进行蓝牙写入操作。
 *
 * Created by admin on 2016/10/21. Modified by admin on 2017/05/13
 */
public class BDBLEHandler extends baseHandler {
    public BluetoothGatt mBluetoothGatt;
    public Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private revThread revthread = new revThread();
    private sendThread txthread = new sendThread();
    private rssiThread rssithread = new rssiThread();
    private static boolean pause = false;

    //广播标志
    public final static String ACTION_GATT_CONNECTED = "com.blutest.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.blutest.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.blutest.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.blutest.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.blutest.EXTRA_DATA";
    public final static String EXTRA_DATA_HEX = "com.blutest.EXTRA_DATA_HEX";
    public final static String ACTION_RSSI = "com.blutest.ACTION_RSSI";

    // 北斗盒子蓝牙读取数据特征码
    private final static String UUID_RX_DATA_1 = "0000ffe4-0000-1000-8000-00805f9b34fb";
    private final static String UUID_TX_DATA_1 = "0000ffe9-0000-1000-8000-00805f9b34fb";
    /*// 我
    private final static String UUID_RX_DATA_2 = "0003cdd1-0000-1000-8000-00805f9b0131";
    private final static String UUID_TX_DATA_2 = "0003cdd2-0000-1000-8000-00805f9b0131";*/
    //晓明
    /*private final static String UUID_RX_DATA_2 = "0003cdd1-0000-1000-8000-00805f9b0131";
    private final static String UUID_TX_DATA_2 = "0003cbb1-0000-1000-8000-00805f9b0131";*/
    // 测试蓝牙模块读取数据特征码
    private final static String UUID_RX_DATA_2 = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private final static String UUID_TX_DATA_2 = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    // 自定义UUID
    private static String UUID_RX_DATA = "";
    private static String UUID_TX_DATA = "";

    /**
     * BLE回调函数声明
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothProfile.STATE_DISCONNECTED && newState == BluetoothProfile.STATE_CONNECTED) {
                BDBLEHandler.this.mBluetoothGatt.discoverServices();
                BLEManager.ConnectState = true;
                Intent intent = new Intent(ACTION_GATT_CONNECTED);
                mContext.getApplicationContext().sendBroadcast(intent);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                BLEManager.getInstance().disConnectBle();
                BLEManager.ConnectState = false;

                if (BeidouParams.gattBluetoothAutoConnect) {
                    Log.v("FDBDTestLog","蓝牙重连");
                    BeidouBleSdk.getInstance().startConnectBle();
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            BDBLEHandler.this.onDisConnectBleSuccess();
                        }
                    });
                }

//                Intent intent = new Intent(ACTION_GATT_DISCONNECTED);
//                mContext.getApplicationContext().sendBroadcast(intent);
            }
        }

        @Override
        // 当远程设备的列表、特征和描述符已被更新时，会调用回调函数，即新的服务已被发现。
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BDBLEHandler.this.displayGattServices(gatt.getServices());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt paramGatt,
                                          BluetoothGattCharacteristic paraCharacteristic, int status) {
        }

        @Override
        // 返回一个特征读取操作的结果。
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        // 当远程有characteristic通知时回传
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if (!pause) {
                BDBLEHandler.this.receiveData(characteristic);
            }
        }

        @Override
        //获得蓝牙RSSI回调
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            final Intent intent = new Intent(ACTION_RSSI);
            intent.putExtra(ACTION_RSSI, "" + rssi);
            mContext.getApplicationContext().sendBroadcast(intent);
        }
    };

    /**
     * 接收数据并放入缓存
     *
     * @param characteristic
     */
    private void receiveData(BluetoothGattCharacteristic characteristic) {
        revthread.setRevTempMsg(characteristic.getValue());

    }

    /**
     * 关闭GATT服务
     */
    protected void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * 构造函数(使用默认UUID)
     *
     * @param paramContext
     *         上下文
     * @param paramBDBLEListener
     *         监听
     */
    protected BDBLEHandler(Context paramContext, BDBLEListener paramBDBLEListener) {
        super(paramBDBLEListener);
        this.mContext = paramContext;

    }

    /**
     * 构造函数(使用自定义UUID)
     *
     * @param paramContext
     *         上下文
     * @param paramBDBLEListener
     *         监听
     * @param UUID_RX
     *         接收通道UUID
     * @param UUID_TX
     *         发送通道UUID
     */
    protected BDBLEHandler(Context paramContext, BDBLEListener paramBDBLEListener, String UUID_RX, String UUID_TX) {
        super(paramBDBLEListener);
        this.mContext = paramContext;
        if (UUID_RX != null && UUID_RX != "") {
            this.UUID_RX_DATA = UUID_RX;
            if (UUID_TX != null && UUID_TX != "") {
                this.UUID_TX_DATA = UUID_TX;
            } else {
            }
        } else {
        }
    }

    /**
     * 初始化本地蓝牙适配器
     *
     * @return 初始化成功返回true
     */
    protected boolean initialize() {
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = ((BluetoothManager) this.mContext
                    .getSystemService(Context.BLUETOOTH_SERVICE));
            if (this.mBluetoothManager == null)
                return false;
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        return this.mBluetoothAdapter != null;
    }

    /**
     * 连接在该蓝牙设备上的GATT服务器，连接成功后启动发送和接收线程
     *
     * @param address
     *         蓝牙设备的物理地址
     *
     * @return 连接成功后范围true，否则false
     */

    protected void connectDevice(final String address) {

        if (mBluetoothAdapter == null || address == null) {
            return;
        }
        final BluetoothDevice device = BDBLEHandler.this.mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            return;
        }
        mBluetoothDeviceAddress = address;
        // 防止多次重复按下连接
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            return;
        } else {
            new Thread() {
                public void run() {
                    mBluetoothGatt = device.connectGatt(BDBLEHandler.this.mContext,
                            false, BDBLEHandler.this.mGattCallback);
                    revthread.start();
                    txthread.start();
                    rssithread.start();
                    return;
                }
            }.start();
        }

    }

    /**
     * 断开现有连接或取消挂起的连接，断开结果通过异步报告
     */
    protected void disconnectDevice() {
        if ((this.mBluetoothAdapter == null) || (this.mBluetoothGatt == null)) {
            Log.v("FDBDChatTest", "=====mBluetoothGatt == null=====");
            return;
        }
        revthread.stopThread();
        txthread.stopThread();
        rssithread.stopThread();
        BLEManager.ConnectState = false;
        Intent intent = new Intent(ACTION_GATT_DISCONNECTED);
        mContext.getApplicationContext().sendBroadcast(intent);
        this.mBluetoothGatt.disconnect();
        this.close();
    }

    /**
     * 读取一个特征值通道中的内容
     *
     * @param characteristic
     *         通道特征值
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 向蓝牙的一个特征值通道内写入内容
     *
     * @param paramArrayOfByte
     *         内容比特串
     */
    public void WriteValue(byte[] paramArrayOfByte) {
        if (mTXCharacteristic != null) {
            this.mTXCharacteristic.setValue(paramArrayOfByte);
            this.mBluetoothGatt.writeCharacteristic(this.mTXCharacteristic);
        }
    }

    /**
     * 启用或禁用一个给定特性的特征值通道
     *
     * @param characteristic
     *         通道特征值
     * @param enabled
     *         启用true;禁用false
     */
    private void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_RX_DATA_1)) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CCCD);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            this.mBluetoothGatt.writeDescriptor(descriptor);
        }

    }

    protected BluetoothGattCharacteristic mRXCharacteristic = null;
    protected BluetoothGattCharacteristic mTXCharacteristic = null;

    /**
     * 查找指定UUID的特征值通道
     *
     * @param paramList
     *         GATT服务列表
     */
    private void displayGattServices(List<BluetoothGattService> paramList) {
        Iterator localIterator1 = paramList.iterator();
        while (localIterator1.hasNext()) {
            Iterator localIterator2 = ((BluetoothGattService) localIterator1
                    .next()).getCharacteristics().iterator();
            while (localIterator2.hasNext()) {
                BluetoothGattCharacteristic localBluetoothGattCharacteristic = (BluetoothGattCharacteristic) localIterator2
                        .next();
                if (UUID_RX_DATA == "" || UUID_TX_DATA == "") {
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(UUID_RX_DATA_1)) {
                        setCharacteristicNotification(
                                localBluetoothGattCharacteristic, true);
                        this.mRXCharacteristic = localBluetoothGattCharacteristic;
                    }
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(UUID_TX_DATA_1)) {

                        setCharacteristicNotification(
                                localBluetoothGattCharacteristic, true);
                        this.mTXCharacteristic = localBluetoothGattCharacteristic;
                        Intent intent = new Intent(
                                ACTION_GATT_SERVICES_DISCOVERED);
                        mContext.getApplicationContext().sendBroadcast(intent);
                    }
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(UUID_RX_DATA_2)) {
                        setCharacteristicNotification(
                                localBluetoothGattCharacteristic, true);
                        this.mRXCharacteristic = localBluetoothGattCharacteristic;
                    }
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(UUID_TX_DATA_2)) {

                        setCharacteristicNotification(
                                localBluetoothGattCharacteristic, true);
                        this.mTXCharacteristic = localBluetoothGattCharacteristic;
                        Intent intent = new Intent(
                                ACTION_GATT_SERVICES_DISCOVERED);
                        mContext.getApplicationContext().sendBroadcast(intent);
                    }
                } else {
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(UUID_RX_DATA)) {
                        setCharacteristicNotification(
                                localBluetoothGattCharacteristic, true);
                        this.mRXCharacteristic = localBluetoothGattCharacteristic;
                    }
                    if (localBluetoothGattCharacteristic.getUuid().toString()
                            .equalsIgnoreCase(UUID_TX_DATA)) {

                        setCharacteristicNotification(
                                localBluetoothGattCharacteristic, true);
                        this.mTXCharacteristic = localBluetoothGattCharacteristic;
                        Intent intent = new Intent(
                                ACTION_GATT_SERVICES_DISCOVERED);
                        mContext.getApplicationContext().sendBroadcast(intent);
                    }
                }
            }
        }
        if ((this.mRXCharacteristic != null) && (this.mTXCharacteristic != null)) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable()
            {
                public void run() {
                    Log.v("FDBDTestLog","链接成功");
                    BDBLEHandler.this.onConnectBleSuccess();
                }
            });
            return;
        }
        Log.v("FDBDTestLog","未找到服务与通道");
        this.mBluetoothGatt.discoverServices();
    }

    /**
     * @param paramArrayOfByte
     *         发送的比特串
     */
    @Override
    public void send(final byte[] paramArrayOfByte) {
        txthread.setSendTempMsg(paramArrayOfByte);
    }
}

package com.blesdk.executor.handler;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blesdk.impl.BleScanListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙BLE搜索操作类
 * 用作搜索操作，得到周围BLE设备名称与物理地址
 *
 * Created by admin on 2017/1/18.
 * Modified by admin on 2017/5/13
 */
public class BleScanManager {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Activity mActivityContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothAdapter.LeScanCallback mScanCallbackold;
    private ScanCallback mScanCallbacknew;
    private BleScanListener mBleScanListener;
    private List<BluetoothDevice> devices = new ArrayList();
    private boolean mScanning;

    public BleScanManager() {
    }

    /**
     * 蓝牙扫描初始化方法
     *
     * @param activity
     *         activity的上下文
     * @param bleScanListener
     *         搜索监听类
     *
     * @return true：初始化成功   false:初始化失败
     */
    public boolean bleScanInit(Activity activity, BleScanListener bleScanListener) {
        mActivityContext = activity;
        mBleScanListener = bleScanListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ContextCompat.checkSelfPermission(mActivityContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivityContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            if (!bleRightInit() || !GPSRightInit(activity)) {
                return false;
            }
            scanCallbackInit();
            return true;
        } else {
            if (!bleRightInit() || !GPSRightInit(activity)) {
                return false;
            }
            scanCallbackInit();
            return true;
        }
    }

    /**
     * 判断是否打开GPS服务，大于23版本弹框提示
     *
     * @param context activity弹框提示
     */
    private boolean GPSRightInit(Activity context) {
        if (!isOpenLocService(context) && Build.VERSION.SDK_INT >= 23) {
            showGpsRightDialog(context);
            return false;
        } else return true;
    }

    /**
     * 显示GPS权限申请对话框
     *
     * @param mcontext activity上下文
     */
    private void showGpsRightDialog(final Activity mcontext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext)
                .setTitle("提示")
                .setMessage("请打开本机位置服务，用于蓝牙搜索。");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        gotoLocServiceSettings(mcontext);
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 判断是否有满足打开ble的权限
     *
     * @return true/false
     */
    private boolean bleRightInit() {
        BluetoothManager bluetoothManager = (BluetoothManager) mActivityContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivityContext.startActivity(enableBtIntent);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 初始化ble扫描，配置扫描反馈，>安卓5.0版本采用新的扫描反馈方式
     */
    private void scanCallbackInit() {
        if (mBluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                mScanCallbacknew = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, final ScanResult result) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (!devices.contains(result.getDevice())) {
                                devices.add(result.getDevice());
                                mBleScanListener.onScanResult(result.getDevice(), result.getRssi());
                            }
                        }
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        super.onScanFailed(errorCode);
                        if (mBleScanListener != null) {
                            mBleScanListener.onScanFailed(errorCode);
                        }
                    }
                };
            } else {
                mScanCallbackold = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice paradevice, int rssi, byte[]
                            scanRecord) {
                        if (!devices.contains(paradevice)) {
                            devices.add(paradevice);
                            mBleScanListener.onScanResult(paradevice, rssi);
                        }
                    }
                };
            }
        }
    }

    /**
     * 开始扫描BLE设备
     *
     * @param enable
     *         输入true开始搜索,输入false停止搜索
     *
     * @return 返回当前搜索所使用的BleScanManager
     */
    public BleScanManager scanDevice(final boolean enable) {
        if (enable) {
            mScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mBluetoothLeScanner != null) {
                mBluetoothLeScanner.startScan(mScanCallbacknew);
            } else {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.startLeScan(mScanCallbackold);
                }
            }
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mBluetoothLeScanner != null) {
                mBluetoothLeScanner.stopScan(mScanCallbacknew);
            } else {
                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.stopLeScan(mScanCallbackold);
                }
            }
        }
        return this;
    }

    /**
     * 判断是否启动定位服务
     *
     * @param context
     *         全局信息接口
     *
     * @return 是否启动定位服务
     */
    private static boolean isOpenLocService(final Context context) {

        boolean isGps = false; //判断GPS定位是否启动
        boolean isNetwork = false; //判断网络定位是否启动

        if (context != null) {

            LocationManager locationManager
                    = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null) {
                //通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
                isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                //通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
                isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }

            if (isGps || isNetwork) {
                return true;
            }

        }

        return false;
    }

    /**
     * 跳转定位服务界面
     *
     * @param context
     *         全局信息接口
     */
    private static void gotoLocServiceSettings(Context context) {
        final Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

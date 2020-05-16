package com.example.mybuletoothble;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.blesdk.executor.handler.BDBLEHandler;
import com.blesdk.executor.handler.BLEManager;
import com.blesdk.executor.handler.BleScanManager;
import com.blesdk.impl.AgentListener;
import com.blesdk.impl.BleScanListener;
import com.blesdk.tools.BDMethod;
import com.example.mybuletoothble.Adapter.DataAdapter;
import com.example.mybuletoothble.Adapter.ScanBleAdapter;

import java.util.Calendar;

public class BuleToothMainActivity extends AppCompatActivity implements View.OnClickListener, AgentListener {
    private Context mContext;
    private BleScanManager mbleScanManager;
    private DataAdapter mDataAdapter;
    private ScanBleAdapter mscanAdapter;
    private TextView tv_connectedDevice;
    private ListView lv_Data;
    private EditText et_sendText;
    private Button btn_open_ble;
    private Button btn_close_ble;
    private Button btn_send;
    private boolean ifHexRev = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewinit();
        datainit();
        BLEManager.getInstance().agentListeners.add(this);
         mContext = this;
    }

    @Override
    public void onBackPressed() { //返回按钮
        super.onBackPressed();
       // mbleScanManager.scanDevice(false); //取消扫描
       // BLEManager.getInstance().disConnectBle();//取消链接
        finish(); //结束当前界面
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BLEManager.getInstance().agentListeners.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // bleInit();
    }

    private void bleInit() {
        mbleScanManager = new BleScanManager();
        //获得ifBleOpen如果为true则符合扫描条件，反之亦然
        mbleScanManager.bleScanInit(this, new BleScanListener() {
            @Override
            //设置扫描结果返回响应
            public void onScanResult(final BluetoothDevice bluetoothDevice, int rssi) {
            }

            @Override
            public void onScanFailed(int code) {

            }
        });
    }

    private void datainit() {
        mscanAdapter = new ScanBleAdapter(this);
        mDataAdapter = new DataAdapter(this);
        lv_Data.setAdapter(mDataAdapter);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    private void viewinit() {
        //连接设备显示文本
        tv_connectedDevice = (TextView) findViewById(R.id.tv_connectedDevice);
        //接收显示列表控件
        lv_Data=(ListView)findViewById(R.id.lv_Data);
        //发送编辑框
        et_sendText=(EditText)findViewById(R.id.et_sendText);
        btn_open_ble=(Button)findViewById(R.id.btn_open_ble);
        btn_close_ble=(Button)findViewById(R.id.btn_close_ble);
        btn_send=(Button)findViewById(R.id.btn_send);
        btn_open_ble.setOnClickListener(this);
        btn_close_ble.setOnClickListener(this);
        btn_send.setOnClickListener(this);

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BDBLEHandler.ACTION_GATT_CONNECTED.equals(action)) {
                tv_connectedDevice.setText(BLEManager.getInstance().getDeviceName());
                invalidateOptionsMenu();
                //蓝牙连接返回操作
            } else if (BDBLEHandler.ACTION_GATT_DISCONNECTED.equals(action)) {
                tv_connectedDevice.setText("");
                invalidateOptionsMenu();
                //蓝牙断开返回操作
            } else if (BDBLEHandler.ACTION_DATA_AVAILABLE.equals(action)) {
                if(ifHexRev){
                    mDataAdapter.arr.add(BDMethod.castCalendarToString(Calendar.getInstance(), null) + "  " +
                            new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA_HEX)));
                }else {
                    mDataAdapter.arr.add(BDMethod.castCalendarToString(Calendar.getInstance(), null) + "  " +
                            new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)));
                    Log.i("BuleToothMainActivity","接收测试");
                }
                mDataAdapter.notifyDataSetChanged();
                //接收数据状态
            } else if (BDBLEHandler.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //发现服务通道状态
            } else if (BDBLEHandler.ACTION_RSSI.equals(action)) {
                intent.getStringExtra(BDBLEHandler.ACTION_RSSI);//接收到的RSSI值
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BDBLEHandler.EXTRA_DATA);
        intentFilter.addAction(BDBLEHandler.EXTRA_DATA_HEX);
        intentFilter.addAction(BDBLEHandler.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BDBLEHandler.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BDBLEHandler.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BDBLEHandler.ACTION_RSSI);
        return intentFilter;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_open_ble) {
            Intent localIntent = new Intent(BuleToothMainActivity.this, ScanBleActivity.class);
            startActivity(localIntent);
        } else if (id == R.id.btn_close_ble) {
            BLEManager.getInstance().disConnectBle();
        } else if (id == R.id.btn_send) {//获取编辑框内容
            String sendtext = et_sendText.getText().toString();
            //发送
            BLEManager.getInstance().send(sendtext.getBytes());
        }
        
    }

    @Override
    public void onData(String paraMsg) {

    }
}

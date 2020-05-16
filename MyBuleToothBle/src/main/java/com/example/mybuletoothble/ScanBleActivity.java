package com.example.mybuletoothble;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mybuletoothble.Adapter.ScanBleAdapter;
import com.blesdk.BeidouBleSdk;
import com.blesdk.executor.handler.BLEManager;
import com.blesdk.executor.handler.BleScanManager;
import com.blesdk.impl.BleScanListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/6/17.
 */


public class ScanBleActivity extends AppCompatActivity {
    private ScanBleAdapter mscanAdapter;
    private ListView listViewContent;
    private List<BluetoothDevice> devices = new ArrayList();
    private BleScanManager mbleScanManager;
    private BeidouBleSdk beidouBleSdk;
    private boolean ifBleOpen = false;
    private Toolbar scanToolbar;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_scan_ble);
        viewsInit();
        dataInit();
    }

    @Override
    public void onResume() {
        super.onResume();
        bleInit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mbleScanManager.scanDevice(false);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void dataInit() {
        beidouBleSdk = BeidouBleSdk.getInstance(getApplicationContext());
        mscanAdapter = new ScanBleAdapter(this);
        listViewContent.setAdapter(this.mscanAdapter);
        listViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BLEManager.getInstance()
                        .setDeviceAddress(((BluetoothDevice) devices.get(position)).getAddress())
                        .setDeviceName(((BluetoothDevice) devices.get(position)).getName());
                //BLEManager.getInstance().connectBle(getApplicationContext());
                beidouBleSdk.startConnectBle();
                mbleScanManager.scanDevice(false);
                finish();
            }
        });
    }

    protected void bleInit() {
        mbleScanManager = new BleScanManager();
        //获得ifBleOpen如果为true则符合扫描条件，反之亦然
        ifBleOpen = mbleScanManager.bleScanInit(this, new BleScanListener() {
            @Override
            //设置扫描结果返回响应
            public void onScanResult(final BluetoothDevice bluetoothDevice, int rssi) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!devices.contains(bluetoothDevice)) {
                            devices.add(bluetoothDevice);
                            mscanAdapter.setDatas(devices);
                            mscanAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onScanFailed(int code) {

            }
        });
        if (ifBleOpen) {
            mbleScanManager.scanDevice(true);
        } else {
            return;
        }
    }

    protected void viewsInit() {
        listViewContent = (ListView) findViewById(R.id.device_list);

    }


}

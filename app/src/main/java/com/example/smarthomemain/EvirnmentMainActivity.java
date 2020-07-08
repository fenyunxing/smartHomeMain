package com.example.smarthomemain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blesdk.executor.handler.BDBLEHandler;
import com.blesdk.executor.handler.BLEManager;
import com.blesdk.tools.BDMethod;

import java.util.Calendar;

public class EvirnmentMainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView mTempture_tv;
    TextView mHumidity_tv;
    TextView mFireair_tv;
    ImageButton mTempture_imbtn;
    ImageButton mHumidity_imbtn;
    ImageButton mFireair_imbtn;
    ImageButton mClear_imbtn;
    //public final static String CLEAR_BROADCAST_FLAG="clearbroadcast";
    Context mcontext;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evirnment_main);
        initview();
        //注册广播接收
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        handler = new MyHandler();
    }

    private void initview() {
        mTempture_tv = (TextView) findViewById(R.id.tempture_tv);
        mHumidity_tv = (TextView) findViewById(R.id.humidity_tv);
        mFireair_tv = (TextView) findViewById(R.id.fireair_tv);
        mTempture_imbtn = (ImageButton) findViewById(R.id.tempture_imbtn);
        mHumidity_imbtn = (ImageButton) findViewById(R.id.humidity_imbtn);
        mFireair_imbtn = (ImageButton) findViewById(R.id.fireair_imbtn);
        mClear_imbtn = (ImageButton) findViewById(R.id.clear_imbtn);
        //设置监听
        mClear_imbtn.setOnClickListener(this);
        mTempture_imbtn.setOnClickListener(this);
        mHumidity_imbtn.setOnClickListener(this);
        mFireair_imbtn.setOnClickListener(this);
    }

    //设置要监听的广播
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BDBLEHandler.EXTRA_DATA);
        intentFilter.addAction(BDBLEHandler.EXTRA_DATA_HEX);
        //intentFilter.addAction(CLEAR_BROADCAST_FLAG);
        // intentFilter.addAction(BDBLEHandler.ACTION_GATT_CONNECTED);
        // intentFilter.addAction(BDBLEHandler.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BDBLEHandler.ACTION_DATA_AVAILABLE);
        // intentFilter.addAction(BDBLEHandler.ACTION_RSSI);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BDBLEHandler.ACTION_DATA_AVAILABLE.equals(action)) {
                if(new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)).startsWith("#")){ //温度类型数据
                    //去除首字符标志
                     String temp=new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)).substring(1);
                     mTempture_tv.setText(temp);
                  //  mHumidity_tv.setText(new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)));
                  //  mFireair_tv.setText(new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)));
                } else if(new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)).startsWith("!")){ //湿度类型数据
                    String temp=new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)).substring(1);
                    mHumidity_tv.setText(temp);
                } else if(new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)).startsWith("*")){ //气体类型数据
                    String temp=new String(intent.getStringExtra(BDBLEHandler.EXTRA_DATA)).substring(1);
                    mFireair_tv.setText(temp);
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);//取消注册
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tempture_imbtn) {
            String s = "wd";
            BLEManager.getInstance().send(s.getBytes());

        } else if (v.getId() == R.id.humidity_imbtn) {
            String s = "sd";
            BLEManager.getInstance().send(s.getBytes());
        } else if (v.getId() == R.id.fireair_imbtn) {

            String s = "qt";
            BLEManager.getInstance().send(s.getBytes());
        } else if (v.getId() == R.id.clear_imbtn) {
//           Intent intent = new Intent();
//           intent.setAction(CLEAR_BROADCAST_FLAG);
//           intent.putExtra("name", "test");
//           mcontext.getApplicationContext().sendBroadcast(intent);
            Thread thread = new NetworkThread();
            thread.start();

        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            //把文本控件清空
            mTempture_tv.setText(s);
            mHumidity_tv.setText(s);
            mFireair_tv.setText(s);
        }

    }

    class NetworkThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1 * 1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //发送清空消息
            String s = " ";
            Message msg = handler.obtainMessage();
            msg.obj = s;
            handler.sendMessage(msg);
        }

    }


}

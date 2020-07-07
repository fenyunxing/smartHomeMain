package com.example.fancontrolmodule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blesdk.executor.handler.BLEManager;

public class FanControlMainActivity extends AppCompatActivity implements View.OnClickListener {
ImageButton mReduce_btn;
ImageButton mAdd_btn;
ImageButton mFan_btn;
boolean fanFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_control_main);
        initview();  //初始化控件
    }

    private void initview() {
        mReduce_btn=(ImageButton)findViewById(R.id.reduce_imbtn);
        mAdd_btn=(ImageButton)findViewById(R.id.add_imbtn);
        mFan_btn=(ImageButton)findViewById(R.id.fan_btn);

        mFan_btn.setOnClickListener(this);
        mReduce_btn.setOnClickListener(this);
        mAdd_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.reduce_imbtn){  //判断是否按下减

        } else if(v.getId()==R.id.add_imbtn){ //判断是否按下加

        } else if(v.getId()==R.id.fan_btn){
            if(fanFlag){
                String s="f";
                BLEManager.getInstance().send(s.getBytes());
                fanFlag=false;
            } else {
                String s="e";
                BLEManager.getInstance().send(s.getBytes());
                fanFlag=true;
            }

        }
    }

//    private Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            //处理消息
//            switch (msg.what){
//                case 1:
//                    mHour_tv.setText(hour);
//                    break;
//
//                    default:break;
//            }
//            return false;
//        }
//    });
}

package com.example.smarthomemain;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.blesdk.executor.handler.BLEManager;

public class RemoterMainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton mLamp_switch_imbtn;
    ImageButton mFan_imbtn;
    ImageButton mFace_door_imbtn;
    ImageButton mNumber1_imbtn,mNumber2_imbtn,mNumber3_imbtn,mNumber4_imbtn,mNumber5_imbtn,mNumber6_imbtn;
    private boolean mLampFalg=false;
    private boolean fanFlag=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remoter_main);
        initview();
    }

    private void initview() {
        mLamp_switch_imbtn=(ImageButton)findViewById(R.id.lamp_swith_imbtn);
        mFan_imbtn=(ImageButton)findViewById(R.id.fan_imbtn);
        mFace_door_imbtn=(ImageButton)findViewById(R.id.facedoor_imbtn);
        mNumber1_imbtn=(ImageButton)findViewById(R.id.number1_imbtn);
        mNumber2_imbtn=(ImageButton)findViewById(R.id.number2_imbtn);
        mNumber3_imbtn=(ImageButton)findViewById(R.id.number3_imbtn);
        mNumber4_imbtn=(ImageButton)findViewById(R.id.number4_imbtn);
        mNumber5_imbtn=(ImageButton)findViewById(R.id.number5_imbtn);
        mNumber6_imbtn=(ImageButton)findViewById(R.id.number6_imbtn);

        mLamp_switch_imbtn.setOnClickListener(this);
        mFace_door_imbtn.setOnClickListener(this);
        mFan_imbtn.setOnClickListener(this);
        mNumber1_imbtn.setOnClickListener(this);
        mNumber2_imbtn.setOnClickListener(this);
        mNumber3_imbtn.setOnClickListener(this);
        mNumber4_imbtn.setOnClickListener(this);
        mNumber5_imbtn.setOnClickListener(this);
        mNumber6_imbtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.lamp_swith_imbtn){//电灯点击
           if(mLampFalg){
               String s="lampclose"  ;
               BLEManager.getInstance().send(s.getBytes());
               mLampFalg=false;
           }else {
               String s="lampopen"  ;
               BLEManager.getInstance().send(s.getBytes());
               mLampFalg=true;
           }
       } else if(v.getId()==R.id.fan_imbtn){ //风扇点击
           //风扇启动和关闭
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
}

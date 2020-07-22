package com.example.fancontrolmodule;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blesdk.executor.handler.BLEManager;

public class FanControlMainActivity extends AppCompatActivity implements View.OnClickListener {
ImageButton mReduce_btn;
ImageButton mAdd_btn;
ImageButton mFan_btn;
ImageButton mNumber1_imbtn;
ImageButton mNumber2_imbtn;
ImageButton mNumber3_imbtn;
ImageButton mNumber4_imbtn;
ImageButton mNumber5_imbtn;
ImageButton mNumber6_imbtn;
ImageButton mHand_control_imbtn;
ImageButton mAuto_imbtn;
ImageButton mSleep_imbtn;
EditText msetclock_et;
ImageButton msetclock_imbtn;
boolean fanFlag=false;
int clockModeFlag=1;
int modeFlag=1;
private String setclock_val=null;

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
        mNumber1_imbtn=(ImageButton)findViewById(R.id.number1_imbtn);
        mNumber2_imbtn=(ImageButton)findViewById(R.id.number2_imbtn);
        mNumber3_imbtn=(ImageButton)findViewById(R.id.number3_imbtn);
        mNumber4_imbtn=(ImageButton)findViewById(R.id.number4_imbtn);
        mNumber5_imbtn=(ImageButton)findViewById(R.id.number5_imbtn);
        mNumber6_imbtn=(ImageButton)findViewById(R.id.number6_imbtn);
        mHand_control_imbtn=(ImageButton)findViewById(R.id.hand_control_imbtn);
        mAuto_imbtn=(ImageButton)findViewById(R.id.auto_imbtn);
        mSleep_imbtn=(ImageButton)findViewById(R.id.sleep_imbtn);
        msetclock_et=(EditText)findViewById(R.id.setclock_et);
        msetclock_imbtn=(ImageButton) findViewById(R.id.setclock_imbtn);
        //设置按键监听
        mNumber1_imbtn.setOnClickListener(this);
        mNumber2_imbtn.setOnClickListener(this);
        mNumber3_imbtn.setOnClickListener(this);
        mNumber4_imbtn.setOnClickListener(this);
        mNumber5_imbtn.setOnClickListener(this);
        mNumber6_imbtn.setOnClickListener(this);

        mFan_btn.setOnClickListener(this);
        mReduce_btn.setOnClickListener(this);
        mAdd_btn.setOnClickListener(this);
        msetclock_imbtn.setOnClickListener(this);

        mHand_control_imbtn.setOnClickListener(this);
        mAuto_imbtn.setOnClickListener(this);
        mSleep_imbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.reduce_imbtn){  //判断是否按下减
          //逻辑代码

        } else if(v.getId()==R.id.add_imbtn){ //判断是否按下加
            //逻辑代码
        }else if(v.getId()==R.id.setclock_imbtn){ //设置闹钟
            setclock_val=msetclock_et.getText().toString();  //获取编辑框内容
            int val=Integer.parseInt(setclock_val);  //转成int
            //开启一个定时任务
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //do something

                }
            }, val);    //延时1s执行

        } else if(v.getId()==R.id.fan_btn){
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

        } else if(v.getId()==R.id.number1_imbtn && modeFlag==1){
            resetNumberImage();
            mNumber1_imbtn.setImageResource(R.drawable.ic_number1_click);

            //发送1档指令
            String s1="1";
            BLEManager.getInstance().send(s1.getBytes());

        } else if(v.getId()==R.id.number2_imbtn && modeFlag==1){
            resetNumberImage();
            mNumber2_imbtn.setImageResource(R.drawable.ic_number2_click);

            //发送2档指令
            String  s ="2";
            BLEManager.getInstance().send(s.getBytes());

        }else if(v.getId()==R.id.number3_imbtn && modeFlag==1){
            resetNumberImage();
            mNumber3_imbtn.setImageResource(R.drawable.ic_number3_click);

            //发送3档指令
             String s="3";
              BLEManager.getInstance().send(s.getBytes());

        }else if(v.getId()==R.id.number4_imbtn && modeFlag==1){
            resetNumberImage();
            mNumber4_imbtn.setImageResource(R.drawable.ic_number4_click);

            //发送4档指令
            String s="4";
            BLEManager.getInstance().send(s.getBytes());

        }else if(v.getId()==R.id.number5_imbtn && modeFlag==1){
            resetNumberImage();
            mNumber5_imbtn.setImageResource(R.drawable.ic_number5_click);

            //发送5档指令
             String s="5";
             BLEManager.getInstance().send(s.getBytes());

        }else if(v.getId()==R.id.number6_imbtn && modeFlag==1){
            resetNumberImage();
            mNumber6_imbtn.setImageResource(R.drawable.ic_number6_click);

            //发送6档指令
             String s="6";
             BLEManager.getInstance().send(s.getBytes());

        } else if(v.getId()==R.id.hand_control_imbtn){ //手动控制
            resetModeImage();
            mHand_control_imbtn.setImageResource(R.drawable.ic_hand_control_click);
            modeFlag=1;
            //发送1档指令
             String s="h";
             BLEManager.getInstance().send(s.getBytes());

        } else if(v.getId()==R.id.auto_imbtn){
            resetModeImage();
            resetNumberImage();
            mAuto_imbtn.setImageResource(R.drawable.ic_auto_btn_click);
            modeFlag=2;
            //发送1档指令
            String s="g";
            BLEManager.getInstance().send(s.getBytes());

        } else if(v.getId()==R.id.sleep_imbtn){
            resetModeImage();
            resetNumberImage();
            mSleep_imbtn.setImageResource(R.drawable.ic_sleep_btn_click);
            modeFlag=3;
            //发送1档指令
            // String s="1";
            //  BLEManager.getInstance().send(s.getBytes());

        }




    }

    private void resetModeImage() {
        mHand_control_imbtn.setImageResource(R.drawable.ic_hand_control);
        mAuto_imbtn.setImageResource(R.drawable.ic_auto_btn);
        mSleep_imbtn.setImageResource(R.drawable.ic_sleep_btn);
    }

    private void resetNumberImage() {
        mNumber1_imbtn.setImageResource(R.drawable.ic_number1);
        mNumber2_imbtn.setImageResource(R.drawable.ic_number2);
        mNumber3_imbtn.setImageResource(R.drawable.ic_number3);
        mNumber4_imbtn.setImageResource(R.drawable.ic_number4);
        mNumber5_imbtn.setImageResource(R.drawable.ic_number5);
        mNumber6_imbtn.setImageResource(R.drawable.ic_number6);
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

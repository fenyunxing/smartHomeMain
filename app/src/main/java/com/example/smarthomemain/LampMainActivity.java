package com.example.smarthomemain;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.blesdk.executor.handler.BLEManager;

public class LampMainActivity extends AppCompatActivity implements View.OnClickListener {
ImageButton mLamp_imbtn;
SeekBar mSeekbar;
Button mLamp_exit_btn;
boolean mLampFalg=false;
int mcurrentProgress=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_main);
        initview();
    }

    private void initview() {
        mLamp_imbtn=(ImageButton)findViewById(R.id.lamp_swith_imbtn);
        mSeekbar=(SeekBar)findViewById(R.id.sb_seekBar);
        mLamp_exit_btn=(Button)findViewById(R.id.lamp_exit_btn);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mcurrentProgress=progress;
                Log.i("进度条测试",String.valueOf( mcurrentProgress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String s="?"+String.valueOf( mcurrentProgress);
                BLEManager.getInstance().send(s.getBytes());
            }
        });
        mLamp_imbtn.setOnClickListener(this);
        mLamp_exit_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.lamp_swith_imbtn){
          if(mLampFalg){
              String s="lampclose"  ;
              BLEManager.getInstance().send(s.getBytes());
              mLampFalg=false;
          }else {
            String s="lampopen"  ;
            BLEManager.getInstance().send(s.getBytes());
            mLampFalg=true;
          }

        } else if(v.getId()== R.id.lamp_exit_btn){

           finish();
        }
    }
}

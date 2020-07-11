package com.example.a12053.voicectroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blesdk.executor.handler.BLEManager;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class VoiceCtrollerMainActivity extends Activity implements OnClickListener {

    //存放听写分析结果文本
    private HashMap<String, String> hashMapTexts = new LinkedHashMap<String, String>();
    private Button b_btn;  //初始化控件
    private EditText e_text;
    SpeechRecognizer hearer;  //听写对象

    RecognizerDialog dialog;  //讯飞提示框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicectroller);

        b_btn = (Button) findViewById(R.id.listen_btn);
        e_text = (EditText) findViewById(R.id.content_et);
        b_btn.setOnClickListener(this);
       // voiceInit();


    }

    private void voiceInit() {
        SpeechUtility.createUtility(VoiceCtrollerMainActivity.this, SpeechConstant.APPID + "=5b932d09");

        // 1.创建SpeechRecognizer对象，第2个参数：本地听写时传InitListener
        hearer = SpeechRecognizer.createRecognizer(VoiceCtrollerMainActivity.this, null);
        // 交互动画
        dialog = new RecognizerDialog(VoiceCtrollerMainActivity.this, null);
        // 2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        hearer.setParameter(SpeechConstant.DOMAIN, "iat"); // domain:域名
        hearer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        hearer.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话

        //3.开始听写
        dialog.setListener(new RecognizerDialogListener() {  //设置对话框

            @Override
            public void onResult(RecognizerResult results, boolean isLast) {
                // TODO 自动生成的方法存根
                Log.d("Result", results.getResultString());
                //(1) 解析 json 数据<< 一个一个分析文本 >>
                StringBuffer strBuffer = new StringBuffer();
                try {
                    JSONTokener tokener = new JSONTokener(results.getResultString());
                    Log.i("TAG", "Test" + results.getResultString());
                    Log.i("TAG", "Test" + results.toString());
                    JSONObject joResult = new JSONObject(tokener);

                    JSONArray words = joResult.getJSONArray("ws");
                    for (int i = 0; i < words.length(); i++) {
                        // 转写结果词，默认使用第一个结果
                        JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                        JSONObject obj = items.getJSONObject(0);
                        strBuffer.append(obj.getString("w"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            		String text = strBuffer.toString();
                // (2)读取json结果中的sn字段
                String sn = null;

                try {
                    JSONObject resultJson = new JSONObject(results.getResultString());
                    sn = resultJson.optString("sn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //(3) 解析语音文本<< 将文本叠加成语音分析结果  >>
                hashMapTexts.put(sn, strBuffer.toString());
                StringBuffer resultBuffer = new StringBuffer();  //最后结果
                for (String key : hashMapTexts.keySet()) {
                    resultBuffer.append(hashMapTexts.get(key));
                }
                //Log.i("文字测试",resultBuffer.toString());
                try {
                    String s=resultBuffer.toString().substring(0,resultBuffer.toString().length()-1);
                    BLEManager.getInstance().send(s.getBytes("UTF-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                e_text.setText(resultBuffer.toString());
                e_text.requestFocus();//获取焦点
                e_text.setSelection(resultBuffer.toString().length());//将光标定位到文字最后，以便修改
            }

            @Override
            public void onError(SpeechError error) {
                // TODO 自动生成的方法存根
                error.getPlainDescription(true);
            }
        });
        dialog.show();  //显示对话框
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.listen_btn) {// 语音配置对象初始化
            voiceInit();
           // Log.i("测试",e_text.getText().toString());
        }
    }
}



package com.example.smarthomemain;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridView mGridView;   //MyGridView
    //定义图标数组
    private int[] imageRes = { R.drawable.buletooth, R.drawable.curtain,
            R.drawable.facedoor, R.drawable.fan, R.drawable.humiture3, R.drawable.lamp,
            R.drawable.pic9, R.drawable.pic12, R.drawable.remote,
            R.drawable.tvplay, R.drawable.voice, R.drawable.pic12 };
    //定义标题数组
    private String[] itemName = { "蓝牙 ", "窗帘", "人脸门禁", "风扇", "环境监测",
            "电灯", "心理测评", "生活量表", "基本遥控", "电视", "语音控制", "综合查询" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);  //设置Activity标题不显示
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  //设置全屏显示
        setContentView(R.layout.mian_interface);
        mGridView = (GridView) findViewById(R.id.MyGridView);
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int length = itemName.length;
        for (int i = 0; i < length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImageView", imageRes[i]);
            map.put("ItemTextView", itemName[i]);
            data.add(map);
        }
        //为itme.xml添加适配器
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this,
                data, R.layout.item, new String[] { "ItemImageView","ItemTextView" }, new int[] { R.id.ItemImageView,R.id.ItemTextView });
        mGridView.setAdapter(simpleAdapter);
        //为mGridView添加点击事件监听器
        mGridView.setOnItemClickListener(new GridViewItemOnClick());
    }
    //定义点击事件监听器
    public class GridViewItemOnClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            Toast.makeText(getApplicationContext(), position + "",
                    Toast.LENGTH_SHORT).show();
        }
    }
}


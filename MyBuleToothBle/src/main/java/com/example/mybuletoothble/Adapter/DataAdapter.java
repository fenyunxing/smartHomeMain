package com.example.mybuletoothble.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mybuletoothble.R;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/23.
 */

public class DataAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    public ArrayList<String> arr;

    public DataAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
        arr = new ArrayList<String>();

/*        for (int i = 0; i < 1; i++) { // listview初始化1个子项
            arr.add("欢迎使用盒子助手v1.0.0!\n\r");
        }*/
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arr.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (view == null) {
            view = inflater.inflate(R.layout.item_rev, null);
        }
        final TextView edit = (TextView) view.findViewById(R.id.tv_revData);
        edit.setText(arr.get(position)); // 在重构adapter的时候不至于数据错乱
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (arr.size() > 0) {
                    arr.set(position, edit.getText().toString());
                }
            }
        });
        return view;
    }
}

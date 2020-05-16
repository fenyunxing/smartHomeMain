package com.example.mybuletoothble.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mybuletoothble.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/23.
 */

public class ScanBleAdapter extends BaseAdapter {

    private List<BluetoothDevice> devices = new ArrayList();
    private Context mContext;
    private LayoutInflater mInflator;

    private class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    public ScanBleAdapter(Context paramContext) {
        this.mContext = paramContext;
    }

    @Override
    public int getCount() {
        return this.devices.size();
    }

    @Override
    public Object getItem(int position) {
        return (BluetoothDevice) this.devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String strName = ((BluetoothDevice) this.devices.get(position)).getName();
        String strAddress = ((BluetoothDevice) this.devices.get(position)).getAddress();
        if (convertView == null) {
            convertView = mInflator.from(mContext).inflate(R.layout.item_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (strName != null && strName.length() > 0) {
            viewHolder.deviceName.setText(strName);
        } else {
            viewHolder.deviceName.setText("未知设备");
        }
        viewHolder.deviceAddress.setText(strAddress);

        return convertView;

    }

    /*    public void addDevice(BluetoothDevice device) {
            if(!devices.contains(device)) {
                devices.add(device);
               // Log.d(TAG,"add_device");
            }
        }*/
    public void setDatas(List<BluetoothDevice> paramList) {
        devices.clear();
        devices.addAll(paramList);
    }

    public void addDevice(BluetoothDevice device) {
        if (!devices.contains(device)) {
            devices.add(device);
        }
    }
}

package com.blesdk.executor.thread;

import android.content.Context;
import android.content.Intent;

import com.blesdk.executor.handler.BDBLEHandler;
import com.blesdk.executor.handler.BLEManager;
import com.blesdk.tools.BDMethod;


/**
 * 蓝牙接收组装线程类
 * 负责将放在接收缓存的数据组装成完整命令，并传递给后续解析方法
 *
 * Created by admin on 2016/10/21.
 */
public class revThread extends Thread{

    private byte[] data;
    private byte[] dataTEMP = new byte[300];
    private int begin = 0;
    private boolean stop = false;
    final Intent intent = new Intent(BDBLEHandler.ACTION_DATA_AVAILABLE);
    private MessageTempList mRevMessage = new MessageTempList();
    private Context mContext;


    /**
     * 将接收信息存入接收缓存列表
     * @param parambytes 接收字节信息
     */
    public void setRevTempMsg(byte[] parambytes) {
        mRevMessage.setByteList(parambytes);
    }

    @Override
    public void run() {

        try {
            while (!isStop()) {
                if (mRevMessage.getByteList() != null && mRevMessage.getSize() > 0) {
                    data = mRevMessage.getByteList();
                    /*intent.putExtra(BDBLEHandler.EXTRA_DATA,
                            BDMethod.castBytesToHexString(data));*/
                    intent.putExtra(BDBLEHandler.EXTRA_DATA,
                            new String(data));
                    intent.putExtra(BDBLEHandler.EXTRA_DATA_HEX,
                            BDMethod.castBytesToHexString(data));
                    if(BLEManager.getInstance().bdbleHandler !=null){
                        BLEManager.getInstance().bdbleHandler.mContext.getApplicationContext().sendBroadcast(intent);
                        BLEManager.getInstance().bdbleHandler.onMessageReceivered(data);
                    }
                    mRevMessage.delByteList();
                } else {
                    Thread.currentThread().sleep(50L);
                }
            }
        }catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean isStop(){
        return this.stop;
    }

    /**
     * 停止接收将数据放入接收缓存
     */
    public void stopThread(){
        this.stop = true;
    }

}


package com.blesdk.executor.thread;

import android.content.Intent;

import com.blesdk.executor.handler.BDBLEHandler;
import com.blesdk.executor.handler.BLEManager;
import com.blesdk.tools.BDMethod;


/**
 * 蓝牙发送分包线程类
 * 负责去除发送缓存中的数据，并将发送的内容拆分为20个字节每单位通过蓝牙发送
 *
 * Created by admin on 2016/10/21.
 */
public class sendThread extends Thread{
    private byte[] sendByte;
    private boolean stop = false;
    private int i;
    private int sendByteLen;
    private MessageTempList mSendMessage = new MessageTempList();
    private long sleepTime = 100L;
    final Intent intent = new Intent(BDBLEHandler.ACTION_DATA_AVAILABLE);

    @Override
    public void run() {

        try{
            i=0;
            while (!isStop()) {

                if (mSendMessage.getByteList() != null && mSendMessage.getSize() > 0) {
                    sendByte = mSendMessage.getByteList();
                    sendByteLen = sendByte.length;
                    byte[] arrayOfByte1 = new byte[20];
                    if(sendByteLen < 20){
                        BLEManager.getInstance().bdbleHandler.WriteValue(sendByte);
                        mSendMessage.delByteList();

                        //发送广播///
                        intent.putExtra(BDBLEHandler.EXTRA_DATA,
                                new String(sendByte));
                        intent.putExtra(BDBLEHandler.EXTRA_DATA_HEX,
                                BDMethod.castBytesToHexString(sendByte));
                        if(BLEManager.getInstance().bdbleHandler !=null){
                            BLEManager.getInstance().bdbleHandler.mContext.getApplicationContext().sendBroadcast(intent);
                        }
                        /////
                        Thread.currentThread().sleep(sleepTime);
                    }else{
                        if (i < sendByteLen / 20){
                            for (int j = 0; j < 20; j++){
                                arrayOfByte1[j] = sendByte[(j + i * 20)];
                            }
                            BLEManager.getInstance().bdbleHandler.WriteValue(arrayOfByte1);
                            Thread.currentThread().sleep(sleepTime);
                            i++;
                        }
                        else{
                            byte[] arrayOfByte2 = new byte[sendByte.length - i * 20];
                            for (int k = 0; k < arrayOfByte2.length; k++){
                                arrayOfByte2[k] = sendByte[(k + i * 20)];
                            }
                            BLEManager.getInstance().bdbleHandler.WriteValue(arrayOfByte2);

                            //发送广播///
                            intent.putExtra(BDBLEHandler.EXTRA_DATA,
                                    new String(sendByte));
                            if(BLEManager.getInstance().bdbleHandler !=null){
                                BLEManager.getInstance().bdbleHandler.mContext.getApplicationContext().sendBroadcast(intent);
                            }
                            /////

                            i=0;
                            mSendMessage.delByteList();
                            Thread.currentThread().sleep(sleepTime);
                        }
                    }
                }else{

                    Thread.currentThread().sleep(sleepTime);
                }
            }
        }catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将发送数存入发送缓存列表
     * @param paramByte
     */
    public void setSendTempMsg(byte[] paramByte){
        this.mSendMessage.setByteList(paramByte);
    }
    private boolean isStop(){
        return this.stop;
    }

    /**
     * 停止将发送数据存入发送缓存
     */
    public void stopThread(){
        this.stop = true;
    }
}


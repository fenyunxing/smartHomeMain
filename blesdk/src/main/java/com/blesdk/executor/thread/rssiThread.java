package com.blesdk.executor.thread;


import com.blesdk.executor.handler.BLEManager;

/**
 * 蓝牙BLE rssi值读取线程类
 *
 * Created by admin on 2016/10/21.
 */
public class rssiThread extends Thread{

    private boolean stop = false;


    @Override
    public void run() {

        while (!isStop()) {
            try {
                if(BLEManager.getInstance().bdbleHandler != null){
                    BLEManager.getInstance().bdbleHandler.mBluetoothGatt.readRemoteRssi();
                    Thread.currentThread().sleep(2000L);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private boolean isStop(){
        return this.stop;
    }

    /**
     * 停止读取RSSI
     */
    public void stopThread(){
        this.stop = true;
    }
}

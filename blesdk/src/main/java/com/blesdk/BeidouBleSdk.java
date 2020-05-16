package com.blesdk;

import android.content.Context;
import android.os.AsyncTask;

import com.blesdk.executor.handler.BLEManager;

public class BeidouBleSdk {
    private static BeidouBleSdk beidouSDK;
    private static Context context;

    public static BeidouBleSdk getInstance(Context applicationContext)
    {
        if (beidouSDK == null) {
            beidouSDK = new BeidouBleSdk();
            context = applicationContext;
        }
        return beidouSDK;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        context = context;
    }

    public static BeidouBleSdk getInstance() {
        if (beidouSDK == null) {
            beidouSDK = new BeidouBleSdk();
        }
        return beidouSDK;
    }

    public void startConnectBle()
    {
        new AsyncTask<Void,Void,Boolean>()
        {
            protected Boolean doInBackground(Void[] params)
            {
                BLEManager.getInstance().connectBle(context);
                return Boolean.valueOf(true);
            }
        } .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

}

package com.paymentwall.pandapptest.ui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.paymentwall.pandapptest.config.SharedPreferenceManager;
import com.paymentwall.pwunifiedsdk.brick.core.Brick;

/**
 * Created by nguyen.anh on 4/8/2016.
 */
public class MerchantBackendService extends Service {

    private SharedPreferenceManager preference;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(getPackageName() + Brick.BROADCAST_FILTER_MERCHANT)) {
                Log.i(getClass().getSimpleName(), intent.getStringExtra(Brick.KEY_BRICK_TOKEN));
                processBackend();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        preference = SharedPreferenceManager.getInstance(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(getPackageName() + Brick.BROADCAST_FILTER_MERCHANT));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void processBackend() {
//        int waitingTime = preference.getIntValue(SharedPreferenceManager.PREF_MERCHANT_BACKEND_STATUS) == SharedPreferenceManager.MERCHANT_BACKEND_SUCCESS ? 3000 : (preference.getIntValue(SharedPreferenceManager.PREF_MERCHANT_BACKEND_STATUS) == SharedPreferenceManager.MERCHANT_BACKEND_FAIL ? 3000 : 300000000);
        int waitingTime = 3000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int backendResult = 1; //1 means your processing is successful, 0 is failed
                Intent intent = new Intent();
                intent.setAction(getPackageName() + Brick.BROADCAST_FILTER_SDK);
                intent.putExtra(Brick.KEY_MERCHANT_SUCCESS, backendResult);
                LocalBroadcastManager.getInstance(MerchantBackendService.this).sendBroadcast(intent);
            }
        }, waitingTime);
    }
}

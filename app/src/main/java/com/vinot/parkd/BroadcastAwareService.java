package com.vinot.parkd;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BroadcastAwareService extends Service implements BroadcastAware {

    // todo think about where broadcastDeregistration belongs; in onUnbind or in onDestroy.

    protected static String TAG;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroadcastAwareService.this.onBroadcastReceived(context, intent);
        }
    };
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
        mIntentFilter = getIntentFilter();
        broadcastRegistration(mIntentFilter, mBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        if (mBroadcastReceiver != null) broadcastDeregistration(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mBroadcastReceiver != null) broadcastDeregistration(mBroadcastReceiver);
        return super.onUnbind(intent);
    }

    @Override
    public void broadcastRegistration(IntentFilter intentFilter, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(BroadcastAwareService.this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void broadcastDeregistration(BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    protected abstract void onBroadcastReceived(Context context, Intent intent);
    protected abstract IntentFilter getIntentFilter();
}



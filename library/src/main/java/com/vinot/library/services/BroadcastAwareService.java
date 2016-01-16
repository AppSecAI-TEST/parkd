package com.vinot.library.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.vinot.library.BroadcastAware;

public abstract class BroadcastAwareService extends BaseService
        implements BroadcastAware {

    // todo think about where broadcastDeregistration belongs; in onUnbind or in onDestroy.

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroadcastAwareService.this.onBroadcastReceived(context, intent);
        }
    };
    private IntentFilter mIntentFilter = getIntentFilter();

    @Override
    public void onCreate() {
        super.onCreate();
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

    /**
     * Used to specify those Intents you wish to filter for in local broadcasting
     * @return the IntentFilter used for broadcast filtering.
     */
    protected abstract IntentFilter getIntentFilter();
}

package com.vinot.parkd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BroadcastAwareSessionServiceBoundActivity extends SessionServiceBoundActivity implements BroadcastAware {

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroadcastAwareSessionServiceBoundActivity.this.onBroadcastReceived(context, intent);
        }
    };
    private IntentFilter mIntentFilter;

    @Override
    protected void onStart() {
        super.onStart();
        mIntentFilter = getIntentFilter();
        broadcastRegistration(mIntentFilter, mBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        if (mBroadcastReceiver != null) broadcastDeregistration(mBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void broadcastRegistration(IntentFilter intentFilter, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(BroadcastAwareSessionServiceBoundActivity.this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void broadcastDeregistration(BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    protected abstract void onBroadcastReceived(Context context, Intent intent);
    protected abstract IntentFilter getIntentFilter();

}

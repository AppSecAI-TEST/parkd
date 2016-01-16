package com.vinot.library.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.vinot.library.BroadcastAware;

/**
 * To be extended for automatic awareness of local broadcasts.
 * Broadcast registration occurs onStart, deregistration onStop.  This is a good way to
 * programmatically register and deregister ServiceBoundActivities from local broadcasts
 */
public abstract class BroadcastAwareServiceBoundActivity extends ServiceBoundActivity
        implements BroadcastAware {

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BroadcastAwareServiceBoundActivity.this.onBroadcastReceived(context, intent);
        }
    };
    private IntentFilter mIntentFilter = getIntentFilter();

    @Override
    protected void onStart() {
        super.onStart();
        broadcastRegistration(mIntentFilter, mBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        if (mBroadcastReceiver != null) broadcastDeregistration(mBroadcastReceiver);
        super.onStop();
    }

    @Override
    public void broadcastRegistration(IntentFilter intentFilter, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(BroadcastAwareServiceBoundActivity.this).registerReceiver(broadcastReceiver, intentFilter);
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

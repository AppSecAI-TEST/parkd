package com.vinot.library;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public interface BroadcastAware {
    void broadcastRegistration(IntentFilter intentFilter, BroadcastReceiver broadcastReceiver);
    void broadcastDeregistration(BroadcastReceiver broadcastReceiver);
}

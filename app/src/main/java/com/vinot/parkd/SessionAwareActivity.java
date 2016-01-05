package com.vinot.parkd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Activity to be extended for automatic binding to SessionService
 */
public class SessionAwareActivity extends AppCompatActivity {

    private static String TAG = SessionAwareActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();
        bindService(
                new Intent(this, SessionService.class), mServiceConnection, Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBoundToSessionService) {
            unbindService(mServiceConnection);
            mBoundToSessionService = false;
        }
    }

    // binding
    protected boolean mBoundToSessionService = false;
    protected SessionService mSessionService = null;
    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof SessionService.SessionServiceBinder) {
                mSessionService = ((SessionService.SessionServiceBinder) service).getBoundService();
                Log.d(TAG, "Successfully bound to SessionService");
                mBoundToSessionService = true;
            } else {
                Log.wtf(TAG, new ClassCastException("service IBinder is not an instance of SessionService.HttpServiceBinder"));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundToSessionService = false;
            Log.wtf(TAG, "Unexpected disconnection from SessionService");
        }
    };
}
package com.vinot.parkd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * To be extended for automatic binding to a Service.  See implementations such as
 * {@link SessionAwareActivity} for more hints on implementing.
 *
 * Implementer must provide, on top of that which is required by inheritance,
 * an IBinder to interface with the Service and set it in the override of setBoundService.
 */
public abstract class ServiceBoundActivity extends AppCompatActivity {

    protected static String TAG;

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, getBoundServiceClass()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getLocalClassName();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBoundToService) {
            unbindService(mServiceConnection);
            mBoundToService = false;
        }
    }

    // binding
    protected boolean mBoundToService = false;
    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceBoundActivity.this.onServiceConnected(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            ServiceBoundActivity.this.onServiceDisconnected();
        }
    };

    protected void onServiceConnected(IBinder service) {
        Class<IBinder> toBeCastTo = getBoundServiceBinderClass();
        if (toBeCastTo.isInstance(service)) {
            setBoundService(toBeCastTo.cast(service));
            Log.d(TAG, "Successfully bound to " + service.getClass().getSimpleName());
            mBoundToService = true;
        } else {
            Log.wtf(TAG, new ClassCastException("service IBinder is not an instance of " + service.getClass().getSimpleName() + "'s Binder"));
        }
    }
    protected void onServiceDisconnected() {
        mBoundToService = false;
        Log.wtf(TAG, "Unexpected disconnection from SessionService");
    }

    protected abstract void setBoundService(IBinder iBinder);
    protected abstract Class getBoundServiceClass();
    protected abstract Class getBoundServiceBinderClass();

}

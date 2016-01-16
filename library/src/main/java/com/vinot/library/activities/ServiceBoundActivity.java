package com.vinot.library.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * To be extended for automatic binding to a Service.
 * This class allows for binding to one Service via a Binder object.  Binding will be onStart and
 * unbinding onStop.
 * Generally this class is to be extended by another abstract class to implement those methods
 * required to specify the Service being bound to.
 */
public abstract class ServiceBoundActivity extends AppCompatActivity {

    protected static String TAG;
    protected Binder mServiceBinder = null;
    private boolean mBoundToService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getLocalClassName();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBoundToService) {
            unbindService();
        }
    }

    // binding

    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceBoundActivity.this.onServiceConnected(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            ServiceBoundActivity.this.onServiceDisconnected(name);
        }
    };

    protected void onServiceConnected(IBinder service) {
        Class<IBinder> toBeCastTo = getBoundServiceBinderClass();
        if (toBeCastTo.isInstance(service)) {
            mServiceBinder = (Binder) service;
            Log.d(TAG, "Successfully bound to " + service.getClass().getSimpleName());
            mBoundToService = true;
        } else {
            Log.wtf(TAG, new ClassCastException("service IBinder is not an instance of " + toBeCastTo.getSimpleName()));
        }
    }
    protected void onServiceDisconnected(ComponentName name) {
        mBoundToService = false;
        Log.wtf(TAG, "Unexpected disconnection from " + name.toShortString());
    }

    /**
     * Wrapper around ContextWrapper.bindService to bind automatically to the Service we are supposed
     * to be bound to.
     * @return same boolean that ContextWrapper.bindService returns
     */
    protected boolean bindService() {
        return bindService(new Intent(this, getBoundServiceClass()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Wrapper around unbindService to unbind automatically from the Service we are supposed to be
     * bound to.
     */
    protected void unbindService() {
        unbindService(mServiceConnection);
        mBoundToService = false;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        mBoundToService = false;
    }

    /**
     * Is Activity bound?
     * @return whether or not Activity is bound
     */
    protected final boolean isBoundToService() {
        return mBoundToService;
    }

    /**
     * Obtain the Binder that is being used to interface with the Service we are bound to.
     * Inheriting classes ought to cast the Binder appropriately for their own Binder child class.
     * @return Binder to Service
     */
    protected abstract Binder getBoundService();

    /**
     * To be used by inheriting abstract classes to specify the class of the Service being bound to
     * @return class of Service being bound to
     */
    protected abstract Class getBoundServiceClass();

    /**
     * To be used by inheriting abstract classes to specify the class of Binder being used for
     * binding
     * @return class of Binder
     */
    protected abstract Class getBoundServiceBinderClass();

}

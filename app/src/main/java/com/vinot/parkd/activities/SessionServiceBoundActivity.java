package com.vinot.parkd.activities;

import android.os.IBinder;

import com.vinot.parkd.services.SessionService;

/**
 * To be extended for automatic binding to SessionService
 */
public abstract class SessionServiceBoundActivity extends ServiceBoundActivity {

    protected SessionService.SessionServiceBinder mService = null;

    @Override
    protected final void setBoundService(IBinder iBinder) {
        mService = (SessionService.SessionServiceBinder) iBinder;
    }

    @Override
    protected final Class getBoundServiceClass() {
        return SessionService.class;
    }

    @Override
    protected final Class getBoundServiceBinderClass() {
        return SessionService.SessionServiceBinder.class;
    }
}

package com.vinot.parkd;

import android.os.IBinder;

/**
 * To be extended for automatic binding to SessionService
 */
public class SessionAwareActivity extends ServiceBoundActivity {

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

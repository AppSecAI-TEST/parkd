package com.vinot.parkd.activities;

import com.vinot.parkd.services.SessionService;
import com.vinot.library.activities.BroadcastAwareServiceBoundActivity;

public abstract class BroadcastAwareSessionServiceBoundActivity extends BroadcastAwareServiceBoundActivity {

    @Override
    protected SessionService.SessionServiceBinder getBoundService() {
        return (SessionService.SessionServiceBinder) mServiceBinder;
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

package com.vinot.parkd.activities;

import com.vinot.parkd.services.SessionService;
import com.vinot.library.activities.ServiceBoundActivity;

/**
 * To be extended for automatic binding to SessionService
 */
public abstract class SessionServiceBoundActivity extends ServiceBoundActivity {

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

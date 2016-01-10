package com.vinot.parkd.activities;


import android.os.IBinder;

import com.vinot.parkd.services.PaymentTimerService;

/**
 * To be extended for automatic binding to PaymentTimerService
 */
public abstract class PaymentTimerServiceBoundActivity extends ServiceBoundActivity {

    protected PaymentTimerService.PaymentTimerBinder mService = null;

    @Override
    protected final void setBoundService(IBinder iBinder) {
        mService =  (PaymentTimerService.PaymentTimerBinder) iBinder;
    }

    @Override
    protected final Class getBoundServiceClass() {
        return PaymentTimerService.class;
    }

    @Override
    protected final Class getBoundServiceBinderClass() {
        return PaymentTimerService.PaymentTimerBinder.class;
    }
}

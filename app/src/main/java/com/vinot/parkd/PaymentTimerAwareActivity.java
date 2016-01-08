package com.vinot.parkd;


import android.os.IBinder;

/**
 * To be extended for automatic binding to PaymentTimerService
 */
public class PaymentTimerAwareActivity extends ServiceBoundActivity {

    protected PaymentTimerService.PaymentTimerBinder mService = null;

    @Override
    protected final void setBoundService(IBinder p) {
        mService =  (PaymentTimerService.PaymentTimerBinder) p;
    }

    @Override
    protected final Class getBoundServiceClass() {
        return SessionService.class;
    }

    @Override
    protected final Class getBoundServiceBinderClass() {
        return PaymentTimerService.PaymentTimerBinder.class;
    }
}

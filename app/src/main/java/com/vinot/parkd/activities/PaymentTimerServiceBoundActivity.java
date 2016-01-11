package com.vinot.parkd.activities;

import com.vinot.parkd.services.PaymentTimerService;

/**
 * To be extended for automatic binding to PaymentTimerService
 */
public abstract class PaymentTimerServiceBoundActivity extends ServiceBoundActivity {

    @Override
    protected PaymentTimerService.PaymentTimerBinder getBoundService() {
        return (PaymentTimerService.PaymentTimerBinder) mServiceBinder;
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

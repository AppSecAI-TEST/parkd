package com.vinot.library.services;

import android.app.Service;

public abstract class BaseService extends Service {

    protected static String TAG;

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
    }
}

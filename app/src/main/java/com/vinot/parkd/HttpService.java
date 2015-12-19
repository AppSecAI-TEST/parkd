package com.vinot.parkd;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class HttpService extends Service {

    private final static String TAG = MainActivity.class.getSimpleName();

    IBinder mHttpServiceBinder = new HttpServiceBinder();

    public HttpService() {
    }
    /////////////
    // Binding //
    /////////////
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mHttpServiceBinder;
    }

    public class HttpServiceBinder extends Binder {
        public HttpService getBoundService() { return HttpService.this; }
    }

}

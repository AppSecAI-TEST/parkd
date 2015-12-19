package com.vinot.parkd;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class HttpService extends Service {

    IBinder mHttpServiceBinder = new HttpServiceBinder();

    public HttpService() {
    }

    // binding

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mHttpServiceBinder;
    }

    public class HttpServiceBinder extends Binder {
        public HttpService getBoundService() { return HttpService.this; }
    }

}

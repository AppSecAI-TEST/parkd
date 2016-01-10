package com.vinot.parkd;

import android.app.Application;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;

public class ParkdApplication extends Application {
    @Override
    public void onCreate() {
        if (!Hawk.isBuilt()) {
            final String TAG = "ParkdApplication";
            Hawk.init(this)
                    .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
                    .setStorage(HawkBuilder.newSharedPrefStorage(this))
                    .setLogLevel(LogLevel.FULL)
                    .setCallback(new HawkBuilder.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Hawk built.");
                        }

                        @Override
                        public void onFail(Exception e) {
                            Log.wtf(TAG, e);
                        }
                    }).build();
        }
        super.onCreate();
    }
}

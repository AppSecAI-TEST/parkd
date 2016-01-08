package com.vinot.parkd;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class SplashScreenActivity extends SessionAwareActivity {

    public static final String TAG = SplashScreenActivity.class.getSimpleName();
    private final int SPLASH_TIME_OUT = 2000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        findViewById(R.id.splash).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        final Intent nextActivityIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startActivity(nextActivityIntent);
                        finish();
                    }
                },
                SPLASH_TIME_OUT
        );

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SplashScreenActivity.this.onServiceConnected(service);
                if (mBoundToService) {
                    if (mService.loggedIn()) {
                        // fixme It may take a long time to ascertain whether or not a user is
                        // fixme logged in, so this may be better handled asynchronously.
                        nextActivityIntent.setClass(SplashScreenActivity.this, MainActivity.class);
                    }
                } else {
                    Log.wtf(TAG, "Did not successfully bind to SessionService");
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                SplashScreenActivity.this.onServiceDisconnected();
            }
        };
    }
}

package com.vinot.parkd.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.vinot.parkd.R;

public class SplashScreenActivity extends SessionServiceBoundActivity {

    public static final String TAG = SplashScreenActivity.class.getSimpleName();
    private final int SPLASH_TIME_OUT = 2000; // milliseconds

    private Intent mNextActivityIntent;

    @Override
    protected void onServiceConnected(IBinder service) {
        super.onServiceConnected(service);
        if (isBoundToService()) {
            if (getBoundService().loggedIn()) {
                // fixme It may take a long time to ascertain whether or not a user is
                // fixme logged in, so this may be better handled asynchronously.
                mNextActivityIntent.setClass(SplashScreenActivity.this, MainActivity.class);
            }
        } else {
            Log.wtf(TAG, "Did not successfully bind to SessionService");
        }
    }

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

        mNextActivityIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        startActivity(mNextActivityIntent);
                        finish();
                    }
                },
                SPLASH_TIME_OUT
        );
    }
}
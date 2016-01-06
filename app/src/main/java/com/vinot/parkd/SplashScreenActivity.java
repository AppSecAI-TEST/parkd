package com.vinot.parkd;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SplashScreenActivity extends AppCompatActivity {

    public static final String TAG = SplashScreenActivity.class.getSimpleName();
//    private final int SPLASH_TIME_OUT = 3000; // milliseconds
    private final int SPLASH_TIME_OUT = 3000; // milliseconds

    // manually control how the splash works; this is for debugging
    private final boolean mTimerSplash = true;

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

        if (mTimerSplash) {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            startActivity((new Intent(SplashScreenActivity.this, LoginActivity.class)));
                            finish();
                        }
                    },
                    SPLASH_TIME_OUT
            );
        } else {
            // Code involving connecting to a network, or something more intensive that is
            // not just based on a timer.
        }
    }
}

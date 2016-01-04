package com.vinot.parkd;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 507;
    private static final int RC_ACCESS_NETWORK_STATE = 128;
    private static final int RC_INTERNET = 26;

    private Intent mMainActivityIntent;
    private ConnectivityManager mConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSupportActionBar((Toolbar) findViewById(R.id.login_activity_toolbar));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        // todo implement this onConnectionFailedListener
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mMainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SignInButton signInButton = (SignInButton) findViewById(R.id.activity_login_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new SignInButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetConnected()) {
                    Snackbar.make(
                            findViewById(R.id.login_activity_coordinator_layout),
                            getString(R.string.no_network_connection),
                            Snackbar.LENGTH_LONG
                    ).show();
                    return;
                }
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        getSupportActionBar().setTitle(R.string.activity_login_title);

        bindService(
                new Intent(LoginActivity.this, SessionService.class), mServiceConnection, Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            if (mBound && mSessionService.init(result.getSignInAccount())) {
                startActivity(mMainActivityIntent);
                finish();
            } else {
                Log.wtf(TAG, "Failed to bind instance of SessionService");
            }

        } else {
            Snackbar.make(
                    findViewById(R.id.login_activity_coordinator_layout), R.string.activity_login_failed_login, Snackbar.LENGTH_LONG
            ).show();
        }
    }

    // binding
    private boolean mBound = false;
    private SessionService mSessionService = null;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof SessionService.SessionServiceBinder) {
                mSessionService = ((SessionService.SessionServiceBinder) service).getBoundService();
                Log.d(TAG, "Successfully bound to SessionService");
                mBound = true;
            } else {
                Log.wtf(TAG, new ClassCastException("service IBinder is not an instance of SessionService.HttpServiceBinder"));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Log.wtf(TAG, "Unexpected disconnection from SessionService");
        }
    };

    private boolean isInternetConnected() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    RC_ACCESS_NETWORK_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    RC_ACCESS_NETWORK_STATE);
        }

        NetworkInfo networkInfo =  mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

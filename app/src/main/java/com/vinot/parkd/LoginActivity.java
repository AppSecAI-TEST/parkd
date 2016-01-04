package com.vinot.parkd;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
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

    private Intent mMainActivityIntent;
    private Snackbar mSnackbar;
    private BroadcastReceiver mBroadcastReceiver;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;

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
        mSnackbar = Snackbar.make(
                findViewById(R.id.login_activity_coordinator_layout),
                getString(R.string.no_network_connection),
                Snackbar.LENGTH_INDEFINITE
        );
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo =  mConnectivityManager.getActiveNetworkInfo();

        broadcastRegistation();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SignInButton signInButton = (SignInButton) findViewById(R.id.activity_login_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new SignInButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        getSupportActionBar().setTitle(R.string.activity_login_title);

        if (!isInternetConnected() && !mSnackbar.isShown()) {
            mSnackbar.show();
        }

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
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            if (mBound) {
                mSessionService.init(result.getSignInAccount());
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

    // broadcasting
    private void broadcastRegistation() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "ConnectivityManager.CONNECTIVITY_ACTION");
                if (mBound) {
                    if (!isInternetConnected() && !mSnackbar.isShown()) {
                        mSnackbar.show();
                    } else if (isInternetConnected() && mSnackbar.isShown()) {
                        mSnackbar.dismiss();
                    }
                } else {
                    Log.wtf(TAG, "Not bound to SessionService");
                }

                if (mSnackbar.isShown()) Log.d(TAG, "SHOWN");
                else Log.d(TAG, "DISMISSED");
            }
        };

        registerReceiver(mBroadcastReceiver, intentFilter);
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
        return mNetworkInfo != null && mNetworkInfo.isConnected();
    }
}

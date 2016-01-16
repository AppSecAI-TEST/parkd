package com.vinot.parkd.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vinot.parkd.R;
import com.vinot.parkd.services.SessionService;

public class LoginActivity extends BroadcastAwareSessionServiceBoundActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 507;
    private static final int RC_ACCESS_NETWORK_STATE = 128;
    private static final int RC_INTERNET = 26;

    private Intent mMainActivityIntent;
    private ConnectivityManager mConnectivityManager;
    private GoogleApiClient mGoogleApiClient;
    private Snackbar mLoginFailedSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSupportActionBar((Toolbar) findViewById(R.id.login_activity_toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.activity_login_title);

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
        mLoginFailedSnackbar = Snackbar.make(
                findViewById(R.id.login_activity_coordinator_layout), R.string.activity_login_failed_login, Snackbar.LENGTH_LONG
        );

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    @Override
    public IntentFilter getIntentFilter() {
        return new IntentFilter(SessionService.ACTION_LOGIN);
    }

    @Override
    public void onBroadcastReceived(Context context, Intent intent) {
        if (isBoundToService()) {
            if (intent.getBooleanExtra(SessionService.EXTRA_LOGIN_SUCCESS, false)) {
                startActivity(mMainActivityIntent);
                finish();
            } else {
                if (!mLoginFailedSnackbar.isShown()) mLoginFailedSnackbar.show();
            }
        } else {
            Log.wtf(TAG, new IllegalStateException("Not bound to SessionService"));
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            if (isBoundToService()) {
                getBoundService().login(result.getSignInAccount());
            } else {
                Log.wtf(TAG, new IllegalStateException("Failed to bind instance of SessionService"));
            }
        } else {
            Log.d(TAG, "Login failed because GoogleSignInResult.isSuccess() was false");
            if (!mLoginFailedSnackbar.isShown()) mLoginFailedSnackbar.show();
        }
    }

    private boolean isInternetConnected() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    RC_ACCESS_NETWORK_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    RC_INTERNET);
        }

        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}

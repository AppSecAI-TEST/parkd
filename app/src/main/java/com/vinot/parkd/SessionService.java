package com.vinot.parkd;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.LinkedHashMap;


public class SessionService extends Service {

    public static final String ACTION_LOGIN = SessionService.class.getCanonicalName() + ".ACTION_LOGIN";
    public static final String EXTRA_LOGIN_SUCCESS = SessionService.class.getCanonicalName() + ".EXTRA_LOGIN_SUCCESS";

    IBinder mSessionServiceBinder = new SessionServiceBinder();

    private SharedPreferences mSession;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mLoggingIn = false;

    @Override
    public boolean onUnbind(Intent intent) {
        if (mBroadcastReceiver != null) unregisterReceiver(mBroadcastReceiver);
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        mSession = getSharedPreferences(
                getString(R.string.sharedpreferences_session), Context.MODE_PRIVATE
        );
        broadcastRegistation();
        return mSessionServiceBinder;
    }

    public class SessionServiceBinder extends Binder {
        public SessionService getBoundService() {
            return SessionService.this;
        }
    }

    /**
     * Send user ID token to the Park'd server for validation.
     *
     * @param acct GoogleSignInAccount obtained after a successful Google login
     */
    public void init(GoogleSignInAccount acct) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("idToken", acct.getIdToken());

        mLoggingIn = true;

        new PostRequester(
                getString(R.string.url_authenticate_user), params
        ).performPostRequest();
    }

    /**
     * Once we know that we are authenticated with the Park'd server we can commit the session
     * information to local storage (SharedPreferences).
     */
    public void cacheLogin(GoogleSignInAccount acct) {
        mSession.edit().putString(
                getString(R.string.sharedpreferences_session_idtoken), acct.getIdToken()
        ).apply();
        // todo further local storage logic (username, email, etc.)
    }

    // broadcasting
    private void broadcastRegistation() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PostRequester.ACTION_POST_COMPLETED);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            /**
             * If we are already in the process of logging in, then pass on a rebranded version of
             * the PostRequester.ACTION_POST_COMPLETED intent.
             */
            public void onReceive(Context context, Intent intent) {
                if (mLoggingIn) {
                    Intent loginIntent = new Intent(ACTION_LOGIN);
                    loginIntent.putExtra(EXTRA_LOGIN_SUCCESS, intent.getBooleanExtra(PostRequester.EXTRA_SUCCESS, false));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(loginIntent);
                    mLoggingIn = false;
                }
            }
        };

        registerReceiver(mBroadcastReceiver, intentFilter);
    }
}
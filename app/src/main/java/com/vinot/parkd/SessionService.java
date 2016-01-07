package com.vinot.parkd;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;

import java.util.LinkedHashMap;

public class SessionService extends Service {

    public static final String TAG = SessionService.class.getSimpleName();
    public static final String ACTION_LOGIN = SessionService.class.getCanonicalName() + ".ACTION_LOGIN";
    public static final String ACTION_POST_REQUEST = SessionService.class.getCanonicalName() + ".ACTION_LOGIN";
    public static final String EXTRA_LOGIN_SUCCESS = SessionService.class.getCanonicalName() + ".EXTRA_LOGIN_SUCCESS";
    public static final String EXTRA_URL = SessionService.class.getCanonicalName() + ".EXTRA_URL";
    public static final String EXTRA_PARAMS = SessionService.class.getCanonicalName() + ".EXTRA_PARAMS";

    IBinder mSessionServiceBinder = new SessionServiceBinder();

    private BroadcastReceiver mBroadcastReceiver;
    private boolean mLoggingIn = false;
    private GoogleSignInAccount mGoogleSignInAccount;

    @Override
    public boolean onUnbind(Intent intent) {
        if (mBroadcastReceiver != null) LocalBroadcastManager.getInstance(SessionService.this).unregisterReceiver(mBroadcastReceiver);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!Hawk.isBuilt()) {
            initHawk(this);
        }
        broadcastRegistation();
    }

    @Override
    public IBinder onBind(Intent intent) { return mSessionServiceBinder; }

    public void cacheLogin() throws Exception {
        Log.d(TAG, "Caching login and session details in local storage");
        Hawk.put(getString(R.string.hawk_session_idtoken), mGoogleSignInAccount.getIdToken());
        Hawk.put(getString(R.string.hawk_session_logged_in), true);
        // todo further local storage logic (username, email, etc.)
        // todo use CookieManager for something similar to this
    }

    public class SessionServiceBinder extends Binder {
        public SessionService getBoundService() { return SessionService.this; }
    }

    /**
     * Send user ID token to the Park'd server for validation.
     *
     * @param acct GoogleSignInAccount obtained after a successful Google login
     */
    public void init(GoogleSignInAccount acct) {
        mGoogleSignInAccount = acct;

        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("idToken", mGoogleSignInAccount.getIdToken());

        mLoggingIn = true;

        Intent postRequesterIntent = new Intent(SessionService.this, PostRequesterService.class);
        postRequesterIntent.setAction(ACTION_POST_REQUEST);
        postRequesterIntent.putExtra(EXTRA_PARAMS, params);
        postRequesterIntent.putExtra(EXTRA_URL, getString(R.string.url_authenticate_user));
        startService(postRequesterIntent);
    }

    public boolean loggedIn() {
        // todo this should be logic affirming that we are authenticated with the server.
        // todo it will perform a check with the server that we are logged in, rather than relying
        // todo on cached information.
        return Hawk.get(getString(R.string.hawk_session_logged_in), false);
    }

    // broadcasting
    private void broadcastRegistation() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PostRequesterService.ACTION_POST_COMPLETED);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            /**
             * If we are already in the process of logging in, then pass on a rebranded version of
             * the PostRequesterService.ACTION_POST_COMPLETED intent.
             */
            public void onReceive(Context context, Intent intent) {
                boolean success;
                if (mLoggingIn) {
                    if (success = intent.getBooleanExtra(PostRequesterService.EXTRA_SUCCESS, false)) {
                        try {
                            cacheLogin();
                        } catch (Exception e) {
                            Log.wtf(TAG, "Unable to save session information to local storage");
                        }
                    }
                    Intent loginIntent = new Intent(ACTION_LOGIN);
                    loginIntent.putExtra(EXTRA_LOGIN_SUCCESS, success);
                    LocalBroadcastManager.getInstance(SessionService.this).sendBroadcast(loginIntent);
                    mLoggingIn = false;
                }
            }
        };

        LocalBroadcastManager.getInstance(SessionService.this).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public static void initHawk(Context context) {
        Hawk.init(context)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.NO_ENCRYPTION)
                .setStorage(HawkBuilder.newSharedPrefStorage(context))
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
}
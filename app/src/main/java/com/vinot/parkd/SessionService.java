package com.vinot.parkd;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SessionService extends Service {

    private final static String TAG = SessionService.class.getSimpleName();
    IBinder mSessionServiceBinder = new SessionServiceBinder();

    private SharedPreferences mSession;


    public SessionService() {}

    @Override
    public IBinder onBind(Intent intent) {
        mSession = getSharedPreferences(
                getString(R.string.sharedpreferences_session), Context.MODE_PRIVATE
        );
        return mSessionServiceBinder;
    }

    public class SessionServiceBinder extends Binder {
        public SessionService getBoundService() { return SessionService.this; }
    }

    /**
     * Initialise a session in the app, storing appropriate user and session credentials in
     * SharedPreferences.
     *
     * Send the token to the server for validation.  This function *should* be blocking, as we
     * don't want the user proceeding until they login successfully.  Should it take a long time, we
     * need to implement a spinning animation to indicate loading.
     *
     * @param acct GoogleSignInAccount obtained after a successful Google login
     */
    public void init(GoogleSignInAccount acct) {
        mSession.edit().putString(
                getString(R.string.sharedpreferences_session_idtoken), acct.getIdToken()
        ).apply();
    }
}
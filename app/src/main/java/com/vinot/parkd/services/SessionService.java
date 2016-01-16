package com.vinot.parkd.services;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.orhanobut.hawk.Hawk;
import com.vinot.parkd.Location;
import com.vinot.parkd.R;
import com.vinot.library.services.BroadcastAwareService;

import java.util.LinkedHashMap;

public class SessionService extends BroadcastAwareService {

    public static final String ACTION_LOGIN = SessionService.class.getCanonicalName() + ".ACTION_LOGIN";
    public static final String ACTION_POST_REQUEST = SessionService.class.getCanonicalName() + ".ACTION_LOGIN";
    public static final String EXTRA_LOGIN_SUCCESS = SessionService.class.getCanonicalName() + ".EXTRA_LOGIN_SUCCESS";
    public static final String EXTRA_URL = SessionService.class.getCanonicalName() + ".EXTRA_URL";
    public static final String EXTRA_PARAMS = SessionService.class.getCanonicalName() + ".EXTRA_PARAMS";

    IBinder mSessionServiceBinder = new SessionServiceBinder();

    private boolean mLoggingIn = false;
    private GoogleSignInAccount mGoogleSignInAccount;

    /**
     * If we are already in the process of logging in, then pass on a rebranded version of
     * the PostRequesterService.ACTION_POST_COMPLETED intent.
     */
    @Override
    protected void onBroadcastReceived(Context context, Intent intent) {

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

    @Override
    protected IntentFilter getIntentFilter() {
        return new IntentFilter(PostRequesterService.ACTION_POST_COMPLETED);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSessionServiceBinder;
    }

    private void cacheLogin() throws Exception {
        Log.d(TAG, "Caching login and session details in local storage");
        Hawk.put(getString(R.string.hawk_session_idtoken), mGoogleSignInAccount.getIdToken());
        Hawk.put(getString(R.string.hawk_session_logged_in), true);
        // todo further local storage logic (username, email, etc.)
        // todo use CookieManager for something similar to this
    }

    public class SessionServiceBinder extends Binder {

        private Location mLocation;

        /**
         * Set the Location that the logged in user has paid for and put time on.
         */
        public void setParkedLocation(Location location) {
            if (Hawk.isBuilt()) {
                Hawk.put(getString(R.string.hawk_currently_parked_location), location);
            } else {
                Log.wtf(TAG, "Hawk is not built");
            }
        }

        /**
         * Retrieve the Location that the logged in user has paid for and put time on.
         */
        public Location getParkedLocation() {
            if (mLocation == null) {
                if (Hawk.isBuilt())
                    mLocation = Hawk.get(getString(R.string.hawk_currently_parked_location));
                else
                    Log.wtf(TAG, getString(R.string.hawk_not_built));
            }
            return mLocation;
        }

        /**
         * Is there a user currently logged in?
         *
         * @return true if a user is logged in, otherwise false
         */
        public boolean loggedIn() {
            // todo this should be logic affirming that we are authenticated with the server.
            // todo it will perform a check with the server that we are logged in, rather than relying
            // todo on cached information.
            return Hawk.get(getString(R.string.hawk_session_logged_in), false);
        }

        /**
         * Login to the park'd server
         *
         * @param googleSignInAccount the account to use for authenticating with the park'd server
         */
        public void login(GoogleSignInAccount googleSignInAccount) {
            mGoogleSignInAccount = googleSignInAccount;

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("idToken", mGoogleSignInAccount.getIdToken());

            mLoggingIn = true;

            Intent postRequesterIntent = new Intent(SessionService.this, PostRequesterService.class);
            postRequesterIntent.setAction(ACTION_POST_REQUEST);
            postRequesterIntent.putExtra(EXTRA_PARAMS, params);
            postRequesterIntent.putExtra(EXTRA_URL, getString(R.string.url_authenticate_user));
            startService(postRequesterIntent);
        }
    }

}
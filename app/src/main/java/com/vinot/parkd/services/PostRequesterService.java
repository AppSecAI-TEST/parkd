package com.vinot.parkd.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tpor9095.library.services.BaseService;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class PostRequesterService extends BaseService {

    public static final String ACTION_POST_COMPLETED = PostRequesterService.class.getCanonicalName() + ".ACTION_POST_COMPLETED";
    public static final String EXTRA_SUCCESS = PostRequesterService.class.getCanonicalName() + ".EXTRA_SUCCESS";

    private URL mUrl;
    private Map<String, Object> mParams;

    /**
     * Produce byte array to be written to the output stream of a HttpUrlConnection, to perform
     * a POST request.
     *
     * @param params key-value map to be encoded
     * @return byte[] to be written
     * @throws UnsupportedEncodingException
     * @link http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
     */
    public static byte[] producePostRequestBytes(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString().getBytes("UTF-8");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(SessionService.ACTION_POST_REQUEST)) {
            try {
                mUrl = new URL(intent.getStringExtra(SessionService.EXTRA_URL));
                mParams = (Map) intent.getSerializableExtra(SessionService.EXTRA_PARAMS);
            } catch (MalformedURLException | ClassCastException e) {
                Log.wtf(TAG, e);
            }

            new PerformPostTask().execute();
        } else {
            Log.wtf(TAG, "PostRequesterService started with wrong action in intent.");
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    private class PerformPostTask extends AsyncTask<Void, Void, Boolean> {
        private final int READ_TIMEOUT = 10000; /* milliseconds */
        private final int CONNECT_TIMEOUT = 15000; /* milliseconds */

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            try {
                byte[] postDataBytes = producePostRequestBytes(mParams);

                // todo the logic below is dependent on the server functioning as it should
                /*
                conn = (HttpURLConnection) mUrl.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setChunkedStreamingMode(0);
                conn.setRequestMethod("POST");
//            conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Log.d(TAG, PostRequesterService.this.getString(R.string.http_response, mUrl.toString(), conn.getResponseCode()));
                Log.d(TAG, conn.getInputStream().toString());
                */

                // todo logic for getting the server's response after the post request
                // todo this might involve CookieManager

                return true;
            } catch (java.io.IOException e) {
                Log.wtf(TAG, e);
            } finally {
                if (conn != null) conn.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean postResult) {
            super.onPostExecute(postResult);
            Intent intent = new Intent(ACTION_POST_COMPLETED);
            intent.putExtra(EXTRA_SUCCESS, postResult);
            LocalBroadcastManager.getInstance(PostRequesterService.this).sendBroadcast(intent);

            stopSelf();
        }
    }
}

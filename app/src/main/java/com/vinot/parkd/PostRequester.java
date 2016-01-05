package com.vinot.parkd;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class PostRequester extends Activity {

    private static final String TAG = PostRequester.class.getSimpleName();

    public static final String ACTION_POST_COMPLETED = PostRequester.class.getCanonicalName() + ".ACTION_POST_COMPLETED";
    public static final String EXTRA_SUCCESS = PostRequester.class.getCanonicalName() + ".EXTRA_SUCCESS";

    private final int READ_TIMEOUT = 10000; /* milliseconds */
    private final int CONNECT_TIMEOUT = 15000; /* milliseconds */

    private URL mUrl;
    private Map<String, Object> mParams;

    public PostRequester(String url, Map<String, Object> params) {
        try {
            this.mUrl = new URL(url);
            this.mParams = params;
        } catch (IOException e) { Log.wtf(TAG, e); }
    }

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

    /**
     * Perform the a post request with this PostRequester's URL and POST parameters.
     *
     * When the post is completed, fire off a broadcast indicating success or failure.
     */
    public void performPostRequest() {
        HttpURLConnection conn = null;
        Intent intent = new Intent(ACTION_POST_COMPLETED);
        try {
            byte[] postDataBytes = producePostRequestBytes(mParams);

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

            Log.d(TAG, getString(R.string.http_response, mUrl.toString(), conn.getResponseCode()));

            Log.d(TAG, conn.getInputStream().toString());

            // todo logic for getting the server's response after the post request
            // todo this might involve CookieManager

            intent.putExtra(EXTRA_SUCCESS, true);

        } catch (java.io.IOException e) {
            Log.wtf(TAG, e);
        } finally {
            if (conn != null) conn.disconnect();
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}

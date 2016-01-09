package com.vinot.parkd;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PaymentTimerService extends Service {

    public static final String ACTION_PAYMENT_FAIL = "com.vinot.parkd.PaymentTimerService.ACTION_PAYMENT_FAIL";
    public static final String ACTION_PAYMENT_SUCCESS = "com.vinot.parkd.PaymentTimerService.ACTION_PAYMENT_SUCCESS";
    public static final String ACTION_PAYMENT = PaymentTimerService.class.getCanonicalName() + ".ACTION_PAYMENT";
    private static final int NOTIFICATION_PAYMENT_SERVICE = 282;

    private static final String TAG = "PaymentTimerService";
    private IBinder mBinder = new PaymentTimerBinder();
    private Location TESTING_LOCATION;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting " + PaymentTimerService.class.getSimpleName());

        if (intent.getAction() != null && intent.getAction().equals(ACTION_PAYMENT)) {
            float price = intent.getFloatExtra(LocationActivity.EXTRA_PRICE, 0f);
            if (price <= 0f) {
                Log.wtf(TAG, "Price input for PaymentTimerService is equal to zero.  Expected non-zero");
                stopSelf();
            } else {
                (new PaymentTask()).execute(price);
                TESTING_LOCATION = intent.getParcelableExtra(LocationActivity.EXTRA_LOCATION);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public class PaymentTimerBinder extends Binder {
        // todo all of this binder's logic
        public int getHour() {
            return 13;
        }

        public int getMinute() {
            return 37;
        }

        public boolean hasTimeRemaining() {
            return false;
        }

        public Location getLocation() {
            return TESTING_LOCATION;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void sendToForeground() {
        Intent timerActivityIntent = new Intent(this, TimerActivity.class);
        PendingIntent timerActivityPendingIntent = PendingIntent.getActivity(
                this, 0, timerActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        // todo set the backstack correctly
        NotificationCompat.Builder b = (new NotificationCompat.Builder(this))
                .setSmallIcon(R.mipmap.ic_launcher_1)
                .setContentTitle(getString(R.string.service_time_notification_title))
                .setContentText("Swag YOLO")
                .setContentIntent(timerActivityPendingIntent)
                .setPriority(Notification.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_PAYMENT_SERVICE, b.build());
    }

    private class PaymentTask extends AsyncTask<Float, Void, Boolean> {
        @Override // todo this
        protected Boolean doInBackground(Float... price) {
            return performPayment(price[0]);
        }

        @Override
        protected void onPostExecute(Boolean paymentWasSuccessful) {
            super.onPostExecute(paymentWasSuccessful);

            if (paymentWasSuccessful) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                stopSelf();
                                Log.d(TAG, "stopping...");
                            }
                        } catch (Exception e) {
                            Log.wtf(TAG, e);
                        }
                    }
                }, 5000);
                sendToForeground();
            } else {
                Log.wtf(TAG, "Payment failed");
            }
        }

        private boolean performPayment(float price) {
            return true;
        }
    }
}

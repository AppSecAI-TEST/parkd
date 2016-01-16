package com.vinot.parkd.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.tpor9095.library.services.BaseService;
import com.vinot.parkd.Location;
import com.vinot.parkd.activities.LocationActivity;
import com.vinot.parkd.R;
import com.vinot.parkd.activities.TimerActivity;

public class PaymentTimerService extends BaseService {

    public static final String ACTION_PAYMENT_FAIL = "com.vinot.parkd.services.PaymentTimerService.ACTION_PAYMENT_FAIL";
    public static final String ACTION_PAYMENT_SUCCESS = "com.vinot.parkd.services.PaymentTimerService.ACTION_PAYMENT_SUCCESS";

    public static final String ACTION_PAYMENT = PaymentTimerService.class.getCanonicalName() + ".ACTION_PAYMENT";
    public static final String EXTRA_PENDING_INTENT = PaymentTimerService.class.getCanonicalName() + ".EXTRA_PENDING_INTENT";
    public static final String EXTRA_HOUR = PaymentTimerService.class.getCanonicalName() + ".EXTRA_HOUR";
    public static final String EXTRA_MINUTE = PaymentTimerService.class.getCanonicalName() + ".EXTRA_MINUTE";
    public static final String EXTRA_LOCATION = PaymentTimerService.class.getCanonicalName() + ".EXTRA_LOCATION";
    public static final String EXTRA_PRICE = PaymentTimerService.class.getCanonicalName() + ".EXTRA_PRICE";

    private static final int NOTIFICATION_PAYMENT_SERVICE = 282;

    private IBinder mBinder = new PaymentTimerBinder();
    private Location mLocation;
    private PaymentTimer mPaymentTimer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting " + PaymentTimerService.class.getSimpleName());

        if (intent.getAction() != null && intent.getAction().equals(ACTION_PAYMENT)) {
            float price = intent.getFloatExtra(LocationActivity.EXTRA_PRICE, 0f);
            (new PaymentTask(intent)).execute(price);
        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class PaymentTimerBinder extends Binder {
        public int getHour() throws NullPointerException {
            if (mPaymentTimer != null)
                return mPaymentTimer.getHour();
            throw new NullPointerException("PaymentTimer is null");
        }

        public int getMinute() throws NullPointerException {
            if (mPaymentTimer != null)
                return mPaymentTimer.getMinute();
            throw new NullPointerException("PaymentTimer is null");
        }

        public boolean hasTimeRemaining() throws NullPointerException {
            if (mPaymentTimer != null)
                return mPaymentTimer.mMillisUntilFinished > 0;
            throw new NullPointerException("PaymentTimer is null");
        }

        public Location getLocation() {
            if (mLocation == null) {
                if (Hawk.isBuilt())
                    mLocation = Hawk.get(getString(R.string.hawk_currently_parked_location));
                else
                    Log.wtf(TAG, getString(R.string.hawk_not_built));
            }
            return mLocation;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Store the paid for Location for which the clock is running, and put it in a class member
     * variable.
     */
    private void setLocation(Location location) {
        if (Hawk.isBuilt()) {
            Hawk.put(getString(R.string.hawk_currently_parked_location), location);
        } else {
            Log.wtf(TAG, "Hawk is not built");
        }
    }

    private class PaymentTask extends AsyncTask<Float, Void, Boolean> {
        private PendingIntent mPendingIntent;
        private Location mLocation;
        private int mHour, mMinute;

        protected PaymentTask(final Intent intent) {
            super();
            this.mPendingIntent = intent.getParcelableExtra(EXTRA_PENDING_INTENT);
            this.mLocation = intent.getParcelableExtra(EXTRA_LOCATION);
            this.mHour = intent.getIntExtra(EXTRA_HOUR, 0);
            this.mMinute = intent.getIntExtra(EXTRA_MINUTE, 0);
        }

        @Override // todo this
        protected Boolean doInBackground(Float... price) {
            return performPayment(price[0]);
        }

        @Override
        protected void onPostExecute(Boolean paymentWasSuccessful) {
            super.onPostExecute(paymentWasSuccessful);

            if (paymentWasSuccessful) {
                PaymentTimerService.this.setLocation(this.mLocation);

                mPaymentTimer = new PaymentTimer((mHour * 60 + mMinute) * PaymentTimer.MINUTE, PaymentTimer.MINUTE);
                mPaymentTimer.start();

                if (mPendingIntent != null) {
                    try {
                        mPendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        Log.wtf(TAG, e);
                    }
                }
            } else {
                Log.wtf(TAG, "Payment failed");
            }
        }

        private boolean performPayment(float price) {
            return true;
        }
    }

    /**
     * As this class is written, it entirely controls whether the service comes to the foreground
     * and performs all of the notification updates
     */
    private class PaymentTimer extends CountDownTimer {
        public static final long HOUR = 3600000;
        public static final long MINUTE = 60000;
        private long mMillisUntilFinished;
        private NotificationManagerCompat nm;
        private int mTotalTime;
        private NotificationCompat.Builder mNotificationBuilder;

        public PaymentTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

            Intent timerActivityIntent = new Intent(PaymentTimerService.this, TimerActivity.class);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(PaymentTimerService.this);
            taskStackBuilder
                    .addParentStack(TimerActivity.class)
                    .addNextIntent(timerActivityIntent);
            PendingIntent timerActivityPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotificationBuilder = new NotificationCompat.Builder(PaymentTimerService.this)
                    .setSmallIcon(R.mipmap.ic_launcher_1)
                    .setContentTitle(getString(R.string.service_time_notification_title))
                    .setContentIntent(timerActivityPendingIntent)
                    .setPriority(Notification.PRIORITY_DEFAULT);

            mTotalTime = (int) millisInFuture;
            mMillisUntilFinished = millisInFuture;
            startForeground(NOTIFICATION_PAYMENT_SERVICE, getOngoingNotification(false));
            nm = NotificationManagerCompat.from(PaymentTimerService.this);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            this.mMillisUntilFinished = millisUntilFinished;
            nm.notify(NOTIFICATION_PAYMENT_SERVICE, getOngoingNotification(false));
        }

        public int getMinute() {
            return (int) ((this.mMillisUntilFinished % HOUR) / MINUTE);
        }

        public int getHour() {
            return (int) (this.mMillisUntilFinished / HOUR);
        }

        @Override
        public void onFinish() {
            stopForeground(false);
            nm.notify(NOTIFICATION_PAYMENT_SERVICE, getOngoingNotification(true));
//            PaymentTimerService.this.stopSelf(); // will not actually stop until activity unbinds
            // all activities unbind, the service will stop
            // todo stop the service more deliberately
        }

        private Notification getOngoingNotification(boolean finishing) {
            if (finishing) {
                return mNotificationBuilder
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setProgress(100, 100, false)
                        .setContentText("Time's up!")
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .build();
            }
            return mNotificationBuilder
                    .setProgress(mTotalTime, 0, false)
                    .setContentText(getString(R.string.payment_time_notification, getHour(), getMinute()))
                    .build();

        }
    }
}

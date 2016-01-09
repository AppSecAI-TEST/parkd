package com.vinot.parkd;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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

    private static final String TAG = PaymentTimerService.class.getSimpleName();
    private IBinder mBinder = new PaymentTimerBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting " + PaymentTimerService.class.getSimpleName());
        Intent paymentActivityIntent = new Intent(this, TimerActivity.class);
        PendingIntent paymentActivityPendingIntent = PendingIntent.getActivity(
                this, 0, paymentActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        // todo set the backstack correctly

        NotificationCompat.Builder b = (new NotificationCompat.Builder(this))
                .setSmallIcon(R.mipmap.ic_launcher_1)
                .setContentTitle(getString(R.string.service_time_notification_title))
                .setContentText("Swag YOLO")
                .setContentIntent(paymentActivityPendingIntent)
                .setPriority(Notification.PRIORITY_DEFAULT);

        startForeground(NOTIFICATION_PAYMENT_SERVICE, b.build());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        stopSelf();
                        Log.d("PaymentTimerService", "stopping...");
                    }
                } catch (Exception e) {
                }
            }
        }, 5000);
        return super.onStartCommand(intent, flags, startId);
    }

    public class PaymentTimerBinder extends Binder {
        public int getHour() {return -1;}
        public int getMinute() {return -1;}
        public boolean hasTimeRemaining() { return false; }
        public Location getLocation() { return null; }
    }

    @Override
    public IBinder onBind(Intent intent) { return mBinder; }
}

package com.vinot.parkd;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Including logic to finalise payment, based upon the incoming Intent and the EXTRA_PRICE that
 * comes with it.
 */
public class PaymentActivity extends SessionAwareActivity {

    public static final String ACTION_PAYMENT = LocationActivity.class.getCanonicalName() + ".ACTION_PAYMENT";
    private Location mLocation;
    private int mHour, mMinute;
    private TimePicker mTimePicker = null;

    private static final int MINUTE = 60000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setSupportActionBar((Toolbar) findViewById(R.id.payment_activity_toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTimePicker = (TimePicker) findViewById(R.id.activity_payment_timepicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setEnabled(false);

        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_PAYMENT)) {
            // We have arrived at PaymentActivity with a request to pay
            Snackbar.make(
                    findViewById(R.id.payment_activity_coordinator_layout), R.string.activity_payment_success, Snackbar.LENGTH_LONG
            ).show(); // todo perform actual payment in service or async task somewhere

            mLocation = getIntent().getParcelableExtra(LocationActivity.EXTRA_LOCATION);
            mHour = getIntent().getIntExtra(LocationActivity.EXTRA_HOUR, 0);
            mMinute = getIntent().getIntExtra(LocationActivity.EXTRA_MINUTE, 0);

            updateUserInterface(mLocation, mHour, mMinute);

            startService(new Intent(this, PaymentTimerService.class));
        } else {
            // 1. There is an existing payment running with associated timer OR
            // 2. There is an existing payment that has expired OR
            // 3. There is no existing payment
        }
    }

    private void updateUserInterface(final Location location, final int hour, final int minute) {
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);

        CountDownTimer countDownTimer = new CountDownTimer((hour * 60 + minute) * MINUTE, MINUTE) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mTimePicker != null) {
                    if (mTimePicker.getMinute() == 0 && mTimePicker.getHour() != 0) {
                        // hour transition
                        mTimePicker.setHour(mTimePicker.getHour() - 1);
                        mTimePicker.setMinute(59);
                    } else {
                        // minute transition
                        mTimePicker.setMinute(mTimePicker.getMinute() - 1);
                    }
                } else {
                    Log.wtf(TAG, "TimePicker is null");
                }
            }
            @Override
            public void onFinish() {
                mTimePicker.setMinute(mTimePicker.getMinute() - 1);
                doSomething();
            }
        };
        countDownTimer.start();

        setTitle(location.getName());
        TextView ancillaryFields = (TextView) findViewById(R.id.payment_activity_title_ancillary_fields);
        ancillaryFields.setText(
                String.format(getString(R.string.activity_title_ancillary_fields),
                        location.getSuburb(), location.getState(), location.getPostcode())
        );
        ancillaryFields.setVisibility(View.VISIBLE);
    }

    private void doSomething() {
        // todo this
    }
}

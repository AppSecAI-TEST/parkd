package com.vinot.parkd;

import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Including logic to finalise payment, based upon the incoming Intent and the EXTRA_PRICE that
 * comes with it.
 */
public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = PaymentActivity.class.getSimpleName();
    private Location mLocation;
    private int mHour;
    private int mMinute;
    private TimePicker mTimePicker = null;

    private static final boolean TESTING_SUCCESSFUL = true;
    private static final int MINUTE = 60000; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setSupportActionBar((Toolbar) findViewById(R.id.payment_activity_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocation = getIntent().getParcelableExtra(LocationActivity.EXTRA_LOCATION);
        mHour = getIntent().getIntExtra(LocationActivity.EXTRA_HOUR, 0);
        mMinute = getIntent().getIntExtra(LocationActivity.EXTRA_MINUTE, 0);

        if (TESTING_SUCCESSFUL) {
            Snackbar.make(
                    findViewById(R.id.payment_activity_coordinator_layout), R.string.activity_payment_success, Snackbar.LENGTH_LONG
            ).show();

            mTimePicker = (TimePicker) findViewById(R.id.activity_payment_timepicker);
            mTimePicker.setIs24HourView(true);
            mTimePicker.setHour(mHour);
            mTimePicker.setMinute(mMinute);
            mTimePicker.setEnabled(false);

            CountDownTimer countDownTimer = new CountDownTimer((mHour * 60 + mMinute) * MINUTE, MINUTE) {
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
                    }
                }
                @Override
                public void onFinish() {
                    mTimePicker.setMinute(mTimePicker.getMinute() - 1);
                    doSomething();
                }
            };
            countDownTimer.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        PaymentActivity.this.setTitle(mLocation.getName());
        TextView ancillaryFields = (TextView) findViewById(R.id.payment_activity_title_ancillary_fields);
        ancillaryFields.setText(
                String.format(getString(R.string.activity_title_ancillary_fields),
                        mLocation.getSuburb(), mLocation.getState(), mLocation.getPostcode())
        );
        ancillaryFields.setVisibility(View.VISIBLE);
    }

    private void doSomething() {
        // todo this
    }
}

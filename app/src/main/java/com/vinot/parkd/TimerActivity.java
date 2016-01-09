package com.vinot.parkd;

import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimerActivity extends PaymentTimerAwareActivity {

    private TimePicker mTimePicker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setSupportActionBar((Toolbar) findViewById(R.id.payment_activity_toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTimePicker = (TimePicker) findViewById(R.id.activity_payment_timepicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setEnabled(false);
    }

    private void updateUserInterface(final Location location, final int hour, final int minute) {

        if (getIntent().getAction() != null && getIntent().getAction().equals(PaymentTimerService.ACTION_PAYMENT_SUCCESS)) {
            Snackbar.make(
                    findViewById(R.id.payment_activity_coordinator_layout), R.string.activity_payment_success, Snackbar.LENGTH_LONG
            ).show();
        }
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);

        setTitle(location.getName());
        TextView ancillaryFields = (TextView) findViewById(R.id.payment_activity_title_ancillary_fields);
        ancillaryFields.setText(
                String.format(getString(R.string.activity_title_ancillary_fields),
                        location.getSuburb(), location.getState(), location.getPostcode())
        );
        ancillaryFields.setVisibility(View.VISIBLE);
    }
    
    @Override
    protected void onServiceConnected(IBinder service) {
        super.onServiceConnected(service);
        updateUserInterface(mService.getLocation(), mService.getHour(), mService.getMinute());
    }
}

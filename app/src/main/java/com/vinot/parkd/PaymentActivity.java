package com.vinot.parkd;

import android.support.v7.app.AppCompatActivity;
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
public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = PaymentActivity.class.getSimpleName();
    private Location mLocation;

    private static final boolean TESTING_SUCCESSFUL = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setSupportActionBar((Toolbar) findViewById(R.id.payment_activity_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLocation = getIntent().getParcelableExtra(LocationActivity.EXTRA_LOCATION);
        Log.d(TAG, mLocation.getName());
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

        TextView paymentStatus = (TextView) findViewById(R.id.payment_status_textview);

        if (TESTING_SUCCESSFUL) {
            paymentStatus.setText(getString(R.string.activity_payment_success));
            paymentStatus.setTextColor(getColor(R.color.greenAccent));

            TimePicker timePicker = (TimePicker) findViewById(R.id.activity_payment_timepicker);
            timePicker.setIs24HourView(true);
            timePicker.setHour(getIntent().getIntExtra(LocationActivity.EXTRA_HOUR, 0));
            timePicker.setMinute(getIntent().getIntExtra(LocationActivity.EXTRA_MINUTE, 0));
            timePicker.setEnabled(false);
        }

    }
}

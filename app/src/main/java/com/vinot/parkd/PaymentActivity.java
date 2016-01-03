package com.vinot.parkd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
        mLocation = getIntent().getParcelableExtra(LocationActivity.EXTRA_LOCATION);
        Log.d(TAG, mLocation.getName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        PaymentActivity.this.setTitle(mLocation.getName());

        TextView paymentStatus = (TextView) findViewById(R.id.payment_status_textview);

        if (TESTING_SUCCESSFUL) {
            paymentStatus.setText(getString(R.string.activity_payment_success));
            paymentStatus.setTextColor(getColor(R.color.colorPrimary));
        }

    }
}

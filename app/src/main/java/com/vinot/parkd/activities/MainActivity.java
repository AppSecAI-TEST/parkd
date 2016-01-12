package com.vinot.parkd.activities;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.hawk.Hawk;
import com.vinot.parkd.fragments.NfcDialogFragment;
import com.vinot.parkd.R;

public class MainActivity extends AppCompatActivity implements NfcDialogFragment.NfcDialogListener {

    private NfcAdapter mNfcAdapter;
    private Snackbar mNfcDisabledSnackbar;
    private Intent mNfcSettingsIntent;
    private boolean mIsReturningFromSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.button_enter_location_pin), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcSettingsIntent = new Intent(Settings.ACTION_NFC_SETTINGS);
        // todo need to be able to show this Snackbar any time the NFC settings change (i.e. detect when settings change)
        mNfcDisabledSnackbar = Snackbar.make(
                findViewById(R.id.main_activity_coordinator_layout), getString(R.string.nfc_not_enabled), Snackbar.LENGTH_INDEFINITE
        ).setAction(
                getString(R.string.nfc_settings), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(mNfcSettingsIntent);
                        mIsReturningFromSettings = true;
                    }
                }
        );
        if (mNfcAdapter == null) {
            // Device does not support NFC.  Disable all NFC features and/or warn about not being
            // able to use many of the key features of the app.
        } else {
            if (!mNfcAdapter.isEnabled()) {
                (new NfcDialogFragment()).show(getSupportFragmentManager(), "NfcDialogFragment");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsReturningFromSettings) {
            // we must use this instead of startActivityForResult and onActivityResult
            // as Settings do not support startActivityForResult flow.
            if (!mNfcAdapter.isEnabled()) mNfcDisabledSnackbar.show();
            else mNfcDisabledSnackbar.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_map:
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
                return true;
            case R.id.action_bump:
                return true;
            case R.id.action_clear_data:
                Hawk.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        startActivity(mNfcSettingsIntent);
        mIsReturningFromSettings = true;
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog) {
        // todo disable all NFC features.
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (!mNfcAdapter.isEnabled()) mNfcDisabledSnackbar.show();
        else mNfcDisabledSnackbar.dismiss();
    }
}

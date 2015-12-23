package com.vinot.parkd;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NfcActivity extends AppCompatActivity implements LocationFragment.OnFragmentInteractionListener, OnMapReadyCallback {

    private static final String TAG = NfcActivity.class.getSimpleName();
    private Intent mIntent = null;
    private NdefMessage mMessages[] = null;
    private GoogleMap mMap;
    //private byte[] mTagId = null;
    private boolean mSingleMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mIntent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(mIntent.getAction())) {
            Log.d(TAG, "Tag detected");
            mMessages = produceNdefMessages(mIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
            //mTagId = mIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID); // todo use this instead of *any* identifying NDEF record
            if (mMessages.length == 1) {
                mSingleMessage = true;
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Produce an array of NdefMessages from a Parcelable array.
     * At this stage Android does not even support multiple messages, so this is overkill
     * http://stackoverflow.com/questions/17496811/android-putting-multiple-ndef-messages-in-one-nfc-tag
     * @param rawMsgs
     * @return
     */
    private NdefMessage[] produceNdefMessages(final Parcelable[] rawMsgs) {
        NdefMessage messages[] = null;
        if (rawMsgs != null) {
            messages = new NdefMessage[rawMsgs.length];
            for (int k = 0; k < rawMsgs.length; k++) {
                messages[k] = (NdefMessage) rawMsgs[k];
            }
        }
        return messages;
    }

    ///////////////
    // Fragments //
    ///////////////

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean isNfcInitialised() {
        return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(mIntent.getAction());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    // this is very dependent on how the tag is laid out and if not the correct way to interact with
    // fragments. Needs to be refactored.
    public Uri getLocationUri() {
        if (mSingleMessage && mMessages != null) {
            return mMessages[0].getRecords()[0].toUri();
        }
        return null;
    }


}

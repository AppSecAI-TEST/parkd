package com.vinot.parkd;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class NfcActivity extends AppCompatActivity implements LocationFragment.OnFragmentInteractionListener {

    private static final String TAG = NfcActivity.class.getSimpleName();
    private Intent mIntent = null;
    private NdefMessage mMessages[] = null;
    private byte[] mTagId = null;
    private boolean mSingleMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mIntent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(mIntent.getAction())) {
            Log.d(TAG, "Tag detected");
            mMessages = produceNdefMessages(mIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
            mTagId = mIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID); // todo use this instead of *any* identifying NDEF record
            if (mMessages.length == 1) {
                mSingleMessage = true;
            }
        }
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

    // this is very dependent on how the tag is laid out and if not the correct way to interact with
    // fragments. Needs to be refactored.
    public Uri getLocationUri() {
        if (mSingleMessage && mMessages != null) {
            return mMessages[0].getRecords()[0].toUri();
        }
        return null;
    }
}

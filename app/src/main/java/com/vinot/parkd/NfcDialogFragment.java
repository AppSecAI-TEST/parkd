package com.vinot.parkd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class NfcDialogFragment extends DialogFragment {

    private NfcDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NfcDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NfcDialogListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return
                (new AlertDialog.Builder(getActivity()))
                        .setTitle(R.string.nfc_request)
                        .setMessage(R.string.nfc_request_message)
                        .setPositiveButton(R.string.nfc_switch_on, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.onDialogPositiveClick(NfcDialogFragment.this);
                            }
                        })
                        .setNeutralButton(R.string.nfc_disable, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.onDialogNeutralClick(NfcDialogFragment.this);
                            }
                        })
                        .setNegativeButton(R.string.nfc_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.onDialogNegativeClick(NfcDialogFragment.this);
                            }
                        })
                        .create();
    }

    public interface NfcDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNeutralClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
}

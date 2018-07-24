package com.eajy.easybuy.base;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;

public class BaseNfcActivity extends AppCompatActivity {

    protected NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onStart() {

        super.onStart();
        // to get adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // if get nfc message, call windows via PendingIntent
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
    }

    //to get the focus point
    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    // pause the Activity, get the focus
    @Override
    public  void onPause() {
        super.onPause();
        // return the original state
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }




}

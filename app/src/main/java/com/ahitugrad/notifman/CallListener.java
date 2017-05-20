package com.ahitugrad.notifman;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

/**
 * Created by maziz on 20.05.2017.
 */

public class CallListener extends PhoneStateListener {
    Context mContext;
    public static String LOG_TAG = "CallListener";

    public CallListener(Context context) {
        mContext = context;
    }


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_IDLE");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_RINGING");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
                break;
            default:
                Log.i(LOG_TAG, "UNKNOWN_STATE: " + state);
                break;
        }
    }

}

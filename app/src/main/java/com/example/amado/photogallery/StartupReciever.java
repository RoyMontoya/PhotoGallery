package com.example.amado.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Amado on 15/06/2015.
 */
public class StartupReciever extends BroadcastReceiver {
    private static final String TAG = "StartupReciever";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received broadcast intent: "+intent.getAction());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn= prefs.getBoolean(PollService.PREF_IS_ALARM_ON, false);
        PollService.setServiceAlarm(context, isOn);
    }
}

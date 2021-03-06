package com.danesh.moveover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        SharedPreferences myPrefs = arg0.getSharedPreferences("storedPreferences", Context.MODE_PRIVATE);
        if (myPrefs.getBoolean("startOnBoot", false)){
            myPrefs = arg0.getSharedPreferences("storedArray", Context.MODE_PRIVATE);
            MoveOverActivity.sharedMap = myPrefs.getAll();
            if (!MoveOverActivity.sharedMap.isEmpty()){
                Intent mine = new Intent(arg0, LocalService.class);
                arg0.startService(mine);
            }
        }
    }
}

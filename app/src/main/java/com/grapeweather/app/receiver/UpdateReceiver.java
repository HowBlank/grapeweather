package com.grapeweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.grapeweather.app.servvice.AutoUpdateService;

/**
 * Created by fancheng on 2016/6/23.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);

    }
}

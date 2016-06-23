package com.grapeweather.app.servvice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.grapeweather.app.receiver.UpdateReceiver;
import com.grapeweather.app.utils.HttpCallBackListener;
import com.grapeweather.app.utils.HttpUtil;
import com.grapeweather.app.utils.Utility;

/**
 * Created by fancheng on 2016/6/23.
 */
public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int anHour = 8*60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, UpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){

        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = sf.getString("weather_code","");
        String address = "http://www.weather.com.cn/data/cityinfo"+weatherCode+".html";
        HttpUtil.sendHttpRequest(address,new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(response,AutoUpdateService.this);
            }

            @Override
            public void onError(Exception e) {
                 e.printStackTrace();
            }
        });
    }
}

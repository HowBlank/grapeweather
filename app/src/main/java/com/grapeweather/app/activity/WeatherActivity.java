package com.grapeweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grapeweather.app.R;
import com.grapeweather.app.utils.HttpCallBackListener;
import com.grapeweather.app.utils.HttpUtil;
import com.grapeweather.app.utils.Utility;

import java.net.URL;

/**
 * Created by fancheng on 2016/6/22.
 */
public class WeatherActivity extends Activity {

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView currentDateText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.publish_text);
        currentDateText = (TextView)findViewById(R.id.current_date);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)) {
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            publishText.setText("同步中...");
            queryWeatherCode(countyCode);
        }else{
            showWeatherInfo();
        }
    }
    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }

    private void queryFromServer(String address,final String type){
        HttpUtil.sendHttpRequest(address,new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if(type.equals("countyCode")){
                    if(!TextUtils.isEmpty(response)){
                        String[] str = response.split("\\|");
                        if(str!=null && str.length == 2){
                            String weatherCode = str[1];
                            queryWeatherInfo(weatherCode);
                        }

                    }
                }else if(type.equals("weatherCode")){
                    Utility.handleWeatherResponse(response,WeatherActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeatherInfo();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          publishText.setText("同步失败");
                      }
                  });
            }
        });
    }
    private void showWeatherInfo(){
        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sf.getString("city_name",""));
        temp1Text.setText(sf.getString("temp2","")+"~");
        temp2Text.setText(sf.getString("temp1",""));
        weatherDespText.setText(sf.getString("weather_desp",""));
        publishText.setText("今天" + sf.getString("publish_time", "") + "发布");
        currentDateText.setText(sf.getString("current_date",""));
        cityNameText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
    }
}

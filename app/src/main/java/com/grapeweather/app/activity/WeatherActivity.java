package com.grapeweather.app.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView currentDateText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private Button switchCity;
    private Button refreshWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.switch_city :
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather :
                publishText.setText("同步中...");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = sp.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode){
        String address1 = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
//        Log.e("MyActivity",address1);
        queryFromServer(address1, "weatherCode");
    }

    private void queryFromServer(final String address,final String type){
//        Log.e("MyActivity",address);
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
            public void onError(final Exception e) {


                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
//                          e.printStackTrace();
                          publishText.setText("同步失败");

                      }
                  });
            }
        });
    }

    /**
     * 显示天气信息
     */
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

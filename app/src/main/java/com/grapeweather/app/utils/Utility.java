package com.grapeweather.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.grapeweather.app.db.City;
import com.grapeweather.app.db.County;
import com.grapeweather.app.db.GrapeWeatherDB;
import com.grapeweather.app.db.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by fancheng on 2016/6/21.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的JSON数据，并将数据存储到本地
     */
    public static void handleWeatherResponse(String response,Context context){

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject js = jsonObject.getJSONObject("weatherinfo");

                String cityName = js.getString("city");
                String weatherCode = js.getString("cityid");
                String temp1 = js.getString("temp1");
                String temp2 = js.getString("temp2");
                String weatherDesp = js.getString("weather");
                String publishTime = js.getString("ptime");
                saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);

            } catch (Exception e) {
                e.printStackTrace();
            }


    }
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                 String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat df = new SimpleDateFormat("yyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",df.format(new Date()));
        editor.commit();
    }
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(String response,GrapeWeatherDB grapeWeatherDB){
        if(!TextUtils.isEmpty(response)) {
            String[] provinces = response.split(",");
            if(provinces!=null&&provinces.length>0) {
                for (String str : provinces) {
                    String[] p = str.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(p[0]);
                    province.setProvinceName(p[1]);
                    grapeWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,GrapeWeatherDB grapeWeatherDB,
                                             int provinceId){
        if(!TextUtils.isEmpty(response)) {
            String[] cities = response.split(",");
            if(cities!=null&&cities.length>0) {
                for (String str : cities) {
                    String[] s = str.split("\\|");
                    City city = new City();
                    city.setCityCode(s[0]);
                    city.setCityName(s[1]);
                    city.setProvinceId(provinceId);
                    grapeWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,GrapeWeatherDB grapeWeatherDB,
                                             int cityId){
        if(!TextUtils.isEmpty(response)) {
            String[] counties = response.split(",");
            if(counties!=null&&counties.length>0) {
                for (String str : counties) {
                    String[] s = str.split("\\|");
                    County county = new County();
                    county.setCountyCode(s[0]);
                    county.setCountyName(s[1]);
                    county.setCityId(cityId);
                    grapeWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
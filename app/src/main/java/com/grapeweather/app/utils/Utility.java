package com.grapeweather.app.utils;

import android.text.TextUtils;

import com.grapeweather.app.db.City;
import com.grapeweather.app.db.County;
import com.grapeweather.app.db.GrapeWeatherDB;
import com.grapeweather.app.db.Province;

/**
 * Created by fancheng on 2016/6/21.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response,GrapeWeatherDB grapeWeatherDB){
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
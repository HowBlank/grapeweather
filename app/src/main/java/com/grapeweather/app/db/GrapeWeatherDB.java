package com.grapeweather.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fancheng on 2016/6/20.
 */
public class GrapeWeatherDB  {
    /*
    数据库名
     */
    public static final String DB_NAME = "grape_weather";
    /*
    数据库版本
     */
    public static final int VERSION = 1;
    private static GrapeWeatherDB grapeWeatherDB;
    private SQLiteDatabase db;
    private GrapeWeatherDB(Context context){
        GrapeWeatherOpenHelper grapeWeatherOpenHelper = new GrapeWeatherOpenHelper(
                context,DB_NAME,null,VERSION);
        db = grapeWeatherOpenHelper.getWritableDatabase();
    }
    /*
    获取GrapeWeatherDB的实例
     */
    public synchronized static GrapeWeatherDB getInstance(Context context){
        if(grapeWeatherDB == null) {
            grapeWeatherDB = new GrapeWeatherDB(context);
        }
        return grapeWeatherDB;
    }
    /*
    将Province实例存储到数据库中
     */
    public void saveProvince(Province province){
        if(province != null) {
            ContentValues contentValues = new ContentValues();
            //contentValues.put("id", province.getId());此句可以不要，因为id是自增的，取得时候再用
            contentValues.put("province_name", province.getProvinceName());
            contentValues.put("province_code", province.getProvinceCode());
            db.insert("Province", null, contentValues);
        }
    }
    /*
    取出所有的省份信息
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = null;
        cursor = db.query(DB_NAME,null,null,null,null,null,null);

        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);

            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }

        return list;
    }
    /*
    将city对象存进数据库
     */
    private void saveCity(City city){
        if(city != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name",city.getCityName());
            contentValues.put("city_code",city.getCityCode());
            contentValues.put("province_id",city.getProvinceId());
            db.insert("City",null,contentValues);
        }
    }
    /*
    取出对应省份的所有城市信息
     */
    private List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},
                null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
    /*
    将County实例存入数据库
     */
    private void saveCounty(County county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }
    private List<County> loadCounty(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County",null,"city_id = ?",new String[]{String.valueOf(cityId)},
                null,null,null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
}
